package edu.washington.cs.conf.experiments.jchord;

import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.diagnosis.MainAnalyzer.SelectionStrategy;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser.RankType;
import edu.washington.cs.conf.experiments.ChordExpUtils;
import edu.washington.cs.conf.experiments.CommonUtils;
import junit.framework.TestCase;

public class TestDiagnoseJChordOptions extends TestCase {

	public void testDiagnose1() {
		String goodRunTrace = "./experiments/jchord-database/simpletest-has-race.txt";
		String badRunTrace = "./experiments/jchord-database/simpletest-no-race.txt";
		List<ConfEntity> jchordConfList = ChordExpUtils.getChordConfList();
		ConfEntityRepository repo = new ConfEntityRepository(jchordConfList);
	    CommonUtils.diagnoseOptions(goodRunTrace, badRunTrace, true, repo, RankType.SINGLE_IMPORT, 0.2f);
	}
	
	public void testDiagnoseAll() {
		String badRunTrace = TestComparingJChordTraces.no_race;
		String[] goodRunTraceArray = TestComparingJChordTraces.db;
		ConfEntityRepository repo = ChordExpUtils.getChordRepository();
		
		MainAnalyzer.diagnoseConfigErrors(badRunTrace, goodRunTraceArray, repo,
				null, null, SelectionStrategy.ALL);
	}
	
	public void testDiagnoseRandom() {
		String badRunTrace = TestComparingJChordTraces.no_race;
		String[] goodRunTraceArray = TestComparingJChordTraces.db;
		ConfEntityRepository repo = ChordExpUtils.getChordRepository();
		
		MainAnalyzer.diagnoseConfigErrors(badRunTrace, goodRunTraceArray, repo,
				null, null, SelectionStrategy.RandomK);
	}
	
	public void testMeasureTime() {
		long start = System.currentTimeMillis();
		testDiagnoseSimilar();
		long end = System.currentTimeMillis();
		System.out.println("elapsed: " + (end - start));
	}
	
	public void testDiagnoseSimilar() {
		String badRunTrace = TestComparingJChordTraces.no_race;
		String[] goodRunTraceArray = TestComparingJChordTraces.db;
		ConfEntityRepository repo = ChordExpUtils.getChordRepository();
		
		MainAnalyzer.doFiltering = true;
		MainAnalyzer.diagnoseConfigErrors(badRunTrace, goodRunTraceArray, repo,
				null, null, null);
		MainAnalyzer.doFiltering = false;
	}
	
	//for ICSE'14
	public void testConfSuggester() {
//		String badRunTrace = "./experiments/jchord-database/trace_dump_ssa_icse14.txt";
		String badRunTrace = "./experiments/jchord-database/trace_dump_print_icse14.txt";
		String[] goodRunTraceArray = TestComparingJChordTraces.db;
		ConfEntityRepository repo = ChordExpUtils.getChordRepository();
		
		MainAnalyzer.doFiltering = true;
		MainAnalyzer.diagnoseConfigErrors(badRunTrace, goodRunTraceArray, repo,
				null, null, null);
		MainAnalyzer.doFiltering = false;
	}
	
}
