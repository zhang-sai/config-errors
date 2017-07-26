package edu.washington.cs.conf.mutation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;

public class FilterPrintStream extends PrintStream {
	
//	static {
//		register();
//	}
	
	public static void register(String outputFile) {
		if(outputFile != null) {
		    file = new File(outputFile);
		}
		register();
	}
	
	private static PrintStream stdOut = System.out;
	private static PrintStream stdErr = System.err;
	
	public static void register() {
		try {
			System.setOut(new FilterPrintStream(System.out));
			System.setErr(new FilterPrintStream(System.err));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void unregister() {
		System.setOut(stdOut);
		System.setErr(stdErr);
	}
	
	private static File defaultFile = new File("./output-redirect.txt");
	public static File file = defaultFile;
	public FilterPrintStream(PrintStream ps) throws FileNotFoundException {
		super(ps);
	}

	@Override
	public void print(String s) {
		// ... process output string here ...
		try {
			Files.writeToFile(s, file, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// pass along to actual console output
		super.print(s);
	}
	
	@Override
	public void println(String s) {
		print(s + Globals.lineSep);
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("Hello world!, second try");
		System.out.println("Hello world!, second try");
	}
}