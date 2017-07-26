package edu.washington.cs.conf.mutation.weka;

import java.io.IOException;
import java.util.List;

import edu.washington.cs.conf.util.Files;

public class WekaCheckingMethods {
	
	public static String wekaCheckingMethod = "edu.washington.cs.conf.mutation.weka.WekaCheckingMethods.isPass";
	
	public static String wekaMsgFetchingMethod = "edu.washington.cs.conf.mutation.weka.WekaCheckingMethods.getErrorMessage";

	public static boolean isPass(String filePath) throws IOException {
		List<String> lines = Files.readWhole(filePath);
		for(String line : lines) {
			if(line.trim().equals("")) {
				continue;
			}
			if(line.startsWith("Weka exception: ")) {
				return false;
			} else {
				break;
			}
		}
		return true;
	}
	
	public static String getErrorMessage(String filePath) throws IOException {
		List<String> lines = Files.readWhole(filePath);
		for(String line : lines) {
			if(line.trim().equals("")) {
				continue;
			}
			if(line.startsWith("Weka exception: ")) {
				return line;
			} else {
				break;
			}
		}
		return null;
	}
	
}