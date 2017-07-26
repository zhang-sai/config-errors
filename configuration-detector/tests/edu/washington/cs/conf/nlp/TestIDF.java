package edu.washington.cs.conf.nlp;

import edu.washington.cs.conf.mutation.UserManual;
import edu.washington.cs.conf.mutation.weka.TestWekaConfigExamples;
import edu.washington.cs.conf.util.Utils;
import junit.framework.TestCase;

public class TestIDF extends TestCase {

	
	public void testComputeIDF() {
		TFIDFWeightCalculator cal = this.createZeroRUserManual();
//		Utils.sortByValue(map, increase)
//		for(String w : cal.getAllWords()) {
//			System.out.println(w + " : " + cal.getIDFValue(w));
//		}
		cal.printIDFValueDescreasingly();
	}
	
	public void testErrorMessageSimilarity() {
		TFIDFWeightCalculator cal = this.createZeroRUserManual();
		BOWAnalyzer analyzer = new BOWAnalyzer(cal);
		String s1 = "Percentage split ('-percentage-split') is missing.";
		for(String userManualText : cal.getOriginalText()) {
			String s2 = userManualText;
			float similarity = analyzer.computeSimilarity(s1, s2);
		    System.out.println(s2 + "  : " + similarity);
		}
	}
	
	private TFIDFWeightCalculator createZeroRUserManual() {
		UserManual manual = new UserManual(TestWekaConfigExamples.zeroR_usermanual);
//		for(String key : manual.getAllOptions()) {
//			System.out.println(key + "  =>  " + manual.getDescription(key));
//		}
		TFIDFWeightCalculator cal = new TFIDFWeightCalculator(manual.getAllTextDesc());
		cal.computeTFIDFValues();
		return cal;
	}
}
