package edu.washington.cs.conf.instrument;

import java.io.PrintStream;
import java.io.Writer;

import com.ibm.wala.shrikeBT.ConstantInstruction;
import com.ibm.wala.shrikeBT.Constants;
import com.ibm.wala.shrikeBT.Disassembler;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeBT.Instruction;
import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.shrikeBT.MethodEditor;
import com.ibm.wala.shrikeBT.ReturnInstruction;
import com.ibm.wala.shrikeBT.ThrowInstruction;
import com.ibm.wala.shrikeBT.Util;
import com.ibm.wala.shrikeBT.MethodEditor.Output;
import com.ibm.wala.shrikeBT.analysis.Verifier;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.OfflineInstrumenter;
import com.ibm.wala.shrikeCT.ClassReader;
import com.ibm.wala.shrikeCT.ClassWriter;

public class ExampleMethodEntryExitInstrumenter extends AbstractInstrumenter {
	private final static boolean disasm = true;

	  private final static boolean verify = true;

	  final private static boolean doEntry = true;

	  private static boolean doExit = true;

	  private static boolean doException = true;

	  public static void main(String[] args) throws Exception {
		  ExampleMethodEntryExitInstrumenter instrumenter = new ExampleMethodEntryExitInstrumenter();
		  instrumenter.instrument("./tests/edu/washington/cs/conf/instrument/test.baseline.entryexit.jar",
				  "./output/test.baseline.entryexit-instrumented.jar");
	  }

	  static final String fieldName = "_Entry_Exit_Enabled_Trace";

	  // Keep these commonly used instructions around
	  static final Instruction getSysErr = Util.makeGet(System.class, "err");

	  static final Instruction callPrintln = Util.makeInvoke(PrintStream.class, "println", new Class[] { String.class });

	  @Override
	  protected void doClass(final ClassInstrumenter ci, Writer w) throws Exception {
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

	        MethodEditor me = new MethodEditor(d);
	        me.beginPass();
	        
	        if (doEntry) {
	          final String msg0 = "Entering call to " + Util.makeClass("L" + ci.getReader().getName() + ";") + "."
	              + ci.getReader().getMethodName(m);
	          me.insertAtStart(new MethodEditor.Patch() {
	            @Override
	            public void emitTo(MethodEditor.Output w) {
	              w.emit(getSysErr);
	              w.emit(ConstantInstruction.makeString(msg0));
	              w.emit(callPrintln);
	            }
	          });
	        }
	        if (doExit) {
	          final String msg0 = "Exiting call to " + Util.makeClass("L" + ci.getReader().getName() + ";") + "."
	              + ci.getReader().getMethodName(m);
	          IInstruction[] instr = me.getInstructions();
	          for (int i = 0; i < instr.length; i++) {
	            if (instr[i] instanceof ReturnInstruction) {
	              me.insertBefore(i, new MethodEditor.Patch() {
	                @Override
	                public void emitTo(MethodEditor.Output w) {
	                  w.emit(getSysErr);
	                  w.emit(ConstantInstruction.makeString(msg0));
	                  w.emit(callPrintln);
	                }
	              });
	            }
	          }
	        }
	        if (doException) {
	          final String msg0 = "Exception exiting call to " + Util.makeClass("L" + ci.getReader().getName() + ";") + "."
	              + ci.getReader().getMethodName(m);
	          me.addMethodExceptionHandler(null, new MethodEditor.Patch() {
	            @Override
	            public void emitTo(Output w) {
	              w.emit(getSysErr);
	              w.emit(ConstantInstruction.makeString(msg0));
	              w.emit(callPrintln);
	              w.emit(ThrowInstruction.make(false));
	            }
	          });
	        }
	        // this updates the data d
	        me.applyPatches();

	        if (disasm) {
	          w.write("Final ShrikeBT code:\n");
	          (new Disassembler(d)).disassembleTo(w);
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