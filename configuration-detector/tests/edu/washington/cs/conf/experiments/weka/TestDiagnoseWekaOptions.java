package edu.washington.cs.conf.experiments.weka;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.ConfDiagnosisOutput;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.diagnosis.MainAnalyzer.SelectionStrategy;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser.RankType;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.experiments.WekaExpUtils;
import junit.framework.TestCase;

public class TestDiagnoseWekaOptions extends TestCase {
	
	public void test1() {
		String goodRunTrace = "./experiments/weka-database/good-iris.txt";
	    String badRunTrace = "./experiments/weka-database/bad-labor.txt";
	    List<ConfEntity> wekaConfList = WekaExpUtils.getWekaConfList();
		ConfEntityRepository repo = new ConfEntityRepository(wekaConfList);
	    CommonUtils.diagnoseOptions(goodRunTrace, badRunTrace, true, repo, RankType.SINGLE_IMPORT, 0.2f);
	}
	
	public void test2() {
		String goodRunTrace = TestComparingWekaTraces.good12;
	    String badRunTrace = "./experiments/weka-database/bad-labor.txt";
	    List<ConfEntity> wekaConfList = WekaExpUtils.getWekaConfList();
		ConfEntityRepository repo = new ConfEntityRepository(wekaConfList);
	    CommonUtils.diagnoseOptions(goodRunTrace, badRunTrace, true, repo, RankType.SINGLE_IMPORT, 0.2f);
	}
	
	//diagnosing the weka profiles
	public void testDiagnoseSimilar() {
		MainAnalyzer.doFiltering = true;
		long start = System.currentTimeMillis();
		diagnoseWeka(null);
		long end = System.currentTimeMillis();
		MainAnalyzer.doFiltering = false;
		System.out.println("eclapsed: " + ((float)end - (float)start)/1000);
	}
	
	public void testConfSuggester() {
		String tmp = TestComparingWekaTraces.laborBadRun;
		TestComparingWekaTraces.laborBadRun = "./experiments/weka-database/trace_dump_weka_icse14.txt";
		testDiagnoseAllProfiles();
		TestComparingWekaTraces.laborBadRun = tmp;
	}
	
	public void testDiagnoseSimilar_ErrorReport() {
		this.confSlices = TestSliceWekaConfigOptions.getWekaConfOutputs();
		this.testDiagnoseSimilar();
	}
	
	public void testDiagnoseAllRandom() {
		diagnoseWeka(SelectionStrategy.RandomK);
	}
	
	public void testDiagnoseMostSimilar() {
		diagnoseWeka(SelectionStrategy.OneMostSimilar);
	}
	
	public void testDiagnoseLeastSimilar() {
		diagnoseWeka(SelectionStrategy.OneLeastSimilar);
	}
	
	public void testDiagnoseAllProfiles() {
		diagnoseWeka(SelectionStrategy.ALL);
	}
	
	/***
	 * Do random selection, see class: MainAnalyzer
	 * */

	String wekaSrc = "D:\\research\\configurations\\workspace\\weka-3.6\\main\\java";
	
	Collection<ConfPropOutput> confSlices = null;
	//uncommet the following code will fetch the src text and line number
		//TestSliceWekaConfigOptions.getWekaConfOutputs();
	
	public void diagnoseWeka(SelectionStrategy strategy) {
		String badRunTrace = TestComparingWekaTraces.laborBadRun;
			//"./experiments/weka-database/bad-labor.txt";
		Collection<String> goodRunTraces = Arrays.asList(TestComparingWekaTraces.db);
		ConfEntityRepository repo = WekaExpUtils.getWekaRepository();
		
		MainAnalyzer analyzer = new MainAnalyzer(badRunTrace, goodRunTraces, repo, wekaSrc, confSlices);
		analyzer.setSelectionStrategy(strategy);
		
		List<ConfDiagnosisOutput> outputs = analyzer.computeResponsibleOptions();
		int i = 1;
		for(ConfDiagnosisOutput o : outputs) {
			System.out.println(i++);
			System.out.println(o.getConfEntity());
			System.out.println(o.getBriefExplanation());
			System.out.println();
			System.out.println(o.getErrorReport());
			System.out.println();
		}
	}
	
}
