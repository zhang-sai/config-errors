package edu.washington.cs.conf.instrument;

import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.shrikeBT.ConstantInstruction;
import com.ibm.wala.shrikeBT.Constants;
import com.ibm.wala.shrikeBT.Disassembler;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeBT.Instruction;
import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.shrikeBT.MethodEditor;
import com.ibm.wala.shrikeBT.Util;
import com.ibm.wala.shrikeBT.analysis.Verifier;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeCT.ClassReader;
import com.ibm.wala.shrikeCT.ClassWriter;

import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.IRStatement;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

/**
 * Only instruments statements which are affected by at least
 * one configuration option values
 * */
public class RelatedStmtInstrumenter extends AbstractInstrumenter {

    static final String fieldName = "_Related_Stmt_enable_trace";
	
	static final Instruction getTracer = Util.makeGet(StmtTracer.class, "tracer");
	static final Instruction callTrace = Util.makeInvoke(StmtTracer.class, "trace", new Class[] { String.class });
	
	private Set<String> skippedClasses = null;
	
	private Set<String> instrumentedClasses = null;
	
	private final Collection<ConfPropOutput> slices;
	
	//All affected instruction methods and index
	private final Map<String, Set<Integer>> mapIndices;
	
	public RelatedStmtInstrumenter(Collection<ConfPropOutput> outputs) {
		slices = new LinkedList<ConfPropOutput>();
		slices.addAll(outputs);
		//create the map for index
		mapIndices = new LinkedHashMap<String, Set<Integer>>();
		//initialize the map
		this.initInstrumentMap(slices, mapIndices);
	}
	
	private void initInstrumentMap(Collection<ConfPropOutput> slices, Map<String, Set<Integer>> mapIndices) {
		Utils.checkNotNull(mapIndices);
		for(ConfPropOutput slice : slices) {
			for(IRStatement irs : slice.statements) {
				String methodSig = irs.getMethodSig();
				Integer index = irs.getInstructionIndex();
				if(!mapIndices.containsKey(methodSig)) {
					mapIndices.put(methodSig, new HashSet<Integer>());
				}
				mapIndices.get(methodSig).add(index);
			}
		}
	}
	
	//full name
	public void setSkippedClasses(Collection<String> classes) {
		skippedClasses = new LinkedHashSet<String>();
		skippedClasses.addAll(classes);
	}
	
	public void setInstrumentedClassPrefix(Collection<String> classes) {
		instrumentedClasses = new LinkedHashSet<String>();
		instrumentedClasses.addAll(classes);
	}
	
	@Override
	protected void doClass(ClassInstrumenter ci, Writer w) throws Exception {
		final String className = ci.getReader().getName();
	    w.write("Class: " + className + "\n");
	    w.flush();
	    
	    if(this.skippedClasses != null) {
	    	String cName = Utils.translateSlashToDot(className);
	    	if(Utils.startWith(cName, this.skippedClasses.toArray(new String[0]))) {
//	    	if(this.skippedClasses.contains(cName)) {
	    		System.out.print("Skip. " + cName + "\n");
	    		return;
	    	}
	    }
	    
	    if(this.instrumentedClasses != null) {
	    	String cName = Utils.translateSlashToDot(className);
	    	if(!Utils.startWith(cName, this.instrumentedClasses.toArray(new String[0]))) {
	    		System.out.print("Not in instrumented class: " + cName + "\n");
	    		return;
	    	}
	    }

	    for (int m = 0; m < ci.getReader().getMethodCount(); m++) {
	      MethodData d = ci.visitMethod(m);
	      // d could be null, e.g., if the method is abstract or native
	      if (d != null) {
	        w.write("Instrumenting " + ci.getReader().getMethodName(m) + " " + ci.getReader().getMethodType(m) + ":\n");
	        w.flush();
	        if (disasm) {
	          w.write("Initial ShrikeBT code:\n");
	          (new Disassembler(d)).disassembleTo(w);
	          w.flush();
	        }
	        if (verify) {
	          Verifier v = new Verifier(d);
	          v.verify();
	        }

	        MethodEditor me = new MethodEditor(d);
	        me.beginPass();

	        //the unique method signature
        	String methodSig = WALAUtils.getMethodSignature(d);
        	
        	//instrument every instruction
	        int length = me.getInstructions().length;
	        for(int i = 0; i < length; i++) {
	        	IInstruction inst = me.getInstructions()[i];
	        	String instStr = inst.toString();
	        	if(this.mapIndices.containsKey(methodSig)) {
	        		if(this.mapIndices.get(methodSig).contains(i)) {
	        			final String msg = methodSig + SEP + instStr +  SEP  + i;  //a unique encoding
	        			System.out.println("instrument msg: " + msg);
	    	        	me.insertBefore(i, new MethodEditor.Patch() {
	                        @Override
	                        public void emitTo(MethodEditor.Output w) {
	                          //w.emit(getSysErr);
	                      	  w.emit(getTracer);
	                          w.emit(ConstantInstruction.makeString(msg));
	                          w.emit(callTrace);
	                          //keep the statistics
	                          InstrumentStats.addInsertedInstructions(1);
	                        }
	                     });
	        		}
	        	}
	        }
	        
	        // this updates the data, instrumentation ends
	        me.applyPatches();
	        if (disasm) {
	          w.write("Final ShrikeBT code:\n");
	          (new Disassembler(d)).disassembleTo(w);
	          w.write(Globals.lineSep);
	          w.flush();
	        }
	      }
	    }

	    if (ci.isChanged()) {
	      ClassWriter cw = ci.emitClass();
	      cw.addField(ClassReader.ACC_PUBLIC | ClassReader.ACC_STATIC, fieldName, Constants.TYPE_boolean, new ClassWriter.Element[0]);
	      instrumenter.outputModifiedClass(ci, cw);
	    }
	}

}
