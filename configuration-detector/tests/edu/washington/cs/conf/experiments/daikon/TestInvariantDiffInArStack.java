package edu.washington.cs.conf.experiments.daikon;

import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.diagnosis.InvariantUtils;
import junit.framework.TestCase;

public class TestInvariantDiffInArStack extends TestCase {
	
	public void testArStack() throws Exception {
		String filename1 = "./tests/edu/washington/cs/conf/experiments/daikon/StackArTester.inv.gz";
		String filename2 = "./tests/edu/washington/cs/conf/experiments/daikon/StackArTester.inv-backup.gz";
		Set<String> sets = InvariantUtils.fetchMethodsWithDiffInvariants(filename1, filename2);
		
		System.out.println(sets);
		assertEquals(3, sets.size());
		assertEquals("[DataStructures.StackArTester.createItem(int), DataStructures.StackArTester.push(int), DataStructures.StackArTester.push_noobserve(int)]",
				sets.toString());
		
		Map<String, Integer> map = InvariantUtils.fetchRankedMethodsWithDiffInvariants(filename1, filename2);
		System.out.println(map.size());
//		Map<String> violatedNums = In
		System.out.println(map);
		System.out.println(sets);
	}
	
	public void testMethodEquals() {
		String daikonMethod = "randoop.util.ProgressDisplay.display(java.lang.String)";
		String jvmMethod = "randoop.util.ProgressDisplay.display(Ljava/lang/String;)V";
//		System.out.println(InvariantUtils.stringEquals(daikonMethod, jvmMethod));
		assertTrue(InvariantUtils.stringEquals(daikonMethod, jvmMethod));
		
		daikonMethod = "randoop.util.MethodReflectionCode.MethodReflectionCode(java.lang.reflect.Method, java.lang.Object, java.lang.Object[])";
		jvmMethod = "randoop.util.MethodReflectionCode.MethodReflectionCode(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)V";
//		System.out.println(InvariantUtils.stringEquals(daikonMethod, jvmMethod));
		assertTrue(InvariantUtils.stringEquals(daikonMethod, jvmMethod));
		
		daikonMethod = "randoop.util.MethodReflectionCode.MethodReflectionCode(int)";
		jvmMethod = "randoop.util.MethodReflectionCode.MethodReflectionCode(I)V";
//		System.out.println(InvariantUtils.stringEquals(daikonMethod, jvmMethod));
		assertTrue(InvariantUtils.stringEquals(daikonMethod, jvmMethod));
	}

}
