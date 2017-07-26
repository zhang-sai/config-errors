package edu.washington.cs.conf.instrument;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;

/**
 * An instrumentation class
 * */
public class ConfTracer {
	
	private static String CONTEXT_SEP = "%%CONTEXT%%";
	
	public static int context_length = 0;
	public static ConfTracer tracer = new ConfTracer();
	
	private ConfTracer() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
	        @Override
	        public void run() {
	                System.out.println("----------dumping traces to files-------");
	                synchronized(traceMap)
	                {
	                	StringBuilder sb = new StringBuilder();
	                	for(String key : traceMap.keySet()) {
		                	sb.append(key + AbstractInstrumenter.SEP + traceMap.get(key));
		                	sb.append(Globals.lineSep);
		                }
	                	try {
	                		long time = System.currentTimeMillis();
	                		System.out.println("At: " + time);
	                		String fileName = "./trace_dump_" + time + ".txt";
	                		File f = new File(fileName);
	                		System.out.println("write to file: " + f.getAbsolutePath());
	                		
							Files.writeToFile(sb.toString(), fileName);
						} catch (IOException e) {
							e.printStackTrace();
						}
	                }
	        }
	    });
	}
	
	Map<String, Long> traceMap = new LinkedHashMap<String, Long>();
	Stack<String> methodOnStack = new Stack<String>();
	
	//push a method call to the stack
	public void pushEntry(String input) {
		synchronized (this.methodOnStack) {
		    methodOnStack.push(input);
		}
		//System.out.println("push " + input + ":" + this.methodOnStack.toString());
	}
	
	//pop the method on the stack
	public void popExit(String input) {
		synchronized (this.methodOnStack) {
			if(methodOnStack.isEmpty()) {
				return;
			}
			String topMethod = methodOnStack.peek();
			if (topMethod.equals(input)) {
				methodOnStack.pop();
			} else {
				System.err
						.println("Wrong in instrumentation, method not in stack: "
								+ input);
				while (!methodOnStack.isEmpty()) {
					String top = methodOnStack.pop();
					if (top.equals(input)) {
						break;
					}
				}
			}
		}
		// System.out.println("leave " + input + ":" +
		// this.methodOnStack.toString());
	}
	
	public void popExceptionExit(String input) {
		this.popExit(input);
	}
	
	public void trace(String input) {
		//System.out.println(input);
		synchronized(traceMap) {
			//the format of line: PRE (or POST), config name, method as context
			//we should record the call context, e.g, stacktrace
			
			String line = input;
			if(context_length > 0) {
			    line = input
			        + CONTEXT_SEP
			        //+ ExecutionContext.getCurrentContextAsString(context_length)
			        + this.getTopContexts(context_length);
			        ;
			}
			
			if(traceMap.containsKey(line)) {
				traceMap.put(line, traceMap.get(line) + 1);
			} else {
				traceMap.put(line, 1L);
			}
		}
	}
	
	private String getTopContexts(int length) {
		synchronized (this.methodOnStack) {
			if (this.methodOnStack.size() < length) {
				return this.methodOnStack.toString();
			} else {
				return this.methodOnStack.subList(
						this.methodOnStack.size() - length,
						this.methodOnStack.size()).toString();
			}
		}
	}
}