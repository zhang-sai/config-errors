package edu.washington.cs.conf.diagnosis;

import junit.framework.TestCase;

public class TestPredicateProfile extends TestCase {

	public void testImportanceValue() {
		PredicateProfile p0 = new PredicateProfile("option1", "context1",
				1, 0);
		PredicateProfile p1 = new PredicateProfile("option1", "context1",
				1, 1);
		PredicateProfile p20 = new PredicateProfile("option2", "context2",
				5, 5);
		PredicateProfile p2 = new PredicateProfile("option2", "context2",
				5, 4);
		PredicateProfile p21 = new PredicateProfile("option2", "context2",
				5, 3);
		PredicateProfile p31 = new PredicateProfile("option2", "context2",
				100, 100);
		PredicateProfile p3 = new PredicateProfile("option2", "context2",
				100, 90);
		PredicateProfile p30 = new PredicateProfile("option2", "context2",
				100, 0);
		
		System.out.println(p0.importanceValue());
		assertEquals(1.0f, p0.importanceValue());
		System.out.println(p1.importanceValue());
		assertEquals(1.0f, p1.importanceValue());
		assertEquals(1.6666666f, p20.importanceValue());
		System.out.println(p2.importanceValue());
		assertEquals(1.3793103f, p2.importanceValue());
		System.out.println(p21.importanceValue());
		assertEquals(1.0714285f, p21.importanceValue());
		System.out.println(p3.importanceValue());
		assertEquals(1.980198f, p31.importanceValue());
		assertEquals(1.7839445f, p3.importanceValue());
		System.out.println(p30.importanceValue());
		assertEquals(0.019998f, p30.importanceValue());
	}
	
	public void testAbsoluteImportanceValue() {
		PredicateProfile p1 = new PredicateProfile("option1", "context1",
				1, 0);
		System.out.println(p1.absImportanceValue());
		PredicateProfile p2 = new PredicateProfile("option1", "context1",
				100, 0);
		System.out.println(p2.absImportanceValue());
		PredicateProfile p3 = new PredicateProfile("option1", "context1",
				100, 50);
		System.out.println(p3.absImportanceValue());
		PredicateProfile p4 = new PredicateProfile("option1", "context1",
				5, 2);
		System.out.println(p4.absImportanceValue());
		PredicateProfile p5 = new PredicateProfile("option1", "context1",
				100, 60);
		System.out.println(p5.absImportanceValue());
	}
	
	public void testImportanceValueDiff() {
		PredicateProfile p1 = new PredicateProfile("option1", "context1",
				9, 5);
		PredicateProfile p11 = new PredicateProfile("option2", "context2",
				5, 2);
		System.out.println(p1.importanceValue());
		assertEquals(1.0465117f, p1.importanceValue());
		System.out.println(p11.importanceValue());
		assertEquals(0.7407407f, p11.importanceValue());
		System.out.println(p11.importanceValue() - p1.importanceValue());
		assertEquals(-0.30577093f, p11.importanceValue() - p1.importanceValue());
		
		System.out.println("------------------");
		
		PredicateProfile p2 = new PredicateProfile("option1", "context1",
				1, 1);
		PredicateProfile p20 = new PredicateProfile("option1", "context1",
				1, 0);
		System.out.println(p2.importanceValue());
		assertEquals(1.0f, p2.importanceValue());
		System.out.println(p20.importanceValue());
		assertEquals(1.0f, p20.importanceValue());
		System.out.println(p20.importanceValue() - p2.importanceValue());
		assertEquals(0.0f, p20.importanceValue() - p2.importanceValue());
		
		System.out.println("------------------");
		
		PredicateProfile p3 = new PredicateProfile("option1", "context1",
				10, 9);
		PredicateProfile p30 = new PredicateProfile("option1", "context1",
				9, 2);
		System.out.println(p3.importanceValue());
		assertEquals(1.651376f, p3.importanceValue());
		System.out.println(p30.importanceValue());
		assertEquals(0.43373492f, p30.importanceValue());
		System.out.println(p30.importanceValue() - p3.importanceValue());
		assertEquals(-1.2176411f, p30.importanceValue() - p3.importanceValue());
		
		System.out.println("------------------");
		
		PredicateProfile p4 = new PredicateProfile("option1", "context1",
				5, 5);
		PredicateProfile p40 = new PredicateProfile("option1", "context1",
				5, 0);
		System.out.println(p4.importanceValue());
		assertEquals(1.6666666f, p4.importanceValue());
		System.out.println(p40.importanceValue());
		assertEquals(0.3846154f, p40.importanceValue());
		System.out.println(p40.importanceValue() - p4.importanceValue());
		assertEquals(-1.2820512f, p40.importanceValue() - p4.importanceValue());
		
		System.out.println("------------------");
	}
	
	//really limitation?
	public void testImportanceMetricLimitations() {
		PredicateProfile p1 = new PredicateProfile("option1", "context1",
				1, 0);
		System.out.println(p1.importanceValue());
		PredicateProfile p2 = new PredicateProfile("option1", "context1",
				1, 1);
		System.out.println(p2.importanceValue());
		System.out.println("delta: " + (p2.importanceValue() - p1.importanceValue()));
		System.out.println("absolute value p1: " + p1.absImportanceValue());
		System.out.println("absolute value p2: " + p2.absImportanceValue());
		System.out.println();
		PredicateProfile p3 = new PredicateProfile("option1", "context1",
				100, 90);
		System.out.println(p3.importanceValue());
		PredicateProfile p4 = new PredicateProfile("option1", "context1",
				100, 30);
		System.out.println(p4.importanceValue());
		System.out.println("delta: " + (p4.importanceValue() - p3.importanceValue()));
		System.out.println("absolute value p3: " + p3.absImportanceValue());
		System.out.println("absolute value p4: " + p4.absImportanceValue());
		PredicateProfile p5 = new PredicateProfile("option1", "context1",
				100, 50);
		System.out.println(p5.importanceValue());
		PredicateProfile p6 = new PredicateProfile("option1", "context1",
				100, 30);
		System.out.println(p6.importanceValue());
		System.out.println("delta: " + (p5.importanceValue() - p6.importanceValue()));
		System.out.println("absolute value p5: " + p5.absImportanceValue());
		System.out.println("absolute value p6: " + p6.absImportanceValue());
	}
	
	public void testStatisticProperties1() {
		PredicateProfile p1 = new PredicateProfile("option1", "context1",
				50, 22);
		PredicateProfile p2 = new PredicateProfile("option1", "context1",
				50, 24);
		System.out.println(p2.importanceValue() - p1.importanceValue());
		
		PredicateProfile p3 = new PredicateProfile("option1", "context1",
				50, 26);
		System.out.println(p3.importanceValue() - p2.importanceValue());
		
		PredicateProfile p4 = new PredicateProfile("option1", "context1",
				500, 22);
		PredicateProfile p5 = new PredicateProfile("option1", "context1",
				500, 24);
		System.out.println(p5.importanceValue() - p4.importanceValue());
		PredicateProfile p6 = new PredicateProfile("option1", "context1",
				500, 26);
		System.out.println(p6.importanceValue() - p5.importanceValue());
	}
	
	public void testStatisticProperties2() {
		PredicateProfile p1 = new PredicateProfile("option1", "context1",
				500, 220);
		PredicateProfile p2 = new PredicateProfile("option1", "context1",
				500, 240);
		System.out.println(p2.importanceValue() - p1.importanceValue());
		assertEquals(0.07985288f, p2.importanceValue() - p1.importanceValue());
		
		PredicateProfile p3 = new PredicateProfile("option1", "context1",
				500, 260);
		System.out.println(p3.importanceValue() - p2.importanceValue());
		assertEquals(0.0798403f, p3.importanceValue() - p2.importanceValue());
		
		//the following
		PredicateProfile p4 = new PredicateProfile("option1", "context1",
				50, 22);
		PredicateProfile p5 = new PredicateProfile("option1", "context1",
				50, 24);
		System.out.println(p5.importanceValue() - p4.importanceValue());
		assertEquals(0.078548014f, p5.importanceValue() - p4.importanceValue());
		PredicateProfile p6 = new PredicateProfile("option1", "context1",
				50, 26);
		System.out.println(p6.importanceValue() - p5.importanceValue());
		assertEquals(0.07842374f, p6.importanceValue() - p5.importanceValue());
		
		System.out.println("----------------");
		System.out.println(p2.logRunImportanceValue() - p1.logRunImportanceValue());
		System.out.println(p3.logRunImportanceValue() - p2.logRunImportanceValue());
		System.out.println(p5.logRunImportanceValue() - p4.logRunImportanceValue());
		System.out.println(p6.logRunImportanceValue() - p5.logRunImportanceValue());
		
	}
}
