package edu.washington.cs.conf.instrument;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeBT.shrikeCT.OfflineInstrumenter;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public abstract class AbstractInstrumenter {

	  protected boolean disasm = false;
	  protected final boolean verify = true;
	  protected OfflineInstrumenter instrumenter;
	  
	  private String disasmFile = "report.txt";
	  
	  public static String PRE = "evaluating";
	  public static String POST = "entering";
	  public static String SEP = "#";
	  public static String CONF_SEP = "=-="; //separate multiple confs to reduce instrumentatiion num
	  
	  //this is for separating context with other relevant information
	  //such as line number, source text, etc.
	  //see TraceAnalyzer for usage example
	  public static String SUB_SEP = "%%"; //not used for context separation
	  public static String INDEX_SEP = "_index_";
	  
	  public void simpleTraverse(String inputJar) throws Exception {
		  System.out.println("start instrumentating");
		  instrumenter = new OfflineInstrumenter();
		  instrumenter.addInputElement(inputJar);
		  instrumenter.setPassUnmodifiedClasses(true);
	      instrumenter.beginTraversal();
	      ClassInstrumenter ci;
	      //do the instrumentation
	      while ((ci = instrumenter.nextClass()) != null) {
	    	  try {
	             doClass(ci, null);
	    	  } catch (Throwable e) {
	    		  e.printStackTrace();
	    		  continue;
	    	  }
	      }
	  }
	  
	  public void instrument(String inputElement, String outputJar) throws Exception {
		  System.out.println("start instrumentating");
	      instrumenter = new OfflineInstrumenter();
	      Writer w = null;
	      if(this.disasmFile != null) {
	          w = new BufferedWriter(new FileWriter(this.disasmFile, false));
	      }
	      instrumenter.addInputElement(inputElement);
	      instrumenter.setOutputJar(new File(outputJar));
	      instrumenter.setPassUnmodifiedClasses(true);
	      instrumenter.beginTraversal();
	      ClassInstrumenter ci;
	      //do the instrumentation
	      while ((ci = instrumenter.nextClass()) != null) {
	    	  try {
	             doClass(ci, w);
	    	  } catch (Throwable e) {
	    		  e.printStackTrace();
	    		  continue;
	    	  }
	      }
	      instrumenter.close();
	  }
	  
	  public void setDisasm(boolean disasm) {
		  this.disasm = disasm;
	  }
	  
	  public void setDisasmFile(String fileName) {
		  this.disasmFile = fileName;
	  }
	  
	  protected abstract void doClass(final ClassInstrumenter ci, Writer w) throws Exception;
	  
}
