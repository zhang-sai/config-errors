package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class TestStmtExecutedDiffer extends TestCase {
	
	public void testDiffer() {
		List<StmtExecuted> good1 = StmtFileReader.readStmts("./tests/edu/washington/cs/conf/diagnosis/stmts_dump_good1.txt");
		List<StmtExecuted> good2 = StmtFileReader.readStmts("./tests/edu/washington/cs/conf/diagnosis/stmts_dump_good2.txt");
		List<StmtExecuted> bad1 = StmtFileReader.readStmts("./tests/edu/washington/cs/conf/diagnosis/stmts_dump_bad.txt");
		
		StmtExecuted.addSourceNumber("./subjects/testdata.jar", good1,good2, bad1);
		
		Collection<Collection<StmtExecuted>> goodRuns
	        = new LinkedList<Collection<StmtExecuted>>();
		goodRuns.add(good1);
		goodRuns.add(good2);
		
		Collection<Collection<StmtExecuted>> badRuns
            = new LinkedList<Collection<StmtExecuted>>();
		badRuns.add(bad1);
		
		//do diff
		computeScore(goodRuns, badRuns);
	}
	
	public void testDiffer2() {
		String dir = "./tests/edu/washington/cs/conf/diagnosis/";
		List<StmtExecuted> good1 = StmtFileReader.readStmts(dir + "test.baseline-good1-no-args.txt");
		List<StmtExecuted> good2 = StmtFileReader.readStmts(dir + "test.baseline-good2-option1-1.txt");
		List<StmtExecuted> good3 = StmtFileReader.readStmts(dir + "test.baseline-good3-option1-1-option2-2.txt");
		List<StmtExecuted> good4 = StmtFileReader.readStmts(dir + "test.baseline-good4-option1-1-option2-2-option3-3.txt");
		List<StmtExecuted> good5 = StmtFileReader.readStmts(dir + "test.baseline-good5-option1-1-option2-2-option3--3.txt");
		List<StmtExecuted> good6 = StmtFileReader.readStmts(dir + "test.baseline-good6-option1-1-option2--2-option3-3.txt");
		
		List<StmtExecuted> bad1 = StmtFileReader.readStmts(dir + "test.baseline-bad-1-2-0.txt");
		
		StmtExecuted.addSourceNumber(dir + "test.baseline.diagnoser.jar", good1,good2, good3, good4, good5, good4, bad1);
		
		Collection<Collection<StmtExecuted>> goodRuns
	        = new LinkedList<Collection<StmtExecuted>>();
		goodRuns.add(good1);
		goodRuns.add(good2);
		goodRuns.add(good3);
		goodRuns.add(good4);
		goodRuns.add(good5);
		goodRuns.add(good6);
		
		Collection<Collection<StmtExecuted>> badRuns
            = new LinkedList<Collection<StmtExecuted>>();
		badRuns.add(bad1);
		
		//do diff
		computeScore(goodRuns, badRuns);
	}
	
	public static Map<String, Float> computeScore(Collection<Collection<StmtExecuted>> goodRuns,
			Collection<Collection<StmtExecuted>> badRuns) {
		StmtExecutedDiffer differ = new StmtExecutedDiffer(goodRuns, badRuns);
		differ.computeStmtScores();
		Map<String, Float> scores = differ.getRankedStmts();
		
		for(String s : scores.keySet()) {
			System.out.println(s + "   " + scores.get(s));
		}
		
		return scores;
	}

}
