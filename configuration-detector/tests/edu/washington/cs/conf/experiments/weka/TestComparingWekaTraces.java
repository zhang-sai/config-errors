package edu.washington.cs.conf.experiments.weka;

import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.experiments.CommonUtils;
import junit.framework.TestCase;

public class TestComparingWekaTraces extends TestCase {
	
	public void test1() {
	    String goodRunTrace = "./experiments/weka-database/good-iris.txt";
	    String badRunTrace = "./experiments/weka-database/bad-labor.txt";
//	    CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.0f);
	    CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.16477811f);
	}
	
	static String laborBadRun = "./experiments/weka-database/bad-labor.txt";
	
	//other traces
	static String dir = "./experiments/weka-database/";
	
	static String good1 = dir + "discretize-iris.txt";
	static String good2 = dir + "iris-simplified-last.txt";
	static String good3 = dir + "iris-simplified.txt";
	static String good4 = dir + "nomToBinary-contact-lenses.txt";
	static String good5 = dir + "resample-soybean-uniform.txt";
	static String good6 = dir + "resample-soybean.txt";
	static String good7 = dir + "soybean-instance.txt";
	static String good8 = dir + "stra-remove-folds-soybean-nov.txt";
	static String good9 = dir + "stra-remove-folds-soybean.txt";
	static String good10 = dir + "weather-j48.txt"; //good
	static String good11 = dir + "weather-rules.txt";
	static String good12 = dir + "good-iris.txt"; //good
	
	static String[] db = new String[]{good1, good2, good3, good4, good5, good6, /*good7,*/ good8, good9, good10, good11, good12};
	
	public void testAll() {
	    
	    String badRunTrace = "./experiments/weka-database/bad-labor.txt";
	    for(String goodRunTrace : db) {
	    	System.out.println(goodRunTrace);
	        CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, null, false);
	    }
	}
}