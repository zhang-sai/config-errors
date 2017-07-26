package edu.washington.cs.conf.analysis.evol;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.wala.ipa.callgraph.CGNode;

import edu.washington.cs.conf.analysis.evol.experimental.MethodMatchingLogics;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;
import junit.framework.TestCase;

public class TestMethodMatcher extends TestCase {

	public void testMethodRandoopCode() {
		CodeAnalyzer coder121 = CodeAnalyzerRepository.getRandoop121Analyzer();
		coder121.buildAnalysis();
		
		CodeAnalyzer coder132 = CodeAnalyzerRepository.getRandoop132Analyzer();
		coder132.buildAnalysis();
		
		AnalysisScope scope = AnalysisScopeRepository.createRandoopScore();
		
		AnalysisCache cache = AnalysisCache.createCache(coder121, coder132, scope);
		
		String methodSig = "randoop.util.ReflectionExecutor.executeReflectionCode(Lrandoop/util/ReflectionCode;Ljava/io/PrintStream;)Ljava/lang/Throwable;";
		
		CGNode oldNode = WALAUtils.lookupMatchedCGNode(coder121.getCallGraph(), methodSig);
		CGNode newNode = WALAUtils.lookupMatchedCGNode(coder132.getCallGraph(), methodSig);
		
		System.out.println("old node: " + oldNode);
		System.out.println("new node: " + newNode);
		
		boolean matched = false;
		
		MethodMatcher.debug = false;
		MethodMatcher matcher = new MethodMatcher(coder121.getCallGraph(),
				coder132.getCallGraph(), scope, cache);
		
		List<CGNode> matchedNodes = new LinkedList<CGNode>();
		for(CGNode n : coder132.getCallGraph()) {
			if(WALAUtils.getFullMethodName(n.getMethod()).startsWith("randoop.")) {
				matched = matcher.fuzzMatchNodes(oldNode, n, MethodMatcher.default_threshold, MethodMatcher.default_la);
				if(matched) {
					matchedNodes.add(n);
				}
			}
		}
		assertEquals(1, matchedNodes.size());
		assertEquals("Node: < Application, Lrandoop/util/ReflectionExecutor, " +
				"executeReflectionCode(Lrandoop/util/ReflectionCode;" +
				"Ljava/io/PrintStream;)Ljava/lang/Throwable; > Context: " +
				"Everywhere",
				matchedNodes.get(0).toString());
	}
	
	//Matching node: Node: < Application, Lrandoop/RegressionCaptureVisitor, visitAfter(Lrandoop/ExecutableSequence;I)Z >
	//with: Node: < Application, Lrandoop/ExecutableSequence, executeStatement(Lrandoop/Sequence;Ljava/util/List;I[Ljava/lang/Object;)V > Context: Everywhere

	public void testRandoopBuggyMatching() {
		CodeAnalyzer oldAnalyzer = CodeAnalyzerRepository.getRandoop121Analyzer();
		oldAnalyzer.buildAnalysis();
		
		CodeAnalyzer newAnalyzer = CodeAnalyzerRepository.getRandoop132Analyzer();
		newAnalyzer.buildAnalysis();
		
		AnalysisScope scope = AnalysisScopeRepository.createRandoopScore();
		AnalysisCache cache = AnalysisCache.createCache(oldAnalyzer, newAnalyzer, scope);
		
		String oldMethodSig = "randoop.RegressionCaptureVisitor.visitAfter(Lrandoop/ExecutableSequence;I)Z";
		String newMethodSig = "randoop.ExecutableSequence.executeStatement(Lrandoop/Sequence;Ljava/util/List;I[Ljava/lang/Object;)V";
		CGNode oldNode = WALAUtils.lookupMatchedCGNode(oldAnalyzer.getCallGraph(), oldMethodSig);
		CGNode newNode = WALAUtils.lookupMatchedCGNode(newAnalyzer.getCallGraph(), newMethodSig);
		
		Utils.checkNotNull(oldNode);
		Utils.checkNotNull(newNode);
		
		//The matching will never end
		MethodMatcher matcher = new MethodMatcher(oldAnalyzer.getCallGraph(), newAnalyzer.getCallGraph(), scope, cache);
		boolean matched = matcher.fuzzMatchNodes(oldNode, newNode, MethodMatcher.default_threshold, MethodMatcher.default_la);
		System.out.println(matched);
		assertFalse(matched);
	}
	
	public void testAllNodeMatchesInRandoop() {
		MethodMatcher.debug = false;
		
		CodeAnalyzer oldAnalyzer = CodeAnalyzerRepository.getRandoop121Analyzer();
		oldAnalyzer.buildAnalysis();
		
		CodeAnalyzer newAnalyzer = CodeAnalyzerRepository.getRandoop132Analyzer();
		newAnalyzer.buildAnalysis();
		
		AnalysisScope scope = AnalysisScopeRepository.createRandoopScore();
		AnalysisCache cache = AnalysisCache.createCache(oldAnalyzer, newAnalyzer, scope);
		
		MethodMatchingLogics matcher = new MethodMatchingLogics(oldAnalyzer, newAnalyzer, scope, cache);
		Map<CGNode, List<CGNode>> map = matcher.getAllMatchedMethods();
		for(CGNode node : map.keySet()) {
			List<CGNode> matchedNodes = map.get(node);
			if(matchedNodes.size() != 1 ) {
				System.out.println(node);
				System.out.println("   " + matchedNodes.size());
			}
		}
	}
	
//	public void showAllMatches(CodeAnalyzer oldAnalyzer, CodeAnalyzer newAnalyzer,
//			AnalysisScope scope, AnalysisCache cache) {
//		MethodMatchingLogics matcher = new MethodMatchingLogics(oldAnalyzer, newAnalyzer,
//				scope, cache);
//		for(CGNode node : oldAnalyzer.getCallGraph()) {
//			if(!scope.isInScope(node.getMethod().getDeclaringClass())) {
//				continue;
//			}
//			List<CGNode> nodeList = matcher.getMatchedMethods(node);
//			if(nodeList.size() != 1) {
//				System.out.println("matching: " + node);
//				System.out.println("   " + nodeList.size());
//			}
//		}
//	}
}
