package edu.washington.cs.conf.experiments.randoop;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.diagnosis.MainAnalyzer;
import edu.washington.cs.conf.diagnosis.PredicateProfileTuple;
import edu.washington.cs.conf.experiments.RandoopExpUtils;
import junit.framework.TestCase;

public class TestRandoopFullSlice extends TestCase {
	
	String fullSliceBadRun = "./experiments/randoop-database/nanoxml-full-slice.txt";
	
	String treeSet = "./experiments/randoop-database/treeset-full-slice-60s.txt";
	String treeSetCollFile = "./experiments/randoop-database/treeset-collections-myclass-full-slice.txt";
	String simpleDs = "./experiments/randoop-database/simple-ds-full-slice.txt";
	String javax2x = "./experiments/randoop-database/javax2xml-full-slice.txt";
	String javautils = "./experiments/randoop-database/javautils-full-slice.txt";
	String graph = "./experiments/randoop-database/graph-full-slice.txt";
	String binomialHeap = "./experiments/randoop-database/binomialheap-full-slice.txt";
	String binarySearchTree = "./experiments/randoop-database/binarysearchtree-full-slice.txt";
	String arrayList = "./experiments/randoop-database/arraylist-full-slice.txt";
	String arrayListColl = "./experiments/randoop-database/arraylist-collections-full-slice.txt";
	String apachePrim = "./experiments/randoop-database/apache-primitives-full-slice.txt";
	
	String[] fullSliceDb = new String[]{
			treeSet, treeSetCollFile, simpleDs, javax2x, javautils,
			graph, binomialHeap, binarySearchTree, arrayList, arrayListColl,
			apachePrim
	};

	public void testDiangoseSimilar() {
		PredicateProfileTuple.USE_CACHE = true;
		
		ConfEntityRepository repo = RandoopExpUtils.getRandoopConfRepository();
		MainAnalyzer.diagnoseConfigErrors(fullSliceBadRun,
				fullSliceDb,
				repo,
				null, null, null);
	}
	
	@Override
	public void tearDown() {
		PredicateProfileTuple.USE_CACHE = false;
	}
	
}
