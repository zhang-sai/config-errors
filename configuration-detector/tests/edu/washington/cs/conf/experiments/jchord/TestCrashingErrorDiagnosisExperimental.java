package edu.washington.cs.conf.experiments.jchord;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.ConfDiagnosisOutput;
import edu.washington.cs.conf.diagnosis.CrashingErrorDiagnoser;
import edu.washington.cs.conf.diagnosis.PredicateProfileTuple;
import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.diagnosis.TraceAnalyzer;
import edu.washington.cs.conf.experiments.ChordExpUtils;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;
import junit.framework.TestCase;

public class TestCrashingErrorDiagnosisExperimental extends TestCase {
	
	public static String invalidReflectKind
	    = "./experiments/jchord-crashing-error/chord-crash-invalid-reflect-kind.txt";
	public static String invalidReflectKindStackTrace
    = "./experiments/jchord-crashing-error/chord-crash-invalid-reflect-kind-stacktrace.txt";
	
	public static String invalidScopeKind
       = "./experiments/jchord-crashing-error/chord-crash-invalid-scope-kind.txt";
	public static String invalidScopeKindStackTrace
       = "./experiments/jchord-crashing-error/chord-crash-invalid-scope-kind-stacktrace.txt";
	
	public static String noMainClass
       = "./experiments/jchord-crashing-error/chord-crash-no-main-method.txt";
	public static String noMainClassStackTrace
       = "./experiments/jchord-crashing-error/chord-crash-no-main-method-stacktrace.txt";
	
	public static String noSuchAnalysis
       = "./experiments/jchord-crashing-error/chord-crash-no-such-analyses.txt";
	public static String noSuchAnalysisStackTrace
       = "./experiments/jchord-crashing-error/chord-crash-no-such-analyses-stacktrace.txt";
	
	public static String printInvalidRels
       = "./experiments/jchord-crashing-error/chord-crash-print-invalid-rels.txt";
	public static String printInvalidRelsStackTrace
       = "./experiments/jchord-crashing-error/chord-crash-print-invalid-rels-stacktrace.txt";
	
	public static String wrongClassPath
       = "./experiments/jchord-crashing-error/chord-crash-wrong-class-path.txt";
	public static String wrongClassPathStackTrace
       = "./experiments/jchord-crashing-error/chord-crash-wrong-class-path-stacktrace.txt";
	
	public static String noMainMethodInClass
	   = "./experiments/jchord-crashing-error/chord-crash-class-has-no-main-method.txt";
	public static String noMainMethodInClassStackTrace
	   = "./experiments/jchord-crashing-error/chord-crash-class-has-no-main-method-stacktrace.txt";
	
	public static String noCtxtKind
	   = "./experiments/jchord-crashing-error/chord-crash-no-ctxt-kind.txt";
	public static String noCtxtKindStackTrace
	   = "./experiments/jchord-crashing-error/chord-crash-no-ctxt-kind-stacktrace.txt";
	
	public static String printNoClass
	   = "./experiments/jchord-crashing-error/chord-crash-print-no-class.txt";
	public static String printNoClassStackTrace
	   = "./experiments/jchord-crashing-error/chord-crash-print-no-class-stacktrace.txt";
	
	public static String[] allCrashingTraces = new String[]{invalidReflectKind,
		invalidScopeKind, noMainClass, noSuchAnalysis, printInvalidRels,
		wrongClassPath, noMainMethodInClass, noCtxtKind, printNoClass};
	
	public static String[] allStackTraces = new String[]{invalidReflectKindStackTrace,
		invalidScopeKindStackTrace, noMainClassStackTrace, noSuchAnalysisStackTrace, printInvalidRelsStackTrace,
		wrongClassPathStackTrace, noMainMethodInClassStackTrace, noCtxtKindStackTrace, printNoClassStackTrace};
	
	//the good run
	public static String goodRunTrace
	   = "./experiments/jchord-database/simpletest-has-race.txt";
	public static String goodRunTrace1
	  = "./experiments/jchord-database/deadlock_simpletest.txt";
	public static String goodRunTrace2
	  = "./experiments/jchord-database/dlog_simpletest.txt";
	public static String goodRunTrace3
	  = "./experiments/jchord-database/ctxtsanalysis_default.txt";
	public static String goodRunTrace4
	  = "./experiments/jchord-database/do_nothing.txt";
	public static String goodRunTrace5
	  = "./experiments/jchord-database/print_projects.txt";
	
	public static String[] goodRunDb = new String[]{goodRunTrace, goodRunTrace1, goodRunTrace2,
			goodRunTrace3, goodRunTrace4, goodRunTrace5};
	
	public void testCalcDistances() {
		CommonUtils.compareTraceDistance(goodRunTrace, invalidReflectKind, DistanceType.INTERPRODUCT, 0.9998555f);
		CommonUtils.compareTraceDistance(goodRunTrace, invalidScopeKind, DistanceType.INTERPRODUCT, 0.6472167f);
		CommonUtils.compareTraceDistance(goodRunTrace, noMainClass, DistanceType.INTERPRODUCT, 0.6461625f);
		CommonUtils.compareTraceDistance(goodRunTrace, noSuchAnalysis, DistanceType.INTERPRODUCT, 0.6580911f);
		CommonUtils.compareTraceDistance(goodRunTrace, printInvalidRels, DistanceType.INTERPRODUCT, 0.10745859f);
		CommonUtils.compareTraceDistance(goodRunTrace, wrongClassPath, DistanceType.INTERPRODUCT, 0.6448741f);
	}
	
	//rank 18 reflectKind
	public void testUsingNonCrashingDiagnosis1() {
		List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.NONCRASHING, invalidReflectKind, invalidReflectKindStackTrace,
				new String[]{goodRunTrace});
		dumpOutputs(results);
	}
	
	//rank 39 scopeKind
    public void testUsingNonCrashingDiagnosis2() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.NONCRASHING, invalidScopeKind, invalidScopeKindStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //rank 23 for mainClassName
    public void testUsingNonCrashingDiagnosis3() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.NONCRASHING, noMainClass, noMainClassStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //rank 10, runAnalyses
    public void testUsingNonCrashingDiagnosis4() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.NONCRASHING, noSuchAnalysis, noSuchAnalysisStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //rank 10 printRels
    public void testUsingNonCrashingDiagnosis5() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.NONCRASHING, printInvalidRels, printInvalidRelsStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //rank 22 userClassPathName
    public void testUsingNonCrashingDiagnosis6() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.NONCRASHING, wrongClassPath, wrongClassPathStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //24 mainClassName
    public void testUsingNonCrashingDiagnosis7() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.NONCRASHING, noMainMethodInClass, noMainMethodInClassStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //FIXME not runnable now
    public void testUsingNonCrashingDiagnosis8() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.NONCRASHING, noCtxtKind, noCtxtKindStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //9 printClasses
    public void testUsingNonCrashingDiagnosis9() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.NONCRASHING, printNoClass, printNoClassStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    public enum DiagnosisType {NONCRASHING, CRASHING, STACKTRACE}
    
    public static List<ConfDiagnosisOutput>  doDiagnosis(DiagnosisType t, String badTraceFile, String badStackTraceFiles,
    		String[] goodTraceFiles) {
    	return doDiagnosis(t, badTraceFile, badStackTraceFiles, goodTraceFiles, CrashingErrorDiagnoser.default_experiment_value);
    }
    
    public static List<ConfDiagnosisOutput>  doDiagnosis(DiagnosisType t, String badTraceFile, String badStackTraceFiles,
    		String[] goodTraceFiles, float threshold) {
    	//create the repo
    	List<ConfEntity> jchordConfList = ChordExpUtils.getChordConfList();
		ConfEntityRepository repo = new ConfEntityRepository(jchordConfList);
		
		//create profile tuples
    	PredicateProfileTuple badTuple = TraceAnalyzer.createBadProfileTuple(badTraceFile, badStackTraceFiles);
    	List<PredicateProfileTuple> goodTuples = new LinkedList<PredicateProfileTuple>();
    	for(String goodTrace : goodTraceFiles) {
    		goodTuples.add(TraceAnalyzer.createGoodProfileTuple(goodTrace, "goodTrace"));
    	}
    	
    	System.out.println("use crashing error diagnoser: " + t);
    	
    	//start the diagnosis
    	CrashingErrorDiagnoser diagnoser = new CrashingErrorDiagnoser(goodTuples, badTuple, repo);
    	diagnoser.setSimilarThreshold(threshold);
    	diagnoser.setStackTraces(badStackTraceFiles);

    	if(t.equals(DiagnosisType.NONCRASHING)) {
    		return diagnoser.computeResponsibleOptionsAsNonCrashingErrors();
    	} else if (t.equals(DiagnosisType.CRASHING)) {
    		return diagnoser.computeResponsibleOptionsInCrashingTrace();
    	} else if (t.equals(DiagnosisType.STACKTRACE)) {
//    		return diagnoser.computeResponsibleOptionsWithStackTrace(RankType.IMPORT_SUM);
    		return diagnoser.computeResponsibleOptionsWithStackTrace();
    	} else {
    		throw new Error();
    	}
    }
    
    void dumpOutputs(List<ConfDiagnosisOutput> results) {
    	System.out.println("Number: " + results.size());
    	System.out.println("----------- explanation ------------");
    	for(int i = 0; i < results.size(); i++) {
    		ConfDiagnosisOutput o = results.get(i);
    		System.out.println((i + 1) + ". " + o.getConfEntity());
    		System.out.println("  final score: " + o.getFinalScore());
    		System.out.println("  " + o.getBriefExplanation());
    	}
    }
    
    void rankByStackTraceCoverage(String stackTraceFile, Collection<ConfDiagnosisOutput> outputs) {
    	rankByStackTraceCoverage(stackTraceFile, outputs, false);
    }
    
    static List<ConfDiagnosisOutput> rankByStackTraceCoverage(String stackTraceFile, Collection<ConfDiagnosisOutput> outputs, boolean pruned) {
    	Collection<ConfPropOutput> slices = TestSliceJChordConfigOptions.sliceOptionsInJChord(ChordExpUtils.getChordConfList(), pruned);
    	return rankByStackTraceCoverage(stackTraceFile, outputs, slices);
    }
    
    static List<ConfDiagnosisOutput> rankByStackTraceCoverage(String stackTraceFile, Collection<ConfDiagnosisOutput> outputs,
    		Collection<ConfPropOutput> slices) {
    	String[] stackTraces = Files.readWholeNoExp(stackTraceFile).toArray(new String[0]);
    	Map<ConfDiagnosisOutput, Integer> map = CrashingErrorDiagnoser.computeMatchedStacktraceNum(slices, outputs, stackTraces);
    	System.out.println("----------- number of overlap ----------");
    	for(ConfDiagnosisOutput o : map.keySet()) {
    		System.out.println(o.getConfEntity().getFullConfName());
    		System.out.println("     " + map.get(o));
    	}
    	
    	System.out.println("------------final ranking---------------");
    	
    	List<ConfDiagnosisOutput> rankedOutputs = CrashingErrorDiagnoser.rankConfigurationOptions(map, outputs);
    	for(ConfDiagnosisOutput o : rankedOutputs) {
    		System.out.println(o.getConfEntity().getFullConfName());
    	}
    	
    	return rankedOutputs;
    }
    
    void rankByStackTraceDistanceInSlice(String stackTraceFile, Collection<ConfDiagnosisOutput> outputs) {
    	rankByStackTraceDistanceInSlice(stackTraceFile, outputs, false);
    }
    
    public static void rankByStackTraceDistanceInSlice(String stackTraceFile, Collection<ConfDiagnosisOutput> outputs, boolean pruned) {
    	rankByStackTraceDistanceInSlice(stackTraceFile, outputs, pruned, false);
    }
    
    static void rankByStackTraceDistanceInSlice(String stackTraceFile, Collection<ConfDiagnosisOutput> outputs, boolean pruned, boolean noLib) {

    	Map<Float, List<ConfDiagnosisOutput>> multiRanking = new LinkedHashMap<Float, List<ConfDiagnosisOutput>>();
    	for(ConfDiagnosisOutput output : outputs) {
    		Float finalScore = output.getFinalScore();
    		if(!multiRanking.containsKey(finalScore)) {
    			multiRanking.put(finalScore, new LinkedList<ConfDiagnosisOutput>());
    		}
    		multiRanking.get(finalScore).add(output);
    	}
    	
    	multiRanking = Utils.sortByKey(multiRanking, false);
    	for(Float f : multiRanking.keySet()) {
    		System.out.println(f);
    		for(ConfDiagnosisOutput output : multiRanking.get(f)) {
    			System.out.println("    " + output.getConfEntity().getFullConfName());
    		}
    		System.out.println();
    	}
    	
    	//then sort each multi-ranking
    	Collection<ConfPropOutput> slices = TestSliceJChordConfigOptions.sliceOptionsInJChord(ChordExpUtils.getChordConfList(), pruned);
    	String[] stackTraces = Files.readWholeNoExp(stackTraceFile).toArray(new String[0]);
    	for(Float f : multiRanking.keySet()) {
    		List<ConfDiagnosisOutput> list = multiRanking.get(f);
    		
    		//rank the list based on the slice distance
    		//roughly
    		Map<ConfDiagnosisOutput, Integer> map = new LinkedHashMap<ConfDiagnosisOutput, Integer>();
    		for(ConfDiagnosisOutput output : list) {
    			Map<String, Integer> distance = CrashingErrorDiagnoser.computeStackTraceDistance(slices, output, stackTraces, noLib);
    			for(String m : distance.keySet()) {
    				Integer d = distance.get(m);
    				if(d != Integer.MAX_VALUE) {
    					map.put(output, d);
    					System.out.println(" >> distance: " + output.getConfEntity().getFullConfName() + ", : " + d);
    					break;
    				}
    			}
    		}
    		List<ConfDiagnosisOutput>  rankedList = Utils.sortByValueAndReturnKeys(map, true);
    		
    		multiRanking.put(f, rankedList);
    	}
    	System.out.println("---------  after final ranking ----------");
    	for(Float f : multiRanking.keySet()) {
    		System.out.println(f);
    		for(ConfDiagnosisOutput output : multiRanking.get(f)) {
    			System.out.println("    " + output.getConfEntity().getFullConfName());
    		}
    		System.out.println();
    	}
    }
    
    /**
     * Focus on the wrong trace only
     * */
    //18, reflectKind, can rank  1 when using stack trace info
    public void testDiagnoseWithCrashingTrace1() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.CRASHING, invalidReflectKind, invalidReflectKindStackTrace,
    			new String[]{goodRunTrace});
		dumpOutputs(results);
		rankByStackTraceCoverage(invalidReflectKindStackTrace, results, false); //make it number 1
	}
    
    //XXX A possible way is to improve the slice accuracy
	
    //1, scopeKind
    public void testDiagnoseWithCrashingTrace2() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.CRASHING, invalidScopeKind, invalidScopeKindStackTrace,
//    			new String[]{goodRunTrace, goodRunTrace1,
//    			goodRunTrace2, goodRunTrace3, goodRunTrace4
//    			, goodRunTrace5
//    			}
    	        goodRunDb
    	        , 0.3f
    	        );
    	dumpOutputs(results);
//    	rankByStackTraceCoverage(invalidScopeKindStackTrace, results, false);
	}
    
    //24, mainClassName
    //can rank to 1 - by distance
    //0.985317
//    chord.project.Config.mainClassName
//    chord.project.analyses.BasicDynamicAnalysis.runBefore
//    chord.program.Program.runBefore
//    chord.project.Config.traceKind
    //
    public void testDiagnoseWithCrashingTrace3() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.CRASHING, noMainClass, noMainClassStackTrace,
    			new String[]{goodRunTrace, goodRunTrace1,
    			    goodRunTrace2, goodRunTrace3, goodRunTrace4,
    	            goodRunTrace5
    	            },
    			0.3f);
    	dumpOutputs(results);
    	rankByStackTraceDistanceInSlice(noMainClassStackTrace, results, false);
	}
    
    //11 runAnalyses
    // 17 use stack info
    public void testDiagnoseWithCrashingTrace4() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.CRASHING, noSuchAnalysis, noSuchAnalysisStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
//    	rankByStackTraceCoverage(noSuchAnalysisStackTrace, results, false);
    	rankByStackTraceDistanceInSlice(noSuchAnalysisStackTrace, results, false);
	}
    
    //9, printRels
    //noise ranked before: chord.project.analyses.BasicDynamicAnalysisrunBefore,
    // chord.program.Program : runBefore  ---  chord.analyses.method.RelExtraEntryPoints : extraMethodsList
    // --- chord.project.Config : instrSchemeFileName ---  chord.project.Config : traceKind 
    // --- chord.project.Config : instrKind --- chord.project.Config : dlogAnalysisPathName
    // ---chord.project.Config : javaAnalysisPathName ---
    //ranked 15
    public void testDiagnoseWithCrashingTrace5() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.CRASHING, printInvalidRels, printInvalidRelsStackTrace, 
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
    	//rankByStackTraceCoverage(printInvalidRelsStackTrace, results, false);
    	rankByStackTraceDistanceInSlice(printInvalidRelsStackTrace, results, false);
	}
    
    //22 userClassPathName
    //rank 8
    public void testDiagnoseWithCrashingTrace6() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.CRASHING, wrongClassPath, wrongClassPathStackTrace,
    			new String[]{goodRunTrace, goodRunTrace1,
			    goodRunTrace2, goodRunTrace3, goodRunTrace4,
	            goodRunTrace5
	            },
	            0.3f);
    	dumpOutputs(results);
//    	rankByStackTraceCoverage(wrongClassPathStackTrace, results, false);
    	rankByStackTraceDistanceInSlice(wrongClassPathStackTrace, results, false);
	}
    
    //3 mainClassName ranked 3 with stack trace info
    public void testDiagnoseWithCrashingTrace7() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.CRASHING, noMainMethodInClass, noMainMethodInClassStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
    	rankByStackTraceDistanceInSlice(noMainMethodInClassStackTrace, results, false);
	}
    
    //rank 1
    public void testDiagnoseWithCrashingTrace8() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.CRASHING, noCtxtKind, noCtxtKindStackTrace,
    			new String[]{goodRunTrace3}); //NOTE a diff ctxt run
    	dumpOutputs(results);
	}
    
    public void testDiagnoseWithCrashingTrace8_1() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.CRASHING, noCtxtKind, noCtxtKindStackTrace,
    			new String[]{goodRunTrace}); //NOTE a diff ctxt run
    	dumpOutputs(results);
	}
    
    //9 printClasses , drop to 15 when using distance
    public void testDiagnoseWithCrashingTrace9() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.CRASHING, printNoClass, printNoClassStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
    	rankByStackTraceDistanceInSlice(printNoClassStackTrace, results, false, true);
	}
    
    
    /**
     * Use the stack trace
     * */
    //19 reflectKind
    public void testDiagnoseWithCrashingStackTrace1() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.STACKTRACE, invalidReflectKind, invalidReflectKindStackTrace,
    			new String[]{goodRunTrace});
		dumpOutputs(results);
		rankByStackTraceCoverage(invalidReflectKindStackTrace, results);
	}
	
    //31, scopeKind
    public void testDiagnoseWithCrashingStackTrace2() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.STACKTRACE, invalidScopeKind, invalidScopeKindStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
    	rankByStackTraceCoverage(invalidScopeKindStackTrace, results);
	}
    
    //26 mainClassName
    public void testDiagnoseWithCrashingStackTrace3() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.STACKTRACE, noMainClass, noMainClassStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
    	rankByStackTraceCoverage(noMainClassStackTrace, results);
	}
    
    //13 runAnalyses
    public void testDiagnoseWithCrashingStackTrace4() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.STACKTRACE, noSuchAnalysis, noSuchAnalysisStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
    	rankByStackTraceCoverage(noSuchAnalysisStackTrace, results);
	}
    
    //13 printRels
    public void testDiagnoseWithCrashingStackTrace5() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.STACKTRACE, printInvalidRels, printInvalidRelsStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
    	rankByStackTraceCoverage(printInvalidRelsStackTrace, results);
	}
    
    //25 userClassPathName
    public void testDiagnoseWithCrashingStackTrace6() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.STACKTRACE, wrongClassPath, wrongClassPathStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
    	rankByStackTraceCoverage(wrongClassPathStackTrace, results, false);
	}
    
  //27 mainClassName
    public void testDiagnoseWithCrashingStackTrace7() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.STACKTRACE, noMainMethodInClass, noMainMethodInClassStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //FIXME not runnable now
    public void testDiagnoseWithCrashingStackTrace8() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.STACKTRACE, noCtxtKind, noCtxtKindStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
    
    //14 printClasses
    public void testDiagnoseWithCrashingStackTrace9() {
    	List<ConfDiagnosisOutput> results = doDiagnosis(DiagnosisType.STACKTRACE, printNoClass, printNoClassStackTrace,
    			new String[]{goodRunTrace});
    	dumpOutputs(results);
	}
}