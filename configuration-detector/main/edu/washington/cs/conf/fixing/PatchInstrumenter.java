package edu.washington.cs.conf.fixing;

import java.io.PrintStream;
import java.io.Writer;

import com.ibm.wala.shrikeBT.ConditionalBranchInstruction;
import com.ibm.wala.shrikeBT.ConstantInstruction;
import com.ibm.wala.shrikeBT.Constants;
import com.ibm.wala.shrikeBT.Disassembler;
import com.ibm.wala.shrikeBT.GetInstruction;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeBT.Instruction;
import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.shrikeBT.MethodEditor;
import com.ibm.wala.shrikeBT.Util;
import com.ibm.wala.shrikeBT.analysis.Verifier;
import com.ibm.wala.shrikeBT.shrikeCT.CTDecoder;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeCT.ClassReader;
import com.ibm.wala.shrikeCT.ClassWriter;

import edu.washington.cs.conf.instrument.AbstractInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentStats;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

/**
 * Patching the original program via instrumentation
 * */
public class PatchInstrumenter extends AbstractInstrumenter {

	static final String fieldName = "_patch_enabled";
	
	private final FixingPlan plan;
	
	static final Instruction getSysErr = Util.makeGet(System.class, "err");
	static final Instruction callPrintln = Util.makeInvoke(PrintStream.class, "println", new Class[] { String.class });
	
	static final Instruction getAlter = Util.makeGet(PredicateAlter.class, "alter");
	static final Instruction callReturnInt = Util.makeInvoke(PredicateAlter.class, "returnIntThres", new Class[] {  });
	static final Instruction callReturnRandom = Util.makeInvoke(PredicateAlter.class, "returnRandom", new Class[] { });
	
	public PatchInstrumenter(FixingPlan plan) {
		//Utils.checkNotNull(plan);
		super.disasm = true;
		this.plan = plan;
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

	        final MethodEditor me = new MethodEditor(d);
	        me.beginPass();

	        //the unique method signature
        	String methodSig = WALAUtils.getMethodSignature(d);
        	System.out.println(methodSig);
        	
        	//instrument every instruction
	        int length = me.getInstructions().length;
	        for(int i = 0; i < length; i++) {
	        	IInstruction inst = me.getInstructions()[i];
	        	final String instStr = inst.toString();
	        	
	        	if(inst instanceof ConditionalBranchInstruction) {
	        		final ConditionalBranchInstruction branch = (ConditionalBranchInstruction)inst;
//	        		branch
	        		if(branch.getTarget() == 20) {
	        		  System.err.println("conditional: " + instStr + ", dest: " + branch.getTarget());
	        		  me.insertBefore(i, new MethodEditor.Patch() {
	        			final int noTraceLabel1 = me.allocateLabel();
	        			final int noTraceLabel2 = me.allocateLabel();
		                @Override
		                public void emitTo(MethodEditor.Output w) {
		                	//w.emit(GetInstruction.make(Constants.TYPE_boolean, CTDecoder.convertClassToType(className), fieldName, true));
		                	
		                	w.emit(ConditionalBranchInstruction.make(branch.getType(), branch.getOperator(),
			                        noTraceLabel1));
		                	w.emitLabel(noTraceLabel1);
		                	
		                	//return two number
		                	
		                	
		                	
		                	w.emit(getAlter);
		                	w.emit(callReturnInt);
		                	
		                	w.emit(getAlter);
		                    w.emit(callReturnRandom);
		                    
//		                    w.emit(ConstantInstruction.make(22));
//		                    w.emit(ConstantInstruction.make(22));
		                    
		                    //if true, then no trace
//		                    w.emit(ConditionalBranchInstruction.make(Constants.TYPE_int, ConditionalBranchInstruction.Operator.NE,
//		                        noTraceLabel2));
//		                    w.emit(getSysErr);
//		                    w.emit(ConstantInstruction.makeString(instStr));
//		                    w.emit(callPrintln);
//		                    w.emitLabel(noTraceLabel2);
		                    
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
