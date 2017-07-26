package edu.washington.cs.conf.mutation.jmeter;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.mutation.ConfFileParser;
import edu.washington.cs.conf.mutation.ConfMutator;
import edu.washington.cs.conf.mutation.ExecResult;
import edu.washington.cs.conf.mutation.ExecResultManager;
import edu.washington.cs.conf.mutation.MutatedConf;
import edu.washington.cs.conf.mutation.ProgramRunnerByScript;
import edu.washington.cs.conf.mutation.ScriptCommand;
import junit.framework.TestCase;

//by running from command line
@Deprecated
public class JMeterOldExamples extends TestCase {

	//dir: E:\conf-vul\programs\jmeter\apache-jmeter-2.9\bin
	//command: jmeter -n -t ../threadgroup.jmx -l ../output.jtl -j ../testplan.log
	String dir = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin";
	String script = "jmeter";
	
	@Deprecated
	public void testRunSimpleExample() {
		Collection<ScriptCommand> cmds = new LinkedList<ScriptCommand>();
		//create a command
		Collection<String> args = Arrays.asList("-n", "-t", "../threadgroup.jmx", "-l",
				"../output.jtl", "-j", "../testplan.log");
//		args = Arrays.asList();
//		script = "dir";
		
		ScriptCommand cmd = new ScriptCommand(dir, script);
		cmd.addArgs(args);
		cmds.add(cmd);
		
		//set the configs
		Collection<MutatedConf> configs = new LinkedList<MutatedConf>();
		ConfFileParser parser = new ConfFileParser(Arrays.asList("a=1", "b=2"));
		
		MutatedConf conf = new MutatedConf(parser, "a", "2", 0);
		configs.add(conf);
		
		//create a runner
		ProgramRunnerByScript runner = new ProgramRunnerByScript();
		runner.setCommands(cmds);
		runner.setMutatedConfigs(configs);
		runner.setOutputFile("./tmp-output-folder/jmeter_output.txt");
		
		System.out.println("Num of cmds: " + cmds.size() + ", num of mutated confs: " + configs.size());
		
		//set the method checkiing and error msg fetching methods
		ExecResultManager.setOracleCheckingMethod(JMeterCheckingMethods.jmeterCheckingMethods);
		ExecResultManager.setMessageFetchingMethod(JMeterCheckingMethods.jmeterMsgFetchingMethods);
		
//		ProgramRunnerByScript.VERBOSE = true;
		
		Collection<ExecResult> results = runner.execute();
		
		for(ExecResult r : results) {
			System.out.println("Execution result: ");
			System.out.println(r.getMessage());
		}
	}
	
	public void testRunMultiConfigsInJMeter() {
		Collection<ScriptCommand> cmds = new LinkedList<ScriptCommand>();
		//create a command
		Collection<String> args = Arrays.asList("-n", "-t", "../threadgroup.jmx", "-l",
				"../output.jtl", "-j", "../testplan.log");
//		args = Arrays.asList();
//		script = "dir";
		
		ScriptCommand cmd = new ScriptCommand(dir, script);
		cmd.addArgs(args);
		cmds.add(cmd);
		
		//set the configs		
		ConfMutator mutator = new ConfMutator(TestJMeterConfigOptions.config_file_1);
		Collection<MutatedConf> configs = mutator.mutateConfFile();
		
		//create a runner
		ProgramRunnerByScript runner = new ProgramRunnerByScript();
		runner.setCommands(cmds);
		runner.setMutatedConfigs(configs);
		runner.setConfigFile(TestJMeterConfigOptions.config_file_1);
		runner.setOutputFile("./tmp-output-folder/jmeter_output.txt");
		
		System.out.println("Num of cmds: " + cmds.size() + ", num of mutated confs: " + configs.size());
		
		//set the method checkiing and error msg fetching methods
		ExecResultManager.setOracleCheckingMethod(JMeterCheckingMethods.jmeterCheckingMethods);
		ExecResultManager.setMessageFetchingMethod(JMeterCheckingMethods.jmeterMsgFetchingMethods);
		
		ProgramRunnerByScript.VERBOSE = true;
		
		Collection<ExecResult> results = runner.execute();
		
		for(ExecResult r : results) {
			System.out.println("Execution result: ");
			System.out.println(r.getMessage());
		}
	}
	
}