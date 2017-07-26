package edu.washington.cs.conf.diagnosis;

import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;

/**
 * A class for generating well-formatted, human-readable explanation
 * */
public class ExplanationGenerator {
	
	public static String TOKEN = "of the time";

	public static String createWellFormattedExpanation
	    (String confName, String context, String predicate, int lineNum,
		 int goodRunNum, int goodEnter,
		 int badRunNum, int badEnter) {
		//avoid the dvide-by-zero bug
		float goodEnterRatio = goodRunNum != 0 ? ((float)goodEnter/goodRunNum)*100 : Float.NaN;
		float badEnterRatio = badRunNum != 0 ? ((float)badEnter/badRunNum)*100 : Float.NaN;
		
		//fetch the class name
		int lastDot = context.lastIndexOf(")");
		String method = lastDot == -1 ? context : context.substring(0, lastDot + 1);
		
		//create the string below
		StringBuilder sb = new StringBuilder();
		sb.append("Suspicious configuration option: " + confName);
		sb.append(Globals.lineSep);
		sb.append(Globals.lineSep);
		sb.append("It affects the behavior of predicate: ");
		sb.append(Globals.lineSep);
		sb.append("\"" + predicate + "\" ");
		sb.append(Globals.lineSep);
		sb.append("(line: " + lineNum + ", method: " + method + ")");
		sb.append(Globals.lineSep);
		sb.append(Globals.lineSep);
		sb.append("This predicate evaluates to true: ");
		sb.append(Globals.lineSep);
		sb.append("   $" + goodEnterRatio + "$$% " + TOKEN + " in normal runs (#" + goodRunNum + "## observations)");
		sb.append(Globals.lineSep);
		sb.append("   " + badEnterRatio + "% " + TOKEN + " in an undesirable run (" + badRunNum + " observations)");
		sb.append(Globals.lineSep);
		return sb.toString();
	}
	
	public static String replaceWithGoodRunNum(String report, int goodEnterNum, int goodEvalNum) {
		Utils.checkTrue(goodEvalNum != 0);
		Utils.checkTrue(goodEvalNum >= goodEnterNum);
		String NA = "N/A";
		
		String goodRatioStr = goodEvalNum != -1 ? "" + ((float)goodEnterNum/goodEvalNum)*100 : NA;
		String goodEnterStr = goodEnterNum != -1 ? goodEnterNum + "" : NA;
		if(goodEnterNum < 0) {
			goodRatioStr = NA;
			goodEnterStr = NA;
		}
		
//		
//		if(goodEvalNum != -1 && goodEnterNum != -1) {
			Utils.checkTrue(goodEvalNum != 0);
			String[] strs = report.split(Globals.lineSep);
			
			StringBuilder sb = new StringBuilder();
			
			for(String str : strs) {
				if(str.indexOf("$") != -1) {
					if(goodRatioStr.equals(NA)) {
						str = str.replaceAll("\\$", "");
					} else {
					    int startIndex = str.indexOf("$");
					    int endIndex = str.indexOf("$$");
					    Utils.checkTrue(endIndex > startIndex);
					    str = str.substring(0, startIndex) + goodRatioStr + str.substring(endIndex + "$$".length());
					}
				}
				if(str.indexOf("#") != -1) {
					if(goodEnterStr.equals(NA)) {
						str = str.replaceAll("#", "");
					} else {
						int startIndex = str.indexOf("#");
					    int endIndex = str.indexOf("##");
					    Utils.checkTrue(endIndex > startIndex);
					    str = str.substring(0, startIndex) + goodEnterStr + str.substring(endIndex + "##".length());
					}
				}
				
				sb.append(str);
				sb.append(Globals.lineSep);
			}
			
			return sb.toString();
//		}
//		return report;
	}
	
}