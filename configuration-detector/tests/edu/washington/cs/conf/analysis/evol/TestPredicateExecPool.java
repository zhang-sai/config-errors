package edu.washington.cs.conf.analysis.evol;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;
import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecPool;

import junit.framework.TestCase;

@Deprecated
public class TestPredicateExecPool extends TestCase {

	public void testRandoop() {
		this.testGetMostFrequent(TraceRepository.randoopOldTrace);
	}
	
	public void testSynoptic() {
		this.testGetMostFrequent(TraceRepository.synopticOldTrace);
	}
	
	public void testJMeter() {
		this.testGetMostFrequent(TraceRepository.jmeterOldTrace);
	}
	
	public void testWeka() {
		this.testGetMostFrequent(TraceRepository.wekaOldTrace);
	}
	
	public void testJChordP1() {
		this.testGetMostFrequent(TraceRepository.jchordP1OldTrace);
	}
	
	public void testJChordP2() {
		this.testGetMostFrequent(TraceRepository.jchordP2OldTrace);
	}
	
	private void testGetMostFrequent(String fileName) {
		PredicateExecPool pool = new PredicateExecPool(fileName);
		//PredicateExecInfo p = pool.getMostFrequentlyExecuted();
		//need t sort
		//System.out.println(p);
		
		Comparator<PredicateExecInfo> comparator = PredicateMetrics.getFreqComparator();
		
		Collections.sort(pool.predicates, comparator);
		Collections.reverse(pool.predicates);
		
		for(int i = 0; i < 200; i++) {
			System.out.println(pool.predicates.get(i));
		}
	}
}
