package edu.washington.cs.conf.experiments.jchord;

import java.util.Collection;

import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.StmtCoverageBasedDiagnoserMain;
import junit.framework.TestCase;

public class TestCrashingErrorDiagnosisStmts extends TestCase {

	String ctxtAnalysis = "./experiments/jchord-baseline-stmt/ctxts-analysis-stmt.txt";
	String doNothing = "./experiments/jchord-baseline-stmt/do-nothing-stmt.txt";
	String good_datarace = "./experiments/jchord-baseline-stmt/good_datarace.txt";
	String good_deadlock = "./experiments/jchord-baseline-stmt/good_deadlock.txt";
	String good_dlog = "./experiments/jchord-baseline-stmt/good_dlog.txt";
	String print_project = "./experiments/jchord-baseline-stmt/print-project-stmt.txt";
	
	String ctxtAnalysisRelated = "./experiments/jchord-baseline-stmt/ctxts-analysis-stmt-related.txt";
	String doNothingRelated = "./experiments/jchord-baseline-stmt/do-nothing-stmt-related.txt";
	String good_dataraceRelated = "./experiments/jchord-baseline-stmt/good_datarace-related.txt";
	String good_deadlockRelated = "./experiments/jchord-baseline-stmt/good_deadlock-related.txt";
	String good_dlogRelated = "./experiments/jchord-baseline-stmt/good_dlog-related.txt";
	String print_projectRelated = "./experiments/jchord-baseline-stmt/print-project-stmt-related.txt";
	
	String[] db = new String[]{ctxtAnalysisRelated, doNothingRelated, good_dataraceRelated, good_deadlockRelated,
			good_dlogRelated, print_projectRelated};
	
	String[] dbRelated = new String[]{
			ctxtAnalysis, 
			doNothing, 
			good_datarace,
			good_deadlock, 
			good_dlog, 
			print_project
			};
	
	String[] selected = new String[]{
			ctxtAnalysis, 
//			doNothing, 
			good_datarace,
//			good_deadlock, 
//			good_dlog, 
//			print_project
			};
	
	//crashing error coverage
	String invalidCtxtKind = "./experiments/jchord-crashing-error-linecoverage/chord-crash-invalid-ctxt-kind-stmt.txt";
	String invalidReflectKind = "./experiments/jchord-crashing-error-linecoverage/chord-crash-invalid-reflect-kind-stmt.txt";
	String invalidScopeKind = "./experiments/jchord-crashing-error-linecoverage/chord-crash-invalid-scope-kind-stmt.txt";
	String noMainMethodInClass = "./experiments/jchord-crashing-error-linecoverage/chord-crash-no-main-method-in-class-stmt.txt";
	String noMainMethod = "./experiments/jchord-crashing-error-linecoverage/chord-crash-no-main-method-stmt.txt";
	String noPrintRel = "./experiments/jchord-crashing-error-linecoverage/chord-crash-no-print-rels-stmt.txt";
	String noSuchAnalysis = "./experiments/jchord-crashing-error-linecoverage/chord-crash-no-such-analyses-stmt.txt";
	String printNonexist = "./experiments/jchord-crashing-error-linecoverage/chord-crash-print-nonexist-class-stmt.txt";
	String wrongClasspath = "./experiments/jchord-crashing-error-linecoverage/chord-crash-wrong-classpath-stmt.txt";
	
	String invalidCtxtKindRelated = "./experiments/jchord-crashing-error-linecoverage/chord-crash-invalid-ctxt-kind-stmt-related.txt";
	String invalidReflectKindRelated = "./experiments/jchord-crashing-error-linecoverage/chord-crash-invalid-reflect-kind-stmt-related.txt";
	String invalidScopeKindRelated = "./experiments/jchord-crashing-error-linecoverage/chord-crash-invalid-scope-kind-stmt-related.txt";
	String noMainMethodInClassRelated = "./experiments/jchord-crashing-error-linecoverage/chord-crash-no-main-method-in-class-stmt-related.txt";
	String noMainMethodRelated = "./experiments/jchord-crashing-error-linecoverage/chord-crash-no-main-method-stmt-related.txt";
	String noPrintRelRelated = "./experiments/jchord-crashing-error-linecoverage/chord-crash-no-print-rels-stmt-related.txt";
	String noSuchAnalysisRelated = "./experiments/jchord-crashing-error-linecoverage/chord-crash-no-such-analyses-stmt-related.txt";
	String printNonexistRelated = "./experiments/jchord-crashing-error-linecoverage/chord-crash-print-nonexist-class-stmt-related.txt";
	String wrongClasspathRelated = "./experiments/jchord-crashing-error-linecoverage/chord-crash-wrong-classpath-stmt-related.txt";
	
	static Collection<ConfPropOutput> outputs = TestSliceJChordConfigOptions.getJChordConfOutputs();
	
	void diagnoseByStmt(String stmtFile) {
		String outputFile = stmtFile + "_result.txt";
		StmtCoverageBasedDiagnoserMain.findResponsibleOptions(outputs, new String[]{stmtFile}, db,
				outputFile, new String[]{"chord."});
	}
	
	void diagnoseByRelatedStmt(String stmtFile) {
		String outputFile = stmtFile + "_related_result.txt";
		StmtCoverageBasedDiagnoserMain.findResponsibleOptions(outputs,
				new String[]{stmtFile}, 
				//selected, 
				dbRelated,
				outputFile, new String[]{"chord."});
	}
	
	public void testAll() {
		testInvalidCtxtKind();
		testInvalidReflectKind();
		testInvalidScopeKind();
		testNoMainMethodInClass();
		testNoMainMethod();
		testNoPrintRel();
		testPrintNonexist();
		testNoSuchAnalysis();
		testWrongClasspath();
	}
	
	public void testAllRelated() {
		//25
		testInvalidCtxtKindRelated();
		//5
		testInvalidReflectKindRelated();
		//1
		testInvalidScopeKindRelated();
		//1
		testNoMainMethodInClassRelated();
		//1
		testNoMainMethodRelated();
		//20
		testNoPrintRelRelated();
		//13
		testPrintNonexistRelated();
		//17
		testNoSuchAnalysisRelated();
		//21
		testWrongClasspathRelated();
	}
	
	public void testInvalidCtxtKind() {
		this.diagnoseByStmt(invalidCtxtKind);
	}
	
	public void testInvalidCtxtKindRelated() {
		this.diagnoseByRelatedStmt(invalidCtxtKindRelated);
	}
	
	public void testInvalidReflectKind() {
		this.diagnoseByStmt(invalidReflectKind);
	}
	
	public void testInvalidReflectKindRelated() {
		this.diagnoseByRelatedStmt(invalidReflectKindRelated);
	}
	
	public void testInvalidScopeKind() {
		this.diagnoseByStmt(invalidScopeKind);
	}
	
	public void testInvalidScopeKindRelated() {
		this.diagnoseByRelatedStmt(invalidScopeKindRelated);
	}
	
	public void testNoMainMethodInClass() {
		this.diagnoseByStmt(noMainMethodInClass);
	}
	
	public void testNoMainMethodInClassRelated() {
		this.diagnoseByRelatedStmt(noMainMethodInClassRelated);
	}
	
    public void testNoMainMethod() {
    	this.diagnoseByStmt(noMainMethod);
	}
    
    public void testNoMainMethodRelated() {
    	this.diagnoseByRelatedStmt(noMainMethodRelated);
	}
    
    public void testNoPrintRel() {
    	this.diagnoseByStmt(noPrintRel);
	}
    
    public void testNoPrintRelRelated() {
    	this.diagnoseByRelatedStmt(noPrintRelRelated);
	}
    
    public void testPrintNonexist() {
    	this.diagnoseByStmt(printNonexist);
	}
    
    public void testPrintNonexistRelated() {
    	this.diagnoseByRelatedStmt(printNonexistRelated);
	}
    
    public void testNoSuchAnalysis() {
    	this.diagnoseByStmt(noSuchAnalysis);
	}
    
    public void testNoSuchAnalysisRelated() {
    	this.diagnoseByRelatedStmt(noSuchAnalysisRelated);
	}
    
    public void testWrongClasspath() {
    	this.diagnoseByStmt(wrongClasspath);
	}
    
    public void testWrongClasspathRelated() {
    	this.diagnoseByRelatedStmt(wrongClasspathRelated);
	}
}

