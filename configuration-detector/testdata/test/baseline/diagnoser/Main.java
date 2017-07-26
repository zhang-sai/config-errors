package test.baseline.diagnoser;

public class Main {
	
	public static int option1 = -1;
	public static int option2 = -1;
	public static int option3 = -1;
	
	public static void main(String[] args) {
		if(args.length >= 3) {
			option1 = Integer.parseInt(args[0]);
			option2 = Integer.parseInt(args[1]);
			option3 = Integer.parseInt(args[2]);
		} else if (args.length >= 2) {
			option1 = Integer.parseInt(args[0]);
			option2 = Integer.parseInt(args[1]);
		} else if (args.length >= 1) {
			option1 = Integer.parseInt(args[0]);
		}
		new Main().nonStaticMain();
	}
	
	public void nonStaticMain() {
		if(option1 > 0) {
			callPrint1();
		}
		if(option2 > 0) {
			callPrint2();
		}
		if(option3 > 0) {
			callPrint3();
		}
		if(option1 > 0 && option3 >0) {
			String x = "option1, option 3 both > 0";
			System.out.println(x);
		}
		if(option1 > 0 && option2 > 0) {
			String z = "still ok, option1, option 2 > 0";
			System.out.println(z);
		}
		if(option1 > 0 && option2 > 0 && option3 == 0) {
			String y = "option1, option2 > 0, option3 = 0, bad things happend";
			System.out.println(y);
		}
	}
	
	private void callPrint1() {
		System.out.println("print-callprint-1");
	}
	
	private void callPrint2() {
		System.out.println("print-callprint-2");
	}
	
	private void callPrint3() {
		System.out.println("print-callprint-2");
	}
}
