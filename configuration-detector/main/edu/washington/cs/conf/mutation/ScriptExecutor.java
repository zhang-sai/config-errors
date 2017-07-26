package edu.washington.cs.conf.mutation;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import edu.washington.cs.conf.util.Utils;

public class ScriptExecutor {
	
	public static String name = "Default-";
	
	public static boolean useThread = true;
	public static int timelimit = 5000; //milli second
	
	public static ScriptExecOutcome executeScript(List<String> args, String dir) {
		if(useThread) {
			return executeScriptWithThread(args, dir);
		} else {
			return executeScriptWithoutThread(args, dir);
		}
	}
	
	public static ScriptExecOutcome executeScriptWithThread(List<String> args, String dir) {
		ScriptRunnerThread thread = new ScriptRunnerThread(args, dir);
		try {
			thread.start();
			thread.join(timelimit);
			if(!thread.finish()) {
				System.out.println("Exceed the max waiting time: " + timelimit);
//				thread.stop(); //the only way to stop a thread
				//re-turn the time exceed exception
				ScriptExecOutcome hangOutcome = new ScriptExecOutcome(null, null, new TimeoutExceeded());
								
				return hangOutcome;
			}
		} catch (java.lang.InterruptedException e) {
		      throw new IllegalStateException("A thread shouldn't be interrupted by anyone! ");
		}
		return thread.getExecutionResult();
	}

	static ScriptExecOutcome executeScriptWithoutThread(List<String> args, String dir) {
		String outputMessage = null;
		String errorMessage = null;
		Throwable error = null;
		try {
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.directory(new File(dir));
			pb.redirectErrorStream(true);
			Process p = pb.start();
			final BufferedReader stdOutput = new BufferedReader(
					new InputStreamReader(p.getInputStream()), 8 * 1024);
			final BufferedReader stdError = new BufferedReader(
					new InputStreamReader(p.getErrorStream()));

			// get the input and output
			BufferReaderThread stdOutputThread = new BufferReaderThread(
					stdOutput, name + "Output-Stream");
			BufferReaderThread stdErrorThread = new BufferReaderThread(
					stdError, name + "Error-Stream");

			// get the input and output
			stdOutputThread.start();
			stdErrorThread.start();

			// FIXME This suppose the threads will finish
			// wait until these two thread finish
			stdOutputThread.join();
			stdErrorThread.join();
			
			//get the message
			outputMessage = stdOutputThread.getMessage();
			errorMessage = stdErrorThread.getMessage();
		} catch (Throwable e) {
			error = e;
		}
		
		return new ScriptExecOutcome(outputMessage, errorMessage, error);
	}
}


/**
 * For convenience, just put the class here
 * */
class ScriptRunnerThread extends Thread {
	
	private boolean finish = false;
	private List<String> args = null;
	private String dir = null;
	
	private ScriptExecOutcome outcome = null;
	
	public ScriptRunnerThread(List<String> args, String dir) {
		this.args = args;
		this.dir = dir;
	}
	
	public boolean finish() {
		return this.finish;
	}
	
	@Override
	public void run() {
		Utils.checkTrue(!finish, "cannot run a thread twice");
		finish = false;
		ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithoutThread(args, dir);
		finish = true;
		//keep the outcome
		this.outcome = outcome;
	}
	
	public ScriptExecOutcome getExecutionResult() {
		return this.outcome;
	}
}
