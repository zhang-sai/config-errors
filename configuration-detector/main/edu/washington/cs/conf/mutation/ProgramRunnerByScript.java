package edu.washington.cs.conf.mutation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;

public class ProgramRunnerByScript extends ProgramRunner {
	
	public static boolean VERBOSE = false;

    public final List<ScriptCommand> commands = new LinkedList<ScriptCommand>();
    
    private String configFileLocation = null;
	
	public void setCommands(Collection<ScriptCommand> cmds) {
		this.commands.addAll(cmds);
	}
	
	public void setConfigFile(String configFileLocation) {
		Utils.checkNotNull(configFileLocation);
		this.configFileLocation = configFileLocation;
	}

	/**
	 * String dir = "E:\\conf-vul\\programs\\jetty\\jetty-distribution-9.2.1.v20140609";
	 * String script = "startjetty.bat";
	 * 
	 * http://stackoverflow.com/questions/14981435/how-do-i-stop-jetty
	 * */
	@Override
	public Collection<ExecResult> execute() {
		Utils.checkTrue(System.getProperty("os.name").startsWith("Windows"),
				"Only support windows now!");
		if(VERBOSE) {
			System.out.println("Num of commands: " + this.commands.size());
			System.out.println("Num of confs: " + this.mutatedConfigs.size());
		}
		
		Collection<ExecResult> results = new LinkedList<ExecResult>();
		for(ScriptCommand cmd : this.commands) {
			String dir = cmd.dir;
			String executable = cmd.getExecutable();
			for(MutatedConf conf : this.mutatedConfigs) {
				this.setupConfigEnv(conf);
				//construct the command to run
				List<String> args = Arrays.asList(new String[]{"cmd.exe", "/C", executable});
				if(VERBOSE) {
				    System.out.println("Running: " + args);
				}
				
				//create a process builder and set the target file
				ProcessBuilder pb = new ProcessBuilder(args);
				pb.directory(new File(dir));
				pb.redirectErrorStream(true);
				try {
				    Process p = pb.start();
				    final BufferedReader stdOutput = new BufferedReader(new InputStreamReader(
							p.getInputStream()), 8 * 1024);
					final BufferedReader stdError = new BufferedReader(new InputStreamReader(
							p.getErrorStream()));
					
					//get the input and output
					BufferReaderThread stdOutputThread = new BufferReaderThread(stdOutput, "Output:");
					BufferReaderThread stdErrorThread = new BufferReaderThread(stdError, "Error:");
					
					//get the input and output
					stdOutputThread.start();
					stdErrorThread.start();
					
					//FIXME This suppose the threads will finish
					//wait until these two thread finish
					stdOutputThread.join();
					stdErrorThread.join();
					
					//dump the error messages
					String outputMsg = stdOutputThread.getMessage();
					String errorMsg = stdErrorThread.getMessage();
					String message = outputMsg + Globals.lineSep + errorMsg;
					
					if(VERBOSE) {
					    System.out.println("console message/ : ");
					    System.out.println(message);
					}
					
					ExecResult result = ExecResultManager.createScriptExecResult(cmd, conf, message);
				    results.add(result);
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
				
				this.revertConfigEnv(conf);
			}
		}
		return results;
	}
	
	//write the mutated configuration to the configuration file

	private FileMover mover = null;
	private void setupConfigEnv(MutatedConf conf) {
		if(this.configFileLocation != null) {
			Utils.checkTrue(mover == null);
			//create a file to store the mutated configuration options
			String mutatedConfigFile = this.configFileLocation + "-mutated";
			Files.createIfNotExistNoExp(mutatedConfigFile);
			conf.writeToFile(mutatedConfigFile);
			//initialize the mover
			mover = new FileMover(this.configFileLocation, mutatedConfigFile);
			mover.setUpMutatedConfFile();
		}
	}

	//revert the original configuration file
	private void revertConfigEnv(MutatedConf conf) {
		if(this.configFileLocation != null) {
			Utils.checkNotNull(mover);
			mover.restoreOriginalConfFile();
			mover = null; //clear the file mover
		}
	}
}
