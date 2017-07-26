package test.fixing.predicate;

import java.util.Random;

public class SamplePredicate {

	static int maxsize = 5;
	static Random r = new Random();
	
	public static void main(String[] args) {
		new SamplePredicate().run(10);
	}
	
	public void run(int k) {
		int c = 0;
		for(int i = 0; i < k; i++) {
			int length = r.nextInt(10);
			if(length < maxsize) { //we want to switch this, always evaluate to true
				System.out.println("small length");
				c++;
			} else {
				System.out.println("big length");
			}
		}
		System.out.println("Exec: " + k + ", small length: " + c
				+ ", big length: " + (k-c));
	}
}