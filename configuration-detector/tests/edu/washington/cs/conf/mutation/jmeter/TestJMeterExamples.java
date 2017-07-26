package edu.washington.cs.conf.mutation.jmeter;

import java.util.Arrays;
import java.util.List;

import edu.washington.cs.conf.mutation.BufferReaderThread;
import edu.washington.cs.conf.mutation.ConfMutator;
import edu.washington.cs.conf.mutation.MutatedConf;
import edu.washington.cs.conf.mutation.ScriptExecOutcome;
import edu.washington.cs.conf.mutation.ScriptExecutor;
import edu.washington.cs.conf.util.Files;
import junit.framework.TestCase;

public class TestJMeterExamples extends TestCase {
	
	String jmeterConfigFile = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin\\jmeter.properties";
	
	String systemConfigFile = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin\\system.properties";

	String execDir = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin";
	
	static String webtestfolder = "./sample-console-output/jmeter/webtestplan/";
	public void testWebTest_jmeter() {
		List<MutatedConf> mutates = TestJMeterOptions.getMutatedJMeterOptions();
		
		BufferReaderThread.folder = webtestfolder;
		BufferReaderThread.WRITE_TO_FILE = true;
		
		ScriptExecutor.name = "JMeter";
		String newConfFile = jmeterConfigFile;
		
		int i = 0;
		for(MutatedConf mutate : mutates) {
			mutate.writeMutatedOptionToFile(newConfFile);
			
			BufferReaderThread.tag = mutate.toString();
			
			//jmeter -n -t ../testplans/WebTestingPlan.jmx -l ../output.jtl -j ../testplan.log
			List<String> args = Arrays.asList("cmd.exe", "/C",
					"jmeter", "-n", "-t", "../testplans/WebTestingPlan.jmx",
					"-l", "../output.jtl",
					"-j", "../testplan.log");
			ScriptExecutor.timelimit = 10000;
			ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithThread(args, execDir);
			System.out.println(outcome);
			
			//move the output file
			String srcFilePath = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\output.jtl";
			String destFilePath = "./sample-console-output/jmeter/webtestplan/output/output_" + (i) + ".txt";
			Files.copyFileNoExp(srcFilePath, destFilePath);
			Files.writeToFileNoExp("", srcFilePath);
			
			String logSrcFilePath = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\testplan.log";
			String logDestFilePath = "./sample-console-output/jmeter/webtestplan/log/log_" + (i) + ".txt";
			Files.copyFileNoExp(logSrcFilePath, logDestFilePath);
			Files.writeToFileNoExp("", logSrcFilePath);
			
			i++;
//			break;
		}
	}
	
}
