package edu.washington.cs.conf.analysis.evol;

import java.util.List;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;
import junit.framework.TestCase;

public class TestFineGrainedPredicateMatcher extends TestCase {

	public void testMatchRandoopPredicate() {
		CodeAnalyzer oldAnalyzer = CodeAnalyzerRepository.getRandoop121Analyzer();
		oldAnalyzer.buildAnalysis();
		CodeAnalyzer newAnalyzer = CodeAnalyzerRepository.getRandoop132Analyzer();
		newAnalyzer.buildAnalysis();
		AnalysisScope scope = AnalysisScopeRepository.createRandoopScore();
		AnalysisCache cache = AnalysisCache.createCache(oldAnalyzer, newAnalyzer, scope);
		
		FineGrainedPredicateMatcher matcher
		    = new FineGrainedPredicateMatcher(oldAnalyzer.getCallGraph(),
		    		newAnalyzer.getCallGraph(), scope, cache);
		
		String methodSig = "randoop.util.Reflection.canBeUsedAs(Ljava/lang/Class;Ljava/lang/Class;)Z";
		CGNode oldNode = WALAUtils.lookupMatchedCGNode(oldAnalyzer.getCallGraph(), methodSig);
		CGNode newNode = WALAUtils.lookupMatchedCGNode(newAnalyzer.getCallGraph(), methodSig);
		Utils.checkNotNull(oldNode);
		Utils.checkNotNull(newNode);
		
		for(SSAInstruction oldSSA : WALAUtils.getAllIRs(oldNode)) {
			if(CodeAnalysisUtils.isPredicateInstruction(oldSSA)) {
		        List<SSAInstruction> matchedInstructions
		            = matcher.matchInstructionInNewCG(oldNode, newNode, oldSSA);
		        System.out.println("matching: " + oldSSA);
		        System.out.println("   is: " + matchedInstructions);
			}
		}
	}
	
}