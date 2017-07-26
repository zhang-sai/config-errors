package edu.washington.cs.conf.analysis.evol;

import java.io.File;
import java.io.FileNotFoundException;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;

import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class CodeAnalyzerRepository {

	public static final String randoopMain = "Lrandoop/main/Main";
	//for the randoop case
	public static final String randoop121Path = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.2.1\\randoop-1.2.1.jar";
	public static CodeAnalyzer getRandoop121Analyzer() {
		CodeAnalyzer coder121 = new CodeAnalyzer(randoop121Path, randoopMain);
		coder121.slicer.setExclusionFile("JavaAllExclusions.txt");
		coder121.slicer.setCGType(CG.ZeroCFA);
		
		return coder121;
	}
	
	public static final String randoop132Path = "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\randoop-1.3.2.jar"
		+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\lib\\plume.jar"
		+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\randoop\\randoop-1.3.2\\lib\\jakarta-oro-2.0.8.jar";
	public static CodeAnalyzer getRandoop132Analyzer() {
		CodeAnalyzer coder132 = new CodeAnalyzer(randoop132Path, randoopMain);
		coder132.slicer.setExclusionFile("JavaAllExclusions.txt");
		coder132.slicer.setCGType(CG.ZeroCFA);
		
		return coder132;
	}
	
	public static String synopticMainClass = "Lsynoptic/main/Main";
	public static final String oldSynopticPath = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.0.5\\synoptic.jar"
		+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.0.5\\lib\\plume.jar"
		+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.0.5\\lib\\junit-4.9b2.jar";
	
	public static CodeAnalyzer getSynopticOldAnalyzer() {
		CodeAnalyzer oldCoder = new CodeAnalyzer(oldSynopticPath, synopticMainClass);
		oldCoder.slicer.setExclusionFile("JavaAllExclusions.txt");
		oldCoder.slicer.setCGType(CG.ZeroCFA);
		
		return oldCoder;
	}
	
	public static final String newSynopticPath = "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.1\\lib\\synoptic.jar"
		+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.1\\lib\\plume.jar"
		+ Globals.pathSep + "D:\\research\\confevol\\subject-programs\\synoptic\\synoptic-0.1\\lib\\junit-4.9b2.jar";
	public static CodeAnalyzer getSynopticNewAnalyzer() {
		CodeAnalyzer newCoder = new CodeAnalyzer(newSynopticPath, synopticMainClass);
		newCoder.slicer.setExclusionFile("JavaAllExclusions.txt");
		newCoder.slicer.setCGType(CG.ZeroCFA);
		
		return newCoder;
	}
	
	public static String wekaMainClass = "Lweka/classifiers/trees/J48";
	public final static String oldWekaPath = "D:\\research\\confevol\\subject-programs\\weka\\weka-3-6-1\\weka-3-6-1\\weka.jar";
	public static CodeAnalyzer getWekaOldAnalyzer() {
		CodeAnalyzer oldCoder = new CodeAnalyzer(oldWekaPath, wekaMainClass);
		oldCoder.slicer.setExclusionFile("JavaAllExclusions.txt");
		oldCoder.slicer.setCGType(CG.ZeroCFA);
		return oldCoder;
	}
	
	public final static String newWekaPath = "D:\\research\\confevol\\subject-programs\\weka\\weka-3-6-2\\weka-3-6-2\\weka.jar";
	public static CodeAnalyzer getWekaNewAnalyzer() {
		CodeAnalyzer newCoder = new CodeAnalyzer(newWekaPath, wekaMainClass);
		newCoder.slicer.setExclusionFile("JavaAllExclusions.txt");
		newCoder.slicer.setCGType(CG.ZeroCFA);
		
		return newCoder;
	}
	
	static String jmeterStartClass = "org.apache.jmeter.JMeter";
	static String jmeterMainClassSig = "Lorg/apache/jmeter/NewDriver";
	static String jmeterStartMethod = "start";
	
	static String jmeterReportClass = "org.apache.jmeter.reporters.ResultCollector";
	
	//void start(String[] args)
	public static CodeAnalyzer getJMeterOldAnalyzer() {
		String classPath = getOldJMeterPath();
		
		CodeAnalyzer oldAnalyzer = new CodeAnalyzer(classPath, jmeterMainClassSig);
		oldAnalyzer.slicer.setExclusionFile("JavaAllExclusions.txt");
		oldAnalyzer.slicer.setExclusionFile("JMeterExclusions-new.txt");
		oldAnalyzer.slicer.setCGType(CG.RTA);
//		oldAnalyzer.slicer.setCGType(CG.ZeroCFA);
//		oldAnalyzer.slicer.setCGType(CG.OneCFA);
		
		//must customize the entry points
		oldAnalyzer.slicer.buildClassHierarchy();
		ClassHierarchy cha = oldAnalyzer.slicer.getClassHierarchy();
		Iterable<Entrypoint> entryPoints = WALAUtils.createEntrypoints(jmeterStartClass, jmeterStartMethod, cha);
		Iterable<Entrypoint> points = entryPoints;
//		points = Utils.combine(entryPoints, publicPoints);
		
		oldAnalyzer.slicer.setEntrypoints(points);
		//must explicitly set the data dependence
		oldAnalyzer.slicer.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		
		return oldAnalyzer;
	}
	
	
	public static CodeAnalyzer getJMeterNewAnalyzer() {
		String classPath = getNewJMeterPath();
		
		//the main class will be overriden later by user-specified entry points
		CodeAnalyzer newAnalyzer = new CodeAnalyzer(classPath, jmeterMainClassSig);
		newAnalyzer.slicer.setExclusionFile("JavaAllExclusions.txt");
		newAnalyzer.slicer.setExclusionFile("JMeterExclusions-new.txt");
		newAnalyzer.slicer.setCGType(CG.RTA);
//		newAnalyzer.slicer.setCGType(CG.ZeroCFA);
		
		//must customize the entry points
		newAnalyzer.slicer.buildClassHierarchy();
		ClassHierarchy cha = newAnalyzer.slicer.getClassHierarchy();
		Iterable<Entrypoint> entryPoints = WALAUtils.createEntrypoints(jmeterStartClass, jmeterStartMethod, cha);
		Iterable<Entrypoint> points = entryPoints;
//	    points = Utils.combine(entryPoints, publicPoints);
		
		newAnalyzer.slicer.setEntrypoints(points);
		//must explicitly set the data dependence
		newAnalyzer.slicer.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		
		return newAnalyzer;
	}
	
	public static String chordMainClass = "Lchord/project/Main";
	public static String chordExclusions = "ChordExclusions.txt";
	public static String jChordOldPath ="D:\\research\\configurations\\workspace\\chord-2.0\\chord.jar"; 
	public static CodeAnalyzer getJChordOldAnalyzer() {
		String oldChordPath = jChordOldPath;
		CodeAnalyzer oldCoder = new CodeAnalyzer(oldChordPath, chordMainClass);
		oldCoder.slicer.setExclusionFile(chordExclusions);
		oldCoder.slicer.setCGType(CG.ZeroCFA);
		return oldCoder;
	}
	
	public static String jChordNewPath =  "D:\\research\\configurations\\workspace\\chord-2.1\\chord.jar";
	public static CodeAnalyzer getJChordNewAnalyzer() {
		String newChordPath = jChordNewPath;
		CodeAnalyzer newCoder = new CodeAnalyzer(newChordPath, chordMainClass);
		newCoder.slicer.setExclusionFile(chordExclusions);
		newCoder.slicer.setCGType(CG.ZeroCFA);
		return newCoder;
	}
	
//	static String javalancheOldPath = null;
	static String javalancheMainClass = "Lde/unisb/cs/st/javalanche/mutation/analyze/AnalyzeMain";
	static String javalancheEntryClass = "de.unisb.cs.st.javalanche.mutation.runtime.testDriver.MutationTestDriver";
	static String javalancheEntryMethod = "run";
	public static CodeAnalyzer getJavalancheOldAnalyzer() {
		CodeAnalyzer oldCoder = new CodeAnalyzer(getJavalancheOldPath(), javalancheMainClass);
		oldCoder.slicer.setExclusionFile("JavalancheExclusions.txt");
//		oldCoder.slicer.setCGType(CG.ZeroCFA);
		
//		oldCoder.slicer.buildClassHierarchy();
//		ClassHierarchy cha = oldCoder.slicer.getClassHierarchy();
//		Iterable<Entrypoint> entryPoints = WALAUtils.createEntrypoints(javalancheEntryClass, javalancheEntryMethod, cha);
//		Iterable<Entrypoint> points = entryPoints;
//		oldCoder.slicer.setEntrypoints(points);
		
		oldCoder.slicer.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		oldCoder.slicer.setCGType(CG.RTA);
		
		return oldCoder;
	}
	
//	static String javalancheNewPath = null;
	public static CodeAnalyzer getJavalancheNewAnalyzer() {
		CodeAnalyzer newCoder = new CodeAnalyzer(getJavalancheNewPath(), javalancheMainClass);
		newCoder.slicer.setExclusionFile("JavalancheExclusions.txt");
//		newCoder.slicer.setCGType(CG.ZeroCFA);
		newCoder.slicer.setCGType(CG.RTA);
		
//		
//
//		
//		newCoder.slicer.buildClassHierarchy();
//		ClassHierarchy cha = newCoder.slicer.getClassHierarchy();
//		Iterable<Entrypoint> entryPoints = WALAUtils.createEntrypoints(javalancheEntryClass, javalancheEntryMethod, cha);
//		Iterable<Entrypoint> points = Utils.combine(c1, entryPoints);
//		newCoder.slicer.setEntrypoints(points);
		
		return newCoder;
	}

	
	//the long classpath for JMeter
	public static String getOldJMeterPath() {
		String startJar = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\bin\\ApacheJMeter.jar";
		
		String allJars = 
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\activation-1.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\avalon-framework-4.1.4.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\bsf-2.4.0.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\bsf-api-3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\bsh-2.0b5.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\bshclient.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-codec-1.6.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-collections-3.2.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-httpclient-3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-io-2.2.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-jexl-1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-jexl-2.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-lang-2.6.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-lang3-3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-logging-1.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\commons-net-3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\excalibur-datasource-1.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\excalibur-instrument-1.0.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\excalibur-logger-1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\excalibur-pool-1.2.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_components.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_ftp.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_functions.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_http.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_java.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_jdbc.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_jms.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_junit.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_ldap.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_mail.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_monitors.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_native.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_report.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\ApacheJMeter_tcp.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\evoltracer.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\ext\\original\\ApacheJMeter_core.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\geronimo-jms_1.1_spec-1.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\htmllexer-2.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\htmlparser-2.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\httpclient-4.2.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\httpcore-4.2.2.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\httpmime-4.2.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\jcharts-0.7.5.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\jdom-1.1.2.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\jorphan.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\jtidy-r938.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\junit-4.10.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\junit\\test.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\logkit-2.0.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\mail-1.4.4.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\oro-2.0.8.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\rhino-1.7R3.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\serializer-2.7.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\soap-2.3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\xalan-2.7.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\xercesImpl-2.9.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\xml-apis-1.3.04.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\xmlgraphics-commons-1.3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\xmlpull-1.1.3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\xpp3_min-1.1.4c.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.8\\lib\\xstream-1.4.2.jar";

		
		return allJars + Globals.pathSep + startJar;
	}
	public static String getNewJMeterPath() {
		String allJars =
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\activation-1.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\avalon-framework-4.1.4.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\bsf-2.4.0.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\bsh-2.0b5.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\bshclient.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-codec-1.6.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-collections-3.2.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-httpclient-3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-io-2.2.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-jexl-1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-jexl-2.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-lang-2.6.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-lang3-3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-logging-1.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\commons-net-3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\excalibur-datasource-1.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\excalibur-instrument-1.0.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\excalibur-logger-1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\excalibur-pool-1.2.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_components.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_ftp.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_functions.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_http.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_java.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_jdbc.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_jms.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_junit.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_ldap.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_mail.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_monitors.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_native.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_report.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_tcp.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\evoltracer.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\original\\ApacheJMeter_core.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\geronimo-jms_1.1_spec-1.1.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\htmllexer-2.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\htmlparser-2.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\httpclient-4.2.3.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\httpcore-4.2.3.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\httpmime-4.2.3.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\jcharts-0.7.5.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\jdom-1.1.2.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\jodd-core-3.4.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\jodd-lagarto-3.4.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\jorphan.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\jsoup-1.7.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\jtidy-r938.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\junit-4.10.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\junit\\test.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\logkit-2.0.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\mail-1.4.4.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\oro-2.0.8.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\rhino-1.7R4.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\serializer-2.7.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\slf4j-api-1.7.2.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\soap-2.3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\tika-core-1.3.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\tika-parsers-1.3.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\xalan-2.7.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\xercesImpl-2.9.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\xml-apis-1.3.04.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\xmlgraphics-commons-1.3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\xmlpull-1.1.3.1.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\xpp3_min-1.1.4c.jar" + Globals.pathSep +
			"D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\xstream-1.4.2.jar";

		return allJars;
	}
	
	public static String getJavalancheOldPath() {
		String dir = "D:\\research\\confevol\\subject-programs\\javalanche\\versions\\javalanche\\javalanche-0.3.6-bin\\lib\\";
		String allJars = 
			dir + "original\\javalanche-mutation-0.3.6.jar" + Globals.pathSep +
			dir + "adabu2-core-0.4-SNAPSHOT.jar" +Globals.pathSep +
			dir + "adabu2-hoare-0.4-SNAPSHOT.jar" +Globals.pathSep +
			dir + "adabu2-tracer-0.4-modified.jar" +Globals.pathSep +
			dir + "antlr-2.7.6.jar" + Globals.pathSep +
			dir + "asm-3.0.jar" + Globals.pathSep +
			dir + "asm-all-3.1.jar" + Globals.pathSep +
			dir + "asm-commons-3.1.jar" + Globals.pathSep +
			dir + "asm-tree-3.1.jar" +Globals.pathSep +
			dir + "asm-util-3.1.jar" +Globals.pathSep +
			dir + "bytecodeTransformer-0.1.jar" +Globals.pathSep +
			dir + "c3p0-0.9.1.2.jar" +Globals.pathSep +
			dir + "cglib-2.1_3.jar" +Globals.pathSep +
			dir + "commons-collections-3.2.1.jar" +Globals.pathSep +
			dir + "commons-io-1.4.jar" +Globals.pathSep +
			dir + "commons-lang-2.3.jar" +Globals.pathSep +
			dir + "commons-logging-1.0.4.jar" +Globals.pathSep +
			dir + "daikon-local.jar" +Globals.pathSep +
			dir + "dom4j-1.6.1.jar" +Globals.pathSep +
			dir + "ds-util-transformed.jar" +Globals.pathSep +
			dir + "easymock-2.3.jar" +Globals.pathSep +
			dir + "ehcache-1.2.3.jar" +Globals.pathSep +
			dir + "evoltracer.jar" +Globals.pathSep +
			dir + "freemarker-2.3.4.jar" +Globals.pathSep +
			dir + "google-collect-snapshot-20080530.jar" +Globals.pathSep +
			dir + "hamcrest-all-1.1.jar" +Globals.pathSep +
			dir + "hibernate-3.2.0.ga.jar" +Globals.pathSep +
			dir + "hibernate-annotations-3.2.0.ga.jar" +Globals.pathSep +
			dir + "hibernate-commons-annotations-3.3.0.ga.jar" +Globals.pathSep +
			dir + "hibernate-tools-3.2.0.beta9a.jar" +Globals.pathSep +
			dir + "hsqldb.jar" +Globals.pathSep +
			dir + "invariants-0.1.jar" +Globals.pathSep +
			dir + "j2h-1.3.1.jar" +Globals.pathSep +
			dir + "jarjar-1.2.jar" +Globals.pathSep +
			dir + "javaagent.jar" +Globals.pathSep +
			dir + "jgrapht-jdk1.5-0.7.3.jar" +Globals.pathSep +
			dir + "jta-1.0.1B.jar" +Globals.pathSep +
			dir + "jtidy-r8-20060801.jar" +Globals.pathSep +
			dir + "junit-4.7.jar" +Globals.pathSep +
			dir + "log4j-1.2.14.jar" +Globals.pathSep +
			dir + "mysql-connector-java-5.0.4.jar" +Globals.pathSep +
			dir + "persistence-api-1.0.jar" +Globals.pathSep +
			dir + "prefuse-beta-20060220.jar" +Globals.pathSep +
			dir + "sibrelib-local.jar" +Globals.pathSep +
			dir + "util-0.1.jar" +Globals.pathSep +
			dir + "xpp3_min-1.1.3.4.O.jar" +Globals.pathSep +
			dir + "xstream-transformed.jar";
		return allJars;
	}
	
	public static String getJavalancheNewPath() {
	  String dir = "D:\\research\\confevol\\subject-programs\\javalanche\\versions\\javalanche\\javalanche-0.4.0-bin\\lib\\";
	  String allJars = 
		dir + "original\\javalanche-0.4.jar" + Globals.pathSep +
	    dir + "antlr-2.7.6.jar" +Globals.pathSep +
		dir + "asm-debug-all-3.3.1.jar" +Globals.pathSep +
		dir + "bsh-2.0b4.jar" +Globals.pathSep +
		dir + "c3p0-0.9.1.2.jar" +Globals.pathSep +
		dir + "cglib-nodep-2.1_3.jar" +Globals.pathSep +
		dir + "commons-collections-2.1.1.jar" +Globals.pathSep +
		dir + "commons-io-2.0.1.jar" +Globals.pathSep +
		dir + "commons-lang-2.6.jar" +Globals.pathSep +
		dir + "commons-logging-1.0.4.jar" +Globals.pathSep +
		dir + "dom4j-1.6.1.jar" +Globals.pathSep +
		dir + "ds-util-0.3.2.1.jar" +Globals.pathSep +
		dir + "ehcache-1.2.3.jar" +Globals.pathSep +
		dir + "evoltracer.jar" +Globals.pathSep +
		dir + "freemarker-2.3.8.jar" +Globals.pathSep +
		dir + "guava-r09.jar" +Globals.pathSep +
		dir + "hamcrest-core-1.3.RC2.jar" +Globals.pathSep +
		dir + "hamcrest-integration-1.3.RC2.jar" +Globals.pathSep +
		dir + "hamcrest-library-1.3.RC2.jar" +Globals.pathSep +
		dir + "hibernate-3.2.7.ga.jar" +Globals.pathSep +
		dir + "hibernate-annotations-3.3.0.ga.jar" +Globals.pathSep +
		dir + "hibernate-commons-annotations-3.3.0.ga.jar" +Globals.pathSep +
		dir + "hibernate-tools-3.2.3.GA.jar" +Globals.pathSep +
		dir + "hsqldb-1.8.0.10.jar" +Globals.pathSep +
		dir + "jgrapht-jdk1.5-0.7.3.jar" +Globals.pathSep +
		dir + "jta-1.0.1B.jar" +Globals.pathSep +
		dir + "jtidy-r8-20060801.jar" +Globals.pathSep +
		dir + "junit-dep-4.8.2.jar" +Globals.pathSep +
		dir + "log4j-1.2.16.jar" +Globals.pathSep +
		dir + "mysql-connector-java-5.1.17.jar" +Globals.pathSep +
		dir + "opencsv-2.3.jar" +Globals.pathSep +
		dir + "persistence-api-1.0.jar" +Globals.pathSep +
		dir + "xmlpull-1.1.3.1.jar" +Globals.pathSep +
		dir + "xpp3_min-1.1.4c.jar" +Globals.pathSep +
		dir + "xstream-1.4.1.jar";
		return allJars;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		String dir = "D:\\research\\confevol\\subject-programs\\javalanche\\versions\\javalanche\\javalanche-0.3.6-bin\\lib\\";
		for(File f : Files.getFileListing(new File(dir))) {
			if(f.getName().endsWith(".jar")) {
			    System.out.println(f.getName());
			}
		}
	}
}