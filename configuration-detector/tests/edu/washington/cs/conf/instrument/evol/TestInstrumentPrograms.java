package edu.washington.cs.conf.instrument.evol;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import edu.washington.cs.conf.instrument.InstrumentStats;
import edu.washington.cs.conf.util.Log;
import junit.framework.TestCase;

public class TestInstrumentPrograms extends TestCase {

	public static String jmeter_28_sigmap = "jmeter-2.8-sigmap.txt";
	public void testJMeter28() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\original\\ApacheJMeter_core.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_core-instrumetned.jar";
		this.doInstrumentation(inputJar, outputJar, false, jmeter_28_sigmap);
	}
	
	public static String jmeter_29_sigmap = "jmeter-2.9-sigmap.txt";
	public static String jmeter29InputJar = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\original\\ApacheJMeter_core.jar";
	public void testJMeter29() {
		String inputJar = jmeter29InputJar;
		String outputJar = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_core-instrumetned.jar";
		this.doInstrumentation(inputJar, outputJar, false, jmeter_29_sigmap);
		InstrumentStats.writeInstrumentedPositions("./tmp-output-folder/inst_points.txt");
	}
	
	public static String randoop_121_sigmap = "randoop-1.2.1-sigmap.txt";
	public void testRandoop1_2_1() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.2.1\\randoop-1.2.1.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.2.1\\randoop-1.2.1-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("randoop."), false, randoop_121_sigmap);
	}
	
	public static String randoop_132_sigmap = "randoop-1.3.2-sigmap.txt";
	public void testRandoop1_3_2() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\randoop-1.3.2.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\randoop-1.3.2-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("randoop."), false, randoop_121_sigmap);
	}
	
	public static String synoptic_05_sigmap = "synoptic-0.5-sigmap.txt";
	public void testSynoptic0_5() {
		synoptic_05_sigmap = null;
		String inputJar = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.0.5\\synoptic.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.0.5\\synoptic-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("synoptic."),
				false, synoptic_05_sigmap);
	}
	
	public static String synoptic_10_sigmap = "synoptic-1.0-sigmap.txt";
	public void testSynoptic1_0() {
		synoptic_10_sigmap = null;
		String inputJar = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.1\\lib\\synoptic.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.1\\lib\\synoptic-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("synoptic."),
				false, synoptic_10_sigmap);
	}
	
	public static String weka_361_sigmap = "weka-3.6.1-sigmap.txt";
	public void testWeka_3_6_1() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\weka\\weka-3-6-1\\weka-3-6-1\\weka.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\weka\\weka-3-6-1\\weka-3-6-1\\weka-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("weka."), false, weka_361_sigmap);
	}
	
	public static String weka_362_sigmap = "weka-3.6.2-sigmap.txt";
	public void testWeka_3_6_2() {
		String inputJar = "D:\\research\\confevol\\subject-programs\\weka\\weka-3-6-2\\weka-3-6-2\\weka.jar";
		String outputJar = "D:\\research\\confevol\\subject-programs\\weka\\weka-3-6-2\\weka-3-6-2\\weka-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("weka."), false, weka_362_sigmap);
	}
	
	public static String chord_20_sigmap = "chord-2.0-sigmap.txt";
	public void testJChord2_0() {
		chord_20_sigmap = null;
		Log.logConfig("./log.txt");
		String inputJar = "D:\\research\\configurations\\workspace\\chord-2.0\\chord.jar";
		String outputJar = "D:\\research\\configurations\\workspace\\chord-2.0\\chord-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("chord."),
				false, chord_20_sigmap, true /*stmt*/, true /*method*/, true /*predicate*/);
		Log.removeLogging();
	}
	
	/**
	 * NOTE:
	 * for ssa problem, should instrument joeq.* package, remember
	 * to replace the corresponding sig map and the trace file
	 * */
	public static String chord_21_sigmap = "chord-2.1-sigmap.txt";
    public void testJChord2_1() {
    	chord_21_sigmap = null;
    	Log.logConfig("./log.txt");
    	String inputJar = "D:\\research\\configurations\\workspace\\chord-2.1\\chord.jar";
		String outputJar = "D:\\research\\configurations\\workspace\\chord-2.1\\chord-instrumented.jar";
		this.doInstrumentation(inputJar, outputJar, Arrays.asList("chord."/*, "joeq."*/),
				false, chord_21_sigmap, true /*stmt*/, true /*method*/, true /*predicate*/);
		Log.removeLogging();
	}
    
//    @Deprecated
//    public void testJChord_trunk() {
//    	String inputJar = "D:\\research\\configurations\\workspace\\chord-trunk\\chord.jar";
//		String outputJar = "D:\\research\\configurations\\workspace\\chord-trunk\\chord-instrumented.jar";
//		this.doInstrumentation(inputJar, outputJar, Arrays.asList("chord."),
//				false, null, false /*stmt*/, false /*method*/, false /*predicate*/);
//	}
    
    public static String javalanche_36 = "javalanche-0.3.6-sigmap.txt";
    static String[] javalanche_pkgs = new String[]{"de.unisb.cs.st.javalanche."};
    public void testJavalanche036() {
    	String inputJarFile = "D:\\research\\confevol\\subject-programs\\javalanche\\versions\\javalanche\\javalanche-0.3.6-bin\\lib\\original\\javalanche-mutation-0.3.6.jar";
    	String outputJarFile = "D:\\research\\confevol\\subject-programs\\javalanche\\versions\\javalanche\\javalanche-0.3.6-bin\\lib\\javalanche-mutation-0.3.6-instrumented.jar";
    	this.doInstrumentation(inputJarFile, outputJarFile, Arrays.asList(javalanche_pkgs), false, javalanche_36);
    }
    
    public static String javalanche_40 = "javalanche-0.4.0-sigmap.txt";
    public static String javalanche40InputJar = "D:\\research\\confevol\\subject-programs\\javalanche\\versions\\javalanche\\javalanche-0.4.0-bin\\lib\\original\\javalanche-0.4.jar"; 
    public void testJavalanche040() {
    	String inputJarFile = javalanche40InputJar;
    	String outputJarFile = "D:\\research\\confevol\\subject-programs\\javalanche\\versions\\javalanche\\javalanche-0.4.0-bin\\lib\\javalanche-0.4-instrumented.jar";
    	this.doInstrumentation(inputJarFile, outputJarFile, Arrays.asList(javalanche_pkgs), false, javalanche_40);
    }
    

    private void doInstrumentation(String input, String output, Collection<String> appPkgs,
    		boolean disasm, String sigMapFileName, boolean instrumentEveryStmt,
    		boolean instrumentEveryMethod, boolean instrumentEveryPredicate) {
    	PredicateInstrumenter instrumenter = new PredicateInstrumenter(appPkgs, Collections.<String> emptyList());
		instrumenter.setDisasm(disasm);
		instrumenter.setUseSigMap(sigMapFileName != null);
		instrumenter.setEveryStmt(instrumentEveryStmt);
		instrumenter.setEveryMethod(instrumentEveryMethod);
		instrumenter.setEveryPredicate(instrumentEveryPredicate);
		try {
		    instrumenter.instrument(input, output);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if(sigMapFileName != null) {
			instrumenter.saveSigMappingsNoExp(sigMapFileName);
		}
		InstrumentStats.showInstrumentationStats();
    }
    
    private void doInstrumentation(String input, String output, Collection<String> appPkgs,
    		boolean disasm, String sigMapFileName) {
    	doInstrumentation(input, output, appPkgs, disasm, sigMapFileName, true, true, true);
	}
    
	private void doInstrumentation(String input, String output, boolean disasm, String sigMapFileName) {
		this.doInstrumentation(input, output, Collections.<String> emptyList(), disasm, sigMapFileName);
	}
}
