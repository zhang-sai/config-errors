package edu.washington.cs.conf.experiments.jchord;

import java.util.Collection;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.instrument.ConfInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentStats;
import edu.washington.cs.conf.instrument.RelatedStmtInstrumenter;
import edu.washington.cs.conf.util.Log;
import junit.framework.TestCase;

public class TestInstrumentJChord extends TestCase {
	
	static String jchord_notrace = "./subjects/jchord/chord-no-trace.jar";
	static String jchord_instrument = "./subjects/jchord/chord-no-trace-instrumented.jar";
	static String jchord_instrument_related_stmt = "./subjects/jchord/chord-no-trace-instrumented-related-stmts.jar";
	static String jchord_instrument_full_slice = "./subjects/jchord/chord-no-trace-instrumented-full-slice.jar";
	
	public void testInstrument() throws Exception {
		String filePath = TestSliceJChordConfigOptions.jchord_instrument_file;
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		Log.logConfig("./instrument-jchord-log.txt");
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument(jchord_notrace, jchord_instrument);
		
		Log.removeLogging();
		
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testInstrumentRelated() throws Exception {
		Collection<ConfPropOutput> outputs = TestSliceJChordConfigOptions.getJChordConfOutputs();
		RelatedStmtInstrumenter instrumenter = new RelatedStmtInstrumenter(outputs);
		instrumenter.instrument(jchord_notrace, jchord_instrument_related_stmt);
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testInstrumentFullSlice() throws Exception {
		String filePath = TestSliceJChordConfigOptions.jchord_instrument_file_full_slice;
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
//		Log.logConfig("./instrument-jchord-log.txt");
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		//reduce the number of instrumentation point
		instrumenter.setReduceInstrPoint(true);
		
		instrumenter.instrument(jchord_notrace, jchord_instrument_full_slice);
		
//		Log.removeLogging();
		
		InstrumentStats.showInstrumentationStats();
	}
	
}
