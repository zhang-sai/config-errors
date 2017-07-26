package edu.washington.cs.conf.analysis;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.wala.ipa.slicer.Slicer;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.StatementWithInstructionIndex;
import com.ibm.wala.ipa.slicer.thin.CISlicer;
import com.ibm.wala.ipa.slicer.thin.ThinSlicer;
import com.ibm.wala.ssa.SSAConditionalBranchInstruction;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.graph.traverse.DFSFinishTimeIterator;

import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.experiments.WekaExpUtils;
import edu.washington.cs.conf.util.Log;
import edu.washington.cs.conf.util.WALAUtils;
import junit.framework.TestCase;

public class TestAnalysisOnWeka extends TestCase {
	
	static boolean tmp = false;
	
    public void testWekaOptionsISlicer() throws IllegalArgumentException, CancelException {
		
		String path = "./subjects/weka/weka.jar;./subjects/weka/JFlex.jar;" +
				"./subjects/weka/java-cup.jar";
		String mainClass = "Lweka/classifiers/trees/J48";
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setCGType(CG.ZeroCFA);
		helper.setExclusionFile("JavaAllExclusions.txt");
		helper.buildAnalysis();
		
		CISlicer slicer = new ThinSlicer(helper.getCallGraph(), helper.getPointerAnalysis(),
				DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
				ControlDependenceOptions.NONE);
		
//		ConfEntity entity1 = new ConfEntity("weka.classifiers.trees.J48", "m_root", false);
		
//		DFSFinishTimeIterator.DIRTY_HACK = true;
		Log.logConfig("./weka-option-output.txt");
		
		List<ConfEntity> list = WekaExpUtils.getWekaConfList();
		
		for(ConfEntity entity : list) {
			Statement seed = helper.extractConfStatement(entity);
			
			Collection<Statement> slice = null;
			
			System.out.println("--- forward slicing of entity: " + entity);
			System.out.println("       " + seed);
			Log.logln("--- forward slicing of entity: " + entity);
			
			slice = slicer.computeForwardThinSlice(seed);
			
//			slice = Slicer.computeForwardSlice(seed, helper.getCallGraph(), helper.getPointerAnalysis(),
//					DataDependenceOptions.NO_BASE_NO_EXCEPTIONS,
//					ControlDependenceOptions.NONE);
			
			int count = 0;
			Set<Integer> nums = new LinkedHashSet<Integer>();
			List<Statement> pruned = new LinkedList<Statement>();
			for(Statement s : slice) {
				String fullClassName = WALAUtils.getFullMethodName(s.getNode().getMethod());
				int lineNum = WALAUtils.getStatementLineNumber(s);
//				if(nums.contains(lineNum)) {
//					continue;
//				}
				nums.add(lineNum);
				if(fullClassName.startsWith("weka.") && lineNum != -1) {
					pruned.add(s);
					count++;
				}
			}
			
			if(count > 30) {
				Log.logln("Too many affected statements: " + entity);
				for(Statement s : pruned) {
					String fullMethodName = WALAUtils.getFullMethodName(s.getNode().getMethod());
					if(!tmp) {
						if(fullMethodName.indexOf("weka.classifiers.trees.j48.C45PruneableClassifierTree.prune") != -1) {
							Log.logln(WALAUtils.getAllIRAsString(s.getNode()));
							tmp = true;
						}
					}
					if(s instanceof StatementWithInstructionIndex) {
						StatementWithInstructionIndex stmt = (StatementWithInstructionIndex)s;
						if(stmt.getInstruction() instanceof SSAConditionalBranchInstruction) {
							Log.logln("   ++ " + s + " : " + WALAUtils.getStatementLineNumber(s));
						}
					}
				}
			} else {
				for(Statement s : pruned) {
					Log.logln("   " + s + " : " + WALAUtils.getStatementLineNumber(s));
				}
			}
			
			System.out.println("Slice size: " + count);
			Log.logln("Slice size: " + count);
			
			System.out.println("------------");
			Log.logln("------------");
		}
		
	}
}
