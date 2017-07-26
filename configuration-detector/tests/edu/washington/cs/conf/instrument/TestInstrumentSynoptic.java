package edu.washington.cs.conf.instrument;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import junit.framework.TestCase;

public class TestInstrumentSynoptic extends TestCase {
	
	public void testInstrument() throws Exception {
	    String filePath = "./synoptic_option_instr_ser.dat";
	    InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
	
	    ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
	    instrumenter.instrument("./subjects/synoptic/synoptic.jar",
	    		"./output/synoptic-instrumented.jar");
	
	    InstrumentStats.showInstrumentationStats();
	}
	
	/**
	 * command to run:
	 * java -cp ./synoptic-instrumented.jar;./libs/* synotpic.main.Main
	 * -r "some tokens" -m ""  -o -d D:\tools\graphviz2.8\dot\bin\dot
	*/
}
