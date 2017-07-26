package edu.washington.cs.conf.experiments.weka;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.MethodBasedDiagnoser;
import edu.washington.cs.conf.diagnosis.StmtCoverageBasedDiagnoser;
import edu.washington.cs.conf.diagnosis.StmtCoverageBasedDiagnoserMain;
import edu.washington.cs.conf.diagnosis.StmtExecuted;
import edu.washington.cs.conf.diagnosis.StmtFileReader;
import edu.washington.cs.conf.diagnosis.TestStmtExecutedDiffer;
import edu.washington.cs.conf.instrument.EveryStmtInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestWekaBaseline extends TestCase {
	
	public void testStmtInstrumentation() throws Exception {
		String[] skippedClasses = new String[] {"weka.classifiers.evaluation.Prediction"};
		
		EveryStmtInstrumenter instrumenter = new EveryStmtInstrumenter();
		instrumenter.setSkippedClasses(Arrays.asList(skippedClasses));
		
        instrumenter.instrument("./subjects/weka/weka-no-trace.jar", "./output/weka-everystmt.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
	/**
	 * Diagnose by statements, 
	 * still 7
	 * 
	 * with selected traces: 
	 * */
	public void testDiagnoseByRelatedStmt() {
		Collection<ConfPropOutput> outputs = TestSliceWekaConfigOptions.getWekaConfOutputs();
		
		String[] badStmtFiles = new String[]{"./experiments/weka-baseline/bad_stmt_labor-related.txt"};
		
		String[] goodStmtFiles = new String[]{
				"./experiments/weka-baseline/good_stmt_iris-related.txt",
//				"./experiments/weka-baseline/good_stmt_weather-related.txt",
//				"./experiments/weka-baseline/good-discretize-iris-related.txt",
//				"./experiments/weka-baseline/good-soybean-instance-related.txt",
//				"./experiments/weka-baseline/iris-simplified-last-related.txt",
//				"./experiments/weka-baseline/iris-simplified-related.txt",
//				"./experiments/weka-baseline/nomToBinary-contact-lenses-related.txt",
//				"./experiments/weka-baseline/resample-soybean-uniform-related.txt",
//				"./experiments/weka-baseline/resample-soybean-related.txt",
//				"./experiments/weka-baseline/stra-remove-folds-soybean-nov-related.txt",
//				"./experiments/weka-baseline/stra-remove-folds-soybean-related.txt",
				"./experiments/weka-baseline/weather-j48-related.txt"
				};
		
		StmtCoverageBasedDiagnoserMain.findResponsibleOptions(outputs, badStmtFiles, goodStmtFiles);
	}
	
	/**
	 * Diagnose by statements
	 * */
	public void testDiagnoseByStmt() {
		Collection<ConfPropOutput> outputs = TestSliceWekaConfigOptions.getWekaConfOutputs();
		
		String[] badStmtFiles = new String[]{"./experiments/weka-baseline/bad_stmt_labor.txt"};
		
		String[] goodStmtFiles = new String[]{
				"./experiments/weka-baseline/good_stmt_iris.txt",
				"./experiments/weka-baseline/good_stmt_weather.txt",
				"./experiments/weka-baseline/good-discretize-iris.txt",
				"./experiments/weka-baseline/good-soybean-instance.txt",
				"./experiments/weka-baseline/iris-simplified-last.txt",
				"./experiments/weka-baseline/iris-simplified.txt",
				"./experiments/weka-baseline/nomToBinary-contact-lenses.txt",
				"./experiments/weka-baseline/resample-soybean-uniform.txt",
				"./experiments/weka-baseline/resample-soybean.txt",
				"./experiments/weka-baseline/stra-remove-folds-soybean-nov.txt",
				"./experiments/weka-baseline/stra-remove-folds-soybean.txt",
				"./experiments/weka-baseline/weather-j48.txt"
				};
		
		StmtCoverageBasedDiagnoserMain.findResponsibleOptions(outputs, badStmtFiles, goodStmtFiles);
	}
	
	/**
	 * Diagnose by invariants
	 * */
	public void testDiagnoseByInvariant() {
		String badInv = "D:\\research\\configurations\\daikon\\bin\\weka\\labor.inv.gz";
		
		String goodIrisInv = "D:\\research\\configurations\\daikon\\bin\\weka\\iris.inv.gz";
		String goodWeatherInv = "D:\\research\\configurations\\daikon\\bin\\weka\\weather.inv.gz";
		String goodInv1 = "D:\\research\\configurations\\daikon\\bin\\weka\\iris-simplified-last.inv.gz";
		String goodInv2 = "D:\\research\\configurations\\daikon\\bin\\weka\\discretize-iris.inv.gz";
		String goodInv3 = "D:\\research\\configurations\\daikon\\bin\\weka\\iris-simplified.inv.gz";
		String goodInv4 = "D:\\research\\configurations\\daikon\\bin\\weka\\nomToBinary-contact-lenses.inv.gz";
		String goodInv5 = "D:\\research\\configurations\\daikon\\bin\\weka\\soybean-instance.gz";
		String goodInv6 = "D:\\research\\configurations\\daikon\\bin\\weka\\stra-remove-folds-soybean-nov.inv.gz";
		String goodInv7 = "D:\\research\\configurations\\daikon\\bin\\weka\\stra-remove-folds-soybean.inv.gz";
		
		Collection<ConfPropOutput> confs = TestSliceWekaConfigOptions.getWekaConfOutputs();
		
		System.out.println("start diagnosing... ");
		
		List<String> goodInvs = new LinkedList<String>();
//		goodInvs.add(goodIrisInv);
//		goodInvs.add(goodWeatherInv);
		goodInvs.add(goodInv1);
//		goodInvs.add(goodInv2);
//		goodInvs.add(goodInv3);
//		goodInvs.add(goodInv4);
//		goodInvs.add(goodInv5);
//		goodInvs.add(goodInv6);
//		goodInvs.add(goodInv7);
		
        List<ConfEntity> entities
            = MethodBasedDiagnoser.computeResponsibleOptions(goodInvs,
            		badInv, confs);
		
		System.out.println(entities.size());
		int i = 0;
		for(ConfEntity entity : entities) {
			System.out.println((i+1) + ". " + entity);
			i++;
		}
	}

}
