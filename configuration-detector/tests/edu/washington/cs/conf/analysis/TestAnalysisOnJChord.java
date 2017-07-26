package edu.washington.cs.conf.analysis;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.thin.CISlicer;
import com.ibm.wala.ipa.slicer.thin.ThinSlicer;
import com.ibm.wala.util.CancelException;

import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.experiments.ChordExpUtils;
import edu.washington.cs.conf.experiments.RandoopExpUtils;
import edu.washington.cs.conf.util.Log;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;
import junit.framework.TestCase;

public class TestAnalysisOnJChord extends TestCase {
	public void testChordBuildCG() {
		String path = "./subjects/jchord/chord.jar";
		String mainClass = "Lchord/project/Main";
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setExclusionFile("ChordExclusions.txt");
		helper.setCGType(CG.ZeroCFA);
		helper.buildAnalysis();
		
		int count = 0;
		for(CGNode node : helper.getCallGraph()) {
			String fullMethodName = WALAUtils.getFullMethodName(node.getMethod()); 
			if(fullMethodName.startsWith("chord.analyses.datarace.RelExcludeSameThread")) {
				count++;
				System.out.println(node);
			}
		}
		System.out.println(count);
	}
	
    public void testJChordOptionsISlicer() throws IllegalArgumentException, CancelException {
    	String path = "./subjects/jchord/chord.jar";
		String mainClass = "Lchord/project/Main";
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setCGType(CG.ZeroCFA);
		helper.setExclusionFile("ChordExclusions.txt");
		helper.buildAnalysis();
		
		CISlicer slicer = new ThinSlicer(helper.getCallGraph(), helper.getPointerAnalysis(),
				DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
				ControlDependenceOptions.NONE);
		
		Log.logConfig("./option-output.txt");
//		DFSFinishTimeIterator.DIRTY_HACK = true;
		
		List<ConfEntity> jchordConfList = ChordExpUtils.getChordConfList();
		
		for(ConfEntity entity : jchordConfList) {
			Statement seed = helper.extractConfStatement(entity);
			
			if(seed == null) {
				Log.logln("Can not find statement corresponding to: " + entity.toString());
			}
			
			Collection<Statement> slice = null;
			
			System.out.println("--- forward slicing of entity: " + entity);
			Log.logln("--- forward slicing of entity: " + entity);
			
//			if(!entity.getClassName().equals("chord.analyses.datarace.RelExcludeSameThread")) {
//				continue;
//			}
			
			slice = slicer.computeForwardThinSlice(seed);
			
			int count = 0;
			Set<Integer> nums = new LinkedHashSet<Integer>();
			List<Statement> pruned = new LinkedList<Statement>();
			for(Statement s : slice) {
				String fullClassName = WALAUtils.getFullMethodName(s.getNode().getMethod());
				int lineNum = WALAUtils.getStatementLineNumber(s);
				if(nums.contains(lineNum)) {
					continue;
				}
				nums.add(lineNum);
				if(fullClassName.startsWith("chord") && lineNum != -1) {
					pruned.add(s);
					count++;
				}
			}
			
			if(count > 10) {
				Log.logln("Too many affected statements: " + entity);
			} else {
				for(Statement s : pruned) {
					Log.logln("   " + s + " : " + WALAUtils.getStatementLineNumber(s) + ",  " + s.getKind());
				}
			}
			
			System.out.println("Slice size: " + count);
//			if(entity.getClassName().startsWith("chord.analyses.datarace.RelExcludeSameThread")) {
//				for(Statement s : pruned) {
//					Log.logln("   " + s + " : " + WALAUtils.getStatementLineNumber(s));
//				}
//			}
			Log.logln("Slice size: " + count);
			
			System.out.println("------------");
			Log.logln("------------");
		}
		
	}
}
