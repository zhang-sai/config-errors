package edu.washington.cs.conf.analysis;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.wala.ipa.slicer.SDG;
import com.ibm.wala.ipa.slicer.Slicer;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.thin.CISlicer;
import com.ibm.wala.ipa.slicer.thin.ThinSlicer;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.graph.traverse.DFSFinishTimeIterator;

import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.experiments.RandoopExpUtils;
import edu.washington.cs.conf.util.Log;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;
import junit.framework.TestCase;

public class TestAnalysisOnRandoop extends TestCase {

	public void testRandoopBuildCG() {
		String path = "./subjects/randoop-jamie.jar;./subjects/plume.jar";
		String mainClass = "Lrandoop/main/Main";
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setCGType(CG.ZeroCFA);
		helper.buildAnalysis();
	}
	
	public void testRandoopMaxLength() {
		List<String> options = new LinkedList<String>();
		options.add("randoop.main.GenInputsAbstract.maxsize");
		
		List<Boolean> isStatics = new LinkedList<Boolean>();
		isStatics.add(true);
		
		String path = "./subjects/randoop-jamie.jar;./subjects/plume.jar";
		String mainClass = "Lrandoop/main/Main";
		
		ConfPropagationAnalyzer analyzer = new ConfPropagationAnalyzer(options, null, isStatics, path, mainClass);
		analyzer.setCGType(CG.ZeroCFA);
		analyzer.setExclusionFile("JavaAllExclusions.txt");
		List<ConfPropOutput> outputs = analyzer.doAnalysis();
		
		for(ConfPropOutput output : outputs) {
			System.out.println(output);
			System.out.println("See shrikepoints:");
			System.out.println("+ all shrike points");
			Set<ShrikePoint> pts = output.getAllShrikePoints();
			System.out.println(Utils.dumpCollection(pts));
			System.out.println("+ all numbered points");
			pts = output.getNumberedShrikePoints();
			System.out.println(Utils.dumpCollection(pts));
			System.out.println("+ all branch points");
			pts = output.getNumberedBranchShrikePoints();
			System.out.println(Utils.dumpCollection(pts));
			System.out.println("-----");
		}
	}
	
	public void testRandoopMaxLengthRunningOutOfMemory() throws IllegalArgumentException, CancelException {
		
		String path = "./subjects/randoop-jamie.jar;./subjects/plume.jar";
		String mainClass = "Lrandoop/main/Main";
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setCGType(CG.ZeroCFA);
		helper.setExclusionFile("JavaAllExclusions.txt");
		helper.buildAnalysis();
		ConfEntity entity = new ConfEntity("randoop.main.GenInputsAbstract", "maxsize", true);
		Statement seed = helper.extractConfStatement(entity);
		Collection<Statement> slice = null;
		System.out.println("--- doing forward slicing---");
		SDG.DEBUG_LAZY = true;
		slice = Slicer.computeForwardSlice(seed, helper.getCallGraph(), helper.getPointerAnalysis(),
				DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
	            ControlDependenceOptions.NONE);
		
		System.out.println("Slice size: " + slice.size());
	}
	
    public void testRandoopOptionsISlicer() throws IllegalArgumentException, CancelException {
		
		String path = "./subjects/randoop-jamie.jar;./subjects/plume.jar";
		String mainClass = "Lrandoop/main/Main";
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setCGType(CG.ZeroCFA);
		helper.setExclusionFile("JavaAllExclusions.txt");
		helper.buildAnalysis();
		
		CISlicer slicer = new ThinSlicer(helper.getCallGraph(), helper.getPointerAnalysis(),
				DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
				ControlDependenceOptions.NONE);
		
		Log.logConfig("./option-output.txt");
//		DFSFinishTimeIterator.DIRTY_HACK = true;
		
		List<ConfEntity> randoopConfList = RandoopExpUtils.getRandoopConfList();
		
		for(ConfEntity entity : randoopConfList) {
			Statement seed = helper.extractConfStatement(entity);
			
			Collection<Statement> slice = null;
			
			System.out.println("--- forward slicing of entity: " + entity);
			Log.logln("--- forward slicing of entity: " + entity);
			
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
				if(fullClassName.startsWith("randoop") && lineNum != -1) {
					pruned.add(s);
					count++;
				}
			}
			
			if(count > 10) {
				Log.logln("Too many affected statements: " + entity);
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
