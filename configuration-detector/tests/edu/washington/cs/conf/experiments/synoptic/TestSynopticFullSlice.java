package edu.washington.cs.conf.experiments.synoptic;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.experiments.SynopticExpUtils;
import junit.framework.TestCase;

public class TestSynopticFullSlice extends TestCase {
	
	public String badRun = "./experiments/synoptic-database/2pc_3nodes_100tx_full-slice-bad.txt";
	
	public String good1 = "./experiments/synoptic-database/2pc_3nodes_100tx_full-slice.txt";
	public String good2 = "./experiments/synoptic-database/2pc_3nodes_5tx_full-slice.txt";
	
	public String[] db = new String[]{good1, good2};
	
	public void testDiagnoseSimilar() {
		ConfEntityRepository repo = SynopticExpUtils.getConfEntityRepository();
		MainAnalyzer.diagnoseConfigErrors(badRun, db,
				repo, null, null, null);
	}

}
