package edu.washington.cs.dt.instrument;

import java.io.IOException;
import java.io.Writer;

import com.ibm.wala.shrikeBT.ConstantInstruction;
import com.ibm.wala.shrikeBT.Disassembler;
import com.ibm.wala.shrikeBT.DupInstruction;
import com.ibm.wala.shrikeBT.GetInstruction;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeBT.Instruction;
import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.shrikeBT.MethodEditor;
import com.ibm.wala.shrikeBT.PopInstruction;
import com.ibm.wala.shrikeBT.PutInstruction;
import com.ibm.wala.shrikeBT.ReturnInstruction;
import com.ibm.wala.shrikeBT.SwapInstruction;
import com.ibm.wala.shrikeBT.ThrowInstruction;
import com.ibm.wala.shrikeBT.Util;
import com.ibm.wala.shrikeBT.MethodEditor.Output;
import com.ibm.wala.shrikeBT.analysis.Verifier;
import com.ibm.wala.shrikeBT.analysis.Analyzer.FailureException;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeCT.ClassWriter;

import edu.washington.cs.conf.instrument.AbstractInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentStats;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Log;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class FieldAccessInstrumenter extends AbstractInstrumenter {

	static final Instruction getTracer = Util.makeGet(DTTracer.class, "tracer");
	
	static final Instruction traceMethodEntry = Util.makeInvoke(
			DTTracer.class, "traceMethodEntry",
			new Class[] { String.class });
	
	static final Instruction traceMethodExit = Util.makeInvoke(
			DTTracer.class, "traceMethodExit",
			new Class[] { String.class });
	
	static final Instruction traceFieldRead = Util.makeInvoke(
			DTTracer.class, "traceFieldRead",
			new Class[] {Object.class , String.class, String.class});
	
	static final Instruction traceFieldWrite = Util.makeInvoke(
			DTTracer.class, "traceFieldWrite",
			new Class[] { Object.class,  String.class, String.class});
	
	private String[] methodPrefix = new String[]{};
	
	public void setMethodClassPrefix(String[] prefixes) {
		Utils.checkNoNull(prefixes);
		this.methodPrefix = prefixes;
	}
	
	@Override
	protected void doClass(ClassInstrumenter ci, Writer w) throws Exception {
		final String className = ci.getReader().getName();
		w.write("Class: " + className + "\n");
		w.flush();
		
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

				final String methodSig = WALAUtils.getFullMethodNameNoSig(d);
				
				Log.logln("Instrument: " + methodSig);
				
				//instrument the beginning and end of a method
				if(Utils.startWith(methodSig, this.methodPrefix)) {
					System.out.println("Instrument method: " + methodSig);
				    this.instrumentMethod(me, methodSig);
				}
				
				// profiling the predicates
				int length = me.getInstructions().length;
				for (int i = 0; i < length; i++) {
					IInstruction inst = me.getInstructions()[i];
					if (inst instanceof GetInstruction) {
						GetInstruction getInstr = (GetInstruction)inst;
						final String fieldFullName = getInstr.getClassType() + "." + getInstr.getFieldName();
						final String fieldType = getInstr.getFieldType();
						if(getInstr.isStatic()) {
							me.insertBefore(i, new MethodEditor.Patch() {
								@Override
								public void emitTo(MethodEditor.Output w) {
									w.emit(getTracer);
									w.emit(ConstantInstruction.make(null, null));
									w.emit(ConstantInstruction.makeString(fieldFullName));
									w.emit(ConstantInstruction.makeString(fieldType));
									w.emit(traceFieldRead);
									InstrumentStats.addInsertedInstructions(1);
								}
							});
						} else {
							//non-static get: ...o   ==> ... o.f
							//dup .. o => ... o o
							//gettracer ..o o => ... o o c
							//swap ... o c o
							//put 2 string ==> ... o c o str  str
							me.insertBefore(i, new MethodEditor.Patch() {
								@Override
								public void emitTo(MethodEditor.Output w) {
									w.emit(DupInstruction.make(0));
									w.emit(getTracer);
									w.emit(SwapInstruction.make());
									w.emit(ConstantInstruction.makeString(fieldFullName));
									w.emit(ConstantInstruction.makeString(fieldType));
									w.emit(traceFieldRead);
									InstrumentStats.addInsertedInstructions(1);
								}
							});
						}
					} else if (inst instanceof PutInstruction) {
						PutInstruction putInst = (PutInstruction)inst;
						final String fieldFullName = putInst.getClassType() + "." + putInst.getFieldName();
						final String fieldType = putInst.getFieldType();
						// ... v ==> ...
						if(putInst.isStatic()) {
							me.insertBefore(i, new MethodEditor.Patch() {
								@Override
								public void emitTo(MethodEditor.Output w) {
									w.emit(getTracer);
									w.emit(ConstantInstruction.make(null, null));
									w.emit(ConstantInstruction.makeString(fieldFullName));
									w.emit(ConstantInstruction.makeString(fieldType));
									w.emit(traceFieldWrite);
									InstrumentStats.addInsertedInstructions(1);
								}
							});
						} else {
							//TODO beaware v can be a w?
							// ... o, v ==> ...
							// dup_x1 == > ... v o v
							// pop  ==> ... v o
							// dup_x1 ==> .. o v o
							// get tracer ==> o v  o c
							// swap ==> o v c o
							//put two values: o v c o str str
							me.insertBefore(i, new MethodEditor.Patch() {
								@Override
								public void emitTo(MethodEditor.Output w) {
									w.emit(DupInstruction.make(1));
									w.emit(PopInstruction.make(1));
									w.emit(DupInstruction.make(1));
									w.emit(getTracer);
									w.emit(SwapInstruction.make());
									w.emit(ConstantInstruction.makeString(fieldFullName));
									w.emit(ConstantInstruction.makeString(fieldType));
									w.emit(traceFieldWrite);
									InstrumentStats.addInsertedInstructions(1);
								}
							});
						}
					}
				}
				me.applyPatches();
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
		me.insertAtStart(new MethodEditor.Patch() {
            @Override
            public void emitTo(MethodEditor.Output w) {
              w.emit(getTracer);
              w.emit(ConstantInstruction.makeString(methodSig));
              w.emit(traceMethodEntry);
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
                    w.emit(ConstantInstruction.makeString(methodSig));
                    w.emit(traceMethodExit);
                  InstrumentStats.addNormalInsertations(1);
                }
              });
            }
          }
          
          me.addMethodExceptionHandler(null, new MethodEditor.Patch() {
            @Override
            public void emitTo(Output w) {
            	w.emit(getTracer);
                w.emit(ConstantInstruction.makeString(methodSig));
                w.emit(traceMethodExit);
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

}
