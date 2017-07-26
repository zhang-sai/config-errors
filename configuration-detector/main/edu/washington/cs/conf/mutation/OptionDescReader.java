package edu.washington.cs.conf.mutation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.util.Files;

public class OptionDescReader {

	public static Map<String, String> readOptions(String fileName) {
		Map<String, String> optionDesc = new LinkedHashMap<String, String>();
		List<String> list = Files.readWholeNoExp(fileName);
		for(String line : list) {
			if(line.startsWith("#")) {
			    System.out.println(line);
				continue;
			}
			int firstSharpIndex = line.indexOf("#");
			String optionName = line.substring(0, firstSharpIndex);
			String optionText = line.substring(firstSharpIndex + 1);
			if(optionDesc.containsKey(optionName)) {
				System.out.println("Dup: " + optionName);
			}
//			System.out.println("=> " + optionName);
//			System.out.println("   " + optionText);
			optionDesc.put(optionName, optionText);
		}
		
		return optionDesc;
	}
	
	public static void main(String[] args) {
		String jmeterManual = "./option_manual/jmeter.txt";
		Map<String, String> map = readOptions(jmeterManual);
		System.out.println(map.size());
	}
	
}