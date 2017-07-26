package edu.washington.cs.conf.mutation;

import java.io.File;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

public abstract class ExecResultChecker {

	protected Throwable e;
	protected File logFile;
	protected String oracleCheckingMethod = null;
	protected String messageFetchingMethod = null;
	protected String message;
	
	//to write the message to this file
	private String tmpLogFilePath = "./tmp-output-folder/tmp_log_file.txt";
	
	//check whether the result is desired or not
	public ExecResultChecker(Throwable e, File logFile) {
		Utils.checkNotNull(logFile);
		this.e = e;
		this.logFile = logFile;
	}
	
	//e can be null
	public ExecResultChecker(Throwable e, String message) {
		this.e = e;
		this.message = message;
		//in this case, in order to have a uniform interface,
		//write the message to the temporarily logFile
		this.logFile = new File(this.tmpLogFilePath);
		Files.writeToFileNoExp(message, this.logFile.getAbsolutePath());
	}
	
	public void setOracleCheckingMethod(String method) {
		Utils.checkNotNull(method);
		this.oracleCheckingMethod = method;
	}
	
	public void setMessageFetchingMethod(String method) {
		Utils.checkNotNull(method);
		this.messageFetchingMethod = method;
	}
	
	public void setTmpLogFilePath(String logFilePath) {
		this.tmpLogFilePath = logFilePath;
	}
	
	public abstract boolean pass();
	
	public abstract String fetchMessage();
	
}
