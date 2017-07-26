package edu.washington.cs.conf.analysis;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.util.CancelException;

import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.experiments.ChordExpUtils;
import edu.washington.cs.conf.experiments.RandoopExpUtils;
import edu.washington.cs.conf.experiments.SynopticExpUtils;
import edu.washington.cs.conf.experiments.WekaExpUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentSchema.TYPE;
import edu.washington.cs.conf.util.WALAUtils;

import junit.framework.TestCase;

public class TestGenerateInstrumentationSchema extends TestCase {
	
	public void testSlice1() throws IllegalArgumentException, CancelException {
		String path = "D:\\research\\configurations\\workspace\\configuration-detector\\bin\\test\\slice\\depfield";
		String mainClass = "Ltest/slice/depfield/FieldDeps";
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setCGType(CG.ZeroCFA);
		helper.setExclusionFile("JavaAllExclusions.txt");
		helper.setContextSensitive(false);
		helper.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		helper.setControlDependenceOptions(ControlDependenceOptions.NONE);
		helper.buildAnalysis();
		
		ConfEntity entity = new ConfEntity("test.slice.depfield.FieldDeps", "field_value", true);
		Statement seed = helper.extractConfStatement(entity);
		System.out.println("Seed is: " + seed);
		Collection<Statement> slices = helper.sliceConfOption(entity); 
			//helper.computeContextSensitiveForwardSlice(seed);
		WALAUtils.dumpSlice(slices, new PrintWriter(System.out));
		
		for(Statement s : slices) {
			String fullMethodName = WALAUtils.getFullMethodName(s.getNode().getMethod());
			if(fullMethodName.startsWith("test.slice.depfield.FieldDeps.compute_result2")) {
				System.out.println(WALAUtils.getAllIRAsString(s.getNode()));
			    break;
			}
		}
		
		Collection<IRStatement> irs = ConfigurationSlicer.convert(slices);
		for(IRStatement ir : irs) {
			System.out.println("  " + ir);
		}
		
		ConfPropOutput output = helper.outputSliceConfOption(entity);
		System.out.println(output);
	}

	
	
	public void testSliceWekaCheaply() {
		String path = "./subjects/weka/weka.jar;./subjects/weka/JFlex.jar;" +
		    "./subjects/weka/java-cup.jar";
		String mainClass = "Lweka/classifiers/trees/J48";
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.setCGType(CG.ZeroCFA);
		helper.setExclusionFile("JavaAllExclusions.txt");
		helper.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
		helper.setControlDependenceOptions(ControlDependenceOptions.NONE);
		helper.setContextSensitive(false); //context-insensitive
		helper.buildAnalysis();
		
		List<ConfEntity> wekaConfList = WekaExpUtils.getWekaConfList();
		
		Collection<ConfPropOutput> outputs = new LinkedList<ConfPropOutput>();
		for(ConfEntity entity : wekaConfList) {
			ConfPropOutput output = helper.outputSliceConfOption(entity);
			outputs.add(output);
			System.out.println(" - " + output.statements.size());
		}

		System.out.println("size: " + outputs.size());
		assertEquals(wekaConfList.size(), outputs.size());
		
		//save as configuration schema
		InstrumentSchema schema = new InstrumentSchema();
		schema.addInstrumentationPoint(outputs);
		
		String filePath = "./weka_option_instr_ser.dat";
		ConfOutputSerializer.serializeSchema(schema, filePath);
		ConfOutputSerializer.writeToFileAsText(schema, "./weka_option_instr.txt");
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(filePath);
		assertEquals(schema.toString(), newSchema.toString());
	}
	
	public void testSliceSynopticCheaply() {
		String path = "./subjects/synoptic/synoptic.jar;"
			+ "./subjects/synoptic/libs/plume.jar;"
			+ "./subjects/synoptic/libs/commons-io-2.0.1.jar;"
			+ "./subjects/synoptic/libs/commons-fileupload-1.2.2.jar;"
			+ "./subjects/synoptic/libs/junit-4.9b2.jar";
	    String mainClass = "Lsynoptic/main/Main";
	    ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
	    helper.setCGType(CG.ZeroCFA);
	    helper.setExclusionFile("JavaAllExclusions.txt");
	    helper.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
	    helper.setControlDependenceOptions(ControlDependenceOptions.NONE);
	    helper.setContextSensitive(false); //context-insensitive
	    helper.buildAnalysis();
	
	    List<ConfEntity> synotpicConfList = SynopticExpUtils.getSynopticList();
	
	    Collection<ConfPropOutput> outputs = new LinkedList<ConfPropOutput>();
	    for(ConfEntity entity : synotpicConfList) {
		    ConfPropOutput output = helper.outputSliceConfOption(entity);
		    outputs.add(output);
		    System.out.println(" - " + output.statements.size());
	    }

	    System.out.println("size: " + outputs.size());
	    assertEquals(synotpicConfList.size(), outputs.size());
	
	    //save as configuration schema
	    InstrumentSchema schema = new InstrumentSchema();
	    schema.addInstrumentationPoint(outputs);
	
	    String filePath = "./synoptic_option_instr_ser.dat";
	    ConfOutputSerializer.serializeSchema(schema, filePath);
	    ConfOutputSerializer.writeToFileAsText(schema, "./synoptic_option_instr.txt");
	
	    //recover from the file
	    InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(filePath);
	    assertEquals(schema.toString(), newSchema.toString());
	}
	
	public void testSliceJChordCheaply() {
		String path = "./subjects/jchord/chord.jar";
		String mainClass = "Lchord/project/Main";
		
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
	    helper.setCGType(CG.ZeroCFA);
	    helper.setExclusionFile("ChordExclusions.txt");
	    helper.setDataDependenceOptions(DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS);
	    helper.setControlDependenceOptions(ControlDependenceOptions.NONE);
	    helper.setContextSensitive(false); //context-insensitive
	    helper.buildAnalysis();
	
	    List<ConfEntity> jchordConfList = ChordExpUtils.getChordConfList();
	
	    Collection<ConfPropOutput> outputs = new LinkedList<ConfPropOutput>();
	    for(ConfEntity entity : jchordConfList) {
		    ConfPropOutput output = helper.outputSliceConfOption(entity);
		    outputs.add(output);
		    System.out.println(entity);
		    System.out.println(" - " + output.statements.size());
	    }

	    System.out.println("size: " + outputs.size());
	    assertEquals(jchordConfList.size(), outputs.size());
	
	    //save as configuration schema
	    InstrumentSchema schema = new InstrumentSchema();
	    schema.setType(TYPE.SOURCE_PREDICATE); //change the option here
	    schema.addInstrumentationPoint(outputs);
	
	    String filePath = "./chord_option_instr_ser.dat";
	    ConfOutputSerializer.serializeSchema(schema, filePath);
	    ConfOutputSerializer.writeToFileAsText(schema, "./chord_option_instr.txt");
	
	    //recover from the file
	    InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(filePath);
	    assertEquals(schema.toString(), newSchema.toString());
	}
}
