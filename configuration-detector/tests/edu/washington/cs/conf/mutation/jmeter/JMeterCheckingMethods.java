package edu.washington.cs.conf.mutation.jmeter;

import java.io.IOException;

public class JMeterCheckingMethods {

	public static String jmeterCheckingMethods = "edu.washington.cs.conf.mutation.jmeter.JMeterCheckingMethods.isPass";
	
	public static String jmeterMsgFetchingMethods = "edu.washington.cs.conf.mutation.jmeter.JMeterCheckingMethods.getErrorMessage";
	
	public static boolean isPass(String filePath) throws IOException {
		System.out.println("checking pass...");
		return true;
	}
	
	public static String getErrorMessage(String filePath) throws IOException {
		System.out.println("fetching error msg ...");
		return null;
	}
}
