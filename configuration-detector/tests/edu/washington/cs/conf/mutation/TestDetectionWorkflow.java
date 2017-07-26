package edu.washington.cs.conf.mutation;

import junit.framework.TestCase;

public class TestDetectionWorkflow extends TestCase {

	public void testToyExample() {
		DetectionWorkflow dw = new DetectionWorkflow();
		dw.setProgramRunner(UtilityFactory.createRunnerByReflection());
		dw.setUserManual(UtilityFactory.createUserManual());
		dw.detect();
	}
	
}
