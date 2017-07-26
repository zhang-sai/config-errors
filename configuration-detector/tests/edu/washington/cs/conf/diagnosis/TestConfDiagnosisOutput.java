package edu.washington.cs.conf.diagnosis;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;

import junit.framework.TestCase;

public class TestConfDiagnosisOutput extends TestCase {
	
	public void testRankByAverage() {
		Collection<List<ConfDiagnosisOutput>> coll = new LinkedList<List<ConfDiagnosisOutput>>();
		
		ConfEntity e1 = new ConfEntity("c1", "conf1", true);
		ConfEntity e2 = new ConfEntity("c2", "conf2", true);
		ConfEntity e3 = new ConfEntity("c3", "conf3", true);
		ConfEntity e4 = new ConfEntity("c4", "conf4", true);
		ConfEntity e5 = new ConfEntity("c5", "conf5", true);
		ConfEntity e6 = new ConfEntity("c6", "conf6", true);
		
		ConfDiagnosisOutput o1 = new ConfDiagnosisOutput(e1);
		ConfDiagnosisOutput o2 = new ConfDiagnosisOutput(e2);
		ConfDiagnosisOutput o3 = new ConfDiagnosisOutput(e3);
		ConfDiagnosisOutput o4 = new ConfDiagnosisOutput(e4);
		ConfDiagnosisOutput o5 = new ConfDiagnosisOutput(e5);
		ConfDiagnosisOutput o6 = new ConfDiagnosisOutput(e6);
		
		coll.add(Arrays.asList(o1, o2, o3, o4));
		coll.add(Arrays.asList(o3, o1, o2, o6));
		coll.add(Arrays.asList(o3, o1, o5, o2));
		coll.add(Arrays.asList(o5, o2, o3, o1));
		coll.add(Arrays.asList(o4, o3, o1));
		
		List<ConfDiagnosisOutput> ranking = DiagnosisOutputRanking.rankByAvgRanking(coll);
		for(ConfDiagnosisOutput r : ranking) {
			System.out.println(r);
			r.showExplanations(System.out);
		}
		assertEquals(ranking.get(0).getConfEntity().getClassName(), "c3");
		assertEquals(ranking.get(1).getConfEntity().getClassName(), "c5");
		assertEquals(ranking.get(2).getConfEntity().getClassName(), "c1");
		assertEquals(ranking.get(3).getConfEntity().getClassName(), "c4");
		assertEquals(ranking.get(4).getConfEntity().getClassName(), "c2");
		assertEquals(ranking.get(5).getConfEntity().getClassName(), "c6");
		
	}

}
