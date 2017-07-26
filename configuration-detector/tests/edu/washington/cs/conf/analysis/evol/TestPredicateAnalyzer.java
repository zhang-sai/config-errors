package edu.washington.cs.conf.analysis.evol;

import java.util.Map;

import edu.washington.cs.conf.analysis.evol.experimental.MethodMatchingLogics;
import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;
import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfoAnalyzer;
import edu.washington.cs.conf.util.Log;
import edu.washington.cs.conf.util.Utils;

import plume.Pair;
import junit.framework.TestCase;

/**
 * Check the predicate behaviors
 * */
public class TestPredicateAnalyzer extends TestCase {

	public void testRandoop() {
		MethodMatcher.debug = false;
		
		CodeAnalyzer oldAnalyzer = CodeAnalyzerRepository.getRandoop121Analyzer();
		oldAnalyzer.buildAnalysis();
		CodeAnalyzer newAnalyzer = CodeAnalyzerRepository.getRandoop132Analyzer();
		newAnalyzer.buildAnalysis();
		String oldTraceFile = TraceRepository.randoopOldTrace;
		String newTraceFile = TraceRepository.randoopNewTrace;
		AnalysisScope scope = AnalysisScopeRepository.createRandoopScore();
		AnalysisCache cache = AnalysisCache.createCache(oldAnalyzer, newAnalyzer, scope);
		
		//analyze the predicate
		PredicateExecInfoAnalyzer analyzer
		    = new PredicateExecInfoAnalyzer(oldAnalyzer, newAnalyzer, scope, cache,
				oldTraceFile, newTraceFile);
		
		//only exact matching
//		MethodMatchingLogics.USE_FUZZING_MATCHING = false;
		
		//find the deviation pairs
		Map<Pair<PredicateExecInfo, PredicateExecInfo>, Float> predicatePairs
		    = analyzer.findBehaviorDeviatedPredicatePairs();
		
		predicatePairs = Utils.sortByValue(predicatePairs, false);
		
		for(Pair<PredicateExecInfo, PredicateExecInfo> p : predicatePairs.keySet()) {
			PredicateExecInfo oldP = p.a;
			PredicateExecInfo newP = p.b;
			System.out.println(oldP + " ==> " + newP);
			System.out.println("   " + predicatePairs.get(p));
			
			//only for experiment
//			p.a.showContext(oldAnalyzer.getCallGraph());
//			p.b.showContext(newAnalyzer.getCallGraph());
//			System.out.println("---------------------");
			
//			for(Pair<PredicateExecInfo, PredicateExecInfo> pair : predicatePairs.keySet()) {
//				if(pair.a == p.a) {
//					System.out.println("=== " + pair.b);
//				}
//			}
//			
//			break;
		}
 	}
	
	public void testSynoptic() {
		Log.logConfig("./evol-experiments/synoptic/log.txt");
		boolean matchDebug = false;
		boolean fuzzMatching = false;
		CodeAnalyzer oldAnalyzer = CodeAnalyzerRepository.getSynopticOldAnalyzer();
		CodeAnalyzer newAnalyzer = CodeAnalyzerRepository.getSynopticNewAnalyzer();
		String oldTraceFile = TraceRepository.synopticOldTrace;
		String newTraceFile = TraceRepository.synopticNewTrace;
		AnalysisScope scope = AnalysisScopeRepository.createSynopticScope();
		this.findDeviatedPredicates(matchDebug, fuzzMatching, oldAnalyzer, newAnalyzer, oldTraceFile, newTraceFile, scope);
		Log.removeLogging();
	}
	
	//FIXME, the chord bytecode needs to be re-compiled, seems
	public void testJChord() {
		boolean matchDebug = false;
		boolean fuzzMatching = false;
		CodeAnalyzer oldAnalyzer = CodeAnalyzerRepository.getJChordOldAnalyzer();
		CodeAnalyzer newAnalyzer = CodeAnalyzerRepository.getJChordNewAnalyzer();
		String oldTraceFile = TraceRepository.jchordP1OldTrace;
		String newTraceFile = TraceRepository.jchordP1NewTrace;
		AnalysisScope scope = AnalysisScopeRepository.createJChordScope();
		this.findDeviatedPredicates(matchDebug, fuzzMatching, oldAnalyzer, newAnalyzer, oldTraceFile, newTraceFile, scope);
		
		oldTraceFile = TraceRepository.jchordP2OldTrace;
		newTraceFile = TraceRepository.jchordP2NewTrace;
		this.findDeviatedPredicates(matchDebug, fuzzMatching, oldAnalyzer, newAnalyzer, oldTraceFile, newTraceFile, scope);
	}
	
	public void testJMeter() {
		boolean matchDebug = false;
		boolean fuzzMatching = false;
		CodeAnalyzer oldAnalyzer = CodeAnalyzerRepository.getJMeterOldAnalyzer();
		CodeAnalyzer newAnalyzer = CodeAnalyzerRepository.getJMeterNewAnalyzer();
		String oldTraceFile = TraceRepository.jmeterOldTrace;
		String newTraceFile = TraceRepository.jmeterNewTrace;
		AnalysisScope scope = AnalysisScopeRepository.createJMeterScope();
		this.findDeviatedPredicates(matchDebug, fuzzMatching, oldAnalyzer, newAnalyzer, oldTraceFile, newTraceFile, scope);
	}
	
	public void testWeka() {
		boolean matchDebug = false;
		boolean fuzzMatching = false;
		CodeAnalyzer oldAnalyzer = CodeAnalyzerRepository.getWekaOldAnalyzer();
		CodeAnalyzer newAnalyzer = CodeAnalyzerRepository.getWekaNewAnalyzer();
		String oldTraceFile = TraceRepository.wekaOldTrace;
		String newTraceFile = TraceRepository.wekaNewTrace;
		AnalysisScope scope = AnalysisScopeRepository.createWekaScope();
		this.findDeviatedPredicates(matchDebug, fuzzMatching, oldAnalyzer, newAnalyzer, oldTraceFile, newTraceFile, scope);
	}
	
	public void findDeviatedPredicates(boolean matchDebug, boolean fuzzMatching, CodeAnalyzer oldAnalyzer,
			CodeAnalyzer newAnalyzer, String oldTraceFile, String newTraceFile, AnalysisScope scope) {
		Log.logln(" ..... finding deviated predicates .......");
		MethodMatcher.debug = matchDebug;
		
		oldAnalyzer.buildAnalysis();
		newAnalyzer.buildAnalysis();
		AnalysisCache cache = AnalysisCache.createCache(oldAnalyzer, newAnalyzer, scope);
		
		//analyze the predicate
		PredicateExecInfoAnalyzer analyzer
		    = new PredicateExecInfoAnalyzer(oldAnalyzer, newAnalyzer, scope, cache,
				oldTraceFile, newTraceFile);
		
		//only exact matching
		MethodMatchingLogics.USE_FUZZING_MATCHING = fuzzMatching;
		
		//find the deviation pairs
		Map<Pair<PredicateExecInfo, PredicateExecInfo>, Float> predicatePairs
		    = analyzer.findBehaviorDeviatedPredicatePairs();
		
		predicatePairs = Utils.sortByValue(predicatePairs, false);
		
		for(Pair<PredicateExecInfo, PredicateExecInfo> p : predicatePairs.keySet()) {
			PredicateExecInfo oldP = p.a;
			PredicateExecInfo newP = p.b;
			System.out.println(oldP + " ==> " + newP);
			Log.logln(oldP + " ==> " + newP);
			System.out.println("   " + predicatePairs.get(p));
			Log.logln("   " + predicatePairs.get(p));
		}
	}
	
	public void tearDown() {
		MethodMatchingLogics.USE_FUZZING_MATCHING = true;
		MethodMatcher.debug = true;
	}
}
