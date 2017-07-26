package edu.washington.cs.conf.experiments.weka;

import java.util.Collection;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.instrument.ConfInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentStats;
import edu.washington.cs.conf.instrument.RelatedStmtInstrumenter;
import junit.framework.TestCase;

public class TestInstrumentWeka extends TestCase {
	
	public static String weka_jar = "./subjects/weka/weka-no-trace.jar";
	public static String weka_jar_instrument = "./output/weka-instrumented-no-trace.jar";
	public static String weka_jar_related_stmt = "./output/weka-instrumented-no-trace-related-stmt.jar";
	
	public static String weka_jar_instrument_full_slice = "./output/weka-instrumented-no-trace-full-slice.jar";

	public void testInstrumentWeka() throws Exception {
		String filePath = TestSliceWekaConfigOptions.weka_instrument_file;
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument(weka_jar, weka_jar_instrument);
		
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testInstrumentWekaRelatedStmt() throws Exception {
		Collection<ConfPropOutput> slices = TestSliceWekaConfigOptions.getWekaConfOutputs();
		RelatedStmtInstrumenter instrumenter = new RelatedStmtInstrumenter(slices);
		instrumenter.instrument(weka_jar, weka_jar_related_stmt);
		
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testInstrumentWekaFullSlice() throws Exception {
		String filePath = TestSliceWekaConfigOptions.weka_instrument_file_full_slice;
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument(weka_jar, weka_jar_instrument_full_slice);
		
		InstrumentStats.showInstrumentationStats();
	}
}
