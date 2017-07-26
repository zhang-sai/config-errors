package edu.washington.cs.conf.instrument;

import junit.framework.TestCase;

public class TestStackTrace extends TestCase {
	
	public static void main(String[] args) {
		ExecutionContext.pruneNoApp = false;
		new TestStackTrace().testSeeStackTrace();
	}
	
	public void testSeeStackTrace() {
		foo();
	}
	
	/**
	 * It is not possible to get a stack when the system exits/crashes.
	 * 
	 * just use the human maintained stack in ConfTracer.
	 * */
	public void testSeeStackTraceWhenCrashed() {
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
	        @Override
	        public void run() {
	                System.out.println("----------shutting down hook-------");
	                
	                for(StackTraceElement e : Thread.currentThread().getStackTrace()) {
	                	System.out.println(e);
	                }
	                
	        }
	    });
		crash1();
	}
	
	void foo() {
		bar();
	}
	
	void bar() {
		ExecutionContext c = ExecutionContext.createContext();
		System.out.println("creating c: " + c);
		System.out.println(c.getApplicationStackTrace());
	}
	
	void crash1() {
		crash2();
	}
	
	void crash2() {
		crash3();
	}
	
	void crash3() {
		//throw new Error();
		//System.exit(0);
	}
	
}