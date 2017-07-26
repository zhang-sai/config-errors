package edu.washington.cs.conf.analysis;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

import com.ibm.wala.cfg.Util;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.StatementWithInstructionIndex;
import com.ibm.wala.shrikeCT.InvalidClassFileException;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

/**
 * Keep the results of configuration propagation analysis.
 * Check this thread:
 * http://sourceforge.net/mailarchive/forum.php?thread_name
 * =CANnq52Hh_4_c2wpK63%2BUAqci822niKb38NbF1ni8gTNz2uk31g%40mail
 * .gmail.com&forum_name=wala-wala
 * 
 * and the wala_ir_shrinke_map.txt for more details.
 * */
public class IRStatement {
	
	public static String[] ignoredPackages = new String[]{
			"java.", "javax.", "plume.", "org.apache.bcel.",
			"com.ibm.wala.", "edu.washington.cs.conf."
			,"gnu.trove.", "javassist.", "Lhj.runtime",
			"org.scannotation", "hj.runtime", "org.junit",
			"junit.", "gov.nasa.",
	};
	
	public final StatementWithInstructionIndex s;
	public final SSAInstruction ssa;
	public final String methodSig;
	public final int instructionIndex;
	public final int bcIndex;
	public final int lineNumber;
	
	public IRStatement(StatementWithInstructionIndex stmt) {
		this.s = stmt;
		this.ssa = stmt.getInstruction();
		this.methodSig = this.s.getNode().getMethod().getSignature();
		this.instructionIndex = stmt.getInstructionIndex();
		this.bcIndex = this.getBytecodeIndex(stmt, this.instructionIndex);
		this.lineNumber = WALAUtils.getStatementLineNumber(this.s);
		//check the validity of the IRStatement
		Utils.checkNotNull(this.s);
		Utils.checkNotNull(this.ssa);
		Utils.checkTrue(this.instructionIndex > -1);
		Utils.checkTrue(this.bcIndex > -1);
		//do not check line number, it is possible to get -1
	}
	
	public Statement getStatement() {
		return this.s;
	}
	
	public int getInstructionIndex() {
		return this.instructionIndex;
	}
	
	public String getUniqueSignature() {
		return this.getMethodSig() + "#"
		    + this.ssa.toString() + "#" + this.getInstructionIndex();
	}
	
	public String getMethodSig() {
		 return this.methodSig;
	}
	
	public String getDeclaringFullClassName() {
		return ConfUtils.extractFullClassName(methodSig);
	}
	
	public int getLineNumber() {
		return this.lineNumber;
	}
	
	public int getBcIndex() {
		return this.bcIndex;
	}
	
	public boolean hasLineNumber() {
		return this.lineNumber != -1;
	}
	
	public boolean isBranch() {
		return this.isSSABranch(this.ssa);
	}
	
	private boolean isSSABranch(SSAInstruction ssa) {
		return ssa instanceof SSAConditionalBranchInstruction;
	}
	
	public boolean isBranchInSource() {
		if(this.isBranch()) {
			return true;
		}
		//get the source line
		int lineNum = this.lineNumber;
		CGNode node = this.s.getNode();
		SSAInstruction[] ssas = node.getIR().getInstructions();
		boolean hasSrcInPred = false;
		for(int index = 0; index < ssas.length; index++) {
			SSAInstruction inst = ssas[index];
			if(inst == null) {
				continue;
			}
			if(!this.isSSABranch(inst)) {
				continue;
			}
			int src_line_number = node.getMethod().getLineNumber(index);
			if(src_line_number != -1 && src_line_number == lineNum) {
				hasSrcInPred = true;
				break;
			}
		}
		return hasSrcInPred;
	}
	
	public boolean shouldIgnore() {
		String fullMethodName = WALAUtils.getFullMethodName(this.s.getNode().getMethod());
		for(String packagePrefix : ignoredPackages) {
			if(fullMethodName.startsWith(packagePrefix)) {
				return true;
			}
		}
		return false;
	}
	
	private int getBytecodeIndex(Statement stmt, int index) {
		try {
			return ((ShrikeBTMethod)stmt.getNode().getMethod()).getBytecodeIndex(index);
		} catch (Throwable e) {
			System.err.println("Error in processing: " + stmt);
			throw new Error(e);
		}
	}
	
//	public void
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof IRStatement) {
			IRStatement irs = (IRStatement)o;
			return this.s.equals(irs.s)
			    && this.ssa.equals(irs.ssa)
			    && this.instructionIndex == irs.instructionIndex
			    && this.bcIndex == irs.bcIndex
			    && this.lineNumber == irs.lineNumber;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.s.hashCode() + 13*this.ssa.hashCode()
		    + 17*this.instructionIndex + 29*this.bcIndex
		    + 31*this.lineNumber;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(s.toString());
		sb.append(Globals.lineSep);
		sb.append("    instructionIndex: ");
		sb.append(this.instructionIndex);
		sb.append("    bcIndex: ");
		sb.append(this.bcIndex);
		sb.append("    lineNumber: ");
		sb.append(this.lineNumber);
		sb.append("    isBranch: ");
		sb.append(this.isBranch());
		return sb.toString();
	}
	
    public static void dumpIRStatements(Collection<IRStatement> slice, PrintWriter w) {
		w.println("SLICE:\n");
		int i = 1;
		for (IRStatement irs : slice) {
			Statement  s = irs.getStatement();
			int line_num = WALAUtils.getStatementLineNumber(s);
			String line = (i++) + "   " + s
			    + Globals.lineSep
			    + "\t" + WALAUtils.getFullMethodName(s.getNode().getMethod()) + ",  line num: " + line_num;
			w.println(line);
			w.flush();
		}
	}
}