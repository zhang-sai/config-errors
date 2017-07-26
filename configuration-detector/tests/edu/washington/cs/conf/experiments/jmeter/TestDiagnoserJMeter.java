package edu.washington.cs.conf.experiments.jmeter;

import java.util.Collection;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.evol.EvolConfOptionRepository;
import edu.washington.cs.conf.analysis.evol.experiments.TestOptionsAndSlicing;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.diagnosis.MainAnalyzer.SelectionStrategy;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import junit.framework.TestCase;

public class TestDiagnoserJMeter extends TestCase {
	
	public void testJMeter() {
		String dir = "./experiments/jmeter-database/";
		String badRun = dir + "trace_dump_badrun.txt";
		String[] goodRuns = new String[]{
				dir + "trace_dump_1.txt",
				dir + "trace_dump_2.txt",
				dir + "trace_dump_3.txt",
				dir + "trace_dump_4.txt",
				dir + "trace_dump_5.txt",
				dir + "trace_dump_6.txt",
		};
		ConfEntityRepository repo = EvolConfOptionRepository.jmeterNewConfs();
		String srcDir = null;
		Collection<ConfPropOutput> confSlices = TestOptionsAndSlicing.getJMeterNewConfPropOutput();
		SelectionStrategy strategy = null;
		MainAnalyzer.diagnoseConfigErrors(badRun, goodRuns, repo, srcDir, confSlices, strategy);
	}

}
