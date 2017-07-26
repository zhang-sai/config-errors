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

import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.experiments.SootExpUtils;
import edu.washington.cs.conf.experiments.SynopticExpUtils;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Log;
import edu.washington.cs.conf.util.WALAUtils;
import junit.framework.TestCase;

public class TestAnalysisOnSoot extends TestCase {
	public void testSootBuildCG() {
		String dir = "./subjects/soot-2.5/";
		String path = dir + "soot.jar;" +
		        dir + "libs/coffer.jar;" +
		        dir + "libs/jasminclasses-2.5.0.jar;" +
		        dir + "libs/java_cup.jar;" +
		        dir + "libs/JFlex.jar;" +
		        dir + "libs/pao.jar;" +
		        dir + "libs/polyglot.jar;" +
		        dir + "libs/pth.jar";
		String mainClass = "Lsoot/Main";
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setCGType(CG.ZeroCFA);
		helper.setExclusionFile("JavaAllExclusions.txt");
		helper.buildAnalysis();
	}
	
    public void testSootOptionsISlicer() throws IllegalArgumentException, CancelException {
		
    	String dir = "./subjects/soot-2.5/";
		String path = dir + "soot.jar;" +
		        dir + "libs/coffer.jar;" +
		        dir + "libs/jasminclasses-2.5.0.jar;" +
		        dir + "libs/java_cup.jar;" +
		        dir + "libs/JFlex.jar;" +
		        dir + "libs/pao.jar;" +
		        dir + "libs/polyglot.jar;" +
		        dir + "libs/pth.jar";
		String mainClass = "Lsoot/Main";
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setCGType(CG.ZeroCFA);
		helper.setExclusionFile("JavaAllExclusions.txt");
		helper.buildAnalysis();
		
		CISlicer slicer = new ThinSlicer(helper.getCallGraph(), helper.getPointerAnalysis(),
				DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
				ControlDependenceOptions.NONE);
		
		Log.logConfig("./option-output.txt");
		
		List<ConfEntity> synopticConfList = SootExpUtils.getSootConfList();
		
		for(ConfEntity entity : synopticConfList) {
			Statement seed = helper.extractConfStatement(entity);
			
			Collection<Statement> slice = null;
			
			System.out.println("--- forward slicing of entity: " + entity);
			Log.logln("--- forward slicing of entity: " + entity);
			Log.logln("seed: " + seed);
			
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
				if(fullClassName.startsWith("soot") && lineNum != -1) {
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
