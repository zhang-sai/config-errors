package edu.washington.cs.conf.experiments.jchord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.MethodBasedDiagnoser;
import edu.washington.cs.conf.diagnosis.StmtCoverageBasedDiagnoserMain;
import edu.washington.cs.conf.instrument.EveryStmtInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestJChordBaseline extends TestCase {
	
	public static String jchord_everystmt = "./subjects/jchord/chord-everystmt.jar";

	public void testStmtInstrumentation() throws Exception {
		EveryStmtInstrumenter instrumenter = new EveryStmtInstrumenter();
		
		Collection<String> skippedClasses
		    = Arrays.asList("joeq.Class.jq_ClassFileConstants",
		    		"javassist.bytecode.Opcode",
		    		"joeq.Class.jq_DontAlign",
		    		"net.sf.saxon.",
		    		"joeq.",
		    		"javassist.");
		instrumenter.setSkippedClasses(skippedClasses);
		
		Collection<String> instrumentedClasses
		   = Arrays.asList("chord.");
		instrumenter.setInstrumentedClassPrefix(instrumentedClasses);
		
        instrumenter.instrument(TestInstrumentJChord.jchord_notrace, jchord_everystmt);
		
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testDiagnoseRelatedStmt() {
        Collection<ConfPropOutput> outputs = TestSliceJChordConfigOptions.getJChordConfOutputs();
		
		String[] badStmtFiles = new String[]{"./experiments/jchord-baseline-stmt/bad_no_race-related.txt"};
		String[] goodStmtFiles = new String[]{
				"./experiments/jchord-baseline-stmt/good_datarace-related.txt",
//				"./experiments/jchord-baseline-stmt/good_deadlock-related.txt",
//				"./experiments/jchord-baseline-stmt/good_dlog-related.txt",
//				"./experiments/jchord-baseline-stmt/do-nothing-stmt-related.txt",
//				"./experiments/jchord-baseline-stmt/ctxts-analysis-stmt-related.txt",
//				"./experiments/jchord-baseline-stmt/print-project-stmt-related.txt"
				};
		
		StmtCoverageBasedDiagnoserMain.findResponsibleOptions(outputs, badStmtFiles, goodStmtFiles);
	}
	
	//28
	public void testDiagnoseStmt() {
        Collection<ConfPropOutput> outputs = TestSliceJChordConfigOptions.getJChordConfOutputs();
		
		String[] badStmtFiles = new String[]{"./experiments/jchord-baseline-stmt/bad_no_race.txt"};
		String[] goodStmtFiles = new String[]{
				"./experiments/jchord-baseline-stmt/good_datarace.txt",
//				"./experiments/jchord-baseline-stmt/good_deadlock.txt",
//				"./experiments/jchord-baseline-stmt/good_dlog.txt",
//				"./experiments/jchord-baseline-stmt/do-nothing-stmt.txt",
//				"./experiments/jchord-baseline-stmt/ctxts-analysis-stmt.txt",
//				"./experiments/jchord-baseline-stmt/print-project-stmt.txt"
				};
		
		StmtCoverageBasedDiagnoserMain.findResponsibleOptions(outputs, badStmtFiles, goodStmtFiles);
	}
	
	public void testDiagnoseOptionsByInvariantAnalysis() throws Exception {
		String goodInvFile1 = "D:\\research\\configurations\\workspace\\configuration-detector\\subjects\\jchord\\datarace.inv.gz";
		String goodInvFile2 = "D:\\research\\configurations\\workspace\\configuration-detector\\subjects\\jchord\\deadlock.inv.gz";
		String goodInvFile3 = "D:\\research\\configurations\\workspace\\configuration-detector\\subjects\\jchord\\dlog.inv.gz";
		
		String goodInvFile4 = "D:\\research\\configurations\\workspace\\configuration-detector\\subjects\\jchord\\donothing.inv.gz";
		String goodInvFile5 = "D:\\research\\configurations\\workspace\\configuration-detector\\subjects\\jchord\\print-project.inv.gz";
		String goodInvFile6 = "D:\\research\\configurations\\workspace\\configuration-detector\\subjects\\jchord\\ctxts-analysis.inv.gz";
		
		String badInvFile = "D:\\research\\configurations\\workspace\\configuration-detector\\subjects\\jchord\\datarace-norace.inv.gz";
		//Set<String> affectedMethods = getAffectedMethods(goodInvFile, badInvFile);
		Collection<ConfPropOutput> confs = TestSliceJChordConfigOptions.getJChordConfOutputs();
		
		List<ConfEntity> entities = MethodBasedDiagnoser.computeResponsibleOptions(
				Arrays.asList(goodInvFile1, goodInvFile2, goodInvFile3, goodInvFile4, goodInvFile5, goodInvFile6), 
				badInvFile, confs);
		
		System.out.println(entities.size());
		int i = 0;
		for(ConfEntity entity : entities) {
			System.out.println((i+1) + ". " + entity);
			i++;
		}
		
	}
	
}
