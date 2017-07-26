package edu.washington.cs.conf.analysis.evol;

import java.util.List;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.evol.experiments.RootCauses;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

import junit.framework.TestCase;

public class TestPredicateMatcher extends TestCase {

	public void testMatchSynopticPredicate() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getSynopticOldAnalyzer();
		oldCoder.buildAnalysis();
		
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getSynopticNewAnalyzer();
		newCoder.buildAnalysis();
		
		CGNode oldNode1 = WALAUtils.lookupMatchedCGNode(oldCoder.getCallGraph(), RootCauses.synopticMethod1);
		CGNode newNode1 = WALAUtils.lookupMatchedCGNode(newCoder.getCallGraph(), RootCauses.synopticMethod1);
		Utils.checkNotNull(oldNode1);
		Utils.checkNotNull(newNode1);
		
//		WALAUtils.printCFG(oldNode1);
//		System.out.println("========");
//		WALAUtils.printCFG(newNode1);
//		System.out.println("");
		System.out.println(WALAUtils.getInstruction(oldNode1, RootCauses.synopticIndexOld1));
		System.out.println(WALAUtils.getInstruction(newNode1, RootCauses.matchedSynopticIndex1));
		
		
		CGNode oldNode2 = WALAUtils.lookupMatchedCGNode(oldCoder.getCallGraph(), RootCauses.synopticMethod2);
		CGNode newNode2 = WALAUtils.lookupMatchedCGNode(newCoder.getCallGraph(), RootCauses.synopticMethod2);
		Utils.checkNotNull(oldNode2);
		Utils.checkNotNull(newNode2);
		
		System.out.println(WALAUtils.getInstruction(oldNode2, RootCauses.synopticIndexOld2));
		System.out.println(WALAUtils.getInstruction(oldNode2, RootCauses.matchedSynopticIndex2));
		
//		WALAUtils.printCFG(oldNode2);
//		System.out.println("=======");
//		WALAUtils.printCFG(newNode2);
		
//		
//		System.out.println(oldNode.getIR().getInstructions()[RootCauses.synopticIndexOld]);
//		System.out.println(newNode.getIR().getInstructions()[RootCauses.matchedSynopticIndex]);
//		
//		PredicateMatcher matcher = new PredicateMatcher(oldCoder.getCallGraph(), newCoder.getCallGraph());
//		SSAInstruction oldSSA = WALAUtils.getInstruction(oldNode, RootCauses.synopticIndexOld);
//		Utils.checkNotNull(oldSSA);
//		
//		List<SSAInstruction> ssas = matcher.matchPredicateInNewCG(oldNode, newNode, oldSSA);
//		System.out.println("SSA list size: " + ssas.size());
//		for(SSAInstruction ssa : ssas) {
//			System.out.println("   " + ssa + ", index: " + WALAUtils.getInstructionIndex(newNode, ssa));
//		}
//		
//		assertEquals(1, ssas.size());
	}
	
	public void testMatchWekaPredicate() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getWekaOldAnalyzer();
		oldCoder.buildAnalysis();
		
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getWekaNewAnalyzer();
		newCoder.buildAnalysis();
		
		CGNode oldNode = WALAUtils.lookupMatchedCGNode(oldCoder.getCallGraph(),
				RootCauses.wekaMethod);
		CGNode newNode = WALAUtils.lookupMatchedCGNode(newCoder.getCallGraph(),
				RootCauses.wekaMethod);
		
		System.out.println(oldNode.getIR().getInstructions()[RootCauses.wekaIndexOld]);
		System.out.println(newNode.getIR().getInstructions()[RootCauses.matchedWekaIndex]);
		
		assertEquals(RootCauses.wekaIndexOld, RootCauses.matchedWekaIndex);
	}
	
	public void testMatchRandoopPredicate() {
		CodeAnalyzer coder121 = CodeAnalyzerRepository.getRandoop121Analyzer();
		coder121.buildAnalysis();
		
		CodeAnalyzer coder132 = CodeAnalyzerRepository.getRandoop132Analyzer();
		coder132.buildAnalysis();
		
		String methodSig = RootCauses.randoopMethod;
		int index = RootCauses.randoopIndexOld;
		
		PredicateMatcher matcher = new PredicateMatcher(coder121.getCallGraph(), coder132.getCallGraph());
		CGNode oldNode = matcher.getMethodInOldCG(methodSig);
		System.out.println(oldNode);
		
		CGNode newNode = WALAUtils.lookupMatchedCGNode(coder132.getCallGraph(), oldNode.getMethod().getSignature());
		
		System.out.println(newNode);
		
		SSAInstruction oldSsa = matcher.getPredicateInOldCG(methodSig, index);
		System.out.println(oldSsa);
		
		List<SSAInstruction> ssalist = matcher.matchPredicateInNewCG(oldNode, newNode, oldSsa);
		System.out.println(ssalist);
		assertEquals("[conditional branch(eq) 7,8]", ssalist.toString());
		
		int matchedIndex = WALAUtils.getInstructionIndex(newNode, ssalist.get(0));
		System.out.println(matchedIndex);
		assertEquals(RootCauses.matchedRandoopIndex, matchedIndex);
	}
	
	public void testMatchJChordPredicate_SSA() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getJChordOldAnalyzer();
		oldCoder.buildAnalysis();
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getJChordNewAnalyzer();
		newCoder.buildAnalysis();
		
		PredicateMatcher matcher = new PredicateMatcher(oldCoder.getCallGraph(),
				newCoder.getCallGraph());
		
		String methodSig = RootCauses.chordMethod_SSA;
		int index = RootCauses.chordOldIndex_SSA;
		CGNode oldNode = matcher.getMethodInOldCG(methodSig);
		System.out.println(oldNode);
		
		SSAInstruction oldSSA = WALAUtils.getInstruction(oldNode, index);
		
        CGNode newNode = WALAUtils.lookupMatchedCGNode(newCoder.getCallGraph(),
        		oldNode.getMethod().getSignature());
		System.out.println(newNode);
		
		List<SSAInstruction> ssalist = matcher.matchPredicateInNewCG(oldNode, newNode, oldSSA);
		
		System.out.println("Number of matched instructions: " + ssalist.size());
		System.out.println(ssalist);
		
		SSAInstruction newSSA = WALAUtils.getInstruction(newNode, RootCauses.chordMatchedIndex_SSA);
		System.out.println(RootCauses.chordMatchedIndex_SSA + newSSA.toString());
		
		//use the unique method matching
		String[] pkgs = new String[]{"chord."};
		Set<String> uniqueSet1 = CodeAnalysisUtils.findUniquelyInvokedMethods(oldCoder, pkgs);
		Set<String> uniqueSet2 = CodeAnalysisUtils.findUniquelyInvokedMethods(newCoder, pkgs);
		Set<String> uniqueIntersect = Utils.intersect(uniqueSet1, uniqueSet2);
		
		ssalist = matcher.matchPredicateInNewCG(oldNode, newNode, oldSSA, uniqueIntersect);
		System.out.println("use uniqueness: ");
		System.out.println(ssalist);
		
		assertEquals(1, ssalist.size());
		assertEquals(RootCauses.chordMatchedIndex_SSA, WALAUtils.getInstructionIndex(newNode, ssalist.get(0)));
		assertEquals("conditional branch(eq) 14,11", ssalist.get(0));
	}
	
	public void testMatchJChordPredicate_Print() {
		CodeAnalyzer oldCoder = CodeAnalyzerRepository.getJChordOldAnalyzer();
		oldCoder.buildAnalysis();
		
		CodeAnalyzer newCoder = CodeAnalyzerRepository.getJChordNewAnalyzer();
		newCoder.buildAnalysis();
		
		PredicateMatcher matcher = new PredicateMatcher(oldCoder.getCallGraph(),
				newCoder.getCallGraph());
		
		String methodSig = RootCauses.chordMethod_Print;
		int index = RootCauses.chordOldIndex_Print;
		CGNode oldNode = matcher.getMethodInOldCG(methodSig);
		System.out.println(oldNode);
		
		SSAInstruction oldSSA = WALAUtils.getInstruction(oldNode, index);
		
        CGNode newNode = WALAUtils.lookupMatchedCGNode(newCoder.getCallGraph(),
        		oldNode.getMethod().getSignature());
		System.out.println(newNode);
		
		List<SSAInstruction> ssalist = matcher.matchPredicateInNewCG(oldNode, newNode, oldSSA);
		System.out.println(ssalist);
		
		for(SSAInstruction ssa : ssalist) {
			System.out.println(ssa + "@" + WALAUtils.getInstructionIndex(newNode, ssa));
		}
		
		assertEquals(0, ssalist.size());
		
//		SSAInstruction newSSA = WALAUtils.getInstruction(newNode, RootCauses.chordMatchedIndex_Print);
//		System.out.println(RootCauses.chordMatchedIndex_Print + newSSA.toString());
	}
}
