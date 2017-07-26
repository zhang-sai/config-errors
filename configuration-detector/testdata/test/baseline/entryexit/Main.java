package test.baseline.entryexit;

public class Main {

	public static void main(String[] args) {
		System.out.println("in main");
		try  {
		   handle();
		} catch (Throwable e) {
			
		}
		foo();
	}
	
	static void foo() {
		System.out.println("in foo");
		bar(4);
		err();
	}
	
	static void handle() {
		System.out.println("in handle");
		try {
			throw new Error();
		} catch (Throwable e) {
			
		}
		throw new Error();
	}
	
	static void bar(int i) {
		System.out.println("in bar with i: " + i);
		if(i > 0) {
			bar(i-1);
		}
	}
	
	static void err() {
		System.out.println("in err");
		throw new Error();
	}
	
}