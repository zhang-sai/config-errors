package edu.washington.cs.conf.analysis;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.thin.CISlicer;
import com.ibm.wala.ipa.slicer.thin.ThinSlicer;
import com.ibm.wala.util.CancelException;

import junit.framework.TestCase;
import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.experiments.SynopticExpUtils;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Log;
import edu.washington.cs.conf.util.WALAUtils;

public class TestAnalysisOnSynoptic extends TestCase {
	public void testSynopticBuildCG() {
		String path = "./subjects/synoptic/synoptic.jar;" +
				"./subjects/synoptic/libs/plume.jar;" +
				"./subjects/synoptic/libs/commons-io-2.0.1.jar;" +
				"./subjects/synoptic/libs/commons-fileupload-1.2.2.jar;" +
				"./subjects/synoptic/libs/junit-4.9b2.jar";
		String mainClass = "Lsynoptic/main/Main";
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setCGType(CG.ZeroCFA);
		helper.buildAnalysis();
	}
	
    public void testSynotpicOptionsISlicer() throws IllegalArgumentException, CancelException {
		
		String path = "./subjects/synoptic/synoptic.jar;"
				+ "./subjects/synoptic/libs/plume.jar;"
				+ "./subjects/synoptic/libs/commons-io-2.0.1.jar;"
				+ "./subjects/synoptic/libs/commons-fileupload-1.2.2.jar;"
				+ "./subjects/synoptic/libs/junit-4.9b2.jar";
		String mainClass = "Lsynoptic/main/Main";
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setCGType(CG.ZeroCFA);
		helper.setExclusionFile("JavaAllExclusions.txt");
		helper.buildAnalysis();
		
		CISlicer slicer = new ThinSlicer(helper.getCallGraph(), helper.getPointerAnalysis(),
				DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
				ControlDependenceOptions.NONE);
		
		Log.logConfig("./option-output.txt");
		
		List<ConfEntity> synopticConfList = SynopticExpUtils.getSynopticList();
		
		for(ConfEntity entity : synopticConfList) {
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
				if(fullClassName.startsWith("synoptic") && lineNum != -1) {
					pruned.add(s);
					count++;
				}
			}
			
			if(count > 10) {
				Log.logln("Too many affected statements: " + entity);
			} else {
				for(Statement s : pruned) {
					Log.logln("   " + s + Globals.lineSep
							+ "\t   :" + WALAUtils.getFullMethodName(s.getNode().getMethod())  
							+ ", line#: " + WALAUtils.getStatementLineNumber(s));
				}
			}
			
			System.out.println("Slice size: " + count);
			Log.logln("Slice size: " + count);
			
			System.out.println("------------");
			Log.logln("------------");
		}
		
	}
}
