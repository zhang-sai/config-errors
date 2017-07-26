package edu.washington.cs.conf.analysis.evol;

import java.util.Random;

import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInstructionFactory;
import com.ibm.wala.ssa.SymbolTable;

import edu.washington.cs.conf.util.Utils;

/**
 * Cannot remember where will this class be used for
 * */

@Deprecated
public class EndOfMethodSSAInstruction extends SSAInstruction {
	public final int hashCode;
	public EndOfMethodSSAInstruction() {
		hashCode = new Random().nextInt();
	}
	@Override
	public SSAInstruction copyForSSA(SSAInstructionFactory insts,
			int[] defs, int[] uses) {
		throw new Error("Should never call it.");
	}
	@Override
	public String toString(SymbolTable symbolTable) {
		return "EndOfMethodSSAInstruction";
	}
	@Override
	public void visit(IVisitor v) {
		Utils.unimplemented();
	}
	@Override
	public int hashCode() {
		return this.hashCode;
	}
	@Override
	public boolean isFallThrough() {
		return true;
	}
	
}