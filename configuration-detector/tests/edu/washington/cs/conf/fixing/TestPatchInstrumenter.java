package edu.washington.cs.conf.fixing;

import junit.framework.TestCase;

public class TestPatchInstrumenter extends TestCase {

	String srcJar = "./subjects/predicate.jar";
	String outputJar = "./output/predicate-output.jar";
	
	public void testPredicate() throws Exception {
		PatchInstrumenter instrumenter = new PatchInstrumenter(null);
		instrumenter.instrument(srcJar, outputJar);
	}
	
}
