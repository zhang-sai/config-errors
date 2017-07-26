package edu.washington.cs.conf.mutation;

import edu.washington.cs.conf.util.Utils;

public class ExecCommand {
	/**
	 * This should also be usable for executing scripts.
	 * */
	public final String mainMethod;
	public final String[] args;
	
	//need some extension
	
	public ExecCommand(String mainMethod, String[] args) {
		Utils.checkNotNull(mainMethod);
		Utils.checkNoNull(args);
		this.mainMethod = mainMethod;
		this.args = args;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(mainMethod);
		sb.append(":");
		for(String arg : args) {
			sb.append(" " + arg + " ");
		}
		
		return sb.toString();
	}
}