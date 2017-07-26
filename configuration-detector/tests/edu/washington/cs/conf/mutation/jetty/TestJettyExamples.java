package edu.washington.cs.conf.mutation.jetty;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import edu.washington.cs.conf.mutation.BufferReaderThread;
import edu.washington.cs.conf.mutation.ConfMutator;
import edu.washington.cs.conf.mutation.MutatedConf;
import edu.washington.cs.conf.mutation.ScriptExecOutcome;
import edu.washington.cs.conf.mutation.ScriptExecutor;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;
import junit.framework.TestCase;

public class TestJettyExamples extends TestCase {
	
	public void testGenerateMutates_1() {
		String confFilePath = "E:\\conf-vul\\programs\\jetty\\jetty-distribution-9.2.1.v20140609\\demo-base\\start.ini";
		ConfMutator mutator = new ConfMutator(confFilePath);
		List<MutatedConf> mutates = mutator.mutateConfFile();
		System.out.println(mutates.size());
		int count = 0;
		int msgcount = 0;
		for(int i = 0; i < mutates.size(); i++) {
			int fileIndex = i*4;
			List<String> lines = Files.readWholeNoExp("./sample-console-output/Output-Stream-" + fileIndex + ".txt");
			
			boolean error = false;
			if(this.hasError(lines) ) {
				error = true;
				count++;
//				System.out.println(i + ". " + mutates.get(i).toString());
//				System.out.println("  file: " + fileIndex);
			} else if (Utils.matchStacktrace(lines)) {
				error = true;
//				System.out.println(i + ". " + mutates.get(i).toString());
//				System.out.println("  file: " + fileIndex);
			}
			
			if(error) {
				if(!this.hasKeywords(lines, mutates.get(i))) {
					msgcount ++;
					System.out.println(i + ". " + mutates.get(i).toString());
					System.out.println("  file: " + fileIndex);
				}
			}
		}
		System.out.println("Has error: " + count);
		System.out.println("Bad msg count: " + msgcount);
	}

	public void testLaunch_1() {
		String dir = "E:\\conf-vul\\programs\\jetty\\jetty-distribution-9.2.1.v20140609\\demo-base";

//		BufferReaderThread.THREAD_VERBOSE = true;
		BufferReaderThread.WRITE_TO_FILE = true;

		String confFilePath = "E:\\conf-vul\\programs\\jetty\\jetty-distribution-9.2.1.v20140609\\demo-base\\start.ini";
		ConfMutator mutator = new ConfMutator(confFilePath);
		List<MutatedConf> mutates = mutator.mutateConfFile();
		
		for (MutatedConf mutate : mutates) {
			
			mutate.writeToFile(confFilePath);
			
			List<String> args = Arrays.asList("cmd.exe", "/C", "java",
					"-DSTOP.PORT=8080", "-DSTOP.KEY=stop_jetty", "-jar",
					"../start.jar");
			ScriptExecutor.timelimit = 10000;
			ScriptExecOutcome outcome = ScriptExecutor.executeScriptWithThread(
					args, dir);
			System.out.println(outcome);

			args = Arrays.asList("cmd.exe", "/C", "java", "-DSTOP.PORT=8080",
					"-DSTOP.KEY=stop_jetty", "-jar", "../start.jar", "--stop");
			outcome = ScriptExecutor.executeScriptWithThread(args, dir);
			System.out.println(outcome);

		}
	}

	public void testAnalyzeJettyOutput_1() throws FileNotFoundException {
		String dir = "./sample-console-output";
		List<File> files = Files.getFileListing(new File(dir));
		System.out.println(files.size());
	}
	
	private boolean hasError(List<String> lines) {
		for(String line : lines) {
			if(line.indexOf("Server:main: Started") != -1) {
				return false;
			}
		}
		return true;
	}
	
	private boolean hasKeywords(List<String> lines, MutatedConf mutated) {
		
		for(String line : lines) {
			if(line.indexOf(" " + mutated.getMutatedConfOption()) != -1) {
				return true;
			}
			if(mutated.getMutatedConfValue() != "" &&
					line.indexOf("[" + mutated.getMutatedConfValue() + "]") != -1) {
				return true;
			}
		}
		return false;
	}
}
