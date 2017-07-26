package edu.washington.cs.conf.experiments.javalanche;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.evol.EvolConfOptionRepository;
import edu.washington.cs.conf.instrument.ConfInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentStats;
import edu.washington.cs.conf.instrument.evol.TestInstrumentPrograms;
import junit.framework.TestCase;

public class TestInstrumentJavalanche extends TestCase {
	
	String javalancheOrigJar = TestInstrumentPrograms.javalanche40InputJar;
	String javalancheOutputJar = "D:\\research\\confevol\\subject-programs\\javalanche\\versions\\javalanche\\javalanche-0.4.0-bin\\lib\\javalanche-0.4-confdiagnoser-instrumented.jar";
	String javalancheSchema = EvolConfOptionRepository.javalancheNewCacheFile;
	
	public void testInstrument40() throws Exception {
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(javalancheSchema);
		//instrument
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument(javalancheOrigJar, javalancheOutputJar);
		
		InstrumentStats.showInstrumentationStats();
	}

}
