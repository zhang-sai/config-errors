package edu.washington.cs.conf.instrument;

import junit.framework.TestCase;

/**
 * The testdata.jar only includes java files in the package:
 *    test/slice/depfield
 * */
public class TestStmtInstrumenter extends TestCase {
	public void testSimpleInstrumenter() throws Exception {
		EveryStmtInstrumenter instrumenter = new EveryStmtInstrumenter();
		instrumenter.instrument("./subjects/testdata.jar", "./output/testdata-everystmt.jar");
	}
}
