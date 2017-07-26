package edu.washington.cs.conf.instrument.evol;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.shrikeBT.ConditionalBranchInstruction;
import com.ibm.wala.shrikeBT.ConstantInstruction;
import com.ibm.wala.shrikeBT.Constants;
import com.ibm.wala.shrikeBT.Disassembler;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeBT.Instruction;
import com.ibm.wala.shrikeBT.LoadInstruction;
import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.shrikeBT.MethodEditor;
import com.ibm.wala.shrikeBT.ReturnInstruction;
import com.ibm.wala.shrikeBT.ThrowInstruction;
import com.ibm.wala.shrikeBT.Util;
import com.ibm.wala.shrikeBT.MethodEditor.Output;
import com.ibm.wala.shrikeBT.analysis.Analyzer.FailureException;
import com.ibm.wala.shrikeBT.analysis.Verifier;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeCT.ClassWriter;

import edu.washington.cs.conf.analysis.evol.InstructionExecInfo;
import edu.washington.cs.conf.instrument.AbstractInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentStats;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Log;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

/**
 * Instrument predicate, method in and out
 * */
public class PredicateInstrumenter extends AbstractInstrumenter {

	static final Instruction getTracer = Util.makeGet(EfficientTracer.class, "tracer");
	static final Instruction predicateFreqTrace = Util.makeInvoke(
			EfficientTracer.class, "tracePredicateFrequency",
			new Class[] { String.class });
	static final Instruction predicateResultTrace = Util.makeInvoke(
			EfficientTracer.class, "tracePredicateResult",
			new Class[] { String.class });
	static final Instruction normalInstrTrace = Util.makeInvoke(
			EfficientTracer.class, "traceNormalInstruction",
			new Class[] { String.class });

	private String[] appPkgNames = null;
	private Set<String> skipClassPrefix = new HashSet<String>();
	
//	private InstrumentSchema schema = null;
	
	//rather than recording the whole string, map a string to an integer
	private boolean useSigMapping = false;
	private Map<String, Integer> sigMap = new HashMap<String, Integer>();
	private int sigCount = 0;
	
	private boolean instrumentEveryStmt = true;
	private boolean instrumentEveryMethod = true;
	private boolean instrumentEveryPredicate = true;

	public PredicateInstrumenter() {
		this(Collections.<String> emptyList(), Collections.<String> emptyList());
	}

	public PredicateInstrumenter(Collection<String> pkgNames,
			Collection<String> skipClassPrefix) {
		if (!pkgNames.isEmpty()) {
			this.appPkgNames = pkgNames.toArray(new String[0]);
		}
		this.skipClassPrefix.addAll(skipClassPrefix);
	}
	
//	public void setInstrumentSchema(InstrumentSchema schema) {
//		Utils.checkNotNull(schema);
//		this.schema = schema;
//	}
	
	public void setEveryStmt(boolean instrumentEvery) {
		this.instrumentEveryStmt = instrumentEvery;
	}
	
	public void setEveryMethod(boolean instrumentMethod) {
		this.instrumentEveryMethod = instrumentMethod;
	}
	
	public void setEveryPredicate(boolean instrumentPredicate) {
		this.instrumentEveryPredicate = instrumentPredicate;
	}
	
	public void setUseSigMap(boolean useSigMap) {
		this.useSigMapping = useSigMap;
	}

	@Override
	protected void doClass(ClassInstrumenter ci, Writer w) throws Exception {
		final String className = ci.getReader().getName();
		w.write("Class: " + className + "\n");
		w.flush();

		if(this.shouldSkip(className)) {
			w.write("  skip!");
			w.flush();
			return;
		}
		
		boolean changed = false;
		
		for (int m = 0; m < ci.getReader().getMethodCount(); m++) {
			MethodData d = ci.visitMethod(m);
			// d could be null, e.g., if the method is abstract or native
			if (d != null) {
				w.write("Instrumenting " + ci.getReader().getMethodName(m)
						+ " " + ci.getReader().getMethodType(m) + ":\n");
				w.flush();

				// optionally
				this.disamAndVerify(d, w);

				MethodEditor me = new MethodEditor(d);
				me.beginPass();

				final String methodSig = WALAUtils.getMethodSignature(d);
				
				Log.logln("Instrument: " + methodSig);
				
				//instrument the beginning and end of a method
				if(this.instrumentEveryMethod) {
					changed = true;
				    this.instrumentMethod(me, methodSig);
				}
				
				// profiling the predicates
				int length = me.getInstructions().length;
				for (int i = 0; i < length; i++) {
					
					//check whether this instruction should be instrumented
//					if(this.schema != null) {
//						if(this.shouldSkip(methodSig, i, this.schema)) {
//							continue;
//						}
//					}
					
					IInstruction inst = me.getInstructions()[i];
//					final String stmtSig = methodSig + EfficientTracer.SEP + i;
					final String stmtSig = this.constructStmtSig(methodSig, i);
					if (this.isPredicate(inst) && this.instrumentEveryPredicate) {
						   /**instrument the predicate**/
//						   System.out.println("instr: " + inst);
							// methodSig is not a uniquely-identifiable,
							// so plus the instruction index before evaluation
						    changed = true;
							InstrumentStats.addInstrumentedPositions(stmtSig);
							me.insertBefore(i, new MethodEditor.Patch() {
								@Override
								public void emitTo(MethodEditor.Output w) {
									w.emit(getTracer);
									w.emit(ConstantInstruction.makeString(stmtSig));
									w.emit(predicateFreqTrace);
									InstrumentStats.addInsertedInstructions(1);
								}
							});
							me.insertAfter(i, new MethodEditor.Patch() {
								@Override
								public void emitTo(MethodEditor.Output w) {
									w.emit(getTracer);
									w.emit(ConstantInstruction.makeString(stmtSig));
									w.emit(predicateResultTrace);
									InstrumentStats.addInsertedInstructions(1);
								}
							});
					} else {/**instrument other instructions */
						//FIXME insert before?
						if(this.instrumentEveryStmt) {
							changed = true;
						    me.insertBefore(i, new MethodEditor.Patch() {
							    @Override
							    public void emitTo(MethodEditor.Output w) {
								    w.emit(getTracer);
								    w.emit(ConstantInstruction.makeString(stmtSig));
								    w.emit(normalInstrTrace);
								    InstrumentStats.addInsertedInstructions(1);
							    }
						    });
						    InstrumentStats.addNormalInsertations(1);
						}
					}
				}

				// this updates the data d
				if(changed) {
				    me.applyPatches();
				}
				if (disasm) {
					w.write("Final ShrikeBT code:\n");
					(new Disassembler(d)).disassembleTo(w);
					w.write(Globals.lineSep);
					w.flush();
				}
			}
		}

		// if the class has been modified, issue the modified class
		if (ci.isChanged()) {
			ClassWriter cw = ci.emitClass();
			instrumenter.outputModifiedClass(ci, cw);
		}
	}
	
	/**
	 * Instrument before and after a method
	 * */
	private void instrumentMethod(MethodEditor me, final String methodSig) {
		final String startSig = this.constructStmtSig(methodSig, InstructionExecInfo.startIndex);
		final String endSig = this.constructStmtSig(methodSig, InstructionExecInfo.endIndex);
		me.insertAtStart(new MethodEditor.Patch() {
            @Override
            public void emitTo(MethodEditor.Output w) {
              w.emit(getTracer);
              w.emit(ConstantInstruction.makeString(startSig));
              w.emit(normalInstrTrace);
              InstrumentStats.addNormalInsertations(1);
            }
          });
    	
    	  IInstruction[] instr = me.getInstructions();
          for (int i = 0; i < instr.length; i++) {
            if (instr[i] instanceof ReturnInstruction) {
              me.insertBefore(i, new MethodEditor.Patch() {
                @Override
                public void emitTo(MethodEditor.Output w) {
                	w.emit(getTracer);
                    w.emit(ConstantInstruction.makeString(endSig));
                    w.emit(normalInstrTrace);
                  InstrumentStats.addNormalInsertations(1);
                }
              });
            }
          }
          
          me.addMethodExceptionHandler(null, new MethodEditor.Patch() {
            @Override
            public void emitTo(Output w) {
            	w.emit(getTracer);
                w.emit(ConstantInstruction.makeString(endSig));
                w.emit(normalInstrTrace);
                //keep the statistics
                InstrumentStats.addNormalInsertations(1);
                //must rethrow the exception
                w.emit(ThrowInstruction.make(false));
            }
          });
	}

	private void disamAndVerify(MethodData d, Writer w) throws IOException,
			FailureException {
		if (disasm) {
			w.write("Initial ShrikeBT code:\n");
			(new Disassembler(d)).disassembleTo(w);
			w.flush();
		}
		if (verify) {
			Verifier v = new Verifier(d);
			try {
			    v.verify();
			} catch (Throwable e) {
				System.err.println("Error in: " + d.getSignature());
			}
		}
	}
	
	private String constructStmtSig(String methodSig, int index) {
		String sig = methodSig + EfficientTracer.SEP + index;
		if(!this.useSigMapping) {
		    return sig;
		} else {
			if(this.sigMap.containsKey(sig)) {
				return this.sigMap.get(sig).toString();
			} else {
				this.sigCount++;
				this.sigMap.put(sig, this.sigCount);
				return String.valueOf(this.sigCount);
			}
		}
	}
	
	public static final String sigSep = "=>";
	public static final String defaultSigMapFile = "./sig_map.txt";
	
	public void saveSigMappings() {
		try {
			this.saveSigMappings(defaultSigMapFile);
		} catch (IOException e) {
			throw new Error(e);
		}
	}
	
	public void saveSigMappingsNoExp(String fileName) {
		try {
			this.saveSigMappings(fileName);
		} catch (IOException e) {
			throw new Error(e);
		}
	}
	
	public void saveSigMappings(String fileName) throws IOException {
		System.out.println("Write sig mapping to: " + new File(fileName).getAbsolutePath());
		Utils.checkTrue(this.useSigMapping);
		Files.deleteFile(fileName);
		Files.createIfNotExist(fileName);
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
		try {
		    for(String sig : this.sigMap.keySet()) {
			    writer.append(sig);
			    writer.append(sigSep);
			    writer.append(this.sigMap.get(sig)+"");
		        writer.append(Globals.lineSep);
		    } 
		} finally {
			   writer.close();
		}
	}
	
	//FIXME it is better to double check this, and only
	//instrument predicates thare are affected by some
	//configuration options
	private boolean isPredicate(IInstruction instruction) {
		return instruction instanceof ConditionalBranchInstruction;
	}

	private boolean shouldSkip(String className) {
		if(this.appPkgNames == null || this.appPkgNames.length == 0) {
			return false;
		}
		String cName = Utils.translateSlashToDot(className);
		if(!Utils.startWith(cName, this.appPkgNames)) {
			return true;
		}
		return false;
	}
	
//	private boolean shouldSkip(String methodSig, int instructionIndex, InstrumentSchema schema) {
//		return !schema.hasInstrumentationPredicates(methodSig, instructionIndex);
//	}
}