package edu.washington.cs.conf.experiments.randoop;

import java.util.Collection;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.instrument.ConfInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentStats;
import edu.washington.cs.conf.instrument.RelatedStmtInstrumenter;
import junit.framework.TestCase;

public class TestInstrumentRandoop extends TestCase {
	
	public static String randoop_jar = "./subjects/randoop-jamie-no-trace.jar";
	public static String randoop_jar_instrument = "./output/randoop-instrumented-no-trace.jar";
	public static String randoop_jar_instrument_full_slice = "./output/randoop-instrumented-no-trace-full-slice.jar";
	
	public static String randoop_jar_related_stmt = "./output/randoop-instrumented-no-trace-related-stmt.jar";
	
	public void testInstrument() throws Exception {
		String filePath = TestSliceRandoopConfigOptions.randoop_instrument_file;
//		String randoopSrcDir = "./subjects/randoop/randoop-src/"; //NOT used yet
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument(randoop_jar, randoop_jar_instrument);
		
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testInstrumentFullSlice() throws Exception {
		String filePath = TestSliceRandoopConfigOptions.randoop_instrument_file_full_slice;
//		String randoopSrcDir = "./subjects/randoop/randoop-src/"; //NOT used yet
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument(randoop_jar, randoop_jar_instrument_full_slice);
		
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testInstrumentRelatedStmt() throws Exception {
		Collection<ConfPropOutput> slices = TestSliceRandoopConfigOptions.getConfPropOutputs();
		RelatedStmtInstrumenter instrumenter = new RelatedStmtInstrumenter(slices);
		instrumenter.instrument(randoop_jar, randoop_jar_related_stmt);
		
		InstrumentStats.showInstrumentationStats();
	}
	
	/**
	 * Ignore the following test
	 * */
	@Deprecated
	public void testInstrumentWithContext() throws Exception {
		String filePath = "./randoop_option_instr_ser.dat";
		String randoopSrcDir = "./subjects/randoop/randoop-src/"; //NOT used yet
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.turnOnContextInstrumentation();
		
		instrumenter.instrument("./subjects/randoop-jamie.jar", "./output/randoop-context-instrumented.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
}
