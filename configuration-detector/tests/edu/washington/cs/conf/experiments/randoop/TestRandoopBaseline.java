package edu.washington.cs.conf.experiments.randoop;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfigurationSlicer;
import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.diagnosis.InvariantUtils;
import edu.washington.cs.conf.diagnosis.MethodBasedDiagnoser;
import edu.washington.cs.conf.diagnosis.StmtCoverageBasedDiagnoser;
import edu.washington.cs.conf.diagnosis.StmtExecuted;
import edu.washington.cs.conf.diagnosis.StmtFileReader;
import edu.washington.cs.conf.diagnosis.TestStmtExecutedDiffer;
import edu.washington.cs.conf.experiments.RandoopExpUtils;
import edu.washington.cs.conf.instrument.EveryStmtInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestRandoopBaseline extends TestCase {
	
	public void testStmtInstrumentation() throws Exception {
		EveryStmtInstrumenter instrumenter = new EveryStmtInstrumenter();
        instrumenter.instrument("./subjects/randoop-jamie.jar", "./output/randoop-everystmt.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
	//max size ranks 13 with all traces
	//max size ranks 13 with selected traces
	public void testDiagnoseOptionsByRelatedStmt() {
		List<StmtExecuted> good1 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-arraylist-60s-related-stmt.txt");
		List<StmtExecuted> good2 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-binarysearchtree-60s-related-stmt.txt");
		List<StmtExecuted> good3 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-binomial-60s-related-stmt.txt");
		List<StmtExecuted> good4 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-treeset-60s-related-stmt.txt");
		List<StmtExecuted> good5 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-treeset-collections-60s-related-stmt.txt");
		List<StmtExecuted> good6 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-simple-ds-60s-related-stmt.txt");
		List<StmtExecuted> good7 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-javaxml-60s-related-stmt.txt");
		List<StmtExecuted> good8 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-primitives-60s-related-stmt.txt");
		List<StmtExecuted> good9 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-show-help-related-stmt.txt");
		List<StmtExecuted> good10 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-show-unpub-related-stmt.txt");
		List<StmtExecuted> good11 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-all-java-utils-related-stmt.txt");
		List<StmtExecuted> good12 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-graph-60s-related-stmt.txt");
		
		List<StmtExecuted> bad1 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/bad-nanoxml-60s-related-stmt.txt");
		
		Collection<Collection<StmtExecuted>> goodRuns
	        = new LinkedList<Collection<StmtExecuted>>();
		goodRuns.add(good1);
		goodRuns.add(good2);
		goodRuns.add(good3);
		goodRuns.add(good4);
		goodRuns.add(good5);
		goodRuns.add(good6);
		goodRuns.add(good7);
		goodRuns.add(good8);
//		goodRuns.add(good9);
//		goodRuns.add(good10);
		goodRuns.add(good11);
		goodRuns.add(good12);
		
		Collection<Collection<StmtExecuted>> badRuns
            = new LinkedList<Collection<StmtExecuted>>();
		badRuns.add(bad1);
		
		//do diff
		Map<String, Float> scores = TestStmtExecutedDiffer.computeScore(goodRuns, badRuns);
		
		Collection<ConfPropOutput> outputs = this.getRandoopConfOutputs();
		
		StmtCoverageBasedDiagnoser diagnoser = new StmtCoverageBasedDiagnoser(outputs, scores);
		
		System.out.println("start to diagnose options: ....");
		
		List<ConfEntity> results = diagnoser.computeResponsibleOptions();
		
		List<String> entities = new LinkedList<String>();
		for(ConfEntity result : results) {
//			System.out.println(result);
			if(!entities.contains(result.toString())) {
				entities.add(result.toString());
			}
		}
		
		for(int i = 0; i  < entities.size(); i++) {
			System.out.println(i+1 + ". " + entities.get(i));
		}
	}
	
	public void testDiagnoseOptionsByStmtCoverage() {
		List<StmtExecuted> good1 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-arraylist-60s.txt");
		List<StmtExecuted> good2 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-treeset-60s.txt");
		List<StmtExecuted> good3 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-show-help.txt");
		List<StmtExecuted> good4 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-all-javautils-60s.txt");
		List<StmtExecuted> good5 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-binarysearchtree-60s.txt");
		List<StmtExecuted> good6 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-binomial-60s.txt");
		List<StmtExecuted> good7 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-graph-60s.txt");
		List<StmtExecuted> good8 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-primitives-60s.txt");
		List<StmtExecuted> good9 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-show-unpub.txt");
		List<StmtExecuted> good10 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-simple-ds-60s.txt");
		List<StmtExecuted> good11 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-treeset-collections-60s.txt");
		List<StmtExecuted> good12 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/good-javaxml-60s.txt");
		
		List<StmtExecuted> bad1 = StmtFileReader.readStmts("./experiments/randoop-baseline-stmt/bad-nano-xml-100s.txt");
		
		Collection<Collection<StmtExecuted>> goodRuns
	        = new LinkedList<Collection<StmtExecuted>>();
		goodRuns.add(good1);
		goodRuns.add(good2);
		goodRuns.add(good3);
		goodRuns.add(good4);
		goodRuns.add(good5);
		goodRuns.add(good6);
		goodRuns.add(good7);
		goodRuns.add(good8);
		goodRuns.add(good9);
		goodRuns.add(good10);
		goodRuns.add(good11);
		goodRuns.add(good12);
		
		Collection<Collection<StmtExecuted>> badRuns
            = new LinkedList<Collection<StmtExecuted>>();
		badRuns.add(bad1);
		
		//do diff
		Map<String, Float> scores = TestStmtExecutedDiffer.computeScore(goodRuns, badRuns);
		
		Collection<ConfPropOutput> outputs = this.getRandoopConfOutputs();
		
		StmtCoverageBasedDiagnoser diagnoser = new StmtCoverageBasedDiagnoser(outputs, scores);
		
		System.out.println("start to diagnose options: ....");
		
		List<ConfEntity> results = diagnoser.computeResponsibleOptions();
		
//		for(ConfEntity result : results) {
//			System.out.println(result);
//		}
		
		List<String> entities = new LinkedList<String>();
		for(ConfEntity result : results) {
//			System.out.println(result);
			if(!entities.contains(result.toString())) {
				entities.add(result.toString());
			}
		}
		
		for(int i = 0; i  < entities.size(); i++) {
			System.out.println(i+1 + ". " + entities.get(i));
		}
	}
	
	/**
	 * The daikon invariant file is a little bit large, so I use absolute file path here
	 * @throws Exception 
	 * */
	public void testDiagnoseOptionsByInvariantAnalysis() throws Exception {
		
		String goodInvFile1 = "D:\\research\\configurations\\daikon\\bin\\randoop-examples\\randoop-arraylist-60s.inv.gz";
		String goodInvFile2 = "D:\\research\\configurations\\daikon\\bin\\randoop-examples\\treeset.inv.gz";
		String goodInvFile3 = "D:\\research\\configurations\\daikon\\bin\\randoop-examples\\treeset-collections.inv.gz";
		String goodInvFile4 = "D:\\research\\configurations\\daikon\\bin\\randoop-examples\\simpleds.inv.gz";
		String goodInvFile5 = "D:\\research\\configurations\\daikon\\bin\\randoop-examples\\showhelp.inv.gz";
		String goodInvFile6 = "D:\\research\\configurations\\daikon\\bin\\randoop-examples\\randoop-arraylist-60s.inv.gz";
		String goodInvFile7 = "D:\\research\\configurations\\daikon\\bin\\randoop-examples\\primitives.inv.gz";
		String goodInvFile8 = "D:\\research\\configurations\\daikon\\bin\\randoop-examples\\javaxml.inv.gz";
		String goodInvFile9 = "D:\\research\\configurations\\daikon\\bin\\randoop-examples\\javautilsclasses.inv.gz";
		String goodInvFile10 = "D:\\research\\configurations\\daikon\\bin\\randoop-examples\\graph.inv.gz";
		String goodInvFile11 = "D:\\research\\configurations\\daikon\\bin\\randoop-examples\\gentests-help.inv.gz";
		String goodInvFile12 = "D:\\research\\configurations\\daikon\\bin\\randoop-examples\\binarysearchtree.inv.gz";
		String goodInvFile13 = "D:\\research\\configurations\\daikon\\bin\\randoop-examples\\binomialheap.inv.gz";
		
		Collection<String> goodInvs = new LinkedList<String>();
		goodInvs.add(goodInvFile1);
		goodInvs.add(goodInvFile2);
		goodInvs.add(goodInvFile3);
		goodInvs.add(goodInvFile4);
//		goodInvs.add(goodInvFile5);
		goodInvs.add(goodInvFile6);
		goodInvs.add(goodInvFile7);
		goodInvs.add(goodInvFile8);
		goodInvs.add(goodInvFile9);
		goodInvs.add(goodInvFile10);
//		goodInvs.add(goodInvFile11);
		goodInvs.add(goodInvFile12);
		goodInvs.add(goodInvFile13);
		
		
		String badInvFile = "D:\\research\\configurations\\daikon\\bin\\randoop-examples\\nanoxml-60s.inv.gz";
		//Set<String> affectedMethods = getAffectedMethods(goodInvFile, badInvFile);
		String path = "./subjects/randoop-jamie-no-trace.jar;./subjects/plume.jar";
		Collection<ConfPropOutput> confs = TestSliceRandoopConfigOptions.getConfPropOutputs(path, RandoopExpUtils.getRandoopConfList());
		
		List<ConfEntity> entities = MethodBasedDiagnoser.computeResponsibleOptions(goodInvs, badInvFile, confs);
		
		System.out.println(entities.size());
		int i = 0;
		for(ConfEntity entity : entities) {
			System.out.println((i+1) + ". " + entity);
			i++;
		}
		
	}
	
	public Set<String> getAffectedMethods(String goodInvFile, String badInvFile) throws Exception {
		Set<String> affectedMethods = InvariantUtils.fetchMethodsWithDiffInvariants(goodInvFile, badInvFile);
		//InvariantUtils.fetchRankedMethodsWithDiffInvariants(goodInvFile, badInvFile);
		System.out.println("size: " + affectedMethods.size());
		
		System.out.println("------------ a list of methods -----------");
		int count = 1;
		for(String m : affectedMethods) {
			if(m.startsWith("gco.") || m.startsWith("plume.")) {
				continue;
			}
			System.out.println(count++ + ": " + m);
		}
		return affectedMethods;
	}
	
	public Collection<ConfPropOutput> getRandoopConfOutputs() {
		String path = "./subjects/randoop-jamie.jar;./subjects/plume.jar";
		String mainClass = "Lrandoop/main/Main";
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setCGType(CG.ZeroCFA);
//		helper.setCGType(CG.ZeroOneCFA);
		helper.setExclusionFile("JavaAllExclusions.txt");
		helper.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		helper.setControlDependenceOptions(ControlDependenceOptions.NONE);
		helper.setContextSensitive(false); //context-insensitive
		helper.buildAnalysis();
		
		List<ConfEntity> randoopConfList = RandoopExpUtils.getRandoopConfList();
		
		Collection<ConfPropOutput> outputs = new LinkedList<ConfPropOutput>();
		for(ConfEntity entity : randoopConfList) {
//		  helper.setExcludeStringBuilder(true); //FIXME
			ConfPropOutput output = helper.outputSliceConfOption(entity);
			outputs.add(output);
			System.out.println(" - " + output.statements.size());
		}

		System.out.println("size: " + outputs.size());
		assertEquals(randoopConfList.size(), outputs.size());
		
		return outputs;
	}

}

