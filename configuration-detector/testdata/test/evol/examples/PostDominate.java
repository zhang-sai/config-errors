package test.evol.examples;

import java.util.Random;

public class PostDominate {

	Random r = new Random();
	
	int answer = 0;
	
	public void foo(int k) {
		int x = k;
		int y = 2;
		if(x > 5) {
			x = x+1;
			y = y +1;
		} else {
			y = y + k;
			if(y == 10) {
				System.out.println("Wow");
			}
			bar(y);
			System.out.println("OK");
		}
		if( y > 0) {
			System.out.println("Do sth when y > 0.");
		}
		int z = x + y;
		this.answer = z + 1;
		System.out.println("answer is: " + answer);
	}
	
	public void bar(int y) {
		if(y == 1) {
			return;
		} else {
			int k = y - 1;
			bar(k);
		}
		loop(y);
	}
	
	public void loop(int y) {
		int z = y;
		while(z-- > 0) {
		  System.out.println("k");
		}
		System.err.println("OK");
	}
	
	public static void main(String[] args) {
		PostDominate pd = new PostDominate();
		pd.foo(Integer.parseInt(args[0]));
	}
}