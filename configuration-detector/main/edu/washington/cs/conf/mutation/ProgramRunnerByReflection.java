package edu.washington.cs.conf.mutation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;

public class ProgramRunnerByReflection extends ProgramRunner {
	
	private static File globalLogFile = new File("./output-detect/global_log.txt");
	static {
		//initialize the global log file
		try {
			Files.createIfNotExist(globalLogFile);
			//then clean the file if needed
			Files.emptyFileNoExp(globalLogFile.getAbsolutePath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public final List<ExecCommand> commands = new LinkedList<ExecCommand>();
	
	//a set of options for mutating
	public final Map<String, String> baseOptions = new LinkedHashMap<String, String>();
	
	public void setCommands(Collection<ExecCommand> cmds) {
		this.commands.addAll(cmds);
	}
	
	public void setBaseOptions(Map<String, String> baseOptions) {
		this.baseOptions.putAll(baseOptions);
	}

	@Override
	public Collection<ExecResult> execute() {
		Files.emptyFileNoExp(globalLogFile.getAbsolutePath());
		
		Collection<ExecResult> results = new LinkedList<ExecResult>();
		for(ExecCommand cmd : this.commands) {
			String mainClass = cmd.mainMethod;
			String[] mainArgs = cmd.args;
			for(MutatedConf conf : this.mutatedConfigs) {
				
//				System.out.println(cmd + " + " + conf);
				
				//create the arg list
				List<String> argList = new LinkedList<String>();
				argList.addAll(Arrays.asList(mainArgs));
				try {
					//add the additional params to the arg list
					String[] cmdLineConfArgs = baseOptions.isEmpty()
					    ? conf.createCmdLineAsArgs() : conf.createCmdLinesAsArgs(baseOptions);
					for(String arg : cmdLineConfArgs) {
						argList.add(arg);
					}
					//invoke the main method
					Class<?> clz = Class.forName(mainClass);
					Method mainMethod = clz.getMethod("main", String[].class);
					//register the redirect file
					Utils.checkNotNull(this.outputFile);
					FilterPrintStream.register(this.outputFile);
					
					//move the content to the global log file and empty the current output file
					Files.appendFile(this.outputFile, globalLogFile.getAbsolutePath());
					Files.emptyFileNoExp(this.outputFile);
					
					Throwable error = null;
					error = ReflectionExecutor.executeReflectionCode(mainMethod, argList);
					
//					
//					try {
//						String[] args = argList.toArray(new String[0]);
//						Object argObj = args;
////						System.out.println(argList);
//				        mainMethod.invoke(null, argObj);
//					} catch (Throwable e) {
//						error = e.getCause(); //get the cause of the error
//						Utils.checkNotNull(error);
//						System.err.println("Error: " + error.getClass() + ", " + error.getMessage());
////						e.printStackTrace();
//					}
					
				    //unregister it
				    FilterPrintStream.unregister();
				    
				    //create an execution result
				    ExecResult result = ExecResultManager.createReflectionExecResult(cmd, conf, error, this.outputFile);
				    results.add(result);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
		return results;
	}
}