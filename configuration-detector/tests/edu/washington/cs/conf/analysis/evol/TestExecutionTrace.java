package edu.washington.cs.conf.analysis.evol;

import java.util.Set;

import junit.framework.TestCase;

public class TestExecutionTrace extends TestCase {

	private String[] sampleTraces = new String[]{
			"NORMAL:signature-1##1",
			"NORMAL:signature-2##2",
			"NORMAL:signature-enter##2",
			"NORMAL:signature-3##3",
			"NORMAL:signature-4##4",
			"NORMAL:signature-5##5",
			"NORMAL:signature-exit##2",
			"NORMAL:signature-6##6",
			"NORMAL:signature-enter##2",
			"NORMAL:signature-7##7",
			"NORMAL:signature-exit##2",
			"NORMAL:signature-8##8",
			"NORMAL:signature-enter##3",
			"NORMAL:signature-9##9",
			"NORMAL:signature-exit##3"
	};
	
	public void testParseTrace() {
		ExecutionTrace trace = new ExecutionTrace(sampleTraces);
		String startMethodSig = "signature-enter";
		int startIndex = 2;
		String endMethodSig = "signature-exit";
		int endIndex = 2;
		Set<InstructionExecInfo> set = trace.getExecutedInstructionsBetween(startMethodSig, startIndex, endMethodSig, endIndex);
		System.out.println("Size: " + set.size());
		assertEquals(4, set.size());
		
		startMethodSig = "signature-enter";
		startIndex = 3;
		endMethodSig = "signature-exit";
		endIndex = 3;
		set = trace.getExecutedInstructionsBetween(startMethodSig, startIndex, endMethodSig, endIndex);
		System.out.println("Size: " + set.size());
		assertEquals(1, set.size());
	}
}
