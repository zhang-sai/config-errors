package edu.washington.cs.conf.mutation.derby;

import java.util.Arrays;
import java.util.List;

import edu.washington.cs.conf.mutation.BufferReaderThread;
import edu.washington.cs.conf.mutation.ConfMutator;
import edu.washington.cs.conf.mutation.MutatedConf;
import edu.washington.cs.conf.mutation.ScriptExecOutcome;
import edu.washington.cs.conf.mutation.ScriptExecutor;
import junit.framework.TestCase;

public class TestDerbyExamples extends TestCase {

	static String queryFolder = "./sample-console-output/derby/query/"; 
	public void testLaunch_query() {
		String dir = "E:\\conf-vul\\programs\\derby\\db-derby-10.10.1.1-bin\\derbytutor";
		String confFilePath = dir + "\\example-good-derby.properties";
		ConfMutator mutator = new ConfMutator(confFilePath);
		List<MutatedConf> mutates = mutator.mutateConfFile();
		
		BufferReaderThread.folder = queryFolder;
		BufferReaderThread.WRITE_TO_FILE = true;
		ScriptExecutor.name = "Derby";
		
		
		String newConfFile = dir + "\\derby.properties";
		for(MutatedConf mutate : mutates) {
			mutate.writeMutatedOptionToFile(newConfFile);
			
			//java -jar ..\lib\derbyrun.jar ij connection.txt
			List<String> args = Arrays.asList("cmd.exe", "/C", "java",
					"-jar", "..\\lib\\derbyrun.jar", "ij",
					"connection-query.txt");
			ScriptExecutor.timelimit = 10000;
			ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithThread(
					args, dir);
			System.out.println(outcome);

//			args = Arrays.asList("cmd.exe", "/C", "java",
//					"-jar", "..\\lib\\derbyrun.jar", "ij",
//			        "connection-remove.txt");
//			outcome = ScriptExecutor.executeScriptWithThread(args, dir);
//			System.out.println(outcome);
		}
		
	}
	
	static String insertFolder = "./sample-console-output/derby/insert/";
	public void testLaunch_insert() {
		String dir = "E:\\conf-vul\\programs\\derby\\db-derby-10.10.1.1-bin\\derbytutor";
		String confFilePath = dir + "\\example-good-derby.properties";
		ConfMutator mutator = new ConfMutator(confFilePath);
		List<MutatedConf> mutates = mutator.mutateConfFile();
		
		BufferReaderThread.folder = insertFolder;
		BufferReaderThread.WRITE_TO_FILE = true;
		ScriptExecutor.name = "Derby";
		
		
		String newConfFile = dir + "\\derby.properties";
		for(MutatedConf mutate : mutates) {
			mutate.writeMutatedOptionToFile(newConfFile);
			
			BufferReaderThread.WRITE_TO_FILE = true;
			
			//java -jar ..\lib\derbyrun.jar ij connection.txt
			List<String> args = Arrays.asList("cmd.exe", "/C", "java",
					"-jar", "..\\lib\\derbyrun.jar", "ij",
					"connection-insert.txt");
			ScriptExecutor.timelimit = 10000;
			ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithThread(
					args, dir);
			System.out.println(outcome);

			BufferReaderThread.WRITE_TO_FILE = false;
			args = Arrays.asList("cmd.exe", "/C", "java",
					"-jar", "..\\lib\\derbyrun.jar", "ij",
			        "connection-remove.txt");
			outcome = ScriptExecutor.executeScriptWithThread(args, dir);
			System.out.println(outcome);
		}
		
	}
	
	static String showTableFolder = "./sample-console-output/derby/show/";
	public void testLaunch_showtables() {
		String dir = "E:\\conf-vul\\programs\\derby\\db-derby-10.10.1.1-bin\\derbytutor";
		String confFilePath = dir + "\\example-good-derby.properties";
		ConfMutator mutator = new ConfMutator(confFilePath);
		List<MutatedConf> mutates = mutator.mutateConfFile();
		
		BufferReaderThread.folder = showTableFolder;
		BufferReaderThread.WRITE_TO_FILE = true;
		ScriptExecutor.name = "Derby";
		
		
		String newConfFile = dir + "\\derby.properties";
		for(MutatedConf mutate : mutates) {
			mutate.writeMutatedOptionToFile(newConfFile);
			
			BufferReaderThread.WRITE_TO_FILE = true;
			
			//java -jar ..\lib\derbyrun.jar ij connection.txt
			List<String> args = Arrays.asList("cmd.exe", "/C", "java",
					"-jar", "..\\lib\\derbyrun.jar", "ij",
					"connection-showtables.txt");
			ScriptExecutor.timelimit = 10000;
			ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithThread(
					args, dir);
			System.out.println(outcome);
		}
	}
	
	static String shutDownFolder = "./sample-console-output/derby/server/";
	public void testLaunch_launch_shutdown_server() {
		String dir = "E:\\conf-vul\\programs\\derby\\db-derby-10.10.1.1-bin\\derbytutor";
		String confFilePath = dir + "\\example-good-derby.properties";
		ConfMutator mutator = new ConfMutator(confFilePath);
		List<MutatedConf> mutates = mutator.mutateConfFile();
		
		BufferReaderThread.folder = shutDownFolder;
		BufferReaderThread.WRITE_TO_FILE = true;
		ScriptExecutor.name = "Derby";
		
		
		String newConfFile = dir + "\\derby.properties";
		for(MutatedConf mutate : mutates) {
			mutate.writeMutatedOptionToFile(newConfFile);
						
			//java -jar ..\lib\derbyrun.jar ij connection.txt
			List<String> args = Arrays.asList("cmd.exe", "/C", "java",
					"-jar", "..\\lib\\derbyrun.jar", "server",
					"start");
			ScriptExecutor.timelimit = 10000;
			ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithThread(
					args, dir);
			System.out.println(outcome);

			args = Arrays.asList("cmd.exe", "/C", "java",
					"-jar", "..\\lib\\derbyrun.jar", "server",
			        "shutdown");
			outcome = ScriptExecutor.executeScriptWithThread(args, dir);
			System.out.println(outcome);
		}
		
	}
}
