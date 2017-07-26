package edu.washington.cs.conf.experiments.soot;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.InvariantDiffAnalyzer;
import edu.washington.cs.conf.diagnosis.MethodBasedDiagnoser;
import edu.washington.cs.conf.diagnosis.StmtCoverageBasedDiagnoserMain;
import edu.washington.cs.conf.experiments.weka.TestSliceWekaConfigOptions;
import edu.washington.cs.conf.instrument.EveryStmtInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestSootBaseline extends TestCase {
	public void testStmtInstrumentation() throws Exception {
		EveryStmtInstrumenter instrumenter = new EveryStmtInstrumenter();
		//add the skip class
		Collection<String> classes = Arrays.asList("soot.JastAddJ.SimpleSet",
				"soot.jbco.IJbcoTransform");
		instrumenter.setSkippedClasses(classes);
		//do instrumentation
        instrumenter.instrument("./subjects/soot-2.5/soot.jar", "./output/soot-everystmt.jar");
		InstrumentStats.showInstrumentationStats();
	}
	
	//11
	public void testDiagnoseByRelatedStmt() {
		Collection<ConfPropOutput> outputs = TestSliceSootConfigOptions.getSootConfOutputs();
		
		String[] badStmtFiles = new String[]{"./experiments/soot-baseline/stmt_coverage_helloworld_no_line-related.txt"};
		String[] goodStmtFiles = new String[]{
				"./experiments/soot-baseline/stmt_coverage_helloworld_keeplinenumber-related.txt",
//				"./experiments/soot-baseline/allow-phantom-helloworld-related.txt",
//				"./experiments/soot-baseline/ann-array-bounds-related.txt",
//				"./experiments/soot-baseline/ann-null-ptr-related.txt",
//				"./experiments/soot-baseline/ann-side-effect-spark-related.txt",
//				"./experiments/soot-baseline/ann-side-effect-related.txt",
//				"./experiments/soot-baseline/no-args-related.txt",
//				"./experiments/soot-baseline/parse-jimple-related.txt",
//				"./experiments/soot-baseline/pp_helloworld-related.txt",
//				"./experiments/soot-baseline/pp-process-dir-related.txt",
//				"./experiments/soot-baseline/ppdir-redict-related.txt",
//				"./experiments/soot-baseline/produce-jimple-related.txt",
//				"./experiments/soot-baseline/produce-shimp-related.txt",
//				"./experiments/soot-baseline/soot-help-related.txt",
//				"./experiments/soot-baseline/static-inline-related.txt",
//				"./experiments/soot-baseline/whole-program-opt-related.txt"
		        };
		
		StmtCoverageBasedDiagnoserMain.findResponsibleOptions(outputs, badStmtFiles, goodStmtFiles);
	}
	
	public void testDiagnoseByStmt() {
		Collection<ConfPropOutput> outputs = TestSliceSootConfigOptions.getSootConfOutputs();
		
		String[] badStmtFiles = new String[]{"./experiments/soot-baseline/stmt_coverage_helloworld_no_line.txt"};
		String[] goodStmtFiles = new String[]{
				"./experiments/soot-baseline/stmt_coverage_helloworld_keeplinenumber.txt",
				"./experiments/soot-baseline/allow-phantom-helloworld.txt",
				"./experiments/soot-baseline/ann-array-bounds.txt",
				"./experiments/soot-baseline/ann-null-ptr.txt",
				"./experiments/soot-baseline/ann-side-effect-spark.txt",
				"./experiments/soot-baseline/ann-side-effect.txt",
				"./experiments/soot-baseline/no-args.txt",
				"./experiments/soot-baseline/parse-jimple.txt",
				"./experiments/soot-baseline/pp_helloworld.txt",
				"./experiments/soot-baseline/pp-process-dir.txt",
				"./experiments/soot-baseline/ppdir-redict.txt",
				"./experiments/soot-baseline/produce-jimple.txt",
				"./experiments/soot-baseline/produce-shimp.txt",
				"./experiments/soot-baseline/soot-help.txt",
				"./experiments/soot-baseline/static-inline.txt",
				"./experiments/soot-baseline/whole-program-opt.txt"
		        };
		
		StmtCoverageBasedDiagnoserMain.findResponsibleOptions(outputs, badStmtFiles, goodStmtFiles);
	}
	
	public void testDiagnoseByInvariants() {
		
		String badSootFile = "D:\\research\\configurations\\daikon\\bin\\soot\\soot_helloworld-no-linenum.inv.gz";
		
		String goodSootFile1 = "D:\\research\\configurations\\daikon\\bin\\soot\\soot_helloworld-has-linenum.inv.gz";
		String goodSootFile2 = "D:\\research\\configurations\\daikon\\bin\\soot\\helloworld-allow-phantom.inv.gz";
		String goodSootFile3 = "D:\\research\\configurations\\daikon\\bin\\soot\\helloworld-pp.inv.gz";
		String goodSootFile4 = "D:\\research\\configurations\\daikon\\bin\\soot\\helloworld-process-dir-redict.inv.gz";
		String goodSootFile5 = "D:\\research\\configurations\\daikon\\bin\\soot\\helloworld-process-dir.inv.gz";
		String goodSootFile6 = "D:\\research\\configurations\\daikon\\bin\\soot\\parse-jimple-parse.inv.gz";
		String goodSootFile7 = "D:\\research\\configurations\\daikon\\bin\\soot\\produce-shimple.inv.gz";
		String goodSootFile8 = "D:\\research\\configurations\\daikon\\bin\\soot\\soot-ann-array-bounds.inv.gz";
		String goodSootFile9 = "D:\\research\\configurations\\daikon\\bin\\soot\\soot-ann-null-ptr.inv.gz";
		String goodSootFile10 = "D:\\research\\configurations\\daikon\\bin\\soot\\soot-ann-sideeffect.inv.gz";
		String goodSootFile11 = "D:\\research\\configurations\\daikon\\bin\\soot\\soot-help.inv.gz";
		String goodSootFile12 = "D:\\research\\configurations\\daikon\\bin\\soot\\soot-no-args.inv.gz";
		String goodSootFile13 = "D:\\research\\configurations\\daikon\\bin\\soot\\spark-enabled.inv.gz";
		String goodSootFile14 = "D:\\research\\configurations\\daikon\\bin\\soot\\whole-program-opt.inv.gz";
		
		List<String> goodFiles = new LinkedList<String>();
		goodFiles.add(goodSootFile1);
		goodFiles.add(goodSootFile2);
//		goodFiles.add(goodSootFile3);
//		goodFiles.add(goodSootFile4);
//		goodFiles.add(goodSootFile5);
//		goodFiles.add(goodSootFile6);
//		goodFiles.add(goodSootFile7);
//		goodFiles.add(goodSootFile8);
//		goodFiles.add(goodSootFile9);
//		goodFiles.add(goodSootFile10);
//		goodFiles.add(goodSootFile11);
//		goodFiles.add(goodSootFile12);
//		goodFiles.add(goodSootFile13);
//		goodFiles.add(goodSootFile14);
		
		Collection<ConfPropOutput> confs = TestSliceSootConfigOptions.getSootConfOutputs();
		
		System.out.println("start diagnosing... ");
		
        List<ConfEntity> entities
            = MethodBasedDiagnoser.computeResponsibleOptions(goodFiles,
            		badSootFile, confs);
		
		System.out.println(entities.size());
		int i = 0;
		for(ConfEntity entity : entities) {
			System.out.println((i+1) + ". " + entity);
			i++;
		}
	}
	
	public void testAnalyzeInvariants() {
		String badSootFile = "D:\\research\\configurations\\daikon\\bin\\soot\\soot_helloworld-no-linenum.inv.gz";
		
		String goodSootFile = "D:\\research\\configurations\\daikon\\bin\\soot\\soot_helloworld-has-linenum.inv.gz";
		InvariantDiffAnalyzer analyzer = new InvariantDiffAnalyzer(Collections.singleton(goodSootFile), badSootFile);
		Map<String, Float> scores = analyzer.getMethodsWithDiffInvariants();
	}
}
