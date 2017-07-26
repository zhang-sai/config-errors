package edu.washington.cs.conf.util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

public class TestUtils extends TestCase {

	public void testMapRank() {
		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		map.put("a", 1);
		map.put("b", 5);
		map.put("c", 3);
		map.put("d", 2);
		List<String> sortedKeys = Utils.sortByValueAndReturnKeys(map, false);
		System.out.println(sortedKeys);
		assertEquals("[b, c, d, a]", sortedKeys.toString());
	}
	
	public void testSortMapByKey() {

		Map<String, Integer> map = new LinkedHashMap<String, Integer>();
		map.put("c", 3);
		map.put("d", 2);
		map.put("b", 5);
		map.put("a", 1);
		map = Utils.sortByKey(map, false);
		System.out.println(map);
	}
	
	public void testAverage() {
		Collection<Integer> fs = new LinkedList<Integer>();
		fs.add(4);
		fs.add(3);
		fs.add(5);
		fs.add(6);
		fs.add(2);
		System.out.println(Utils.average(fs));
		assertEquals(4.0f, Utils.average(fs));
	}
	
	public void testLoadClassAndFields() {
		String path = "./subjects/randoop-jamie-no-trace.jar;./subjects/plume.jar";
		String className = "randoop.main.GenInputsAbstract";
		Class<?> c = Utils.loadclass(path, className);
		assertNotNull(c);
		System.out.println(c);
		
		Field f = Utils.lookupField(c, "maxsize");
		assertNotNull(f);
		System.out.println(f);
	}
	
	public void testStringSplit() {
		String s  ="hello-world";
		String[] splits = s.split("not");
		assertEquals(1, splits.length);
		System.out.println(splits[0]);
	}
	
//	public void testTreeSet() {
//	   Object obj = new Object();
//	   Integer int1 = 0;
//	   Integer int2 = 1;
//	   Object[] objs = new Object [] {obj, int1, int2};
//	   List list1 = Arrays.asList(objs);
//	   List list2 = list1.subList(int1, int2);
//	   TreeSet treeSet = new TreeSet();
//	   boolean add = treeSet.add(list2);
//	   Set set = Collections.synchronizedSet(treeSet);
//	   //This assertion fails
//	   assertTrue(set.equals(set));
//    }
	
	public void testCreateTmpDir() {
		File f = Files.createTempDirectoryNoExp();
		System.out.println(f.getAbsolutePath());
		f.delete();
		System.out.println("exist? : " + f.exists());
	}
	
	public void testMergeJarFiles() {
		JarUtils.mergeWithTracer(
				new File("./output.jar"),
				new File(JarUtils.TRACER_FILE),
				new File("./output-tracer.jar"));
	}
	
	public void testMatchingStackTrace() {
		List<String> lists = Arrays.asList(
				"first part",
				
				"java.lang.reflect.InvocationTargetException",
				"at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)",
				"at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)",
				"at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)",
				
				"Other parts"
				);
		
		System.out.println(Utils.matchStacktrace(lists));
	}
}
