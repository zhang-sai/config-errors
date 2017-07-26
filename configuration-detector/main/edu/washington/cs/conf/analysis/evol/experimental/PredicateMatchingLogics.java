package edu.washington.cs.conf.analysis.evol.experimental;

import java.util.LinkedList;
import java.util.List;

import plume.Pair;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.evol.AnalysisCache;
import edu.washington.cs.conf.analysis.evol.AnalysisScope;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzer;
import edu.washington.cs.conf.analysis.evol.FineGrainedPredicateMatcher;
import edu.washington.cs.conf.analysis.evol.PredicateMatcher;
import edu.washington.cs.conf.util.Log;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

@Deprecated
public class PredicateMatchingLogics {
	
	public static boolean USE_FINE_GRAINED = true;
	
	public final CodeAnalyzer oldAnalyzer;
	public final CodeAnalyzer newAnalyzer;
	
	public final PredicateMatcher matcher;
	public final FineGrainedPredicateMatcher fineMatcher;
	public final AnalysisScope scope;
	public final AnalysisCache cache;
	
	public PredicateMatchingLogics(CodeAnalyzer oldAnalyzer, CodeAnalyzer newAnalyzer,
			AnalysisScope scope, AnalysisCache cache) {
		Utils.checkNotNull(oldAnalyzer);
		Utils.checkNotNull(newAnalyzer);
		Utils.checkNotNull(scope);
		Utils.checkNotNull(cache);
		this.oldAnalyzer = oldAnalyzer;
		this.newAnalyzer = newAnalyzer;
		this.scope = scope;
		this.matcher = new PredicateMatcher(oldAnalyzer.getCallGraph(), newAnalyzer.getCallGraph());
		this.fineMatcher = new FineGrainedPredicateMatcher(oldAnalyzer.getCallGraph(),
				newAnalyzer.getCallGraph(), scope, cache);
		this.cache = cache;
	}
	
	public List<Pair<SSAInstruction, CGNode>> getMatchedPredicates(String methodSig, int index) {
		CGNode oldNode = WALAUtils.lookupMatchedCGNode(oldAnalyzer.getCallGraph(), methodSig);
		if(oldNode == null) {
			System.err.println("In predicate matching, No node corresponding to: " + methodSig);
			return new LinkedList<Pair<SSAInstruction, CGNode>>();
		}
		MethodMatchingLogics mmLogics = new MethodMatchingLogics(this.oldAnalyzer, this.newAnalyzer,
				this.scope, this.cache);
		List<CGNode> matchedNewNodes = mmLogics.getMatchedMethods(oldNode);
		
		List<Pair<SSAInstruction, CGNode>> matchedPredicates = new LinkedList<Pair<SSAInstruction, CGNode>>();
		SSAInstruction oldSSA = this.matcher.getPredicateInOldCG(methodSig, index);
		if(oldSSA == null) {
			return matchedPredicates;
		}
		Log.logln("    Number of matched nodes: " + matchedNewNodes.size());
		for(CGNode newNode : matchedNewNodes) {
			Log.logln("     -> " + newNode);
			List<SSAInstruction> ssas = null;
			if(USE_FINE_GRAINED) {
				ssas = this.fineMatcher.matchInstructionInNewCG(oldNode, newNode, oldSSA);
			} else {
			    ssas = this.matcher.matchPredicateInNewCG(oldNode, newNode, oldSSA);
			}
			List<Pair<SSAInstruction, CGNode>> pairList = new LinkedList<Pair<SSAInstruction, CGNode>>();
			for(SSAInstruction ssa : ssas) {
				pairList.add(new Pair<SSAInstruction, CGNode>(ssa, newNode));
			}
			matchedPredicates.addAll(pairList);
		}
		
		return matchedPredicates;
	}
}