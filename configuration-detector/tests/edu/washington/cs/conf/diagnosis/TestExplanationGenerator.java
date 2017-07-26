package edu.washington.cs.conf.diagnosis;

import junit.framework.TestCase;

public class TestExplanationGenerator extends TestCase {
	
	public void testSample() {
		String expl = ExplanationGenerator.createWellFormattedExpanation("randoop.main.GenInputsAbstract.maxsize",
				"randoop.ForwardGenerator.createNewUniqueSequence()",
				"newSequence.size() > GenInputsAbstract.maxsize", 312 ,
				1315, 190, 2727, 880);
		System.out.println(expl);
	}
}