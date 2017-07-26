package edu.washington.cs.conf.mutation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

public class TestScriptExecutor extends TestCase {

	public void testJMeterExamplesNoThread() {
		String dir = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin";
		List<String> args = Arrays.asList(
				"cmd.exe", "/C",
				"jmeter", "-n", "-t",
				"../threadgroup.jmx", "-l",
				"../output.jtl", "-j", "../testplan.log");
		
		ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithoutThread(args, dir);
		System.out.println(outcome);
	}
	
	public void testJMeterExampleThreaded() {
		String dir = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin";
		List<String> args = Arrays.asList(
				"cmd.exe", "/C",
				"jmeter", "-n", "-t",
				"../threadgroup.jmx", "-l",
				"../output.jtl", "-j", "../testplan.log");
		
//		ScriptExecutor.timelimit = 1000;
		ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithThread(args, dir);
		System.out.println(outcome);
	}
	
	public void testJetty() {
		String dir = "E:\\conf-vul\\programs\\jetty\\jetty-distribution-9.2.1.v20140609\\demo-base";
		
		BufferReaderThread.THREAD_VERBOSE = true;
		BufferReaderThread.WRITE_TO_FILE = true;
		
		for(int i = 0; i < 2; i++) {
		    List<String> args = Arrays.asList(
				"cmd.exe", "/C",
				"java", "-DSTOP.PORT=8080", "-DSTOP.KEY=stop_jetty", "-jar", "../start.jar"
				);
		    ScriptExecutor.timelimit = 10000;
		    ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithThread(args, dir);
		    System.out.println(outcome);
		
		    args = Arrays.asList(
				"cmd.exe", "/C",
				"java", "-DSTOP.PORT=8080", "-DSTOP.KEY=stop_jetty", "-jar", "../start.jar", "--stop"
				);
		    outcome = ScriptExecutor.executeScriptWithThread(args, dir);
		    System.out.println(outcome);
		
		}
	}
	
	public void testDerby() {
		String dir = "E:\\conf-vul\\programs\\derby\\db-derby-10.10.1.1-bin\\derbytutor";
		List<String> args = Arrays.asList(
				"cmd.exe", "/C",
				"java", "-jar", "..\\lib\\derbyrun.jar",
				"ij", "connection.txt"
				);
		ScriptExecutor.timelimit = 1000000;
		ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithThread(args, dir);
		System.out.println(outcome);
//		System.exit(1);
	}
	
}
