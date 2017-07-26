package edu.washington.cs.conf.instrument;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import junit.framework.TestCase;

public class TestInstrumentWeka extends TestCase {
	
	public void testInstrument() throws Exception {
		String filePath = "./weka_option_instr_ser.dat";
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument("./subjects/weka/weka.jar", "./output/weka-instrumented.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
}
