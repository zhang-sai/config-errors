package test.slice.depfield;

public class SeeCoverage {

	public static void main(String[] args) {
		if(args.length != 0) {
			if(args.length == 1) {
				System.out.println("Only 1 args");
			} else {
			    int n1 = Integer.parseInt(args[0]);
			    int n2 = Integer.parseInt(args[1]);
			    if(n1 > 0 && n2 > 0) {
			    	int x = 1;
			    	int y = x + n1 + n2;
			    	System.out.println(y);
			    } else if (n1 > 0) {
			    	String v = "str";
			    	System.err.println(v);
			    } else {
			    	return;
			    }
			}
		}
	}
	
}