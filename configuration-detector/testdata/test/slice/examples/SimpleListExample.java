package test.slice.examples;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class SimpleListExample {

	public static String str1 = null;

	public static String str2 = "";

	public static void main(String[] args) {
		addToList("hello");
	}

	private static List<String> addToList(String input) {
		List<String> list = new ArrayList<String>();
//		String l = SimpleListExample.str1 + "  "; //true ? str1 : " ";
//		list.add(l);
		list.add(SimpleListExample.str1);
		list.add("   ");
		//if (str2 != null) {
			list.add(SimpleListExample.str2 + "    ");
		//}
		System.out.println("hello");
		String local = "+";
		list.add(local + " ");
		String local2 = local + " variable";
//		irrelevantCall(input);
//		usePrintOut();
		return list;
	}
	
	private static void usePrintOut() {
		PrintStream out = System.out;
		out.println(SimpleListExample.str1);
		out.println("  ");
		out.println(SimpleListExample.str2 + "   ");
		List<String> list = new ArrayList<String>();
		list.add("   ");
		if (SimpleListExample.str2 != null) {
			list.add(SimpleListExample.str2 + "    ");
		}
	}
	
	private static void irrelevantCall(String input) {
		System.out.println(input + "  ");
	}

}
