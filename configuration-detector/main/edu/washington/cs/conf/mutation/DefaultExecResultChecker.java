package edu.washington.cs.conf.mutation;

import java.io.File;
import java.lang.reflect.Method;

import edu.washington.cs.conf.util.Utils;
//import junit.framework.TestResult;

public class DefaultExecResultChecker extends ExecResultChecker {

	public DefaultExecResultChecker(Throwable e, File logFile) {
		super(e, logFile);
	}
	
	public DefaultExecResultChecker(String message) {
		super(null, message);
	}

	@Override
	public boolean pass() {
		if(super.e != null) {
			return false;
		}
		if(super.oracleCheckingMethod == null) {
			return true;
		}
		return this.executeOralceChecking(oracleCheckingMethod);
	}
	
	//like a script to check the result
	private boolean executeOralceChecking(String methodSig) {
		
		//execute it reflectively
		int lastIndex = methodSig.lastIndexOf(".");
		String className = methodSig.substring(0, lastIndex);
		String methodName = methodSig.substring(lastIndex + 1);
		
		boolean result = false;
		try {
			Class<?> clz = Class.forName(className);
			Method m = clz.getDeclaredMethod(methodName, String.class);
			Utils.checkNotNull(m);
			//may need to check the method signature
			Object object = m.invoke(null, this.logFile.getAbsolutePath());
			result = (Boolean)object;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return result;
	}

	@Override
	public String fetchMessage() {
		if(super.e != null) {
			return super.e.getMessage();
		}
		if(pass()) {
			return null;
		}
		
		String methodSig = this.messageFetchingMethod;
		if(methodSig == null) {
			return null;
		}
		
		int lastIndex = methodSig.lastIndexOf(".");
		String className = methodSig.substring(0, lastIndex);
		String methodName = methodSig.substring(lastIndex + 1);
		
		String message = null;
		try {
			Class<?> clz = Class.forName(className);
			Method m = clz.getDeclaredMethod(methodName, String.class);
			Utils.checkNotNull(m);
			Utils.checkTrue(m.getReturnType().equals(String.class));
			//may need to check the method signature
			Object object = m.invoke(null, this.logFile.getAbsolutePath());
			message = (String)object; //it must have a return type of String
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return message;
	}

}
