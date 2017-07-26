package edu.washington.cs.conf.mutation;

import edu.washington.cs.conf.mutation.weka.TestWekaConfigExamples;
import junit.framework.TestCase;

public class TestUserManual extends TestCase {

	public void testWekaZeroRManual() {
		UserManual manual = new UserManual(TestWekaConfigExamples.zeroR_usermanual);
		for(String key : manual.getAllOptions()) {
			System.out.println(key + "  =>  " + manual.getDescription(key));
		}
	}
	
}
