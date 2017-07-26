package edu.washington.cs.conf.experiments.weka;

import java.util.Collection;
import java.util.List;

import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.experiments.WekaExpUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import junit.framework.TestCase;

public class TestSliceWekaConfigOptions extends TestCase {
	
	public static String weka_instrument_file = "./weka_option_instr_ser.dat";
	public static String weka_instrument_txt = "./weka_option_instr.txt";
	
	public static String weka_instrument_file_full_slice = "./weka_option_instr_ser_full_slice.dat";
	public static String weka_instrument_txt_full_slice = "./weka_option_instr.txt";
	

	public void testInitAllConfigOptions() {
		String path = "./subjects/weka/weka-no-trace.jar;./subjects/weka/JFlex.jar;" +
		"./subjects/weka/java-cup.jar";
        //String mainClass = "Lweka/classifiers/trees/J48";
		List<ConfEntity> wekaConfList = WekaExpUtils.getWekaConfList();
		ConfEntityRepository repo = new ConfEntityRepository(wekaConfList);
		repo.initializeTypesInConfEntities(path);
		for(ConfEntity conf : wekaConfList) {
			System.out.println(conf);
		}
		assertEquals(14, wekaConfList.size());
	}
	
	public void testSliceOptionsInWeka() {
		long start = System.currentTimeMillis();
		getWekaConfOutputs();
		long end = System.currentTimeMillis();
		System.out.println("Elapsed: " + (end - start)/1000);
	}
	
	public void testCreateInstrumentSchema() {
		Collection<ConfPropOutput> outputs = getWekaConfOutputs();
		
		InstrumentSchema schema = new InstrumentSchema();
		schema.addInstrumentationPoint(outputs);
		
		ConfOutputSerializer.serializeSchema(schema, weka_instrument_file);
		ConfOutputSerializer.writeToFileAsText(schema, weka_instrument_txt);
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(weka_instrument_file);
		assertEquals(schema.toString(), newSchema.toString());
	}
	
	public void testCreateInstrumentSchemaFullSlice() {
        Collection<ConfPropOutput> outputs = getWekaConfOutputsFullSlice();
		
		InstrumentSchema schema = new InstrumentSchema();
		schema.addInstrumentationPoint(outputs);
		
		ConfOutputSerializer.serializeSchema(schema, weka_instrument_file_full_slice);
		ConfOutputSerializer.writeToFileAsText(schema, weka_instrument_txt_full_slice);
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(weka_instrument_file_full_slice);
		assertEquals(schema.toString(), newSchema.toString());
	}
	
	static String path = "./subjects/weka/weka-no-trace.jar;./subjects/weka/JFlex.jar;" +
	    "./subjects/weka/java-cup.jar";
    static String mainClass = "Lweka/classifiers/trees/J48";
	
	public static Collection<ConfPropOutput> getWekaConfOutputs() {
        List<ConfEntity> wekaConfList = WekaExpUtils.getWekaConfList();
        Collection<ConfPropOutput> confs = CommonUtils.getConfPropOutputs(path, mainClass, wekaConfList, false);
        return confs;
	}
	
	public static Collection<ConfPropOutput> getWekaConfOutputsFullSlice() {
        List<ConfEntity> wekaConfList = WekaExpUtils.getWekaConfList();
        Collection<ConfPropOutput> confs = CommonUtils.getConfPropOutputs(path, mainClass, wekaConfList,
        		"JavaAllExclusions.txt", CG.ZeroCFA, false,
        		DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS,
        		ControlDependenceOptions.NO_EXCEPTIONAL_EDGES);
        return confs;
	}
}