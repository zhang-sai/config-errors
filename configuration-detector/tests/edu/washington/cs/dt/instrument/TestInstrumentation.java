package edu.washington.cs.dt.instrument;

import java.util.List;

import edu.washington.cs.conf.instrument.InstrumentStats;
import edu.washington.cs.conf.util.Files;
import junit.framework.TestCase;

public class TestInstrumentation extends TestCase {

	public void testToyExample() throws Exception {
		String inputFile = "E:\\testisolation\\dt-instrument-folder\\dtoy.jar";
		String outputFile = "E:\\testisolation\\dt-instrument-folder\\dtoy-instrument.jar";
		String[] prefixes = new String[]{"test.junit.dt.DTTest.test"};
		FieldAccessInstrumenter instrumenter = new FieldAccessInstrumenter();
		instrumenter.setMethodClassPrefix(prefixes);
		instrumenter.instrument(inputFile, outputFile);
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testCrystal() throws Exception {
		String inputFile = "E:\\testisolation\\dt-instrument-folder\\crystal\\crystal-dt.jar";
		String outputFile = "E:\\testisolation\\dt-instrument-folder\\crystal\\crystal-dt-instrument.jar";
		String[] prefixes = new String[]{"crystal.client.ClientPreferencesTest.test",
				"crystal.client.ConflictDaemonTest.test",
				"crystal.client.PreferencesGUIEditorFrameTest.test",
				"crystal.client.ProjectPreferencesTest.test",
				"crystal.model.DataSourceTest.test",
				"crystal.model.LocalStateResultTest.test",
				"crystal.model.RelationshipTest.test",
				"crystal.model.RevisionHistoryTest.test",
				"crystal.server.GuidanceCheckerTest.test",
				"crystal.server.HgLogParser.test",
				"crystal.server.TestConstants.test",
				"crystal.server.TestGitStateChecker.test",
				"crystal.server.TestHgStateChecker.test",
				"crystal.util.SetOperationsTest.test",
				"crystal.util.SpringLayoutUtilityTest.test",
				"crystal.util.ValidInputCheckerTest.test"
		};
		FieldAccessInstrumenter instrumenter = new FieldAccessInstrumenter();
		instrumenter.setMethodClassPrefix(prefixes);
		instrumenter.instrument(inputFile, outputFile);
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testCrystal_auto() throws Exception {
		String inputFile = "E:\\testisolation\\dt-instrument-folder\\crystal\\crystal-auto-tests.jar";
		String outputFile = "E:\\testisolation\\dt-instrument-folder\\crystal\\crystal-auto-tests-instrument.jar";
		String[] prefixes = new String[]{
				"randoop.crystal.RandoopTest0.test",
				"randoop.crystal.RandoopTest1.test",
				"randoop.crystal.RandoopTest2.test",
				"randoop.crystal.RandoopTest3.test",
				"randoop.crystal.RandoopTest4.test",
				"randoop.crystal.RandoopTest5.test",
				"randoop.crystal.RandoopTest6.test"
		};
		FieldAccessInstrumenter instrumenter = new FieldAccessInstrumenter();
		instrumenter.setMethodClassPrefix(prefixes);
		instrumenter.instrument(inputFile, outputFile);
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testXMLSecurity() throws Exception {
		String inputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\xmlsecurity-dt.jar";
		String outputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\xmlsecurity-dt-instrument.jar";
		String[] prefixes = new String[]{
				"randoop.xmlsecurity.RandoopTest0.test",
				"randoop.xmlsecurity.RandoopTest1.test"
		};
		FieldAccessInstrumenter instrumenter = new FieldAccessInstrumenter();
		instrumenter.setMethodClassPrefix(prefixes);
		instrumenter.instrument(inputFile, outputFile);
		
		inputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\xmlsecurity-auto-tests.jar";
		outputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\xmlsecurity-auto-tests-instrument.jar";
		instrumenter.instrument(inputFile, outputFile);
		
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testXMLSecurity_libs() throws Exception {
		String inputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\original\\xalan.jar";
		String outputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\xalan-instrument.jar";
		
		FieldAccessInstrumenter instrumenter = new FieldAccessInstrumenter();
		instrumenter.instrument(inputFile, outputFile);
		
		inputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\original\\xercesImpl.jar";
		outputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\xercesImpl-instrument.jar";
		instrumenter.instrument(inputFile, outputFile);
		
		inputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\original\\xercesImpl.jar";
		outputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\xercesImpl-instrument.jar";
		instrumenter.instrument(inputFile, outputFile);
		
		inputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\original\\xml-apis.jar";
		outputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\xml-apis-instrument.jar";
		instrumenter.instrument(inputFile, outputFile);
		
		inputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\original\\xmlParserAPIs.jar";
		outputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\xmlParserAPIs-apis-instrument.jar";
		instrumenter.instrument(inputFile, outputFile);
		
		inputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\original\\stylebook-1.0-b3_xalan-2.jar";
		outputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\stylebook-1.0-b3_xalan-2-apis-instrument.jar";
		instrumenter.instrument(inputFile, outputFile);
		
		inputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\original\\style-apachexml.jar";
		outputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\style-apachexml-instrument.jar";
		instrumenter.instrument(inputFile, outputFile);
		
		inputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\original\\log4j-1.2.8.jar";
		outputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\log4j-1.2.8-instrument.jar";
		instrumenter.instrument(inputFile, outputFile);
		
		inputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\original\\commons-logging.jar";
		outputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\commons-logging-instrument.jar";
		instrumenter.instrument(inputFile, outputFile);
		
		inputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\original\\commons-logging-api.jar";
		outputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\commons-logging-api-instrument.jar";
		instrumenter.instrument(inputFile, outputFile);
		
		inputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\original\\junit3.8.1.jar";
		outputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\junit3.8.1-instrument.jar";
		instrumenter.instrument(inputFile, outputFile);
		
		inputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\original\\junitSIR.jar";
		outputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\junitSIR-instrument.jar";
		instrumenter.instrument(inputFile, outputFile);
		
		inputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\original\\bc-jce-jdk13-114.jar";
		outputFile = "E:\\testisolation\\dt-instrument-folder\\xml-security\\libs\\bc-jce-jdk13-114-instrument.jar";
		instrumenter.instrument(inputFile, outputFile);
		
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testInstrumentJFreechart() throws Exception {
		String inputFile = "E:\\testisolation\\dt-instrument-folder\\jfreechart\\original\\jfreechart-1.0.15-src-tests.jar";
		String outputFile = "E:\\testisolation\\dt-instrument-folder\\jfreechart\\jfreechart-1.0.15-src-tests-instrumented.jar";
		String[] prefixes = allJFreeChatTests();
		FieldAccessInstrumenter instrumenter = new FieldAccessInstrumenter();
		instrumenter.setMethodClassPrefix(prefixes);
		instrumenter.instrument(inputFile, outputFile);
		InstrumentStats.showInstrumentationStats();
	}
	
	static String[] allJFreeChatTests() {
		List<String> list = Files.readWholeNoExp("E:\\testisolation\\dt-instrument-folder\\jfreechart\\jfreechart-all-manual-tests.txt");
		return list.toArray(new String[0]);
	}
}