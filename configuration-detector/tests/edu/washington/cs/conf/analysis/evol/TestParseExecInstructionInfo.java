package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;
import edu.washington.cs.conf.instrument.evol.CountingTracer;
import edu.washington.cs.conf.instrument.evol.EfficientTracer;
import edu.washington.cs.conf.instrument.evol.HardCodingPaths;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

import junit.framework.TestCase;

public class TestParseExecInstructionInfo extends TestCase {

	public void testParseRandoop_predicate() {
		Collection<PredicateExecInfo>  oldPredExecs
           = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.randoopOldPredicateDump,
        		TraceRepository.randoopOldSig);
	    Collection<PredicateExecInfo> newPredExecs
	       = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.randoopNewPredicateDump,
	    		TraceRepository.randoopNewSig);
	    Set<String> oldUnmatchedMethods = SimpleChecks.getUnmatchedOldMethods(oldPredExecs, newPredExecs);
	    Set<String> newUnmatchedMethods = SimpleChecks.getUnmatchedNewMethods(oldPredExecs, newPredExecs);
	    System.out.println("Number of unmatched old methods: " + oldUnmatchedMethods.size());
//	    System.out.println(oldUnmatchedMethods);
	    System.out.println("Number of unmatched new methods: " + newUnmatchedMethods.size());
//	    System.out.println(newUnmatchedMethods);
	    
	    Set<String> oldUnmatchedPredicates = SimpleChecks.getUnmatchedOldPredicates(oldPredExecs, newPredExecs);
		Set<String> newUnmatchedPredicates = SimpleChecks.getUnmatchedNewPredicates(oldPredExecs, newPredExecs);
		System.out.println("Number of unmatched old predicates: " + oldUnmatchedPredicates.size());
		System.out.println("Number of unmatched new predicates: " + newUnmatchedPredicates.size());
		
	    //XXX for each unmatched predicate, find its corresponding one in the new
		//version
		Set<PredicateExecInfo> onlyInOldPredicates = TraceComparator.mins(oldPredExecs, newPredExecs);
		System.out.println("=====================");
		System.out.println("== total num in old: " + onlyInOldPredicates.size());
		Set<PredicateExecInfo> onlyInNewPredicates = TraceComparator.mins(newPredExecs, oldPredExecs);
		System.out.println("== total num in new: " + onlyInNewPredicates.size());
		
        //the matched predicates
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getRandoop121Analyzer();
		oldCoder.buildAnalysis();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getRandoop132Analyzer();
		newCoder.buildAnalysis();
		Set<PredicateBehaviorAcrossVersions> matchedPreds
		    = SimpleChecks.getMatchedPredicateExecutions(oldPredExecs, newPredExecs, oldCoder, newCoder);
		System.out.println("Number of matched preds: " + matchedPreds.size());
		
		//check the change degrees
		System.out.println("-------------");
		//check the trace
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.randoopOldHistoryDump, 
				TraceRepository.randoopOldSig, TraceRepository.randoopOldPredicateDump);
		
		ExecutionTrace newTrace = new ExecutionTrace(TraceRepository.randoopNewHistoryDump, 
				TraceRepository.randoopNewSig, TraceRepository.randoopNewPredicateDump);
		
		matchedPreds = SimpleChecks.rankByBehaviorChanges(matchedPreds);
		for(PredicateBehaviorAcrossVersions predBehavior : matchedPreds) {
			float degree = predBehavior.getDifferenceDegree();
			if(degree < 0.1f) {
				continue;
			}
			System.out.println(predBehavior);
			System.out.println("      diff: " + degree);
			if(predBehavior.isExecutedOnOldVersion()) {
				Set<InstructionExecInfo> set = oldTrace.getExecutedInstructionsInsidePredicate(oldCoder, predBehavior.createOldPredicateExecInfo());
				System.out.println("     executed: " + set.size());
			} else {
				System.out.println("     not executed on old version.");
			}
			
			if(predBehavior.isExecutedOnNewVersion()) {
				Set<InstructionExecInfo> set = newTrace.getExecutedInstructionsInsidePredicate(newCoder, predBehavior.createNewPredicateExecInfo());
				System.out.println("    executed: " + set.size());
			} else {
				System.out.println("     not executed on new version");
			}
			
			System.out.println();
		}
	}
	
	public void testParseRandoop_predicate_in_trace() {
		List<InstructionExecInfo> oldInstructions
		    = ExecutionTraceReader.createPredicateExecInfoInTrace(TraceRepository.randoopOldHistoryDump,
		    		TraceRepository.randoopOldSig);
		List<InstructionExecInfo> newInstructions
	        = ExecutionTraceReader.createPredicateExecInfoInTrace(TraceRepository.randoopNewHistoryDump,
	        		TraceRepository.randoopNewSig);
		System.out.println("old instruction num: " + oldInstructions.size());
		System.out.println("new instruction num: " + newInstructions.size());
	}
	
	
	public void testCheckRandoop_predicate_in_old_trace() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getRandoop121Analyzer();
		oldCoder.buildAnalysis();
		
		//old trace
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.randoopOldHistoryDump, 
				TraceRepository.randoopOldSig, TraceRepository.randoopOldPredicateDump);
		Set<PredicateExecInfo> predSet = oldTrace.getExecutedPredicates();
		System.out.println("Num of executed predicates in old trace: " + predSet.size());
		//for each predicate, get its immediate post-dominator
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = ExecutionTrace.getImmediatePostDominator(oldCoder, pred);
			if(postDomExec == null) {
				continue;
			}
			Utils.checkTrue(pred.getIndex() != postDomExec.getIndex());
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
		}
	}
	
	public void testCheckRandoop_predicate_in_new_trace() {
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getRandoop132Analyzer();
		newCoder.buildAnalysis();
		
		//old trace
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.randoopNewHistoryDump, 
				TraceRepository.randoopNewSig, TraceRepository.randoopNewPredicateDump);
		Set<PredicateExecInfo> predSet = oldTrace.getExecutedPredicates();
		System.out.println("Num of executed predicates in new trace: " + predSet.size());
		//for each predicate, get its immediate post-dominator
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = ExecutionTrace.getImmediatePostDominator(newCoder, pred);
			if(postDomExec == null) {
				continue;
			}
			Utils.checkTrue(pred.getIndex() != postDomExec.getIndex());
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
		}
	}
	
	//---------------------------
	
	public void testParseSynoptic_predicate() {
		Collection<PredicateExecInfo>  oldPredExecs
	        = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.synopticOldPredicateDump,
	        		TraceRepository.synopticOldSig);
		Collection<PredicateExecInfo> newPredExecs
		    = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.synopticNewPredicateDump,
		    		TraceRepository.synopticNewSig);
		Set<String> oldUnmatchedMethods = SimpleChecks.getUnmatchedOldMethods(oldPredExecs, newPredExecs);
		Set<String> newUnmatchedMethods = SimpleChecks.getUnmatchedNewMethods(oldPredExecs, newPredExecs);
		System.out.println("Number of unmatched old methods: " + oldUnmatchedMethods.size());
//		System.out.println(oldUnmatchedMethods);
		System.out.println("Number of unmatched new methods: " + newUnmatchedMethods.size());
//		System.out.println(newUnmatchedMethods);
		
		Set<String> oldUnmatchedPredicates = SimpleChecks.getUnmatchedOldPredicates(oldPredExecs, newPredExecs);
		Set<String> newUnmatchedPredicates = SimpleChecks.getUnmatchedNewPredicates(oldPredExecs, newPredExecs);
		System.out.println("Number of unmatched old predicates: " + oldUnmatchedPredicates.size());
		System.out.println("Number of unmatched new predicates: " + newUnmatchedPredicates.size());
		
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getSynopticOldAnalyzer();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getSynopticNewAnalyzer();
		oldCoder.buildAnalysis();
		newCoder.buildAnalysis();
		
		Set<PredicateBehaviorAcrossVersions> matchedPreds = SimpleChecks.getMatchedPredicateExecutions(oldPredExecs, newPredExecs,
				oldCoder, newCoder);
		
		System.out.println("Number of matched preds: " + matchedPreds.size());
		matchedPreds = SimpleChecks.rankByBehaviorChanges(matchedPreds);
		
		//check the trace
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.synopticOldHistoryDump, 
				TraceRepository.synopticOldSig, TraceRepository.synopticOldPredicateDump);
		
		ExecutionTrace newTrace = new ExecutionTrace(TraceRepository.synopticNewHistoryDump, 
				TraceRepository.synopticNewSig, TraceRepository.synopticNewPredicateDump);
		
		for(PredicateBehaviorAcrossVersions pred : matchedPreds) {
			if(pred.isBehaviorChanged()) {
//				if(pred.getDifferenceDegree() < 0.1f) {
//					continue;
//				}
				System.out.println("  " + pred);
//				System.out.println("       " + pred.compareBehaviors());
				System.out.println("        behavior diff: " + pred.getDifferenceDegree());
				
				if(pred.isExecutedOnOldVersion()) {
					Set<InstructionExecInfo> set = oldTrace.getExecutedInstructionsInsidePredicate(oldCoder, pred.createOldPredicateExecInfo());
					System.out.println("     executed: " + set.size());
				} else {
					System.out.println("     not executed on old version.");
				}
				
				if(pred.isExecutedOnNewVersion()) {
					Set<InstructionExecInfo> set = newTrace.getExecutedInstructionsInsidePredicate(newCoder, pred.createNewPredicateExecInfo());
					System.out.println("    executed: " + set.size());
				} else {
					System.out.println("     not executed on new version");
				}
				
				System.out.println();
			}
		}
		
		
		
	}
	
	public void testParseSynoptic_predicate_in_trace() {
		List<InstructionExecInfo> oldInstructions
		    = ExecutionTraceReader.createPredicateExecInfoInTrace(TraceRepository.synopticOldHistoryDump,
		    		TraceRepository.synopticOldSig);
		List<InstructionExecInfo> newInstructions
	        = ExecutionTraceReader.createPredicateExecInfoInTrace(TraceRepository.synopticNewHistoryDump,
	        		TraceRepository.synopticNewSig);
		System.out.println("old instruction num: " + oldInstructions.size());
		System.out.println("new instruction num: " + newInstructions.size());
	}
	
	public void testCheckSynoptic_predicate_in_old_trace() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getSynopticOldAnalyzer();
		oldCoder.buildAnalysis();
		
		ExecutionTrace.enable_cache_trace = false;
		//old trace
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.synopticOldHistoryDump, 
				TraceRepository.synopticOldSig, TraceRepository.synopticOldPredicateDump);
		Set<PredicateExecInfo> predSet = oldTrace.getExecutedPredicates();
		System.out.println("Num of executed predicates in old trace: " + predSet.size());
		//for each predicate, get its immediate post-dominator
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = ExecutionTrace.getImmediatePostDominator(oldCoder, pred);
			Utils.checkTrue(pred.getIndex() != postDomExec.getIndex());
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
			
			//see the executed instructions
			Set<InstructionExecInfo> set =
				oldTrace.getExecutedInstructionsBetween(pred.getMethodSig(), pred.getIndex(),
						postDomExec.getMethodSig(), postDomExec.getIndex());
			System.out.println("     Number of instructions: " + set.size());
		}
	}
	
	
	public void testCheckSynoptic_predicate_in_new_trace() {
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getSynopticNewAnalyzer();
		newCoder.buildAnalysis();
		//new trace
		ExecutionTrace newTrace = new ExecutionTrace(TraceRepository.synopticNewHistoryDump, 
				TraceRepository.synopticNewSig, TraceRepository.synopticNewPredicateDump);
		Set<PredicateExecInfo> predSet = newTrace.getExecutedPredicates();
		System.out.println("Num of executed predicates in old trace: " + predSet.size());
		//for each predicate, get its immediate post-dominator
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = ExecutionTrace.getImmediatePostDominator(newCoder, pred);
			Utils.checkTrue(pred.getIndex() != postDomExec.getIndex());
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
			
			//see the executed instructions
			Set<InstructionExecInfo> set =
				newTrace.getExecutedInstructionsBetween(pred.getMethodSig(), pred.getIndex(),
						postDomExec.getMethodSig(), postDomExec.getIndex());
			System.out.println("     Number of instructions: " + set.size());
		}
	}
	
	//----------------------------------
	
	public void testParseWeka_predicate() {
		Collection<PredicateExecInfo>  oldPredExecs
          = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.wekaOldPredicateDump,
		    TraceRepository.wekaOldSig);
		System.out.print("read old predicates: " + oldPredExecs.size());
        Collection<PredicateExecInfo> newPredExecs
          = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.wekaNewPredicateDump,
		    TraceRepository.wekaNewSig);
        Set<String> oldUnmatchedMethods = SimpleChecks.getUnmatchedOldMethods(oldPredExecs, newPredExecs);
        Set<String> newUnmatchedMethods = SimpleChecks.getUnmatchedNewMethods(oldPredExecs, newPredExecs);
        System.out.println("Number of unmatched old methods: " + oldUnmatchedMethods.size());
        System.out.println(oldUnmatchedMethods);
        System.out.println("Number of unmatched new methods: " + newUnmatchedMethods.size());
        System.out.println(newUnmatchedMethods);
        
        Set<String> oldUnmatchedPredicates = SimpleChecks.getUnmatchedOldPredicates(oldPredExecs, newPredExecs);
		Set<String> newUnmatchedPredicates = SimpleChecks.getUnmatchedNewPredicates(oldPredExecs, newPredExecs);
		System.out.println("Number of unmatched old predicates: " + oldUnmatchedPredicates.size());
		System.out.println("Number of unmatched new predicates: " + newUnmatchedPredicates.size());
		
		Set<PredicateBehaviorAcrossVersions> matchedPreds = SimpleChecks.getMatchedPredicateExecutions(oldPredExecs, newPredExecs, null, null);
		System.out.println("Number of matched preds: " + matchedPreds.size());
		
		//see all behaviorally different
		int num = 0;
		for(PredicateBehaviorAcrossVersions pred : matchedPreds) {
			if(pred.isBehaviorChanged()) {
				System.out.println("   " + pred);
				System.out.println();
				num++;
			}
		}
		assertEquals(num, 1);
	}
	
	public void testParseWeka_predicate_in_trace() {
		List<InstructionExecInfo> oldInstructions
		    = ExecutionTraceReader.createPredicateExecInfoInTrace(TraceRepository.wekaOldHistoryDump,
		    		TraceRepository.wekaOldSig);
		List<InstructionExecInfo> newInstructions
	        = ExecutionTraceReader.createPredicateExecInfoInTrace(TraceRepository.wekaNewHistoryDump,
	        		TraceRepository.wekaNewSig);
		System.out.println("old instruction num: " + oldInstructions.size());
		System.out.println("new instruction num: " + newInstructions.size());
	}
	
	public void testCheckWeka_predicate_in_old_trace() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getWekaOldAnalyzer();
		oldCoder.buildAnalysis();
		
		//old trace
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.wekaOldHistoryDump, 
				TraceRepository.wekaOldSig, TraceRepository.wekaOldPredicateDump);
		Set<PredicateExecInfo> predSet = oldTrace.getExecutedPredicates();
		System.out.println("Num of executed predicates in old trace: " + predSet.size());
		//for each predicate, get its immediate post-dominator
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = ExecutionTrace.getImmediatePostDominator(oldCoder, pred);
			Utils.checkTrue(pred.getIndex() != postDomExec.getIndex());
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
			
			//find all instructions between
			Set<InstructionExecInfo> set =
				oldTrace.getExecutedInstructionsBetween(pred.getMethodSig(), pred.getIndex(),
						postDomExec.getMethodSig(), postDomExec.getIndex());
			System.out.println("     Number of instructions: " + set.size());
		}
	}
	
	public void testCheckWeka_predicate_in_new_trace() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getWekaNewAnalyzer();
		oldCoder.buildAnalysis();
		
		//old trace
		ExecutionTrace newTrace = new ExecutionTrace(TraceRepository.wekaNewHistoryDump, 
				TraceRepository.wekaNewSig, TraceRepository.wekaNewPredicateDump);
		Set<PredicateExecInfo> predSet = newTrace.getExecutedPredicates();
		System.out.println("Num of executed predicates in new trace: " + predSet.size());
		//for each predicate, get its immediate post-dominator
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = ExecutionTrace.getImmediatePostDominator(oldCoder, pred);
			Utils.checkTrue(pred.getIndex() != postDomExec.getIndex());
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
			
			//find all instructions between
			Set<InstructionExecInfo> set =
				newTrace.getExecutedInstructionsBetween(pred.getMethodSig(), pred.getIndex(),
						postDomExec.getMethodSig(), postDomExec.getIndex());
			System.out.println("     Number of instructions: " + set.size());
		}
	}
	
	//--------------------------------------
	//need to check which predicate behaves differently than exepcted
	//need to analyze different behaviors of the predicate
	public void testParseJMeter_predicate() {
		Collection<PredicateExecInfo>  oldPredExecs
            = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.jmeterOldPredicateDump,
    		    TraceRepository.jmeterOldSig);
        Collection<PredicateExecInfo> newPredExecs
            = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.jmeterNewPredicateDump,
    		    TraceRepository.jmeterNewSig);
        Set<String> oldUnmatchedMethods = SimpleChecks.getUnmatchedOldMethods(oldPredExecs, newPredExecs);
	    Set<String> newUnmatchedMethods = SimpleChecks.getUnmatchedNewMethods(oldPredExecs, newPredExecs);
	    System.out.println("Number of unmatched old methods: " + oldUnmatchedMethods.size());
	    System.out.println(oldUnmatchedMethods);
	    System.out.println("Number of unmatched new methods: " + newUnmatchedMethods.size());
        System.out.println(newUnmatchedMethods);
        
        Set<String> oldUnmatchedPredicates = SimpleChecks.getUnmatchedOldPredicates(oldPredExecs, newPredExecs);
		Set<String> newUnmatchedPredicates = SimpleChecks.getUnmatchedNewPredicates(oldPredExecs, newPredExecs);
		System.out.println("Number of unmatched old predicates: " + oldUnmatchedPredicates.size());
		System.out.println("Number of unmatched new predicates: " + newUnmatchedPredicates.size());
		
		//see the deviated behaviors
		CodeAnalyzer oldCoder = null;
		CodeAnalyzer newCoder = null;
		
		oldCoder = CodeAnalyzerRepository.getJMeterOldAnalyzer();
		oldCoder.buildAnalysis();
		newCoder = CodeAnalyzerRepository.getJMeterNewAnalyzer();
		newCoder.buildAnalysis();
		
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.jmeterOldHistoryDump, 
				TraceRepository.jmeterOldSig, TraceRepository.jmeterOldPredicateDump);
		ExecutionTrace newTrace = new ExecutionTrace(TraceRepository.jmeterNewHistoryDump, 
				TraceRepository.jmeterNewSig, TraceRepository.jmeterNewPredicateDump);

		Set<PredicateBehaviorAcrossVersions> matchedPreds
		    = SimpleChecks.getMatchedPredicateExecutions(oldPredExecs, newPredExecs, oldCoder, newCoder);
		System.out.println("Number of matched preds: " + matchedPreds.size());
		
		matchedPreds = SimpleChecks.rankByBehaviorChanges(matchedPreds);
		for(PredicateBehaviorAcrossVersions predBehavior : matchedPreds) {
			float degree = predBehavior.getDifferenceDegree();
			if(degree < 0.1f) {
				continue;
			}
			System.out.println(predBehavior);
			System.out.println("      diff: " + degree);
			if(predBehavior.isExecutedOnOldVersion()) {
				Set<InstructionExecInfo> set = oldTrace.getExecutedInstructionsInsidePredicate(oldCoder, predBehavior.createOldPredicateExecInfo());
				System.out.println("     executed: " + set.size());
			} else {
				System.out.println("     not executed on old version.");
			}
			
			if(predBehavior.isExecutedOnNewVersion()) {
				Set<InstructionExecInfo> set = newTrace.getExecutedInstructionsInsidePredicate(newCoder, predBehavior.createNewPredicateExecInfo());
				System.out.println("    executed: " + set.size());
			} else {
				System.out.println("     not executed on new version");
			}
			
			System.out.println();
		}
	}
	
	public void testParseJMeter_predicate_in_trace() {
		List<InstructionExecInfo> oldInstructions
		    = ExecutionTraceReader.createPredicateExecInfoInTrace(TraceRepository.jmeterOldHistoryDump,
		    		TraceRepository.jmeterOldSig);
		List<InstructionExecInfo> newInstructions
	        = ExecutionTraceReader.createPredicateExecInfoInTrace(TraceRepository.jmeterNewHistoryDump,
	        		TraceRepository.jmeterNewSig);
		System.out.println("old instruction num: " + oldInstructions.size());
		System.out.println("new instruction num: " + newInstructions.size());
		
	}
	
	public void testCheckJMeter_predicate_in_old_trace() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getJMeterOldAnalyzer();
		oldCoder.buildAnalysis();
		
		//old trace
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.jmeterOldHistoryDump, 
				TraceRepository.jmeterOldSig, TraceRepository.jmeterOldPredicateDump);
		Set<PredicateExecInfo> predSet = oldTrace.getExecutedPredicates();
		System.out.println("Num of executed predicates in old trace: " + predSet.size());
		//for each predicate, get its immediate post-dominator
		List<String> missingSSAs = new LinkedList<String>();
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = ExecutionTrace.getImmediatePostDominator(oldCoder, pred);
			if(postDomExec == null) {
				missingSSAs.add(pred.toString());
				continue;
			}
			if(pred.getIndex() == postDomExec.getIndex()) {
				System.out.println("The pred dom: " + pred + ", post dom: " + postDomExec);
				Utils.fail("");
			}
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
			
			//find all instructions between
			Set<InstructionExecInfo> set =
				oldTrace.getExecutedInstructionsBetween(pred.getMethodSig(), pred.getIndex(),
						postDomExec.getMethodSig(), postDomExec.getIndex());
			System.out.println("     Number of instructions: " + set.size());
		}
		System.out.println("------------");
		System.out.println("The missiing predicate number: " + missingSSAs.size());
		System.out.println("They are: ");
		Utils.dumpCollection(missingSSAs, System.out);
	}
	
	public void testCheckJMeter_predicate_in_new_trace() {
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getJMeterNewAnalyzer();
		newCoder.buildAnalysis();
		
		//old trace
		ExecutionTrace newTrace = new ExecutionTrace(TraceRepository.jmeterNewHistoryDump, 
				TraceRepository.jmeterNewSig, TraceRepository.jmeterNewPredicateDump);
		Set<PredicateExecInfo> predSet = newTrace.getExecutedPredicates();
		System.out.println("Num of executed predicates in new trace: " + predSet.size());
		//for each predicate, get its immediate post-dominator
		List<String> missingSSAs = new LinkedList<String>();
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = ExecutionTrace.getImmediatePostDominator(newCoder, pred);
			if(postDomExec == null) {
				missingSSAs.add(pred.toString());
				continue;
			}
			if(pred.getIndex() == postDomExec.getIndex()) {
				System.out.println("The pred dom: " + pred + ", post dom: " + postDomExec);
				Utils.fail("");
			}
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
			
			Set<InstructionExecInfo> set =
				newTrace.getExecutedInstructionsBetween(pred.getMethodSig(), pred.getIndex(),
						postDomExec.getMethodSig(), postDomExec.getIndex());
			System.out.println("     Number of instructions: " + set.size());
		}
		System.out.println("------------");
		System.out.println("The missiing predicate number: " + missingSSAs.size());
		System.out.println("They are: ");
		Utils.dumpCollection(missingSSAs, System.out);
	}
	
	//for JChord problem 1
	public void testParseJChord_predicate_SSA() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getJChordOldAnalyzer();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getJChordNewAnalyzer();
		oldCoder.buildAnalysis();
		newCoder.buildAnalysis();
		
		//use uniqueness
		SimpleChecks.unique_matching = true;
		String[] pkgs = new String[]{"chord."};
		Set<String> uniqueSet1 = CodeAnalysisUtils.findUniquelyInvokedMethods(oldCoder, pkgs);
		Set<String> uniqueSet2 = CodeAnalysisUtils.findUniquelyInvokedMethods(newCoder, pkgs);
		Set<String> uniqueIntersect = Utils.intersect(uniqueSet1, uniqueSet2);
		SimpleChecks.uniqueMethods = uniqueIntersect;
		
		Collection<PredicateExecInfo>  oldPredExecs
        = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.chordOldPredicateDump_SSA,
     		TraceRepository.chordOldSig);
	    Collection<PredicateExecInfo> newPredExecs
	       = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.chordNewPredicateDump_SSA,
	    		TraceRepository.chordNewSig);
	    Set<String> oldUnmatchedMethods = SimpleChecks.getUnmatchedOldMethods(oldPredExecs, newPredExecs);
	    Set<String> newUnmatchedMethods = SimpleChecks.getUnmatchedNewMethods(oldPredExecs, newPredExecs);
	    System.out.println("Number of unmatched old methods: " + oldUnmatchedMethods.size());
//	    System.out.println(oldUnmatchedMethods);
	    System.out.println("Number of unmatched new methods: " + newUnmatchedMethods.size());
//	    System.out.println(newUnmatchedMethods);
	    
	    Set<String> oldUnmatchedPredicates = SimpleChecks.getUnmatchedOldPredicates(oldPredExecs, newPredExecs);
		Set<String> newUnmatchedPredicates = SimpleChecks.getUnmatchedNewPredicates(oldPredExecs, newPredExecs);
		System.out.println("Number of unmatched old predicates: " + oldUnmatchedPredicates.size());
		System.out.println("Number of unmatched new predicates: " + newUnmatchedPredicates.size());
		
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.counting_ssa_old);
		ExecutionTrace newTrace = new ExecutionTrace(TraceRepository.counting_ssa_new);
		
		Map<String, Float> outputMap = new HashMap<String, Float>();
		
		Set<PredicateBehaviorAcrossVersions> matchedPreds
	        = SimpleChecks.getMatchedPredicateExecutions(oldPredExecs, newPredExecs, oldCoder, newCoder);
	    System.out.println("Number of matched preds: " + matchedPreds.size());
	    
	    matchedPreds = SimpleChecks.rankByBehaviorChanges(matchedPreds);
	    for(PredicateBehaviorAcrossVersions predBehavior : matchedPreds) {
		    float degree = predBehavior.getDifferenceDegree();
		    if(predBehavior.oldIndex == 8
		    		&& predBehavior.oldMethodSig.indexOf("chord.program.Program.<init>") != -1) {
		    	System.out.println(predBehavior);
		    	System.out.println("Jump!");
		    	return;
		    }
		    if(degree < 0.1f) {
			    continue;
		    }
		    System.out.println(predBehavior);
		    System.out.println("      diff: " + degree);
		    
		    String oldMethodSig = predBehavior.oldMethodSig;
		    int oldIndex = predBehavior.oldIndex;
		    String newMethodSig = predBehavior.newMethodSig;
		    int newIndex = predBehavior.newIndex;
		    
		    int oldInstrNum = oldTrace.getExecutedInstructions(oldMethodSig, oldIndex);
		    int newInstrNum = newTrace.getExecutedInstructions(newMethodSig, newIndex);
		    int delta = Math.abs(oldInstrNum - newInstrNum);
		    
		    float diff = degree * (float)delta;
		    
		    System.out.println("      execution diff: " + diff);
		    
		    outputMap.put(predBehavior.toString(), diff);
	    }
	    
	    outputMap = Utils.sortByValue(outputMap, false);
	    System.out.println("---------results below -----------");
	    for(String key : outputMap.keySet()) {
	    	System.out.println(key + "\n     " + outputMap.get(key));
	    }
	    
	    SimpleChecks.unique_matching = false;
		SimpleChecks.uniqueMethods = null;
	}
	
	//check the post-dominant instruction
	public void testCheckJChord_predicate_in_old_trace_SSA() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getJChordOldAnalyzer();
		oldCoder.buildAnalysis();
		
		Collection<PredicateExecInfo>  predSet
        = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.chordOldPredicateDump_SSA,
     		TraceRepository.chordOldSig);
		
		StringBuilder sb = new StringBuilder();
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = ExecutionTrace.getImmediatePostDominator(oldCoder, pred);
			if(postDomExec == null) {
				continue;
			}
			Utils.checkTrue(pred.getIndex() != postDomExec.getIndex());
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
			
			sb.append(pred.getPredicateSigInstr() + CountingTracer.COUNT_SEP + postDomExec.getPredicateSig() + Globals.lineSep);
		}
		
		Files.writeToFileNoExp(sb.toString(), HardCodingPaths.traceFile);
	}
	
	public void testCheckJChord_predicate_in_new_trace_SSA() {
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getJChordNewAnalyzer();
		newCoder.buildAnalysis();
		
		Collection<PredicateExecInfo>  predSet
        = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.chordNewPredicateDump_SSA,
     		TraceRepository.chordNewSig);
		
		StringBuilder sb = new StringBuilder();
		
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = ExecutionTrace.getImmediatePostDominator(newCoder, pred);
			if(postDomExec == null) {
				continue;
			}
			Utils.checkTrue(pred.getIndex() != postDomExec.getIndex());
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
			
			sb.append(pred.getPredicateSigInstr() + CountingTracer.COUNT_SEP + postDomExec.getPredicateSig() + Globals.lineSep);
		}
		
		Files.writeToFileNoExp(sb.toString(), HardCodingPaths.traceFile);
	}
	
	//for JChord problem 2
	//under this setting, it ranks first!
    public void testParseJChord_predicate_Print() {
    	Collection<PredicateExecInfo>  oldPredExecs
            = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.chordOldPredicateDump_Print,
     		    TraceRepository.chordOldSig);
	    Collection<PredicateExecInfo> newPredExecs
	       = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.chordNewPredicateDump_Print,
	    		TraceRepository.chordNewSig);
    	
//    	for(PredicateExecInfo info : oldPredExecs) {
//    		 if(info.getMethodSig().indexOf("chord.project.Main.run") != -1) {
// 		    	System.out.println("--> " + info);
// 		    }
//    	}
//    	if(true) {
//    		return;
//    	}
	    
	    Set<String> oldUnmatchedMethods = SimpleChecks.getUnmatchedOldMethods(oldPredExecs, newPredExecs);
	    Set<String> newUnmatchedMethods = SimpleChecks.getUnmatchedNewMethods(oldPredExecs, newPredExecs);
	    System.out.println("Number of unmatched old methods: " + oldUnmatchedMethods.size());
//	    System.out.println(oldUnmatchedMethods);
	    System.out.println("Number of unmatched new methods: " + newUnmatchedMethods.size());
//	    System.out.println(newUnmatchedMethods);
	    
	    Set<String> oldUnmatchedPredicates = SimpleChecks.getUnmatchedOldPredicates(oldPredExecs, newPredExecs);
		Set<String> newUnmatchedPredicates = SimpleChecks.getUnmatchedNewPredicates(oldPredExecs, newPredExecs);
		System.out.println("Number of unmatched old predicates: " + oldUnmatchedPredicates.size());
		System.out.println("Number of unmatched new predicates: " + newUnmatchedPredicates.size());
		
		ExecutionTrace oldTrace = new ExecutionTrace(TraceRepository.counting_print_old);
		ExecutionTrace newTrace = new ExecutionTrace(TraceRepository.counting_print_new);
		
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getJChordOldAnalyzer();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getJChordNewAnalyzer();
		oldCoder.buildAnalysis();
		newCoder.buildAnalysis();
		
		Set<PredicateBehaviorAcrossVersions> matchedPreds
	        = SimpleChecks.getMatchedPredicateExecutions(oldPredExecs, newPredExecs, oldCoder, newCoder);
	    System.out.println("Number of matched preds: " + matchedPreds.size());
	
	    Map<String, Float> outputMap = new HashMap<String, Float>();
	
	    matchedPreds = SimpleChecks.rankByBehaviorChanges(matchedPreds);
	    for(PredicateBehaviorAcrossVersions predBehavior : matchedPreds) {
		    float degree = predBehavior.getDifferenceDegree();

//   		    if(predBehavior.oldMethodSig.indexOf("chord.project.Main.run") != -1
//   		    		&& predBehavior.oldIndex == 75) {
//		    	System.out.println("--> " + predBehavior);
//		    	if(true) {
//	   	        	System.out.println("Jump out!");
//	   		        return;
//	   	        }
//		    }
		    
		    if(degree < 0.1f) {
			    continue;
		    }
		    System.out.println(predBehavior);
		    System.out.println("      diff: " + degree);
		    
		    String oldMethodSig = predBehavior.oldMethodSig;
		    int oldIndex = predBehavior.oldIndex;
		    String newMethodSig = predBehavior.newMethodSig;
		    int newIndex = predBehavior.newIndex;
		    
//		    if(oldMethodSig.indexOf("chord.project.Main.run") != -1) {
//		    	System.out.println("--> " + oldMethodSig + "#" + oldIndex);
//		    }
		    
		    int oldInstrNum = oldTrace.getExecutedInstructions(oldMethodSig, oldIndex);
		    int newInstrNum = newTrace.getExecutedInstructions(newMethodSig, newIndex);
		    int delta = Math.abs(oldInstrNum - newInstrNum);
		    
		    float diff = degree * (float)delta;
		    
		    System.out.println("      execution diff: " + diff);
		    System.out.println();
		    
		    outputMap.put(predBehavior.toString(), diff);
	    }
	    
	    outputMap = Utils.sortByValue(outputMap, false);
	    
	    System.out.println("---------results below -----------");
	    for(String key : outputMap.keySet()) {
	    	System.out.println(key + "\n     " + outputMap.get(key));
	    }
	}
	
	public void testCheckJChord_predicate_in_old_trace_Print() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getJChordOldAnalyzer();
		oldCoder.buildAnalysis();
		
		Collection<PredicateExecInfo>  predSet
        = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.chordOldPredicateDump_Print,
     		TraceRepository.chordOldSig);
		
		StringBuilder sb = new StringBuilder();
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = ExecutionTrace.getImmediatePostDominator(oldCoder, pred);
			if(postDomExec == null) {
				continue;
			}
			Utils.checkTrue(pred.getIndex() != postDomExec.getIndex());
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
			
			sb.append(pred.getPredicateSigInstr() + CountingTracer.COUNT_SEP + postDomExec.getPredicateSig() + Globals.lineSep);
		}
		
		Files.writeToFileNoExp(sb.toString(), HardCodingPaths.traceFile);
	}
	
	public void testCheckJChord_predicate_in_new_trace_Print() {
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getJChordNewAnalyzer();
		newCoder.buildAnalysis();
		
		Collection<PredicateExecInfo>  predSet
        = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.chordNewPredicateDump_Print,
     		TraceRepository.chordNewSig);
		
		StringBuilder sb = new StringBuilder();
		
		for(PredicateExecInfo pred : predSet) {
			InstructionExecInfo postDomExec = ExecutionTrace.getImmediatePostDominator(newCoder, pred);
			if(postDomExec == null) {
				continue;
			}
			Utils.checkTrue(pred.getIndex() != postDomExec.getIndex());
			System.out.println("Pred: " + pred);
			System.out.println("     post dom: " + postDomExec);
			
			sb.append(pred.getPredicateSigInstr() + CountingTracer.COUNT_SEP + postDomExec.getPredicateSig() + Globals.lineSep);
		}
		
		Files.writeToFileNoExp(sb.toString(), HardCodingPaths.traceFile);
	}
	
	//----------------parse counting files
	public void testParseCountingFiles() {
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		map = ExecutionTraceReader.parseCountingFile(TraceRepository.counting_ssa_old);
		map = Utils.sortByValue(map, false);
		for(String key : map.keySet()) {
			System.out.println(key + ", " + map.get(key));
		}
		
		map = ExecutionTraceReader.parseCountingFile(TraceRepository.counting_ssa_new);
		map = Utils.sortByValue(map, false);
		for(String key : map.keySet()) {
			System.out.println(key + ", " + map.get(key));
		}
		
		map = ExecutionTraceReader.parseCountingFile(TraceRepository.counting_print_old);
		map = Utils.sortByValue(map, false);
		for(String key : map.keySet()) {
			System.out.println(key + ", " + map.get(key));
		}
		
		map = ExecutionTraceReader.parseCountingFile(TraceRepository.counting_print_new);
		map = Utils.sortByValue(map, false);
		for(String key : map.keySet()) {
			System.out.println(key + ", " + map.get(key));
		}
	}
	
	//for the Javalanche
	public void testParseTracesForJavaLanche() {
//		SimpleChecks.useStrictMatching = false;
		Collection<String> allOldPredicates = TraceRepository.getJavalancheOldPredicateFiles();
		Collection<PredicateExecInfo> oldPreds =
			ExecutionTraceReader.createPredicateExecInfoList(allOldPredicates, TraceRepository.javalancheOldSig);
		Collection<String> allNewPredicates = TraceRepository.getJavalancheNewPredicateFiles();
		Collection<PredicateExecInfo> newPreds =
			ExecutionTraceReader.createPredicateExecInfoList(allNewPredicates, TraceRepository.javalancheNewSig);
		System.out.println("Number in old: " + oldPreds.size());
		System.out.println("Number in new: " + newPreds.size());
		
		Set<String> oldUnmatchedPredicates = SimpleChecks.getUnmatchedOldPredicates(oldPreds, newPreds);
		Set<String> newUnmatchedPredicates = SimpleChecks.getUnmatchedNewPredicates(oldPreds, newPreds);
		System.out.println("Number of unmatched old predicates: " + oldUnmatchedPredicates.size());
		System.out.println("Number of unmatched new predicates: " + newUnmatchedPredicates.size());
		
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getJavalancheOldAnalyzer();
		oldCoder.buildAnalysis();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getJavalancheNewAnalyzer();
		newCoder.buildAnalysis();
		
		String oldMergedTrace = TraceRepository.getJavalancheMergedOldTraceFile();
		ExecutionTrace oldTrace = new ExecutionTrace(oldMergedTrace,
				TraceRepository.javalancheOldSig, null);
		
		String newMergedTrace = TraceRepository.getJavalancheMergedNewTraceFile();
		ExecutionTrace newTrace = new ExecutionTrace(newMergedTrace, 
				TraceRepository.javalancheNewSig, null);
		
		Set<PredicateBehaviorAcrossVersions> matchedPreds
	        = SimpleChecks.getMatchedPredicateExecutions(oldPreds, newPreds, oldCoder, newCoder);
	    System.out.println("Number of matched preds: " + matchedPreds.size());
	    
	    matchedPreds = SimpleChecks.rankByBehaviorChanges(matchedPreds);
	    
	    Map<String, Float> diffMap = new HashMap<String, Float>();
	    
		for(PredicateBehaviorAcrossVersions predBehavior : matchedPreds) {
			float degree = predBehavior.getDifferenceDegree();
			if(degree < 0.1f) {
				continue;
			}
			System.out.println(predBehavior);
			System.out.println("      diff: " + degree);
			
			int delta = 0;
			if(predBehavior.isExecutedOnOldVersion()) {
				Set<InstructionExecInfo> set = oldTrace.getExecutedInstructionsInsidePredicate(oldCoder, predBehavior.createOldPredicateExecInfo());
				System.out.println("     executed: " + set.size());
				delta = delta + set.size();
			} else {
				System.out.println("     not executed on old version.");
			}
			
			if(predBehavior.isExecutedOnNewVersion()) {
				Set<InstructionExecInfo> set = newTrace.getExecutedInstructionsInsidePredicate(newCoder, predBehavior.createNewPredicateExecInfo());
				System.out.println("    executed: " + set.size());
				delta = delta - set.size();
			} else {
				System.out.println("     not executed on new version");
			}
			
			diffMap.put(predBehavior.toString(), degree*(Math.abs(delta)));
			diffMap = Utils.sortByValue(diffMap, false);
			
			System.out.println();
		}
		
		System.out.println("===================");
		
		for(String key : diffMap.keySet()) {
			System.out.println(key);
			System.out.println("    -> " + diffMap.get(key));
			System.out.println();
		}
	}
	
	public void testCheckJavalanche_old_version() {
		Collection<PredicateExecInfo> oldPreds = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.getJavalancheOldPredicateFiles(),
				TraceRepository.javalancheOldSig);
		CodeAnalyzer coder = CodeAnalyzerRepository.getJavalancheOldAnalyzer();
		coder.buildAnalysis();
		int count = 0;
		int matchedCount = 0;
		Set<String> nodes = new HashSet<String>();
		for(PredicateExecInfo pred : oldPreds) {
			SSAInstruction ssa = coder.getInstruction(pred.getMethodSig(), pred.getIndex());
			if(ssa == null) {
				count++;
//				System.err.println(pred);
			}
			CGNode node = WALAUtils.lookupMatchedCGNode(coder.getCallGraph(), pred.getMethodSig());
			if(node == null) {
				nodes.add(pred.getMethodSig());
//				System.out.println(node);
			}
			if(pred.getMethodSig().indexOf("Junit3MutationTestDriver") != -1
					|| pred.getMethodSig().indexOf("SingleTestResult") != -1
					|| pred.getMethodSig().indexOf("MutationTestDriver") != -1
					|| pred.getMethodSig().indexOf("TestSuiteUtil") != -1
					) {
				System.err.println(ssa);
				System.err.println("    " + pred);
				matchedCount++;
			}
		}
		System.out.println("The missing number: " + count);
		System.out.println("The missing methods: " + nodes.size());
		System.out.println("Matched count: " + matchedCount);
		assertEquals(56, matchedCount);
	}
	
	public void testCheckJavalanche_new_version() {
		Collection<PredicateExecInfo> oldPreds = ExecutionTraceReader.createPredicateExecInfoList(TraceRepository.getJavalancheNewPredicateFiles(),
				TraceRepository.javalancheNewSig);
		CodeAnalyzer coder = CodeAnalyzerRepository.getJavalancheNewAnalyzer();
		coder.buildAnalysis();
		int count = 0;
		int matchedCount = 0;
		Set<String> nodes = new HashSet<String>();
		for(PredicateExecInfo pred : oldPreds) {
			SSAInstruction ssa = coder.getInstruction(pred.getMethodSig(), pred.getIndex());
			if(ssa == null) {
				count++;
//				System.err.println(pred);
			}
			CGNode node = WALAUtils.lookupMatchedCGNode(coder.getCallGraph(), pred.getMethodSig());
			if(node == null) {
				nodes.add(pred.getMethodSig());
//				System.out.println(node);
			}
			if(pred.getMethodSig().indexOf("TestMessage") != -1
					|| pred.getMethodSig().indexOf("SingleTestResult") != -1
					|| pred.getMethodSig().indexOf("MutationTestDriver") != -1
					|| pred.getMethodSig().indexOf("TestSuiteUtil") != -1) {
				System.err.println(ssa);
				System.err.println("    " + pred);
				matchedCount++;
			}
		}
		System.out.println("The missing number: " + count);
		System.out.println("The missing methods: " + nodes.size());
		System.out.println("Matched count: " + matchedCount);
		assertEquals(68, matchedCount);
	}
	
	public void testFileMergingInJavalanche() {
		String oldMergedTrace = TraceRepository.getJavalancheMergedOldTraceFile();
	}
	
	//----------------see file size
	public void testFileSize_for_experiment() {
		new ExecutionTrace(TraceRepository.randoopOldHistoryDump,
				TraceRepository.randoopOldSig, TraceRepository.randoopOldPredicateDump);
		new ExecutionTrace(TraceRepository.randoopNewHistoryDump,
				TraceRepository.randoopNewSig, TraceRepository.randoopNewPredicateDump);
		new ExecutionTrace(TraceRepository.synopticOldHistoryDump,
				TraceRepository.synopticOldSig, TraceRepository.synopticOldPredicateDump);
		new ExecutionTrace(TraceRepository.synopticNewHistoryDump,
				TraceRepository.synopticNewSig, TraceRepository.synopticNewPredicateDump);
		new ExecutionTrace(TraceRepository.wekaOldHistoryDump,
				TraceRepository.wekaOldSig, TraceRepository.wekaOldPredicateDump);
		new ExecutionTrace(TraceRepository.wekaNewHistoryDump,
				TraceRepository.wekaNewSig, TraceRepository.wekaNewPredicateDump);
		new ExecutionTrace(TraceRepository.jmeterOldHistoryDump,
				TraceRepository.jmeterOldSig, TraceRepository.jmeterOldPredicateDump);
		new ExecutionTrace(TraceRepository.jmeterNewHistoryDump,
				TraceRepository.jmeterNewSig, TraceRepository.jmeterNewPredicateDump);
	}
	
	public void testMergePredicateInJavalanche() {
		Collection<String> oldFiles = TraceRepository.getJavalancheOldPredicateFiles();
		String mergedFile = TraceRepository.oldPredicateMerged;
		mergePredicates(oldFiles, mergedFile);
		
		mergedFile = TraceRepository.newPredicateMerged;
		Collection<String> newFiles = TraceRepository.getJavalancheNewPredicateFiles();
		mergePredicates(newFiles, mergedFile);
	}
	
	public static void mergePredicates(Collection<String> files, String mergedFile) {
		Map<String, Integer> freqMap = new LinkedHashMap<String, Integer>();
		Map<String, Integer> evalMap = new LinkedHashMap<String, Integer>();
		for(String file : files) {
			List<String> lines = Files.readWholeNoExp(file);
			for(String line : lines) {
				if(line.trim().equals("")) {
					continue;
				}
				//parse line
				String[] splits = line.split(EfficientTracer.PRED_SEP);
				Utils.checkTrue(splits.length == 2);
				String key = splits[0];
				String[] results = splits[1].split(EfficientTracer.EVAL_SEP);
				Utils.checkTrue(results.length == 2);
				Integer freq = Integer.parseInt(results[0]);
				Integer eval = Integer.parseInt(results[1]);
				if(!freqMap.containsKey(key)) {
					Utils.checkTrue(!evalMap.containsKey(key));
					freqMap.put(key, freq);
					evalMap.put(key, eval);
				} else {
					freqMap.put(key, freq + freqMap.get(key));
					if(evalMap.containsKey(key)) {
						evalMap.put(key, eval + evalMap.get(key));
					} else {
						evalMap.put(key, eval);
					}
				}
			}
		}
		//write to the merged file
		StringBuilder sb = new StringBuilder();
		for(String key : freqMap.keySet()) {
			int freq = freqMap.get(key);
			int eval = evalMap.containsKey(key) ? evalMap.get(key) : 0;
			sb.append(key);
			sb.append(EfficientTracer.PRED_SEP);
			sb.append(freq);
			sb.append(EfficientTracer.EVAL_SEP);
			sb.append(eval);
			sb.append(Globals.lineSep);
		}
		Files.writeToFileNoExp(sb.toString(), mergedFile);
	}
}