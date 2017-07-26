package edu.washington.cs.conf.experiments.jchord;

import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.experiments.CommonUtils;
import junit.framework.TestCase;

public class TestComparingJChordTraces extends TestCase {
	
	public static String ctxtanalysis = "./experiments/jchord-database/ctxtsanalysis_default.txt";
	public static String simpletest_deadlock = "./experiments/jchord-database/deadlock_simpletest.txt";//!
	public static String dlog_simpletest = "./experiments/jchord-database/dlog_simpletest.txt";//!
	public static String do_nothing = "./experiments/jchord-database/do_nothing.txt";
	public static String print_projects = "./experiments/jchord-database/print_projects.txt";
	public static String simpletest_race = "./experiments/jchord-database/simpletest-has-race.txt";//!
	public static String example_datarace = "./experiments/jchord-database/example-datarace.txt";//!
	public static String example_datarace_eqth = "./experiments/jchord-database/example_datarace_eqth.txt";//!
	
	public static String no_race = "./experiments/jchord-database/simpletest-no-race.txt";
	
	public static String[] db = new String[]{
		ctxtanalysis, simpletest_deadlock,
		dlog_simpletest, do_nothing, print_projects,
		simpletest_race, 
//		example_datarace,
		example_datarace_eqth
	};
	
	public void testAllDistances() {
		for(String goodRun : db) {
			CommonUtils.compareTraceDistance(goodRun, no_race, DistanceType.INTERPRODUCT, null, false);
		}
	}

	public void test1() {
		String goodRunTrace = "./experiments/jchord-database/simpletest-has-race.txt";
		String badRunTrace = "./experiments/jchord-database/simpletest-no-race.txt";
		
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.28348106f);
		
	}
	
	public void test2() {
		String goodRunTrace = "./experiments/jchord-database/ctxtsanalysis_default.txt";
		String badRunTrace = "./experiments/jchord-crashing-error/chord-crash-no-ctxt-kind.txt";
		
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.16580969f);
	}
	
	public void test3() {
		String goodRunTrace = "./experiments/jchord-database/simpletest-has-race.txt";
		String badRunTrace = "./experiments/jchord-crashing-error/chord-crash-no-ctxt-kind.txt";
		
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.16580969f);
	}
	
}