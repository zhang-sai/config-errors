package edu.washington.cs.conf.mutation.weka;

import junit.framework.TestCase;

public class TestWekaExamples extends TestCase {
	
	public void testWekaInstances() {
//		String[] args = new String[]{"merge", "./subjects/weka/soybean.arff", "./subjects/weka/soybean.arff"};
		String[] args = new String[]{"merge", "./subjects/weka/soybean.arff", "./subjects/weka/soybean.arff"};
//		args = new String[]{"help"};
//		args = new String[]{"headers", "./subjects/weka/soybean.arff", "./subjects/weka/soybean.arff"};
//		args = new String[]{"randomize", "-1000", "./subjects/weka/soybean.arff"};
//		args = new String[]{"append", "./subjects/weka/soybean.arff", "./subjects/weka/soybean.arff"};
		weka.core.Instances.main(args);
	}
	
	public void testZeroR() {
		String[] args = new String[]{"help", "-t", "./subjects/weka/weather.arff"};
		args = new String[]{"-t", "./subjects/weka/weather.arff", "-threshold-file", "./subjects/weka/weather.arff"};
		args = new String[]{"-t", "./subjects/weka/weather.arff"};
		args = new String[]{"help"};
		args = new String[]{"-p", "2",  "-t", "./subjects/weka/weather.arff", "-preserve-order",
				"-split-percentage", "10"};
		args = new String[]{"-p", "2",  "-t", "./subjects/weka/weather.arff", "-preserve-order"};
		weka.classifiers.rules.ZeroR.main(args);
		
	}
	
}
