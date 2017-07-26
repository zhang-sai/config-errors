package edu.washington.cs.conf.experiments.jchord;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.diagnosis.MethodBasedDiagnoser;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;
import junit.framework.TestCase;

public class TestCrashingErrorDiagnosisInvariants extends TestCase {

	//good invariants
	String ctxtAnalysis = "./experiments/jchord-database-invariants/ctxts-analysis.inv.gz";
	String datarace = "./experiments/jchord-database-invariants/datarace.inv.gz";
	String deadlock = "./experiments/jchord-database-invariants/deadlock.inv.gz";
	String dlog = "./experiments/jchord-database-invariants/dlog.inv.gz";
	String donothing = "./experiments/jchord-database-invariants/donothing.inv.gz";
	String printproject = "./experiments/jchord-database-invariants/print-project.inv.gz";
	
	String[] invDb = new String[]{ctxtAnalysis, datarace, deadlock, dlog, donothing,
			printproject};
	
	String[] selectDb = new String[]{ctxtAnalysis, datarace, printproject};
	
	//crashing invariants
	String invalidCtxtsKind = "./experiments/jchord-crashing-error-invariants/chord-crash-invalid-ctxts-kind.inv.gz";
	String invalidReflectKind = "./experiments/jchord-crashing-error-invariants/chord-crash-invalid-reflect-kind.inv.gz";
	String invalidScopeKind = "./experiments/jchord-crashing-error-invariants/chord-crash-invalid-scope-kind.inv.gz";
	String noMainMethodInClass = "./experiments/jchord-crashing-error-invariants/chord-crash-no-main-method-in-class.inv.gz";
	String noMainMethod = "./experiments/jchord-crashing-error-invariants/chord-crash-no-main-method.inv.gz";
	String noSuchAnalysis = "./experiments/jchord-crashing-error-invariants/chord-crash-no-such-analysis.inv.gz";
	String printNonexistRel = "./experiments/jchord-crashing-error-invariants/chord-crash-print-noexist-rels.inv.gz";
	String wrongClasspath = "./experiments/jchord-crashing-error-invariants/chord-crash-wrong-classpath.inv.gz";
	String printNonexistClass = "./experiments/jchord-crashing-error-invariants/chord-crash-print-nonexist-classes.inv.gz";
	
	static Collection<ConfPropOutput> confs = TestSliceJChordConfigOptions.getJChordConfOutputs();
	
	private void diagnoseErrorCauses(String invFile) {
		List<ConfEntity> entities = MethodBasedDiagnoser.computeResponsibleOptions(
				Arrays.asList(invDb), 
//				Arrays.asList(selectDb),
				invFile, confs);
		
		StringBuilder sb = new StringBuilder();
		sb.append(entities.size());
		sb.append(Globals.lineSep);
		
		System.out.println(entities.size());
		int i = 0;
		for(ConfEntity entity : entities) {
			System.out.println((i+1) + ". " + entity);
			sb.append((i+1) + ". " + entity);
			sb.append(Globals.lineSep);
			i++;
		}
		
		//write results to a file
		String newfile = invFile + "-result.txt";
		try {
			Files.createIfNotExist(newfile);
			Files.writeToFile(sb.toString(), newfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testRunAll() {
		testInvalidCtxtsKind();
		testInvalidReflectKind();
		testInvalidScopeKind();
		testNoMainMethodInClass();
		testNoMainMethod();
		testNoSuchAnalysis();
		testPrintNonexistRel();
		testWrongClasspath();
		testPrintNonexistClass();
	}
	
	public void testInvalidCtxtsKind() {
		diagnoseErrorCauses(invalidCtxtsKind);
	}
	
    public void testInvalidReflectKind() {
    	diagnoseErrorCauses(invalidReflectKind);
	}
    
    public void testInvalidScopeKind() {
    	diagnoseErrorCauses(invalidScopeKind);
	}
    
    public void testNoMainMethodInClass() {
    	diagnoseErrorCauses(noMainMethodInClass);
	}
    
    public void testNoMainMethod() {
    	diagnoseErrorCauses(noMainMethod);
	}
    
    public void testNoSuchAnalysis() {
    	diagnoseErrorCauses(noSuchAnalysis);
	}
    
    public void testPrintNonexistRel() {
    	diagnoseErrorCauses(printNonexistRel);
	}
    
    public void testWrongClasspath() {
    	diagnoseErrorCauses(wrongClasspath);
	}
    
    public void testPrintNonexistClass() {
    	diagnoseErrorCauses(printNonexistClass);
	}
}
