package edu.washington.cs.conf.instrument;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.util.Globals;

public class ExecutionContext implements Serializable {
	
	private static final long serialVersionUID = -1175379697645330520L;

	public static boolean pruneNoApp = true;
	
	public final Throwable t;
	
	public ExecutionContext() {
		t = new Throwable();
	}
	
	public static ExecutionContext createContext() {
		return new ExecutionContext();
	}
	
	public static String getCurrentContextAsString(int length) {
		ExecutionContext c = createContext();
		return c.getApplicationCallingContext(length);
	}
	
	public String getApplicationStackTrace() {
		StringBuilder sb = new StringBuilder();
		
		for(String str : this.getApplicationStackTraceAsArray()) {
			sb.append(str);
			sb.append(Globals.lineSep);
		}
		
		return sb.toString();
	}
	
	public String[] getApplicationStackTraceAsArray() {
		List<String> list = new LinkedList<String>();
		for(StackTraceElement elem : t.getStackTrace()) {
			String eleStr = elem.toString();
			if(pruneNoApp && eleStr.startsWith("edu.washington.cs.conf")) {
				continue;
			}
			list.add(eleStr);
		}
		return list.toArray(new String[0]);
	}
	
	public String getApplicationCallingContext(int k) {
		String[] calls = this.getApplicationStackTraceAsArray();
		int length = calls.length < k ? calls.length : k;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < length; i++) {
			sb.append(calls[i]);
		}
		return sb.toString();
	}
}
