package edu.washington.cs.conf.analysis.evol;

import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.cha.ClassHierarchy;

import edu.washington.cs.conf.analysis.ConfigurationSlicer;
import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;
import junit.framework.TestCase;

public class TestCustomizeEntrypoint extends TestCase {

	public void testCreateEntrypointJMeter() {
		String classPath = CodeAnalyzerRepository.getOldJMeterPath();
		ConfigurationSlicer slicer = new ConfigurationSlicer(classPath, CodeAnalyzerRepository.jmeterMainClassSig);
		slicer.buildClassHierarchy();
		ClassHierarchy cha = slicer.getClassHierarchy();
		
		Iterable<Entrypoint> points = WALAUtils.createEntrypoints(CodeAnalyzerRepository.jmeterStartClass,
				CodeAnalyzerRepository.jmeterStartMethod, cha);
		System.out.println(Utils.countIterable(points));
		
		assertEquals(1, Utils.countIterable(points));
	}
	
	public void testCreateEntrypointRandoop() {
		String classPath = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.2.1\\randoop-1.2.1.jar";
		String mainMethod = "Lrandoop/main/Main";
		ConfigurationSlicer slicer = new ConfigurationSlicer(classPath, mainMethod);
		slicer.buildClassHierarchy();
		ClassHierarchy cha = slicer.getClassHierarchy();
		
		String className = "randoop.main.Main";
		String methodName = "main";
		Iterable<Entrypoint> points = WALAUtils.createEntrypoints(className, methodName, cha);
		System.out.println(Utils.dumpCollection(points));
		
		assertEquals(1, Utils.countIterable(points));
		
		slicer.setEntrypoints(points);
		slicer.setExclusionFile("JavaAllExclusions.txt");
		slicer.setCGType(CG.ZeroCFA);
		slicer.buildAnalysis();
		
//		Call graph stats:
//			  Nodes: 8740
//			  Edges: 34436
//			  Methods: 3640
//			  Bytecode Bytes: 193949
	}
	
}