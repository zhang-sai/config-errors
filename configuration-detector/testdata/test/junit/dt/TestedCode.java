package test.junit.dt;

public class TestedCode {

	public static Object obj = new Object();
	
	public boolean enable = true;
	
	public static String SEP = "a";
	
	public static TestedCode instance = null;
	
	public static TestedCode getInstance() {
		if(instance == null) {
			instance = new TestedCode();
		}
		return  instance;
	}
	
	public void doSomething() {
		SEP.hashCode();
		boolean x = !enable;
	}
	
	public void setSomething() {
		SEP = "b";
	}
}