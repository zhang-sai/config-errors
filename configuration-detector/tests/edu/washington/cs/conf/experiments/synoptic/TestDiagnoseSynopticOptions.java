package edu.washington.cs.conf.experiments.synoptic;

import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.diagnosis.MainAnalyzer.SelectionStrategy;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser.RankType;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.experiments.SynopticExpUtils;
import junit.framework.TestCase;

public class TestDiagnoseSynopticOptions extends TestCase {
	
	public void testDiagnoseAll() {
		String badRun = TestComparingSynopticTraces.badRunTrace;
		ConfEntityRepository repo = SynopticExpUtils.getConfEntityRepository();
		MainAnalyzer.diagnoseConfigErrors(badRun, TestComparingSynopticTraces.db,
				repo, null, null, SelectionStrategy.ALL);
	}
	
	public void testDiagnoseRandom() {
		String badRun = TestComparingSynopticTraces.badRunTrace;
		ConfEntityRepository repo = SynopticExpUtils.getConfEntityRepository();
		MainAnalyzer.diagnoseConfigErrors(badRun, TestComparingSynopticTraces.db,
				repo, null, null, SelectionStrategy.RandomK);
	}
	
	public void testmeasureTime() {
		long start = System.currentTimeMillis();
		this.testDiagnoseSimilarity();
		long end = System.currentTimeMillis();
		System.out.println("elapsed: " + (end - start));
	}
	
	public void testDiagnoseSimilarity() {
		MainAnalyzer.doFiltering = true;
		String badRun = TestComparingSynopticTraces.badRunTrace;
		ConfEntityRepository repo = SynopticExpUtils.getConfEntityRepository();
		MainAnalyzer.diagnoseConfigErrors(badRun, TestComparingSynopticTraces.db,
				repo, null, null, null);
		MainAnalyzer.doFiltering = false;
	}
	
	public void testConfSuggester() {
		String tmp = TestComparingSynopticTraces.badRunTrace;
		TestComparingSynopticTraces.badRunTrace = "./experiments/synoptic-database/trace_dump_synoptic_icse2014.txt";
		this.testDiagnoseSimilarity();
		TestComparingSynopticTraces.badRunTrace = tmp;
	}
	
	public void test1() {
		String goodRunTrace = "./experiments/synoptic-database/2pc_3nodes_100tx_bad-injected.txt";
	    String badRunTrace = "./experiments/synoptic-database/2pc_3nodes_100tx_good.txt";
	    List<ConfEntity> synopticConfList = SynopticExpUtils.getSynopticList();
		ConfEntityRepository repo = new ConfEntityRepository(synopticConfList);
	    CommonUtils.diagnoseOptions(goodRunTrace, badRunTrace, true, repo, RankType.SINGLE_IMPORT, 0.3f);
	}
	
}
