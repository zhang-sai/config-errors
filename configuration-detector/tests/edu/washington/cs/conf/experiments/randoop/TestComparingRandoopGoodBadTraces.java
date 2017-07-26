package edu.washington.cs.conf.experiments.randoop;

import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.experiments.CommonUtils;
import junit.framework.TestCase;

public class TestComparingRandoopGoodBadTraces extends TestCase {
	
	public static String badRun = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
	
	public static String badRunICSE14 = "./experiments/randoop-database/trace_dump_randoop_treemap_icse14.txt";
	
	public static String gentestsHelp = "./experiments/randoop-database/gentests_help-pruned.txt";
	public static String graph = "./experiments/randoop-database/good_graph_trace-60s-pruned.txt";
	public static String arraylist = "./experiments/randoop-database/good-arraylist-60s-pruned.txt";
	public static String binarySearchTree = "./experiments/randoop-database/good-binarysearchtree-60s-pruned.txt";
	public static String binomialHeap = "./experiments/randoop-database/good-binomialheap-60s-pruned.txt";
	public static String javaUtils = "./experiments/randoop-database/good-java-utils-60s-pruned.txt";
	public static String treeset = "./experiments/randoop-database/good-treeset-collections-60s-pruned.txt";
	public static String treesetColl = "./experiments/randoop-database/good-treeset-collections-60s-pruned.txt";
	public static String showHelpUnpub = "./experiments/randoop-database/good-show-unpub-help.txt";
	public static String simpleds = "./experiments/randoop-database/simple-ds-60s-pruned.txt";
	public static String primitives = "./experiments/randoop-database/good-apache-primitive-60s-pruned.txt";
	public static String javax2xml = "./experiments/randoop-database/good-javax2xml-60s-pruned.txt";
	
	public static String[] db = new String[]{gentestsHelp, graph, arraylist, binarySearchTree, binomialHeap, javaUtils,
		treeset, treesetColl, showHelpUnpub, simpleds, primitives, javax2xml};
	
	
	public void test1() {
		//compare the same
		String goodRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		String badRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.0f);
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.0f);
	}
	
	public void test2() {
		//2 good traces
		String goodRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		String badRunTrace = "./experiments/randoop-database/good-arraylist-60s.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.01677453f);
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.120200634f);
	}
	
	public void test3() {
		String goodRunTrace = "./experiments/randoop-database/good-treeset-60s.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.11405811f);
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.27575958f);
	}
	
	public void test4() {
		
		String goodRunTrace = "./experiments/randoop-database/good-arraylist-60s.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.2617045f);
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.117307134f);
	}
	
	//this sounds  incorrect
	public void test5() {
		String goodRunTrace = "./experiments/randoop-database/show_help.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.9330544f);
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.85614043f);
	}
	
	public void test6() {
		String goodRunTrace = "./experiments/randoop-database/show_help.txt";
		String badRunTrace = "./experiments/randoop-database/good-arraylist-60s.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.9373041f);
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.85133606f);
	}
	
	public void test7() {
		String goodRunTrace = "./experiments/randoop-database/good-binarysearchtree-60s-pruned.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.11985898f);
		
		goodRunTrace = "./experiments/randoop-database/good-binomialheap-60s-pruned.txt";
		badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.12303108f);
		
		goodRunTrace = "./experiments/randoop-database/gentests_help-pruned.txt";
		badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 1.0f);
		
		goodRunTrace = "./experiments/randoop-database/good-treeset-collections-60s-pruned.txt";
		badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.14591342f);
		
		goodRunTrace = "./experiments/randoop-database/good-treeset-collections-60s-myclasses-pruned.txt";
		badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.17543972f);
		
		goodRunTrace = "./experiments/randoop-database/show_help-pruned.txt";
		badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 1.0f);
	}
	
	public void test8() {
		String goodRunTrace = "./experiments/randoop-database/good_graph_trace-60s-pruned.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.11180502f);
	}
	
	public void test9() {
		String goodRunTrace = "./experiments/randoop-database/good-java-utils-60s-pruned.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.2617579f);
	}
	
	public void test10() {
		String goodRunTrace = "./experiments/randoop-database/good-show-unpub-help.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 1.0f);
	}
	
	public void test11() {
		String goodRunTrace = "./experiments/randoop-database/simple-ds-60s-pruned.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.09734005f);
	}

	public void test12() {
		String goodRunTrace = "./experiments/randoop-database/good-javax2xml-60s-pruned.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.04339099f);
	}
	
	public void test13() {
		String goodRunTrace = "./experiments/randoop-database/good-apache-primitive-60s-pruned.txt";
		String badRunTrace = "./experiments/randoop-database/bad-nano-xml-100s-pruned.txt";
		CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.17921078f);
	}
}
