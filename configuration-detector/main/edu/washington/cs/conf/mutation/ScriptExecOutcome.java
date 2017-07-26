package edu.washington.cs.conf.mutation;

import edu.washington.cs.conf.util.Globals;

public class ScriptExecOutcome {

	private String outputMsg = null;
	private String errorMsg = null;
	private Throwable error = null;
	
	public ScriptExecOutcome(String outputMsg, String errorMsg, Throwable error) {
		this.outputMsg = outputMsg;
		this.errorMsg = errorMsg;
		this.error = error;
	}
	
	public String getOutputMsg() {
		return this.outputMsg;
	}
	
	public String getErrorMsg() {
		return this.errorMsg;
	}
	
	public Throwable getError() {
		return this.error;
	}
	
	@Override
	public String toString() {
		return "Output message: " + Globals.lineSep + this.outputMsg
		    + Globals.lineSep + ", error message: " + Globals.lineSep + errorMsg
		    + Globals.lineSep + ", error: " + error;
	}
}
