package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;

import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;

import junit.framework.TestCase;

@Deprecated
public class TestPredicateExecInfo extends TestCase {

	
	public void testRandoop() {
		this.testParsingTraces("./evol-experiments/randoop/randoop-1.2.1.txt");
	}
	
	public void testSynoptic() {
		this.testParsingTraces(TraceRepository.synopticOldTrace);
	}
	
	public void testJMeter() {
		this.testParsingTraces(TraceRepository.jmeterOldTrace);
	}
	
	public void testWeka() {
		this.testParsingTraces(TraceRepository.wekaOldTrace);
	}
	
	public void testJChordP1() {
		this.testParsingTraces(TraceRepository.jchordP1OldTrace);
	}
	
	public void testJChordP2() {
		this.testParsingTraces(TraceRepository.jchordP2OldTrace);
	}
	
	private void testParsingTraces(String fileName) {
		Collection<PredicateExecInfo> coll = ExecutionTraceReader.createPredicateExecInfoList(fileName);
		
		System.out.println(coll.size());
		
		for(PredicateExecInfo info : coll) {
			System.out.println(info);
		}
	}
}
