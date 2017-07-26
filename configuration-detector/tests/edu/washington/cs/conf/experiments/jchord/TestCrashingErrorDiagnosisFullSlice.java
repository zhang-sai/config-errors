package edu.washington.cs.conf.experiments.jchord;

import java.util.Collection;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.ConfDiagnosisOutput;
import edu.washington.cs.conf.diagnosis.CrashingErrorDiagnoser;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.diagnosis.PredicateProfileBasedDiagnoser;
import edu.washington.cs.conf.diagnosis.PredicateProfileTuple;
import edu.washington.cs.conf.experiments.ChordExpUtils;
import edu.washington.cs.conf.experiments.jchord.TestCrashingErrorDiagnosisExperimental.DiagnosisType;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;
import junit.framework.TestCase;

public class TestCrashingErrorDiagnosisFullSlice extends TestCase {

	String invalidCtxtKind = "./experiments/jchord-crashing-error-full-slice/chord-crash-invalid-ctxt-kind-full-slice.txt";
	String invalidReflectKind = "./experiments/jchord-crashing-error-full-slice/chord-crash-invalid-reflect-kind-full-slice.txt";
	String invalidScopeKind = "./experiments/jchord-crashing-error-full-slice/chord-crash-invalid-scope-kind-full-slice.txt";
	String noMainMethod = "./experiments/jchord-crashing-error-full-slice/chord-crash-no-main-method-full-slice.txt";
	String noMainInClass = "./experiments/jchord-crashing-error-full-slice/chord-crash-no-main-method-in-class-full-slice.txt";
	String noPrintRels = "./experiments/jchord-crashing-error-full-slice/chord-crash-no-print-rels-full-slice.txt";
	String noSuchAnalysis = "./experiments/jchord-crashing-error-full-slice/chord-crash-no-such-analysis-full-slice.txt";
	String printNonexist = "./experiments/jchord-crashing-error-full-slice/chord-crash-print-nonexist-class-full-slice.txt";
	String wrongClasspath = "./experiments/jchord-crashing-error-full-slice/chord-crash-wrong-classpath-full-slice.txt";
	
	
	
	String ctxtAnalysis = "./experiments/jchord-database/simpletest-ctxt-analysis-full-slice.txt";
	String datarace = "./experiments/jchord-database/simpletest-has-race-full-slice.txt";
	String deadlock = "./experiments/jchord-database/simpletest-deadlock-full-slice.txt";
	String dlog = "./experiments/jchord-database/simpletest-dlog-full-slice.txt";
	String printproject = "./experiments/jchord-database/simpletest-print-project-full-slice.txt";
	String donothing = "./experiments/jchord-database/simpletest-do-nothing-full-slice.txt";
	
	String[] db = new String[]{ctxtAnalysis, datarace, deadlock,
			dlog, printproject, donothing};
	
	boolean randomSelection = false;
	
	boolean similarSelection = false;
	
	String[] getDb() {
		if(randomSelection) {
			Object[] randomArray = Utils.randomSubArray(db);
			String[] array = new String[randomArray.length];
			for(int i = 0 ; i < randomArray.length; i++) {
				array[i] = randomArray[i].toString();
			}
			return array;
		}
		return db;
	}
	
	String[] single_db = new String[]{datarace};
	
	static Collection<ConfPropOutput> slices = null;
	
	void diagnoseCauses(String badTraceFile, String stackTraceFile, String[] goodTraceDb) {
		if(slices == null) {
			System.out.println("Compute slice...");
			slices = TestSliceJChordConfigOptions.getJChordConfOutputsFullSlice(null);
//			slices = TestSliceJChordConfigOptions.sliceOptionsInJChord(ChordExpUtils.getChordConfList(), false);
		}
		System.out.println("Start diagnosing...");
		float threshold = similarSelection ? MainAnalyzer.default_threshold : CrashingErrorDiagnoser.default_experiment_value;
		List<ConfDiagnosisOutput> results = TestCrashingErrorDiagnosisExperimental.doDiagnosis(DiagnosisType.CRASHING, badTraceFile, stackTraceFile,
				goodTraceDb,  threshold);
		System.out.println("Start to rank by stack trace coverage...");
		List<ConfDiagnosisOutput> outputs = TestCrashingErrorDiagnosisExperimental.rankByStackTraceCoverage(stackTraceFile, results, slices); //make it number 1
		StringBuilder sb = new StringBuilder();
		int i = 1; 
		for(ConfDiagnosisOutput o : outputs) {
			sb.append(i++ + " " + o.getConfEntity().getFullConfName());
			sb.append(Globals.lineSep);
    	}
	
	    String outputFile = badTraceFile;
	    if(randomSelection) {
	    	outputFile = badTraceFile + "_random";
	    }
	    if(similarSelection) {
	    	outputFile = badTraceFile + "_similar";
	    }
	    outputFile = outputFile + "_result.txt";
		try  {
		    Files.createIfNotExist(outputFile);
		    Files.writeToFile(sb.toString(), outputFile);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		//reclaim memory
		outputs.clear();
	}
	
	public void testRunAll() {
		testInvalidCtxtKind();
		testInvalidReflectKind ();
		testInvalidScopeKind();
		testNoMainMethod();
		testNoMainInClass();
		testNoPrintRels ();
		testNoSuchAnalysis();
		testPrintNonexist();
		testWrongClasspath();
	}
	
	public void testRunRandom() {
		randomSelection = true;
		testRunAll();
	}
	
	public void testRunSimilar() {
		similarSelection = true;
		testRunAll();
	}
	
	@Override
	public void tearDown() {
		PredicateProfileTuple.USE_CACHE = false;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = false;
	}
	
	//individual tests
	public void testInvalidCtxtKind() {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(invalidCtxtKind, TestCrashingErrorDiagnosisExperimental.noCtxtKindStackTrace, getDb());
	}
	public void testInvalidReflectKind () {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(invalidReflectKind, TestCrashingErrorDiagnosisExperimental.invalidReflectKindStackTrace, getDb());
	}
	public void testInvalidScopeKind() {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(invalidScopeKind, TestCrashingErrorDiagnosisExperimental.invalidScopeKindStackTrace, getDb());
	}
	public void testNoMainMethod() {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(noMainMethod, TestCrashingErrorDiagnosisExperimental.noMainClassStackTrace, getDb());
	}
	public void testNoMainInClass() {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(noMainInClass, TestCrashingErrorDiagnosisExperimental.noMainMethodInClassStackTrace, getDb());
	}
	public void testNoPrintRels () {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(noPrintRels, TestCrashingErrorDiagnosisExperimental.printInvalidRelsStackTrace, getDb());
	}
	public void testNoSuchAnalysis() {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(noSuchAnalysis, TestCrashingErrorDiagnosisExperimental.noSuchAnalysisStackTrace, getDb());
	}
	public void testPrintNonexist() {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(printNonexist, TestCrashingErrorDiagnosisExperimental.printNoClassStackTrace, getDb());
	}
	public void testWrongClasspath() {
		PredicateProfileTuple.USE_CACHE = true;
		PredicateProfileBasedDiagnoser.SAVE_MEMORY = true;
		diagnoseCauses(wrongClasspath, TestCrashingErrorDiagnosisExperimental.wrongClassPathStackTrace, getDb());
	}
}
