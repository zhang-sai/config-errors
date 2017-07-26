package edu.washington.cs.conf.analysis.evol.experimental;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import plume.Pair;

import edu.washington.cs.conf.analysis.evol.AnalysisCache;
import edu.washington.cs.conf.analysis.evol.AnalysisScope;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzer;
import edu.washington.cs.conf.analysis.evol.ExecutionTraceReader;
import edu.washington.cs.conf.analysis.evol.PredicateMetrics;
import edu.washington.cs.conf.util.Log;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

/**
 * This class is used in a relatively isolated way. Mainly used
 * for testing purpose.
 * */

@Deprecated
public class PredicateExecInfoAnalyzer {
	
	enum Metrics{Ratio, Behavior}

	public final CodeAnalyzer oldAnalyzer;
	public final CodeAnalyzer newAnalyzer;
	public final String oldTraceFile;
	public final String newTraceFile;
	
	private Collection<PredicateExecInfo> oldPredicates = null;
	private Collection<PredicateExecInfo> newPredicates = null;
	
	private final PredicateMatchingLogics predicateMatcher;
	
	private Metrics metric = Metrics.Behavior;
	
	public PredicateExecInfoAnalyzer(CodeAnalyzer oldAnalyzer, CodeAnalyzer newAnalyzer,
			AnalysisScope scope, AnalysisCache cache,
			String oldTraceFile, String newTraceFile) {
		this.oldAnalyzer = oldAnalyzer;
		this.newAnalyzer = newAnalyzer;
		this.oldTraceFile = oldTraceFile;
		this.newTraceFile = newTraceFile;
		this.predicateMatcher = new PredicateMatchingLogics(oldAnalyzer, newAnalyzer, scope, cache);
	}
	
	public void readPredicates() {
		this.oldPredicates = ExecutionTraceReader.createPredicateExecInfoList(oldTraceFile);
		this.newPredicates = ExecutionTraceReader.createPredicateExecInfoList(newTraceFile);
	}
	
	public void setMetrics(Metrics m) {
		Utils.checkNotNull(m);
		this.metric = m;
	}

	//check each predicate from old traces with
	public Map<Pair<PredicateExecInfo, PredicateExecInfo>, Float> findBehaviorDeviatedPredicatePairs() {
		if(oldPredicates == null || newPredicates == null) {
			this.readPredicates();
			Utils.checkNotNull(this.oldPredicates);
			Utils.checkNotNull(this.newPredicates);
		}
		//the return maps
		Map<Pair<PredicateExecInfo, PredicateExecInfo>, Float> pairScores
		    = new LinkedHashMap<Pair<PredicateExecInfo, PredicateExecInfo>, Float>();
		
		for(PredicateExecInfo oldPredicate : oldPredicates) {
			Log.logln(" => finding matched predicate for: " + oldPredicate);
			//get the matched pairs of a predicate
			List<Pair<SSAInstruction, CGNode>> newPredicatePairList
			    = this.predicateMatcher.getMatchedPredicates(oldPredicate.getMethodSig(),oldPredicate.getIndex());
			//iterate through each matched predicate
			Log.logln("   all matched predicate: " + newPredicatePairList.size());
			for(Pair<SSAInstruction, CGNode> newPredicatePair : newPredicatePairList) {
				PredicateExecInfo newPredicate =
					findPredicate(this.newPredicates, newPredicatePair.b, newPredicatePair.a);
				if(newPredicate == null) {
					System.err.println("No new predicate matching for: " + oldPredicate);
					Log.logln("  -- No new predicate matching for: " + oldPredicate);
					continue;
				}
				Log.logln(" find predicate: " + newPredicate);
				Utils.checkNotNull(newPredicate);
				float deviationScore = 0.0f;
				if(this.metric == Metrics.Behavior) {
					deviationScore = PredicateMetrics.computeBehaviorDiff(oldPredicate, newPredicate);
				} else if (this.metric == Metrics.Ratio) {
					deviationScore = PredicateMetrics.computeTrueRatioDiff(oldPredicate, newPredicate);
				} else {
					throw new Error("Error value: " + this.metric);
				}
				Log.logln("  deviation score. " + deviationScore);
				//put to the map
				Pair<PredicateExecInfo, PredicateExecInfo> predicatePair
				    = new Pair<PredicateExecInfo, PredicateExecInfo>(oldPredicate, newPredicate);
				pairScores.put(predicatePair, deviationScore);
			}
		}
		
		return pairScores;
	}
	
	public static PredicateExecInfo findPredicate(Collection<PredicateExecInfo> coll,
			CGNode hostNode, SSAInstruction ssa) {
		String hostMethodSig = hostNode.getMethod().getSignature();
		int index = WALAUtils.getInstructionIndex(hostNode, ssa);
		Utils.checkTrue(index != -1, "The host method does not contain the ssa.");
		//iterate through the predicate exec info pool, and find the matched ssa
		for(PredicateExecInfo predicateExec : coll) {
			String methodSig = predicateExec.getMethodSig();
			int predIndex = predicateExec.getIndex();
			if(hostMethodSig.equals(methodSig) && index == predIndex) {
				return predicateExec;
			}
		}
		return null;
	}
}