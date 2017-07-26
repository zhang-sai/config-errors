package edu.washington.cs.conf.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import plume.Pair;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.evol.AnalysisCache;
import edu.washington.cs.conf.analysis.evol.AnalysisScope;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzer;
import edu.washington.cs.conf.analysis.evol.FineGrainedPredicateMatcher;
import edu.washington.cs.conf.analysis.evol.experimental.MethodMatchingLogics;

public class AnalysisDebugger {

	public final CodeAnalyzer oldAnalyzer;
	public final CodeAnalyzer newAnalyzer;
	public final AnalysisScope scope;
	public final AnalysisCache cache;
	
	public AnalysisDebugger(CodeAnalyzer oldAnalyzer, CodeAnalyzer newAnalyzer,
			AnalysisScope scope, AnalysisCache cache) {
		this.oldAnalyzer = oldAnalyzer;
		this.newAnalyzer = newAnalyzer;
		this.scope = scope;
		this.cache = cache;
	}
	
	public List<CGNode> getMatchedNodesInNewVersion(String methodSig) {
		MethodMatchingLogics logics = new MethodMatchingLogics(oldAnalyzer, newAnalyzer,
				scope, cache);
		return logics.getMatchedMethods(methodSig);
	}
	
	public Set<Pair<SSAInstruction, SSAInstruction>> getMatchedInstructions(String oldMethodSig, String newMethodSig) {
		FineGrainedPredicateMatcher matcher = new FineGrainedPredicateMatcher(
				oldAnalyzer.getCallGraph(), newAnalyzer.getCallGraph(), scope, cache);
		CGNode oldNode = WALAUtils.lookupMatchedCGNode(oldAnalyzer.getCallGraph(), oldMethodSig);
		CGNode newNode = WALAUtils.lookupMatchedCGNode(newAnalyzer.getCallGraph(), newMethodSig);
		return matcher.createInstructionMap(oldNode, newNode);
	}
}