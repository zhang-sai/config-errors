package edu.washington.cs.conf.analysis.evol;

public class BranchInstructionExecInfo extends InstructionExecInfo {

	public final boolean execOrEval;
	
	/**
	 * The instruction before or after the branch is evaluated.
	 * If after, means this branch is taking true
	 * True: eval,  false: exec
	 * */
	public BranchInstructionExecInfo(String context, int index, boolean execOrEval) {
		super(context, index);
		this.execOrEval = execOrEval;
	}
	
	@Override
	public boolean evaluateToTrue() {
		return execOrEval;
	}
	
	@Override
	public boolean isBranchInstruction() {
		return true;
	}
	
	@Override
	public String toString() {
		return super.toString() + ", eval: " + this.isBranchInstruction();
	}
}
