package edu.washington.cs.conf.experiments.synoptic;

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
import edu.washington.cs.conf.experiments.SynopticExpUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentSchema.TYPE;
import edu.washington.cs.conf.util.Log;
import junit.framework.TestCase;

public class TestSliceSynopticConfigOptions extends TestCase {
	
	public static String synoptic_instrument_file = "./synoptic_option_instr_ser.dat";
	public static String synoptic_instrument_txt = "./synoptic_option_instr.txt";
	
	public static String synoptic_instrument_file_full_slice = "./synoptic_option_instr_ser_full_slice.dat";
	public static String synoptic_instrument_txt_full_slice = "./synoptic_option_instr_full_slice.txt";

	public void testInitAllOptionsInSynoptic() {
		String path = "./subjects/synoptic/synoptic.jar;"
			+ "./subjects/synoptic/libs/plume.jar;"
			+ "./subjects/synoptic/libs/commons-io-2.0.1.jar;"
			+ "./subjects/synoptic/libs/commons-fileupload-1.2.2.jar;"
			+ "./subjects/synoptic/libs/junit-4.9b2.jar";
//	    String mainClass = "Lsynoptic/main/Main";
	    List<ConfEntity> synopticConfList = SynopticExpUtils.getSynopticList();
	    ConfEntityRepository repo = new ConfEntityRepository(synopticConfList);
	    repo.initializeTypesInConfEntities(path);
	    for(ConfEntity conf : synopticConfList) {
	    	System.out.println(conf);
	    }
	    assertEquals(37, synopticConfList.size());
	}
	
	public void testSliceOptionsInSynoptic() {
		//Log.logConfig("./synoptic-options-log.txt");
		long start = System.currentTimeMillis();
		getSynopticConfOutputs();
		long end = System.currentTimeMillis();
		System.out.println("Eclapsed: " + (end - start)/1000);
		Log.removeLogging();
	}
	
	public void testCreateInstrumentSchema() {
        Collection<ConfPropOutput> outputs = getSynopticConfOutputs();
		
		InstrumentSchema schema = new InstrumentSchema();
		schema.setType(TYPE.SOURCE_PREDICATE);
		schema.addInstrumentationPoint(outputs);
		
		ConfOutputSerializer.serializeSchema(schema, synoptic_instrument_file);
		ConfOutputSerializer.writeToFileAsText(schema, synoptic_instrument_txt);
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(synoptic_instrument_file);
		assertEquals(schema.toString(), newSchema.toString());
	}
	
	public void testCreateInstrumentSchemaFullSlice() {
        Collection<ConfPropOutput> outputs = getSynopticConfOutputsFullSlice(TYPE.SOURCE_PREDICATE);
		
		InstrumentSchema schema = new InstrumentSchema();
		schema.setType(TYPE.SOURCE_PREDICATE);
		schema.addInstrumentationPoint(outputs);
		
		ConfOutputSerializer.serializeSchema(schema, synoptic_instrument_file_full_slice);
		ConfOutputSerializer.writeToFileAsText(schema, synoptic_instrument_txt_full_slice);
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(synoptic_instrument_file_full_slice);
		assertEquals(schema.toString(), newSchema.toString());
	}
	
	static String path = "./subjects/synoptic/synoptic.jar;"
		+ "./subjects/synoptic/libs/plume.jar;"
		+ "./subjects/synoptic/libs/commons-io-2.0.1.jar;"
		+ "./subjects/synoptic/libs/commons-fileupload-1.2.2.jar;"
		+ "./subjects/synoptic/libs/junit-4.9b2.jar";
    static String mainClass = "Lsynoptic/main/Main";
    
	public static Collection<ConfPropOutput> getSynopticConfOutputs() {
        List<ConfEntity> synopticConfList = SynopticExpUtils.getSynopticList();
        Collection<ConfPropOutput> confs = CommonUtils.getConfPropOutputs(path, mainClass, synopticConfList, false);
        return confs;
	}
	
	public static Collection<ConfPropOutput> getSynopticConfOutputsFullSlice(TYPE t) {
        List<ConfEntity> synopticConfList = SynopticExpUtils.getSynopticList();
        Collection<ConfPropOutput> confs = CommonUtils.getConfPropOutputsFullSlicing(path, mainClass, synopticConfList, 
        		"JavaAllExclusions.txt", CG.RTA, false,
        		DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS, ControlDependenceOptions.NO_EXCEPTIONAL_EDGES, t);
        return confs;
	}
}