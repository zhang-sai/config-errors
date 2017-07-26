package test.junit.dt;

import junit.framework.TestCase;

public class DTTest extends TestCase {

	private Object data = new Object();
	
	public void test1() {
		data = new Object();
	}
	
	public void test2() {
		data.getClass();
	}
	
	public void test3() {
		TestedCode instance = TestedCode.getInstance();
		instance.setSomething();
	}
	
	public void test4() {
		TestedCode instance = TestedCode.getInstance();
		instance.doSomething();
	}
	
}
