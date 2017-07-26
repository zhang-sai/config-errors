package edu.washington.cs.conf.analysis;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class TestConfEntityRepository extends TestCase {
	
	public void testConfRepository() {
		ConfEntityRepository rep = getSampleConfEntityRepository();
		assertEquals(6, rep.size());
		ConfEntity e = null;
		e = rep.lookupConfEntity("class-name1.conf-option1");
		assertNotNull(e);
		e = rep.lookupConfEntity("class-name2.conf-option2");
		assertNotNull(e);
		e = rep.lookupConfEntity("class-name3.conf-option3");
		assertNotNull(e);
		e = rep.lookupConfEntity("class-name4.conf-option4");
		assertNotNull(e);
		e = rep.lookupConfEntity("class-name5.conf-option5");
		assertNotNull(e);
		e = rep.lookupConfEntity("class-name6.conf-option6");
		assertNotNull(e);
		e = rep.lookupConfEntity("not-existed");
		assertNull(e);
	}
	
	public static ConfEntityRepository getSampleConfEntityRepository() {
		ConfEntityRepository repo = new ConfEntityRepository(getSampleList());
		return repo;
	}

	public static List<ConfEntity> getSampleList() {
		ConfEntity e1 = new ConfEntity("class-name1", "conf-option1", true);
		ConfEntity e2 = new ConfEntity("class-name2", "conf-option2", false);
		ConfEntity e3 = new ConfEntity("class-name3", "conf-option3", true);
		ConfEntity e4 = new ConfEntity("class-name4", "conf-option4", false);
		ConfEntity e5 = new ConfEntity("class-name5", "conf-option5", true);
		ConfEntity e6 = new ConfEntity("class-name6", "conf-option6", false);
		List<ConfEntity> list = new LinkedList<ConfEntity>();
		list.add(e1);
		list.add(e2);
		list.add(e3);
		list.add(e4);
		list.add(e5);
		list.add(e6);
		return list;
		
	}
}
