package edu.washington.cs.conf.analysis;

import junit.framework.TestCase;

public class TestConfEntity extends TestCase {

	public void testParseConfEntity() {
		String line = "weka.classifiers.trees.J48;m_unpruned;;false";
		ConfEntity e = ConfUtils.parseConfEntity(line);
		assertNotNull(e);
		
		line = "weka.classifiers.trees.J48;m_unpruned;<init>;false";;
		ConfEntity e1 = ConfUtils.parseConfEntity(line);
		assertNotNull(e1);
		
		assertEquals(e, e1);
	}
}
