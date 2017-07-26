package edu.washington.cs.conf.mutation;

import java.io.BufferedReader;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;

public class BufferReaderThread extends Thread {
	
	public static String folder = "./sample-console-output/";
	public static int counter = 0;
	
	public static boolean THREAD_VERBOSE = false;
	public static boolean WRITE_TO_FILE = false;

	public final BufferedReader reader;
	public final String title;
	
	public final StringBuilder sb = new StringBuilder();
	
	private String fileName = null;
	
	public BufferReaderThread(BufferedReader p, String title) {
		super();
		this.reader = p;
		this.title = title;
		this.fileName = folder +  title + "-" + (counter++) + ".txt";
		if(WRITE_TO_FILE) {
			if(Files.checkFileExistence(this.fileName)) {
				Files.writeToFileNoExp("", this.fileName); //clear the content
			}
		}
	}
	
	@Override
	public void run() {
		String s = null;
		if(THREAD_VERBOSE) {
		    System.out.println(title);
		}
		try {
		    while ((s = this.reader.readLine()) != null) {
		    	if(THREAD_VERBOSE) {
			        System.out.println(s + Globals.lineSep);
		    	}
		    	if(WRITE_TO_FILE) {
					Files.appendToFile(s + Globals.lineSep, this.fileName);
				}
			    sb.append(s);
			    sb.append(Globals.lineSep);
		    }
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getMessage() {
		return this.sb.toString();
	}
}
