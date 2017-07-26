package edu.washington.cs.conf.analysis;

import java.util.Iterator;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ssa.ISSABasicBlock;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSACFG.BasicBlock;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.util.WALAUtils;
import junit.framework.TestCase;

public class TestControlFlowAnalysisOnSmallExamples extends TestCase {

	public void testSimpleCode() {
		String path = "D:\\research\\configurations\\workspace\\configuration-detector\\bin\\test\\code";
		String mainClass = "Ltest/code/EqthConf";
		performToyControlFlowAnalysis(path, mainClass, new String[]{"test.code"}, null);
	}
	
	public void testBaselineMain() {
		String path = "D:\\research\\configurations\\workspace\\configuration-detector\\bin\\test\\baseline\\entryexit";
		String mainClass = "Ltest/baseline/entryexit/Main";
		performToyControlFlowAnalysis(path, mainClass, new String[]{"test.baseline"}, null);
	}
	
	private void performToyControlFlowAnalysis(String classpath, String mainClass,
			String[] pkgNames, CG type) {
		ConfigurationSlicer helper = new ConfigurationSlicer(classpath, mainClass);
		helper.setExclusionFile("JavaAllExclusions.txt");
		helper.setCGType(CG.ZeroCFA);
		if(type != null) {
			helper.setCGType(type);
		}
		helper.buildAnalysis();
		
		CallGraph cg = helper.getCallGraph();
		for(CGNode node : cg) {
			if(WALAUtils.isClassInPackages(node, pkgNames)) {
				this.printInstrInBB(node);
			}
		}
	}
	
	private void printInstrInBB(CGNode node) {
		System.out.println("node: " + node.getMethod());
		SSACFG cfg = node.getIR().getControlFlowGraph();
		Iterator<ISSABasicBlock> bbiter =  cfg.iterator();
		while(bbiter.hasNext()) {
			ISSABasicBlock bb = bbiter.next();
			System.out.println("  " + bb);
			Iterator<SSAInstruction> ssaIter = bb.iterator();
			while(ssaIter.hasNext()) {
				System.out.println("        " + ssaIter.next());
			}
		}
	}
	
}