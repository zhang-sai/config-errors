package test.evol.slicing;

public class SliceExamples {

	public int conf1 = 2;
	
	public void foo(int x) {
		int z = 10;
		int y = 10;
		if(conf1 > 0) {
			long k = System.currentTimeMillis();
			x = k > 0 ? 10 : 2;
			if(x > 2) {
				y = 11;
				System.out.println("nested-1");
			} else {
				System.err.println("nested-2");
			}
		} else {
			y = 12;
			System.out.println(y);
		}
		
		if(x > 5) {
			System.out.println("Hello");
		}
		
		if(y == 12) {
			System.err.println("yes");
		}
		
		bar(z);
	}
	
	public void bar(int z) {
		if(z > 0) {
			System.out.println(z);
		} else {
			bar(z-1);
		}
	}
	
	public void doExpensiveTasks() {
		for(int i = 0; i < 100; i++) {
			int k = i + 1;
			long j = System.currentTimeMillis();
			System.out.println("Expensive! " + i + k + j);
		}
	}
	
	public static void main(String[] args) {
		new SliceExamples().foo(20);
	}
}