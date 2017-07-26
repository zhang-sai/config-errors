package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfigurationSlicer;
import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.instrument.EveryStmtInstrumenter;
import junit.framework.TestCase;

public class TestTwoBaselineDiagnoser extends TestCase {
	
	String originalJarPath  = "./tests/edu/washington/cs/conf/diagnosis/test.baseline.diagnoser.jar";

	public void testStmtLevelInstrumentation() throws Exception {
		EveryStmtInstrumenter instrumenter = new EveryStmtInstrumenter();
		instrumenter.instrument(originalJarPath,
				"./tests/edu/washington/cs/conf/diagnosis/test.baseline.diagnoser-stmt-instr.jar");
	}
	
	public List<ConfEntity> getConfigs() {
		ConfEntity entity1 = new ConfEntity("test.baseline.diagnoser.Main", "option1", true);
		ConfEntity entity2 = new ConfEntity("test.baseline.diagnoser.Main", "option2", true);
		ConfEntity entity3 = new ConfEntity("test.baseline.diagnoser.Main", "option3", true);
		
        List<ConfEntity> list = new LinkedList<ConfEntity>();
		list.add(entity1);
		list.add(entity2);
		list.add(entity3);
		
		return list;
	}
	
	public List<ConfPropOutput> testSliceConfOption() {
		String path = originalJarPath;
		String mainClass = "Ltest/baseline/diagnoser/Main";
		
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
	    helper.setCGType(CG.ZeroCFA);
	    helper.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
	    helper.setControlDependenceOptions(ControlDependenceOptions.NONE);
	    helper.setContextSensitive(false); //context-insensitive
	    helper.buildAnalysis();
	    
	    List<ConfEntity> configs = this.getConfigs();
	    List<ConfPropOutput> outputs = new LinkedList<ConfPropOutput>();
	    for(ConfEntity entity : configs) {
		    ConfPropOutput output = helper.outputSliceConfOption(entity);
		    outputs.add(output);
//		    System.out.println(entity);
//		    System.out.println(" - " + output.statements.size());
		    System.out.println(output);
	    }
	    assertEquals(9, outputs.get(0).statements.size());
	    assertEquals(7, outputs.get(1).statements.size());
	    assertEquals(7, outputs.get(2).statements.size());
	    
	    return outputs;
	}
	
	/**
	 * Run the above test instrumentation test before running the
	 * following ones
	 * */
	public void testStmtCoverageBased() {
		String dir = "./tests/edu/washington/cs/conf/diagnosis/";
		List<StmtExecuted> good1 = StmtFileReader.readStmts(dir + "test.baseline-good1-no-args.txt");
		List<StmtExecuted> good2 = StmtFileReader.readStmts(dir + "test.baseline-good2-option1-1.txt");
		List<StmtExecuted> good3 = StmtFileReader.readStmts(dir + "test.baseline-good3-option1-1-option2-2.txt");
		List<StmtExecuted> good4 = StmtFileReader.readStmts(dir + "test.baseline-good4-option1-1-option2-2-option3-3.txt");
		List<StmtExecuted> good5 = StmtFileReader.readStmts(dir + "test.baseline-good5-option1-1-option2-2-option3--3.txt");
		List<StmtExecuted> good6 = StmtFileReader.readStmts(dir + "test.baseline-good6-option1-1-option2--2-option3-3.txt");
		
		List<StmtExecuted> bad1 = StmtFileReader.readStmts(dir + "test.baseline-bad-1-2-0.txt");
		
		StmtExecuted.addSourceNumber(dir + "test.baseline.diagnoser.jar", good1,good2, good3, good4, good5, good4, bad1);
		
		Collection<Collection<StmtExecuted>> goodRuns
	        = new LinkedList<Collection<StmtExecuted>>();
		goodRuns.add(good1);
		goodRuns.add(good2);
		goodRuns.add(good3);
		goodRuns.add(good4);
		goodRuns.add(good5);
		goodRuns.add(good6);
		
		Collection<Collection<StmtExecuted>> badRuns
            = new LinkedList<Collection<StmtExecuted>>();
		badRuns.add(bad1);
		
		//see
		Map<String, Float> stmtScores = TestStmtExecutedDiffer.computeScore(goodRuns, badRuns);
		List<ConfPropOutput> options = testSliceConfOption();
		//see the coverage results
		StmtCoverageBasedDiagnoser diagnoser = new StmtCoverageBasedDiagnoser(options, stmtScores);
		List<ConfEntity> results = diagnoser.computeResponsibleOptions();
		
		for(ConfEntity result : results) {
			System.out.println(result);
		}
		
		assertEquals(44, results.size());
	}
	
	public void testMethodBasedDiagnoser() {
		List<ConfPropOutput> options = testSliceConfOption();
		Map<String, Float> methodScores = new LinkedHashMap<String, Float>();
		methodScores.put("test.baseline.diagnoser.Main.callPrint3()V", 0.9f);
		methodScores.put("test.baseline.diagnoser.Main.callPrint1()V", 0.6f);
		methodScores.put("test.baseline.diagnoser.Main.callPrint2()V", 0.7f);
		methodScores.put("test.baseline.diagnoser.Main.nonStaticMain()V", 0.9f);
		methodScores.put("test.baseline.diagnoser.Main.main([Ljava/lang/String;)V", 0.2f);
		
		MethodBasedDiagnoser diagnoser = new MethodBasedDiagnoser(options, methodScores);
        
		List<ConfEntity> results = diagnoser.computeResponsibleOptions();
		
		for(ConfEntity result : results) {
			System.out.println(result);
		}
		
		assertEquals(3, results.size());
	}
	
}
