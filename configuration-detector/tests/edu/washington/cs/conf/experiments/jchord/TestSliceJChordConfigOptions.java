package edu.washington.cs.conf.experiments.jchord;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.experiments.ChordExpUtils;
import edu.washington.cs.conf.experiments.CommonUtils;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentSchema.TYPE;
import junit.framework.TestCase;

public class TestSliceJChordConfigOptions extends TestCase {
	public static String jchord_instrument_file = "./jchord_option_instr_ser.dat";
	public static String jchord_instrument_txt = "./jchord_option_instr.txt";
	public static String jchord_main = "Lchord/project/Main";
	public static String jchord_exclusion = "ChordExclusions.txt";
	
	//use full slice
	public static String jchord_instrument_file_full_slice = "./jchord_option_instr_ser_full_slice.dat";
	public static String jchord_instrument_txt_full_slice = "./jchord_option_instr_full_slice.txt";
	
	public void testInitAllConfigOptions() {
		String path = TestInstrumentJChord.jchord_notrace;
//		String mainClass = "Lchord/project/Main"
		List<ConfEntity> jchordConfList = ChordExpUtils.getChordConfList();
		ConfEntityRepository repo = new ConfEntityRepository(jchordConfList);
		repo.initializeTypesInConfEntities(path);
		for(ConfEntity conf : jchordConfList) {
			System.out.println(conf);
		}
		assertEquals(77, jchordConfList.size());
	}
	
	public void testSliceOptionsInJChordNoPrune() {
		sliceOptionsInJChord(ChordExpUtils.getChordConfList(), false);
	}
	
	public void testSliceOptionsInJChordAndSeeInside() {
		Collection<ConfPropOutput> outputs = sliceOptionsInJChord(ChordExpUtils.getChordConfList(), false);
		
		List<String> options = Arrays.asList("runBefore", "extraMethodsList", "instrSchemeFileName", "traceKind", "instrKind",
				"dlogAnalysisPathName", "javaAnalysisPathName", "printRels", "printClasses", "runAnalyses", "checkExcludeStr",
				"checkExtExcludeStr", "checkStdExcludeStr", "scopeExcludeStr", "scopeExtExcludeStr", "scopeStdExcludeStr",
				"CHkind", "runtimeJvmargs", "reflectKind", "runIDs", "srcPathName", "userClassPathName", "mainClassName");
		for(ConfPropOutput output : outputs) {
			if(options.contains(output.conf.getConfName())) {
				System.out.println("conf name: " + output.conf.getConfName());
				System.out.println("   contain chord.project.Config.<clinit>? : " + output.includeStatement("chord.project.Config.<clinit>", 39));
			}
		}
		
	}
	
	public void testSliceOptionsInJChordWithPrune() {
		sliceOptionsInJChord(ChordExpUtils.getChordConfList(), true);
	}
	
	public void testSliceSampleOptions() {
		sliceOptionsInJChord(ChordExpUtils.getSampleConfList(), false);
	}
	
	public void testSliceOptions() {
		long start = System.currentTimeMillis();
		getJChordConfOutputs();
		long end = System.currentTimeMillis();
		System.out.println("elapsed: " + (end - start)/1000);
	}
	
	public static Collection<ConfPropOutput> getJChordConfOutputs() {
		return sliceOptionsInJChord(ChordExpUtils.getChordConfList(), false);
	}
	
	static String path = TestInstrumentJChord.jchord_notrace;
	static String mainClass = jchord_main;
	static String exFile = jchord_exclusion;
	
	public static Collection<ConfPropOutput> getJChordConfOutputsFullSlice(TYPE t) {
		List<ConfEntity> jchordConfList = ChordExpUtils.getChordConfList();
		Collection<ConfPropOutput> confs = CommonUtils.getConfPropOutputsFullSlicing(path, mainClass, jchordConfList, exFile, CG.RTA, false,
				DataDependenceOptions.NO_BASE_NO_HEAP_NO_EXCEPTIONS, ControlDependenceOptions.NO_EXCEPTIONAL_EDGES, t);
		return confs;
	}
	
	public static Collection<ConfPropOutput> sliceOptionsInJChord(List<ConfEntity> jchordConfList, boolean prune) {
//		Log.logConfig("./jchord-config-slice.txt");
		Collection<ConfPropOutput> confs = CommonUtils.getConfPropOutputs(path, mainClass, jchordConfList, exFile, prune);
//		Log.removeLogging();
		return confs;
	}
	
	public void testCreateInstrumentSchema() {
		Collection<ConfPropOutput> outputs = getJChordConfOutputs();
		//sliceOptionsInJChord(ChordExpUtils.getChordConfList(), false);
		
		InstrumentSchema schema = new InstrumentSchema();
		schema.setType(TYPE.SOURCE_PREDICATE); //NOTE use the abstraction of source predicate
		schema.addInstrumentationPoint(outputs);
		
		ConfOutputSerializer.serializeSchema(schema, jchord_instrument_file);
		ConfOutputSerializer.writeToFileAsText(schema, jchord_instrument_txt);
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(jchord_instrument_file);
		assertEquals(schema.toString(), newSchema.toString());
	}
	
	public void testCreateInstrumentSchemaFullSlice() {
		Collection<ConfPropOutput> outputs = getJChordConfOutputsFullSlice(TYPE.SOURCE_PREDICATE);
		
		InstrumentSchema schema = new InstrumentSchema();
		schema.setType(TYPE.SOURCE_PREDICATE); //NOTE use the abstraction of source predicate
		schema.addInstrumentationPoint(outputs);
		
		ConfOutputSerializer.serializeSchema(schema, jchord_instrument_file_full_slice);
		ConfOutputSerializer.writeToFileAsText(schema, jchord_instrument_txt_full_slice);
		
		//recover from the file
		InstrumentSchema newSchema = ConfOutputSerializer.deserializeAsSchema(jchord_instrument_file_full_slice);
		assertEquals(schema.toString(), newSchema.toString());
	}
}
