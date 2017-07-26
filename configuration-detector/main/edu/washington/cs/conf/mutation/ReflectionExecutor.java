package edu.washington.cs.conf.mutation;

import java.lang.reflect.Method;
import java.util.List;

import edu.washington.cs.conf.util.Utils;

public class ReflectionExecutor {

	public static boolean useThread = true;
	
	public static int timelimit = 5000; //milli seconds
	
	public static Throwable executeReflectionCode(Method mainMethod, List<String> argList) {
		if(useThread) {
			return executeReflectionCodeWithThread(mainMethod, argList);
		} else {
			return executeReflectionCodeWithoutThread(mainMethod, argList);
		}
	}
	
	static Throwable executeReflectionCodeWithThread(Method meth, List<String> argList) {
		ReflectionRunnerThread thread = new ReflectionRunnerThread(meth, argList);
		try {
			thread.start();
			thread.join(timelimit);
			if(!thread.finish()) {
				System.err.println("Exceed the max waiting time: " + timelimit);
//				thread.interrupt();
				Throwable e = new TimeoutExceeded();
				return e;
			}
		} catch (ThreadDeath e) { 
			throw e;
		} catch (java.lang.InterruptedException e) {
		      throw new IllegalStateException("A thread shouldn't be interrupted by anyone! ");
		}
		return thread.getError();
	}
	
	static Throwable executeReflectionCodeWithoutThread(Method meth, List<String> argList) {
		Throwable error = null;
		try {
			String[] args = argList.toArray(new String[0]);
			Object argObj = args;
//			System.out.println(argList);
	        meth.invoke(null, argObj);
		} catch (Throwable e) {
			error = e.getCause(); //get the cause of the error
			Utils.checkNotNull(error);
			System.err.println("Error: " + error.getClass() + ", " + error.getMessage());
//			e.printStackTrace();
		}
		return error;
	}
}

class ReflectionRunnerThread extends Thread {
	
	private boolean finish = false;
	private Method mainMethod = null;
	private List<String> argList = null;
	
	private Throwable error = null;
	
	public ReflectionRunnerThread(Method mainMethod, List<String> argList) {
		Utils.checkNotNull(mainMethod);
		Utils.checkNotNull(argList);
		this.mainMethod = mainMethod;
		this.argList = argList;
	}
	
	public boolean finish() {
		return finish;
	}
	
	public Throwable getError() {
		return this.error;
	}
	
	@Override
	public void run() {
		Utils.checkTrue(!finish);
		finish = false;
		this.error = ReflectionExecutor.executeReflectionCodeWithoutThread(mainMethod, argList);
		finish = true;
	}
}
