package edu.washington.cs.conf.experiments.soot;

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
import edu.washington.cs.conf.experiments.SootExpUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentSchema.TYPE;
import junit.framework.TestCase;

public class TestSliceSootConfigOptions extends TestCase {
	public static String soot_instrument_file = "./soot_option_instr_ser.dat";
	public static String soot_instrument_txt = "./soot_option_instr.txt";
	
	public static String soot_instrument_file_full_slice = "./soot_option_instr_ser_full_slice.dat";
	public static String soot_instrument_txt_full_slice = "./soot_option_instr_full_slice.txt";
	
	public void testInitAllOptionsInSynoptic() {
		String dir = "./subjects/soot-2.5/";
		String path = dir + "soot.jar;" +
		        dir + "libs/coffer.jar;" +
		        dir + "libs/jasminclasses-2.5.0.jar;" +
		        dir + "libs/java_cup.jar;" +
		        dir + "libs/JFlex.jar;" +
		        dir + "libs/pao.jar;" +
		        dir + "libs/polyglot.jar;" +
		        dir + "libs/pth.jar";
		List<ConfEntity> sootConfigs = SootExpUtils.getSootConfList();
		ConfEntityRepository repo = new ConfEntityRepository(sootConfigs);
		repo.initializeTypesInConfEntities(path);
		for(ConfEntity conf : sootConfigs) {
	    	System.out.println(conf);
	    }
	    assertEquals(49, sootConfigs.size());
	}
	
	public void testSliceOptionsInSoot() {
		long start = System.currentTimeMillis();
		getSootConfOutputs();
		long end = System.currentTimeMillis();
		System.out.println("Elapsed: " + (end-start)/1000);
	}
	
	public void testCreateInstrumentSchema() {
        Collection<ConfPropOutput> outputs = getSootConfOutputs();
		
		InstrumentSchema schema = new InstrumentSchema();
		schema.setType(TYPE.SOURCE_PREDICATE);
		schema.addInstrumentationPoint(outputs);
		
		ConfOutputSerializer.serializeSchema(schema, soot_instrument_file);
		ConfOutputSerializer.writeToFileAsText(schema, soot_instrument_txt);
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(soot_instrument_file);
		assertEquals(schema.toString(), newSchema.toString());
	}
	
	public void testCreateInstrumentSchemaFullSlice() {
        Collection<ConfPropOutput> outputs = getSootConfOutputsFullSlice(TYPE.SOURCE_PREDICATE);
		
		InstrumentSchema schema = new InstrumentSchema();
		schema.setType(TYPE.SOURCE_PREDICATE);
		schema.addInstrumentationPoint(outputs);
		
		ConfOutputSerializer.serializeSchema(schema, soot_instrument_file_full_slice);
		ConfOutputSerializer.writeToFileAsText(schema, soot_instrument_txt_full_slice);
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(soot_instrument_file_full_slice);
		assertEquals(schema.toString(), newSchema.toString());
	}
	
	static String dir = "./subjects/soot-2.5/";
	static String path = dir + "soot.jar;" +
	        dir + "libs/coffer.jar;" +
	        dir + "libs/jasminclasses-2.5.0.jar;" +
	        dir + "libs/java_cup.jar;" +
	        dir + "libs/JFlex.jar;" +
	        dir + "libs/pao.jar;" +
	        dir + "libs/polyglot.jar;" +
	        dir + "libs/pth.jar";
	static String mainClass = "Lsoot/Main";
	
	public static Collection<ConfPropOutput> getSootConfOutputs() {
		List<ConfEntity> sootConfigs = SootExpUtils.getSootConfList();
		Collection<ConfPropOutput> confs = CommonUtils.getConfPropOutputs(path, mainClass, sootConfigs, "SootExclusions.txt",
				CG.ZeroCFA, false); //cannot use 1-CFA, which is too expensive
        return confs;
	}
	
	public static Collection<ConfPropOutput> getSootConfOutputsFullSlice(TYPE t) {
		List<ConfEntity> sootConfigs = SootExpUtils.getSootConfList();
		Collection<ConfPropOutput> confs = CommonUtils.getConfPropOutputsFullSlicing(path, mainClass, sootConfigs, "SootExclusions.txt",
				CG.RTA, false, DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS, ControlDependenceOptions.NO_EXCEPTIONAL_EDGES, t); //cannot use 1-CFA, which is too expensive
        return confs;
	}
}