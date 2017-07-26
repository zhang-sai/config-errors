package edu.washington.cs.conf.instrument;

import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import junit.framework.TestCase;

public class TestInstrumentationJChord extends TestCase {
	
	/**
	 * trick to run the chord:
	 * java -cp ./chord.jar;path_to_bin -Dchord.class.path=  -Dchord.run.analyses=
	 *     -Dchord.class.path=path_to_bin;classes/ chord.project.Boot
	 * */
	public void testInstrument() throws Exception {
		String filePath = "./chord_option_instr_ser.dat";
		InstrumentSchema schema = ConfOutputSerializer.deserializeAsSchema(filePath);
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument("./subjects/jchord/chord.jar", "./subjects/jchord/chord-instrumented.jar");
		
		InstrumentStats.showInstrumentationStats();
	}
	
}
