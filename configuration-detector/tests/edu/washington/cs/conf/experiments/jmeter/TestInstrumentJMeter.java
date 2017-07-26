package edu.washington.cs.conf.experiments.jmeter;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.evol.EvolConfOptionRepository;
import edu.washington.cs.conf.experiments.randoop.TestSliceRandoopConfigOptions;
import edu.washington.cs.conf.instrument.ConfInstrumenter;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import edu.washington.cs.conf.instrument.InstrumentStats;
import edu.washington.cs.conf.instrument.evol.TestInstrumentPrograms;
import junit.framework.TestCase;

public class TestInstrumentJMeter extends TestCase {

	String jmeterOriginalJar = TestInstrumentPrograms.jmeter29InputJar;
	String jmeterOutputJar = "D:\\research\\confevol\\subject-programs\\jmeter\\apache-jmeter-2.9\\lib\\ext\\ApacheJMeter_core-confdiagnoser-instrumetned.jar";
	static String jmeterSchema = EvolConfOptionRepository.jmeterNewCacheFile;
	
	public void testInstrumentJMeter29() throws Exception {
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(jmeterSchema);
		//instrument
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument(jmeterOriginalJar, jmeterOutputJar);
		
		InstrumentStats.showInstrumentationStats();
	}
	
}
