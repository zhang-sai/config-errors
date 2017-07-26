package test.slice.depfield;

public class FieldDeps {
	
	private static int field_value = 1;  //a configuration option
	
	public int f_value = 2;
	
	public FieldDeps() {
		this(null);
	}
	
	public FieldDeps(String x) {
		
	}
	
	public int compute_result1(int v) {
//		field_value = 10;
		int result1 = 1;
		if(v > 0) {
			result1 = result1 + 1;
			return v + 1;
		}
		return result1;
	}
	
	public int compute_result2() {
//		int value = getValue(null);   //a configuration option
		int result2 = 0;
//		if(value > 0) {
//			result2 = result2 + 1;
//		}
//		if(f_value > 10) {
//			result2 ++;
//		}
		if(getX(field_value) & getY(field_value)) {
			result2++;
		}
		return result2;
	}
	
	public int compute_arg() {
		return compute_result1(field_value);
	}
	
	public void compute_final_r() {
		if(compute_arg() > 0) {
			System.out.println();
		}
	}
	
	public void dummyFor() {
		for(int i = 0; i < 10; i++) {
			System.out.println();
		}
	}
	
	public int getValue(String key) {
		return 1;
	}
	
	public boolean getX(int a) {
		return a != 1;
	}
	
	public boolean getY(int a) {
		return a != 2;
	}
	
	public static void main(String[] args) {
		System.out.println("=== main start ===");
		FieldDeps f = new FieldDeps();
		f.compute_final_r();
		f.compute_result1(field_value);
		f.compute_result2();
		System.out.println("=== main end ===");
	}
	
}