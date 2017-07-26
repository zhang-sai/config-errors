package edu.washington.cs.conf.mutation;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.util.Utils;

/**
 * Run a program with the mutated configuration
 * */
public abstract class ProgramRunner {

	
	public final List<MutatedConf> mutatedConfigs = new LinkedList<MutatedConf>();
	protected String outputFile = null;
	
	
	public void setMutatedConfigs(Collection<MutatedConf> configs) {
		this.mutatedConfigs.addAll(configs);
	}
	
	public void setOutputFile(String outputFile) {
		Utils.checkNotNull(outputFile);
		this.outputFile = outputFile;
	}
	
	//methods that should be inherited
	public void setUpEnv() {}
	
	public abstract Collection<ExecResult> execute();
	
	public void clearEnv() {}
	
	
	//run a test script, and or run a test
	//observe its output 
	//1. junit
	//2. example
	//   get log4j

	public static void main(String[] args) throws IOException {
		System.out.println(System.getProperty("user.dir"));
		String newUserDir = "";
		String command = "javac";
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec(command);
	}
	
}