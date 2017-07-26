package edu.washington.cs.conf.analysis.evol;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import plume.Pair;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class FineGrainedPredicateMatcher {

	// build a matching between each instruction, and then caching it
	//the set is a pair of matched <old ssa , new ssa>
	private Map<Pair<CGNode, CGNode>, Set<Pair<SSAInstruction, SSAInstruction>>> cache
	    = new LinkedHashMap<Pair<CGNode, CGNode>, Set<Pair<SSAInstruction, SSAInstruction>>>();
	
	private int lookahead = MethodMatcher.default_la;
	private float threshold = MethodMatcher.default_threshold;
	
	public final CallGraph oldGraph;
	public final CallGraph newGraph;
	public final AnalysisScope scope;
	public final AnalysisCache analysisCache;
	
	public FineGrainedPredicateMatcher(CallGraph oldGraph, CallGraph newGraph,
			AnalysisScope scope, AnalysisCache cache) {
		Utils.checkNotNull(oldGraph);
		Utils.checkNotNull(newGraph);
		Utils.checkNotNull(scope);
		Utils.checkNotNull(cache);
		this.oldGraph = oldGraph;
		this.newGraph = newGraph;
		this.scope = scope;
		this.analysisCache = cache;
	}
	
	public void setLookAhead(int la) {
		Utils.checkTrue(la > 0);
		this.lookahead = la;
	}
	
	public void setThreshold(float threshold) {
		Utils.checkTrue(threshold >= 0 && threshold <= 1);
		this.threshold = threshold;
	}

	public List<SSAInstruction> matchInstructionInNewCG(CGNode oldNode,
			CGNode newNode, SSAInstruction oldSSA) {
		Utils.checkTrue(CodeAnalysisUtils.isPredicateInstruction(oldSSA), "Other ssa: " + oldSSA
				+ " is not supported.");
		Set<Pair<SSAInstruction, SSAInstruction>> pairSet = this.findCachedMap(oldNode, newNode);
		if (pairSet == null) {
			pairSet = this.createInstructionMap(oldNode, newNode);
			cache.put(new Pair<CGNode, CGNode>(oldNode, newNode), pairSet);
		}
		//get the matched ssa instructions
		List<SSAInstruction> matchedSSAs = new LinkedList<SSAInstruction>();
		for(Pair<SSAInstruction, SSAInstruction> pair : pairSet) {
			if(pair.a == oldSSA) {
				matchedSSAs.add(pair.b);
			}
		}
		return matchedSSAs;
	}

	private Set<Pair<SSAInstruction, SSAInstruction>> findCachedMap(CGNode oldNode,
			CGNode newNode) {
		for (Pair<CGNode, CGNode> p : cache.keySet()) {
			if (p.a == oldNode && p.b == newNode) {
				return cache.get(p);
			}
		}
		return null;
	}

	// use a JDiff like algorithm
	//FIXME should make it private
	public Set<Pair<SSAInstruction, SSAInstruction>> createInstructionMap(CGNode oldNode, CGNode newNode) {
		MethodMatcher matcher = new MethodMatcher(this.oldGraph, this.newGraph, this.scope, this.analysisCache);
		Set<Pair<ISSABasicBlock, ISSABasicBlock>> matchedBlocks
		    = matcher.createMatchedBlocks(oldNode, newNode, this.threshold, this.lookahead);
		
		//compute the matched ssa
		Set<Pair<SSAInstruction, SSAInstruction>> matchedSSAs =
			new LinkedHashSet<Pair<SSAInstruction, SSAInstruction>>();
		for(Pair<ISSABasicBlock, ISSABasicBlock> matchedBlock : matchedBlocks) {
			ISSABasicBlock oldBB = matchedBlock.a;
			ISSABasicBlock newBB = matchedBlock.b;
//			SSAInstruction[] oldSSAs = WALAUtils.getAllIRs(oldBB).toArray(new SSAInstruction[0]);
//			SSAInstruction[] newSSAs = WALAUtils.getAllIRs(newBB).toArray(new SSAInstruction[0]);
			//use LCS for matching
			if(WALAUtils.getAllIRs(oldBB).isEmpty()) {
				//skip empty block
				continue;
			}
			SSAInstruction oldLast = oldBB.getLastInstruction();
			SSAInstruction newLast = newBB.getLastInstruction();
			if(CodeAnalysisUtils.isPredicateInstruction(oldLast)
				&& CodeAnalysisUtils.isPredicateInstruction(newLast)) {
				matchedSSAs.add(new Pair<SSAInstruction, SSAInstruction>(oldLast, newLast));
			}
		}
		
		return matchedSSAs;
	}
}