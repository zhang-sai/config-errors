package edu.washington.cs.conf.mutation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class TestRunScriptsInCmd {

	// test launching commands
	public static void main(String[] args) throws IOException {

//		Process p = null;
		try {
			
			//Process proc = rt.exec("cmd /c start cmd.exe /K \"cd " + locaction);
			
			String dir = "E:\\conf-vul\\programs\\jetty\\jetty-distribution-9.2.1.v20140609";
			dir = "E:\\conf-vul\\programs\\jmeter\\apache-jmeter-2.9\\bin";
			String script = "startjetty.bat";
			script = "dir";
			script = "jmeter -n -t ../threadgroup.jmx -l ../output.jtl -j ../testplan.log";
			
//			ProcessBuilder pb = new ProcessBuilder(script);
			ProcessBuilder pb = new ProcessBuilder(Arrays.asList(new String[]
                {"cmd.exe", "/C", script}));
			pb.directory(new File(dir));
			pb.redirectErrorStream(true);
			final Process p = pb.start();
			
			
			//encapsulate the below in a thread
			
			
			
			// Process p =
			// Runtime.getRuntime().exec("cmd /C dir E:\\conf-vul\\programs\\jetty\\jetty-distribution-9.2.1.v20140609\\");
//			Process p = Runtime.getRuntime().exec("cmd /C " + dir + script);
			final BufferedReader stdInput = new BufferedReader(new InputStreamReader(
					p.getInputStream()), 8 * 1024);
			final BufferedReader stdError = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			
			BufferReaderThread stdInputThread = new BufferReaderThread(stdInput, "Standard input");
			BufferReaderThread stdOutputThread = new BufferReaderThread(stdError, "Standard output");
			
//			stdInputThread.setDaemon(true);
//			stdOutputThread.setDaemon(true);
			
			stdInputThread.start();
			stdOutputThread.start();
			
			Thread.sleep(2000);
			p.destroy();
//			stdInputThread.stop();
//			stdOutputThread.stop();
			
//			stdInputThread.join(2000);
//			stdOutputThread.join(2000);
			
			
			System.out.println(stdInputThread.getMessage());

		} catch (IOException e1) {
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally  {
			System.out.println("Destroy the process..");
//			p.destroy();
		}

		System.out.println("Done");
	}
}
