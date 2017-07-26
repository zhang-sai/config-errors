package edu.washington.cs.conf.experiments.synoptic;

import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.experiments.CommonUtils;
import junit.framework.TestCase;

public class TestComparingSynopticTraces extends TestCase {

	public static String badRunTrace = "./experiments/synoptic-database/2pc_3nodes_100tx_bad-injected.txt";
	
	public static String good2pc_100tx = "./experiments/synoptic-database/2pc_3nodes_100tx_good.txt";
	public static String good2pc_5tx = "./experiments/synoptic-database/2pc_5tx.txt";
	public static String goodapache_log = "./experiments/synoptic-database/apache_log.txt";
	public static String synoptic_show_all_help = "./experiments/synoptic-database/synoptic_show_all_help.txt";
	public static String synoptic_show_help = "./experiments/synoptic-database/synoptic_show_help.txt";
	public static String synoptic_version = "./experiments/synoptic-database/synoptic_version.txt";
	public static String stackar_trace = "./experiments/synoptic-database/stackar_trace.txt";
	
	public static String[] db = new String[]{
//		good2pc_100tx,
		stackar_trace,
		good2pc_5tx, 
		goodapache_log,
		synoptic_show_all_help, synoptic_show_help, synoptic_version
	};
	
	public void testAllDistance() {
		for(String goodRunTrace : db) {
			CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, null, false);
		}
	}
	
	public void test1() {
	    String goodRunTrace = "./experiments/synoptic-database/2pc_3nodes_100tx_bad-injected.txt";
	    String badRunTrace = "./experiments/synoptic-database/2pc_3nodes_100tx_good.txt";
//	    CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.0f);
	    CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.2584772f);
	}
	
}
