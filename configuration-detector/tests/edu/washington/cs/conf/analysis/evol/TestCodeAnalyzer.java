package edu.washington.cs.conf.analysis.evol;

import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

import junit.framework.TestCase;

public class TestCodeAnalyzer extends TestCase {

	public void testRandoopIndex() {
		//String randoop121Path = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.2.1\\randoop-1.2.1.jar";
		String randoopPath = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\randoop-1.3.2.jar"
			+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\lib\\plume.jar"
			+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\lib\\jakarta-oro-2.0.8.jar";
		
		String randoopMain = "Lrandoop/main/Main";
		CodeAnalyzer coder = new CodeAnalyzer(randoopPath, randoopMain);
		coder.slicer.setExclusionFile("JavaAllExclusions.txt");
		coder.slicer.setCGType(CG.ZeroCFA);
		
		String methodSig = "randoop.util.ReflectionExecutor.executeReflectionCode(Lrandoop/util/ReflectionCode;Ljava/io/PrintStream;)Ljava/lang/Throwable;";
		int index = 2;
		
		coder.buildAnalysis();
		SSAInstruction ssa = coder.getInstruction(methodSig, index);
		System.out.println(ssa);
		
		CGNode matchedNode = WALAUtils.lookupMatchedCGNode(coder.getCallGraph(), methodSig);
		String allIRs = WALAUtils.getAllIRAsString(matchedNode);
		System.out.println(allIRs);
		
		//look at the node
		CGNode node = WALAUtils.lookupMatchedCGNode(coder.slicer.getCallGraph(), methodSig);
		
		WALAUtils.printCFG(node);
	}
	
	public void testRandoopCoder() {
		CodeAnalyzer coder = null;
		
		coder = CodeAnalyzerRepository.getRandoop121Analyzer();
		coder.buildAnalysis();
		
		coder = CodeAnalyzerRepository.getRandoop132Analyzer();
		coder.buildAnalysis();
	}
	
	public void testSynopticCoder() {
		CodeAnalyzer coder = null;
		
		coder = CodeAnalyzerRepository.getSynopticOldAnalyzer();
		coder.buildAnalysis();
		
		coder = CodeAnalyzerRepository.getSynopticNewAnalyzer();
		coder.buildAnalysis();
	}
	
	public void testWekaCoder() {
		CodeAnalyzer coder = null;
		
		coder = CodeAnalyzerRepository.getWekaOldAnalyzer();
		coder.buildAnalysis();
		
		coder = CodeAnalyzerRepository.getWekaNewAnalyzer();
		coder.buildAnalysis();
	}
	
	//around 4556 node, 2336 nodes
	public void testJChordCoder() {
		CodeAnalyzer coder = null;
		
		coder = CodeAnalyzerRepository.getJChordOldAnalyzer();
		coder.buildAnalysis();
		
		coder = CodeAnalyzerRepository.getJChordNewAnalyzer();
		coder.buildAnalysis();
	}
	
	public void testJMeterCoder() {
		CodeAnalyzer coder = null;
		
		coder = CodeAnalyzerRepository.getJMeterOldAnalyzer();
		coder.buildAnalysis();
		
		coder = CodeAnalyzerRepository.getJMeterNewAnalyzer();
		coder.buildAnalysis();
	}
	
	public void testJavalanche() {
		CodeAnalyzer coder = null;
		
		coder = CodeAnalyzerRepository.getJavalancheOldAnalyzer();
		coder.buildAnalysis();
		
		for(CGNode node : coder.getCallGraph()) {
			String sig = node.getMethod().getSignature();
			if(sig.indexOf("MutationTestSuite") != -1
					|| sig.indexOf("Junit3MutationTestDriver") != -1
					|| sig.indexOf("TestSuiteUtil") != -1) {
				System.out.println(node);
			}
		}
		
//		coder = CodeAnalyzerRepository.getJavalancheNewAnalyzer();
//		coder.buildAnalysis();
	}
	
	public void testFindUniqueMethodsJChord() {
        CodeAnalyzer coder = null;
		coder = CodeAnalyzerRepository.getJChordOldAnalyzer();
		coder.buildAnalysis();
		Set<String> set1 = CodeAnalysisUtils.findUniquelyInvokedMethods(coder, new String[]{"chord."});
		System.out.println("size: " + set1.size());
		System.out.println(set1);
		
		coder = CodeAnalyzerRepository.getJChordNewAnalyzer();
		coder.buildAnalysis();
		Set<String> set2 = CodeAnalysisUtils.findUniquelyInvokedMethods(coder, new String[]{"chord."});
		System.out.println("size: " + set2.size());
		System.out.println(set2);
		
		Set<String> intersect = Utils.intersect(set1, set2);
		System.out.println("size: " + intersect.size());
		System.out.println(intersect);
	}
	
}
