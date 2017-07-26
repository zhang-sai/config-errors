package edu.washington.cs.conf.analysis;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.diagnosis.DiagnoserMain;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentSchema.TYPE;
import edu.washington.cs.conf.util.Utils;

import plume.Option;
import plume.Options;

//compute thin slicing, and output an instrumentation schema
public class AnalyserMain {

    public static final String VERSION = edu.washington.cs.conf.diagnosis.DiagnoserMain.VERSION;
	
    @Option("The output instrumentation schema")
	public static String output_schema = null;
	
    @Option("The output text for the instrumentation schema")
	public static String output_schema_txt = null;
    
    public static void main(String[] args) throws FileNotFoundException {
    	parse_and_check_args(args);
    	//perform thin slicing
    	new AnalyserMain().analysis();
    }
    
    private static void parse_and_check_args(String[] args) {
		Options options = new Options("ConfDiagnoser's Analyzer Component usage: ", DiagnoserMain.class, AnalyserMain.class);
        String[] file_args = options.parse_or_usage(args);
        if(file_args.length != 0) {
            Utils.flushToStd(file_args);
            System.exit(1);
        }
        if(DiagnoserMain.help) {
            Utils.flushToStd(new String[]{VERSION});
            Utils.flushToStd(new String[]{options.usage()});
            System.exit(1);
        }
        List<String> errorMsg = new LinkedList<String>();
        if(DiagnoserMain.classpath_for_slicing == null) {
       	   errorMsg.add("You must specify the classpath for slicing via: --classpath_for_slicing");
        }
        
        if(DiagnoserMain.main_for_slicing == null) {
        	errorMsg.add("You must specify the main class for slicing via: --main_for_slicing");
        }
        
        if(DiagnoserMain.config_options == null) {
       	 errorMsg.add("You must specify a file containing all configuration options via: --config_options");
        }
        
        if(!errorMsg.isEmpty()) {
            Utils.flushToStd(errorMsg.toArray(new String[0]));
            Utils.flushToStd(new String[]{options.usage()});
            System.exit(1);
        }
	}
    
    private void analysis() {
    	//read the configuration options
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(DiagnoserMain.config_options);
		ConfEntityRepository conf_repo = new ConfEntityRepository(entities);
		System.out.println("Read: " + entities.size() + " options");
		
    	//perform thin slicing, and then create the instrumentation schema
    	Collection<ConfPropOutput> confSlices = CommonUtils.getConfPropOutputs(
    			DiagnoserMain.classpath_for_slicing,
    			DiagnoserMain.main_for_slicing,
    			conf_repo.getConfEntityList(),
    			DiagnoserMain.ingorable_class_file,
    			DiagnoserMain.cg_type,
				false /**no pruning by default*/);
    	
    	//create the schema
    	InstrumentSchema schema = new InstrumentSchema();
		schema.setType(TYPE.SOURCE_PREDICATE);
		schema.addInstrumentationPoint(confSlices);
		
		if(output_schema != null) {
		    ConfOutputSerializer.serializeSchema(schema, output_schema);
		    //recover from the file, an extra check
			InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(output_schema);
			Utils.checkTrue(schema.toString().equals(newSchema.toString()));
		}
		if(output_schema_txt != null) {
		    ConfOutputSerializer.writeToFileAsText(schema, output_schema_txt);
		}
    }
}
