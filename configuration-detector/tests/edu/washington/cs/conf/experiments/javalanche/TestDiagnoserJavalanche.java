package edu.washington.cs.conf.experiments.javalanche;

import java.util.Collection;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.analysis.evol.EvolConfOptionRepository;
import edu.washington.cs.conf.analysis.evol.experiments.TestOptionsAndSlicing;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.diagnosis.MainAnalyzer.SelectionStrategy;
import junit.framework.TestCase;

public class TestDiagnoserJavalanche extends TestCase {

	public void testJavalance() {
		String dir = "./experiments/javalanche-database/";
		String badRun = dir + "badrun/merged_trace.txt";
		String[] goodRuns = new String[]{
			dir + "goodrun1/merged_trace.txt",
			dir + "goodrun2/merged_trace.txt",
			dir + "goodrun3/merged_trace.txt"
		};
		ConfEntityRepository repo = EvolConfOptionRepository.javalancheNewConfs();
		String srcDir = null;
		Collection<ConfPropOutput> confSlices = TestOptionsAndSlicing.getJavalancheNewPropOutputs();
		SelectionStrategy strategy = null;
		MainAnalyzer.diagnoseConfigErrors(badRun, goodRuns, repo, srcDir, confSlices, strategy);
	}
}
