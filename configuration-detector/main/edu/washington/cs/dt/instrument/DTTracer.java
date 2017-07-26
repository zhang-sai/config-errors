package edu.washington.cs.dt.instrument;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

public class DTTracer {
	
	public static final String lineSep = System.getProperty("line.separator");
	public static final String fileSep = System.getProperty("file.separator");

	public static DTTracer tracer = new DTTracer();
	
	private String fileName = null;
	private List<String> fieldAccessList = new LinkedList<String>();
	
	public static final String READ = "READ";
	public static final String WRITE = "WRITE";
	public static final String SEP = "#";
	
	public static final String ME_START = "ME_START";
	public static final String ME_END = "ME_END";
	
	private String dir = "./dt-output-folder";
	
	public void traceMethodEntry(String methodName) {
		if(fileName != null) {
			throw new Error("File name should be null. now: " + methodName
					+ ", fileName: " + fileName);
		}
		//should not check, since it is possible some initialize code executed before each method
//		if(!this.fieldAccessList.isEmpty()) {
//			throw new Error("The access list is not empty, size: " + fieldAccessList.size());
//		}
		this.fileName = methodName + "_trace.txt";
	}
	
	public void traceMethodExit(String methodName) {
		if(fileName == null) {
			throw new Error("File name should not be null.");
		}
		if(!fileName.equals(methodName + "_trace.txt")) {
			throw new Error("Diff method name: " + fileName + ", method name: " + methodName);
		}
		this.writeToFile();
		fileName = null;
		this.fieldAccessList.clear();
	}
	
	public void traceFieldRead(Object classObj, String fieldFullName, String type) {
		String line = READ + SEP + fieldFullName + SEP + type +
		    SEP + System.identityHashCode(classObj);
		this.fieldAccessList.add(line);
	}
	
	public void traceFieldWrite(Object classObj, String fieldFullName, String type) {
		String line = WRITE + SEP + fieldFullName + SEP + type + 
		    SEP + System.identityHashCode(classObj);
	    this.fieldAccessList.add(line);
	}
	
	private void writeToFile() {
		File dirFile = new File(dir);
		if(!dirFile.exists()) {
			System.out.println("Create folder: " + dirFile.getAbsolutePath());
			dirFile.mkdirs();
		} else {
			if(dirFile.isFile()) {
				System.err.println("Exit, a file: " + dirFile.getAbsolutePath() + " exist!");
				System.exit(1);
			}
		}
		File file = new File(dir + fileSep + this.fileName);
		//write to disk
		if(file.exists()) {
			 file.delete();
			 System.err.println("Delete existing file: " + file.getAbsolutePath());
		 }
		 try {
			 BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		     for(String line : this.fieldAccessList) {
		        writer.append(line + "");
			    writer.append(lineSep);
		    }
		     writer.close();
		 } catch(Throwable e) {
		    throw new Error(e);
		 }
	}
}
