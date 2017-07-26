package edu.washington.cs.conf.mutation.jmeter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import edu.washington.cs.conf.mutation.BufferReaderThread;
import edu.washington.cs.conf.mutation.ConfMutator;
import edu.washington.cs.conf.mutation.MutatedConf;
import edu.washington.cs.conf.mutation.ScriptExecOutcome;
import edu.washington.cs.conf.mutation.ScriptExecutor;
import edu.washington.cs.conf.util.Files;
import junit.framework.TestCase;

public class TestJMeterConfigOptions extends TestCase {

	static String config_file_1 = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin\\jmeter.properties";
	
	static String config_file_2 = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin\\saveservice.properties";
	
	static String config_file_3 = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin\\system.properties";
	
	static String dir = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin";
	static String script = "jmeter";
	
	public void testMutateConfigs() {
		ConfMutator mutator = new ConfMutator(config_file_1);
		List<MutatedConf> mutates = mutator.mutateConfFile();
		int i = 0;
		for(MutatedConf mutate : mutates) {
			System.out.println(i++ + ". " + mutate.toString());
		}
	}
	
	public void testExample_1() throws IOException {
		ConfMutator mutator = new ConfMutator(config_file_1);
		List<MutatedConf> mutates = mutator.mutateConfFile();
		
		//backup the file
		String backupFilePath = config_file_1 + "-backup";
		Files.createIfNotExist(backupFilePath);
		Files.copyFileNoExp(config_file_1, backupFilePath);
		
		System.out.println("Number of mutation: " + mutates.size());
		
		ScriptExecutor.name = "JMeter-Default-";
		BufferReaderThread.WRITE_TO_FILE = true;
		
		int i = 0;
		for(MutatedConf mutate : mutates) {
			mutate.writeToFile(config_file_1);
			
			System.out.println(mutate.toString());
			
			List<String> args = Arrays.asList("cmd.exe", "/C", "jmeter",
					"-n", "-t", "../threadgroup.jmx", "-l",
					"../output.jtl", "-j", "../testplan.log");
			ScriptExecutor.timelimit = 10000;
			ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithThread(
					args, dir);
			System.out.println(outcome);
			
			//Copy the result
			String outputFile = dir + "\\..\\output.jtl";
			String localFile = "./sample-console-output/JMeter-Output-" + (i++) + ".txt";
			Files.createIfNotExistNoExp(localFile);
			Files.copyFileNoExp(outputFile, localFile);
			Files.writeToFileNoExp("", outputFile);
		}
		
		
		
		//restore the file
		Files.copyFileNoExp(backupFilePath, config_file_1);
	}
	
	public void testExample_2() {
		
		for(int i = 0; i < 5; i++) {
		    List<String> args = Arrays.asList("cmd.exe", "/C", "jmeter-server",
				"-H", "my.proxy.server", "-P", "8000");
		    ScriptExecutor.timelimit = 10000;
		    ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithThread(
				args, dir);
		    System.out.println(outcome);
		
		    ScriptExecutor.stopProcess();
		
		    killJava();
		}
//		System.exit(1);
	}
	
	public static void killJava() {
		List<String> args = Arrays.asList("cmd.exe", "/C", "KILLJAVA.bat");
		ScriptExecutor.timelimit = 1000;
		ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithThread(
				args, "E:\\conf-vul\\programs\\jmeter");
	}
	
	@Deprecated
	public void testSampleConfigFiles() {
		ConfMutator mutator = new ConfMutator(config_file_1);
		List<MutatedConf> confs = mutator.mutateConfFile();
		System.out.println("# of mutated confs: " + confs.size());
		for(MutatedConf conf : confs) {
			System.out.println(conf.getMutatedConfOption() + ", " + conf.getMutatedConfValue() + ", orig value: " + conf.getOriginalValue());
			conf.writeToFile("./tmp-output-folder/tmp.properties");
			break;
		}
	}
	
}
