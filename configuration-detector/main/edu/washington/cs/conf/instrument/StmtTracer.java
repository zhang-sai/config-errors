package edu.washington.cs.conf.instrument;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;

/**
 * This tracer is used to trace every executed bytecode statements
 * */
public class StmtTracer {

	public static StmtTracer tracer = new StmtTracer();
	
	private StmtTracer() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
	        @Override
	        public void run() {
	        	System.out.println("------- dump stmts to file -------");
	        	synchronized(stmts) {
	        		StringBuilder sb = new StringBuilder();
                	for(String stmt : stmts) {
	                	sb.append(stmt);
	                	sb.append(Globals.lineSep);
	                }
                	try {
                		long currTime = System.currentTimeMillis();
                		String fileName = "./stmts_dump_" + currTime + ".txt";
                		System.out.println("Write to file: " + fileName);
						Files.writeToFile(sb.toString(), fileName);
					} catch (IOException e) {
						e.printStackTrace();
					}
	        	}
	        }
		});
	}
	
	private static Set<String> stmts = new LinkedHashSet<String>();
	
	//record the statements that get executed
	public void trace(String input) {
		synchronized(stmts) {
			stmts.add(input);
		}
	}
}
