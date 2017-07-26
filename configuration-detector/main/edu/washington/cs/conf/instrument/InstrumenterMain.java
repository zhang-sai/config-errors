package edu.washington.cs.conf.instrument;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.analysis.AnalyserMain;
import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.diagnosis.DiagnoserMain;
import edu.washington.cs.conf.util.Utils;
import plume.Option;
import plume.Options;

/**
 * This class simply performs instrumentation
 * */
public class InstrumenterMain {

	@Option("Show all help options")
	public static boolean help = false;
	
	@Option("The original jar file to instrument")
	public static String original_jar = null;
	
	@Option("The instrumented jar file")
	public static String instrumented_jar = null;
	
	@Option("Reduce instrumentation points")
	public static boolean reduce_ptrs = false;
	
	public static void main(String[] args) throws Exception {
		parse_and_check_args(args);
		new InstrumenterMain().instrument();
	}
	
	private static void parse_and_check_args(String[] args) {
		Options options = new Options("ConfDiagnoser's Instrument Component usage: ",
				InstrumenterMain.class, AnalyserMain.class);
        String[] file_args = options.parse_or_usage(args);
        if(file_args.length != 0) {
            Utils.flushToStd(file_args);
            System.exit(1);
        }
        if(DiagnoserMain.help) {
            Utils.flushToStd(new String[]{DiagnoserMain.VERSION});
            Utils.flushToStd(new String[]{options.usage()});
            System.exit(1);
        }
        List<String> errorMsg = new LinkedList<String>();
        if(InstrumenterMain.original_jar == null) {
       	   errorMsg.add("You must specify the original jar via: --original_jar");
       	   File f = new File(InstrumenterMain.original_jar );
    	   if(!f.exists() || !f.getName().endsWith(".jar")) {
    		   errorMsg.add("You file: " + original_jar + " does not exist, or is not a jar file.");
    	   }
        }
        
        if(InstrumenterMain.instrumented_jar == null) {
        	errorMsg.add("You must specify the instrumented jar via: --instrumented_jar");
        }
        
        if(AnalyserMain.output_schema == null) {
       	 errorMsg.add("You must specify the instrumentation schema via: --output_schema");
        }
        
        if(!errorMsg.isEmpty()) {
            Utils.flushToStd(errorMsg.toArray(new String[0]));
            Utils.flushToStd(new String[]{options.usage()});
            System.exit(1);
        }
	}
	
	private void instrument() throws Exception {
        InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(AnalyserMain.output_schema);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.setReduceInstrPoint(reduce_ptrs);
		instrumenter.instrument(original_jar, instrumented_jar);
		
		InstrumentStats.showInstrumentationStats();
	}
}
