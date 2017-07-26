package edu.washington.cs.conf.instrument.evol;

import edu.washington.cs.conf.instrument.InstrumentStats;
import junit.framework.TestCase;

public class TestPredicateInstrumenter extends TestCase {

	//export the tracer jar as evoltracer.jar, and execute the test driver, via:
	// java -cp ./output.jar;./evoltracer.jar test.slice.depfield.SeeCoverage n1 n2
	public void testSimpleInstrumenter() throws Exception {
		PredicateInstrumenter instrumenter = new PredicateInstrumenter();
		instrumenter.setDisasm(true);
		instrumenter.setUseSigMap(true);
		instrumenter.instrument("./subjects/testdata.jar", "./output.jar");
		instrumenter.saveSigMappings();
		InstrumentStats.showInstrumentationStats();
	}
	
	public void testToyInstrumenter() throws Exception {
		PredicateInstrumenter instrumenter = new PredicateInstrumenter();
		instrumenter.setDisasm(true);
		String file = "D:\\research\\configurations\\workspace\\configuration-detector\\evol-experiments\\zz-toy-examples\\evolex.jar";
		String outputfile = "D:\\research\\configurations\\workspace\\configuration-detector\\evol-experiments\\zz-toy-examples\\evolex-instrument.jar";
		instrumenter.instrument(file, outputfile);
		InstrumentStats.showInstrumentationStats();
	}
}
