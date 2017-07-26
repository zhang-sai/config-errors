package edu.washington.cs.conf.experiments.jchord;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.diagnosis.PredicateProfileTuple;
import edu.washington.cs.conf.experiments.ChordExpUtils;
import junit.framework.TestCase;

public class TestJChordFullSlice extends TestCase {

	String norace = "./experiments/jchord-database/simpletest-no-race-full-slice.txt";
	
	String dlog = "./experiments/jchord-database/simpletest-dlog-full-slice.txt";
	String hasrace = "./experiments/jchord-database/simpletest-has-race-full-slice.txt";
	String deadlock = "./experiments/jchord-database/simpletest-deadlock-full-slice.txt";
	
	String donothing = "./experiments/jchord-database/simpletest-do-nothing-full-slice.txt";
	String print = "./experiments/jchord-database/simpletest-print-project-full-slice.txt";
	String ctxtanalysis = "./experiments/jchord-database/simpletest-ctxt-analysis-full-slice.txt";
	
	String[] db = new String[]{dlog, hasrace, deadlock, donothing, print, ctxtanalysis};
	
	public void testDiagnoseSimilar() {
        PredicateProfileTuple.USE_CACHE = true;
		
		ConfEntityRepository repo = ChordExpUtils.getChordRepository();
		MainAnalyzer.diagnoseConfigErrors(norace,
				db, repo, null, null, null);
	}
	
	@Override
	public void tearDown() {
		PredicateProfileTuple.USE_CACHE = false;
	}
}
