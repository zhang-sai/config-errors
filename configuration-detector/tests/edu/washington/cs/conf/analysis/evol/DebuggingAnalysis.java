package edu.washington.cs.conf.analysis.evol;

import java.util.List;
import java.util.Set;

import plume.Pair;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.evol.experiments.RootCauses;
import edu.washington.cs.conf.util.AnalysisDebugger;
import edu.washington.cs.conf.util.WALAUtils;
import junit.framework.TestCase;

public class DebuggingAnalysis extends TestCase {

	public void testDebugSynoptic() {
		CodeAnalyzer oldAnalyzer = CodeAnalyzerRepository.getSynopticOldAnalyzer();
		CodeAnalyzer newAnalyzer = CodeAnalyzerRepository.getSynopticNewAnalyzer();
		String oldTraceFile = TraceRepository.synopticOldTrace;
		String newTraceFile = TraceRepository.synopticNewTrace;
		AnalysisScope scope = AnalysisScopeRepository.createSynopticScope();
		AnalysisCache cache = new AnalysisCache(oldAnalyzer, newAnalyzer, scope);
		
		oldAnalyzer.buildAnalysis();
		newAnalyzer.buildAnalysis();
		
		AnalysisDebugger debugger = new AnalysisDebugger(oldAnalyzer, newAnalyzer, scope, cache);
		
		String methodSig = "synoptic.main.Main.createInitialPartitionGraph()Lsynoptic/model/PartitionGraph;";
		List<CGNode> nodes = debugger.getMatchedNodesInNewVersion(methodSig);
		System.out.println(nodes);
		
		Set<Pair<SSAInstruction, SSAInstruction>> ssa = debugger.getMatchedInstructions(methodSig, methodSig);
		for(Pair<SSAInstruction, SSAInstruction> p : ssa) {
			System.out.println(p.a + "  ==> ");
			System.out.println("   " + p.b);
		}
		
		CGNode oldNode = WALAUtils.lookupMatchedCGNode(oldAnalyzer.getCallGraph(), methodSig);
		WALAUtils.printCFG(oldNode);
		
		System.out.println("--------- ");
		
		CGNode newNode = WALAUtils.lookupMatchedCGNode(newAnalyzer.getCallGraph(), methodSig);
		WALAUtils.printCFG(newNode);
	}
	
	public void testSSAMethodInChord() {
		CodeAnalyzer coder = CodeAnalyzerRepository.getJChordNewAnalyzer();
		coder.buildAnalysis();
		CGNode node = WALAUtils.lookupMatchedCGNode(coder.getCallGraph(), RootCauses.chordMethod_Print);
		WALAUtils.printAllIRsWithIndices(node);
//		System.out.println(WALAUtils.getInstruction(node, RootCauses.chordMatchedIndex_SSA));
	}
}
