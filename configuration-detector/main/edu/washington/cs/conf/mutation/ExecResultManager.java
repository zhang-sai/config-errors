package edu.washington.cs.conf.mutation;

import java.io.File;

import edu.washington.cs.conf.util.Utils;

/**
 * Observe the execution results of a program, and
 * check it against a testing oracle.
 * */
public class ExecResultManager {
	
	//TODO may need more extensibility
	private static ExecResultChecker oracleChecker = null;
	
	private static String oracleCheckingMethod = null;
	private static String messageFetchingMethod = null;
	
//	//this is meaningless
//	public static void setExecResultChecker(ExecResultChecker checker) {
//		Utils.checkNotNull(checker);
//		oracleChecker = checker;
//	}
	
	public static void setOracleCheckingMethod(String method) {
		Utils.checkNotNull(method);
		oracleCheckingMethod = method;
	}
	
	public static void setMessageFetchingMethod(String method) {
		Utils.checkNotNull(method);
		messageFetchingMethod = method;
	}
	
	public static ExecResult createReflectionExecResult(
			ExecCommand cmd, MutatedConf conf,
			Throwable e, String logFilePath) {
		
		//how to analyze the log file and create ExecResult object
		oracleChecker = new DefaultExecResultChecker(e, new File(logFilePath));
		
		//set two reflection calls
		oracleChecker.setOracleCheckingMethod(oracleCheckingMethod);
		oracleChecker.setMessageFetchingMethod(messageFetchingMethod);
		
		boolean pass = oracleChecker.pass();
		String message = oracleChecker.fetchMessage();
		
		//figure out the status here
		Status status = createStatus(conf, pass);
		
		//create the exec result object
		ExecResult result = new ExecResult(message, conf.getMutatedConfOption(),
				conf.getMutatedConfValue(), status);
		result.setCommand(cmd);
//		result.setUsedConfigs(conf.getMutatedConfOptions());
		
		return result;
	}
	
	public static ExecResult createScriptExecResult(
			ScriptCommand cmd, MutatedConf conf, String inputMessage) {
		
		oracleChecker = new DefaultExecResultChecker(inputMessage);
		
		//set two reflection calls
		oracleChecker.setOracleCheckingMethod(oracleCheckingMethod);
		oracleChecker.setMessageFetchingMethod(messageFetchingMethod);
		
		boolean pass = oracleChecker.pass();
		String message = oracleChecker.fetchMessage();
		
		//figure out the status here
		Status status = createStatus(conf, pass);
		
		//create the exec result object
		ExecResult result = new ExecResult(message, conf.getMutatedConfOption(),
				conf.getMutatedConfValue(), status);
		
		return result;
	}
	
	private static Status createStatus(MutatedConf conf, boolean pass) {
		if(!conf.shouldFail()) {
			return pass ? Status.Pass : Status.Fail; 
		} else {
			return pass ? Status.Fail : Status.Pass;
		}
	}
}
