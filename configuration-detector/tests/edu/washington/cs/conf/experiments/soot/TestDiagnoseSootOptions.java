package edu.washington.cs.conf.experiments.soot;

import java.util.Collection;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.diagnosis.MainAnalyzer.SelectionStrategy;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser.RankType;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.experiments.SootExpUtils;
import junit.framework.TestCase;

public class TestDiagnoseSootOptions extends TestCase {
	
	public void test1() {
		String goodRunTrace = "./experiments/soot-database/soot_helloworld_with_keepline.txt";
	    String badRunTrace = "./experiments/soot-database/soot_helloworld_no_keepline.txt";
	    List<ConfEntity> sootConfList = SootExpUtils.getSootConfList();
		ConfEntityRepository repo = new ConfEntityRepository(sootConfList);
		
//		MainAnalyzer.doFiltering = true;
	    CommonUtils.diagnoseOptions(goodRunTrace, badRunTrace, true, repo, RankType.SINGLE_IMPORT, 0.2f);
	}
	
	public void testDiagnoseAll() {
		String badRunTrace = "./experiments/soot-database/soot_helloworld_no_keepline.txt";
		ConfEntityRepository repo = SootExpUtils.getConfEntityRepository();
		
		MainAnalyzer.diagnoseConfigErrors(badRunTrace, TestComparingSootTraces.db,
				repo, null, null, SelectionStrategy.ALL);
	}
	
	public void testDiagnoseRandom() {
		String badRunTrace = "./experiments/soot-database/soot_helloworld_no_keepline.txt";
		ConfEntityRepository repo = SootExpUtils.getConfEntityRepository();
		
		MainAnalyzer.diagnoseConfigErrors(badRunTrace, TestComparingSootTraces.db,
				repo, null, null, SelectionStrategy.RandomK);
	}
	
	public void testMeasureTime() {
		long start = System.currentTimeMillis();
		this.testDiagnoseSimilar();
		long end = System.currentTimeMillis();
		System.out.println("elapsed: " + (end - start));
	}
	
	public void testDiagnoseSimilar() {
		String badRunTrace = "./experiments/soot-database/soot_helloworld_no_keepline.txt";
		ConfEntityRepository repo = SootExpUtils.getConfEntityRepository();
		
		MainAnalyzer.doFiltering = true;
		MainAnalyzer.diagnoseConfigErrors(badRunTrace, 
				TestComparingSootTraces.db,
				//new String[]{"./experiments/soot-database/soot_helloworld_with_keepline.txt"},
				repo, null, null, null, MainAnalyzer.default_threshold);
		MainAnalyzer.doFiltering = false;
	}
	
	public void testDiagnoseSimilar_ErrorReport() {
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		
		String sootSrc = SootExpUtils.getSootSourceDir();
		Collection<ConfPropOutput> confSlices = TestSliceSootConfigOptions.getSootConfOutputs();
		
		String badRunTrace = "./experiments/soot-database/soot_helloworld_no_keepline.txt";
		ConfEntityRepository repo = SootExpUtils.getConfEntityRepository();
		
		MainAnalyzer.doFiltering = true;
		MainAnalyzer.diagnoseConfigErrors(badRunTrace, 
				TestComparingSootTraces.db,
				//new String[]{"./experiments/soot-database/soot_helloworld_with_keepline.txt"},
				repo, sootSrc, confSlices, null, 0.11f);
		
		MainAnalyzer.doFiltering = false;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = false;
	}
}
