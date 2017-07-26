package edu.washington.cs.conf.mutation.weka;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import edu.washington.cs.conf.mutation.ConfFileParser;
import edu.washington.cs.conf.mutation.ConfMutator;
import edu.washington.cs.conf.mutation.ExecCommand;
import edu.washington.cs.conf.mutation.ExecResult;
import edu.washington.cs.conf.mutation.ExecResultManager;
import edu.washington.cs.conf.mutation.MessageAdequacy;
import edu.washington.cs.conf.mutation.MessageAnalyzer;
import edu.washington.cs.conf.mutation.MutatedConf;
import edu.washington.cs.conf.mutation.ProgramRunnerByReflection;
import edu.washington.cs.conf.mutation.UserManual;

public class TestWekaConfigExamples extends TestCase {

	public static String zeroR_usermanual = "./tests/edu/washington/cs/conf/mutation/weka/zeror_manual.txt"; 
	
	public static String sample_config = "./tests/edu/washington/cs/conf/mutation/weka/zeror_sample_config.txt";
	
	public static String main_zeror = "weka.classifiers.rules.ZeroR";
	
	public void testParseSampleConfig() {
//		ConfParser parser = new ConfParser(sample_config);
//		parser.parseFile();
//		System.out.println(parser.getOptionValueMap());
		
		ConfFileParser reader = new ConfFileParser(sample_config);
		reader.parse();
		reader.dumpFile();
		System.out.println(reader.getConfOptionNames());
		System.out.println(reader.getConfOptionValues());
	}
	
	public void testMutateSampleConfig() throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		ConfMutator mutator = new ConfMutator(sample_config);
		List<MutatedConf> mutatedConfs = mutator.mutateConfFile();
		
		System.out.println(mutatedConfs.size());
		
		for(MutatedConf mutatedConf : mutatedConfs) {
//			System.out.println(mutatedConf.createCmdLine());
			System.out.println("   " + Arrays.asList(mutatedConf.createCmdLineAsArgs()));
			
			String mainClass = main_zeror;
			Class<?> clz = Class.forName(mainClass);
			Method meth = clz.getMethod("main", String[].class);
			String[] args = mutatedConf.createCmdLineAsArgs();
			Object argObj = args;
	        Object o = meth.invoke(null, argObj);
		}
	}
	
	//XX need to figure out the good way
	//NEED TO GET IT RIGHT
	public void testRunZeroRReflectively() {
		ConfMutator mutator = new ConfMutator(sample_config);
		List<MutatedConf> mutatedConfs = mutator.mutateConfFile();
		
		Collection<ExecCommand> cmds = new LinkedList<ExecCommand>();
		cmds.add(new ExecCommand(main_zeror, new String[0]));
		
		ProgramRunnerByReflection runner = new ProgramRunnerByReflection();
		
		runner.setMutatedConfigs(mutatedConfs);
		runner.setCommands(cmds);
		runner.setOutputFile("./weka_output.txt");
		
		ExecResultManager.setOracleCheckingMethod(WekaCheckingMethods.wekaCheckingMethod);
		ExecResultManager.setMessageFetchingMethod(WekaCheckingMethods.wekaMsgFetchingMethod);
		
		Collection<ExecResult> results = runner.execute();
		System.out.println("Execution results: " + results.size());
		for(ExecResult result : results) {
			System.out.println("   " + result.pass() + ", message; " + result.getMessage());
			System.out.println("        " + result);
			System.out.println("        Used configs: " + result.dumpCmdWithConfigs());
		}
	}
	
	public void testRunZeroRReflectively_WithBaseOptions() {
		testRunZeroRReflectively_BaseOptions(true);
	}
	
	public void testRunZeroRReflectively_WithoutBaseOptions() {
		testRunZeroRReflectively_BaseOptions(false);
	}
	
	private void testRunZeroRReflectively_BaseOptions(boolean base) {
		ConfMutator mutator = new ConfMutator(sample_config);
		List<MutatedConf> mutatedConfs = mutator.mutateConfFile();
		
		Collection<ExecCommand> cmds = new LinkedList<ExecCommand>();
		cmds.add(new ExecCommand(main_zeror, new String[0]));
		
		ProgramRunnerByReflection runner = new ProgramRunnerByReflection();
		
		runner.setMutatedConfigs(mutatedConfs);
		runner.setCommands(cmds);
		runner.setOutputFile("./weka_output.txt");
		
		//XXX the base options that must appear in each conf mutation
		Map<String, String> baseOptions = new LinkedHashMap<String, String>();
		//"-p", "2",  "-t", "./subjects/weka/weather.arff"
		baseOptions.put("p", "2");
		baseOptions.put("t", "./subjects/weka/weather.arff");
		if(base) {
		    runner.setBaseOptions(baseOptions);
		}
		
		ExecResultManager.setOracleCheckingMethod(WekaCheckingMethods.wekaCheckingMethod);
		ExecResultManager.setMessageFetchingMethod(WekaCheckingMethods.wekaMsgFetchingMethod);
		
		UserManual manual = new UserManual(TestWekaConfigExamples.zeroR_usermanual);
		
		Collection<ExecResult> results = runner.execute();
		System.out.println("Execution results: " + results.size());
		for(ExecResult result : results) {
			System.out.println("   " + result.pass() + ", message; " + result.getMessage());
			System.out.println("        " + result);
			System.out.println("        Used configs: " + result.dumpCmdWithConfigs());
			if(result.pass()) {
				continue;
			}
			if(result.getMessage() != null) {
			    MessageAdequacy adequancy = MessageAnalyzer.isMessageAdequate(result, manual);
			    System.out.println("        Has error msg, " + adequancy.isAdequate() + " : "
			    		+ adequancy.getExplanation());
			} else {
				System.out.println("         XXXX NO error msg. Not enough!");
			}
		}
	}
}