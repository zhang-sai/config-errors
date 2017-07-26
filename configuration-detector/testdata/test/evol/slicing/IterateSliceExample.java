package test.evol.slicing;

import java.util.Random;

public class IterateSliceExample {

	boolean conf = false;
	
	boolean dump = false;
	
	public void foo() {
		long k = System.currentTimeMillis();
		if(conf) {
			System.err.println("dumping ... " + k);
			dump = true;
		}
		if(dump) {
			System.out.println("start dumping now...");
			bar();
		}
		System.out.println("ending ...");
	}
	
	public void bar() {
		Random r = new Random();
		int v = r.nextInt();
		if(v > 10) {
			System.out.println("OK");
		}
	}
	
	public static void main(String[] args) {
		new IterateSliceExample().foo();
	}
	
}