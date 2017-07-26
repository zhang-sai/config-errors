package test.slice.examples;

import java.util.ArrayList;
import java.util.List;

public class SharingStringExample {

	public static String str1 = "hello";
	
	public static String str2 = "world";
	
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		String r = str1 + "again";
		String x = str1;// + str2;
		if(x != null) {
			String q = r;
		}
		String p = str2 + " I come.";
		String w = p;
	}
	
}
