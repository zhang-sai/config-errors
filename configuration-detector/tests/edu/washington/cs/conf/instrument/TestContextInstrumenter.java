package edu.washington.cs.conf.instrument;

import junit.framework.TestCase;

public class TestContextInstrumenter extends TestCase {

	public void testSampleProgram() throws Exception {
		ConfInstrumenter instrumenter = new ConfInstrumenter(null);
		instrumenter.turnOnContextInstrumentation();
		instrumenter.instrument("./tests/edu/washington/cs/conf/instrument/test.baseline.entryexit.jar",
				  "./output/test.baseline.entryexit-confinstrumented.jar");
		InstrumentStats.showInstrumentationStats();
	}
	
}
