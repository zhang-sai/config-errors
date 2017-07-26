package edu.washington.cs.conf.diagnosis;

import java.util.Collection;

import edu.washington.cs.conf.util.Files;

import junit.framework.TestCase;

public class TestTraceFileParsing extends TestCase {
		
	public void testReadSepConfs() {
		String trace_file = "./experiments/jchord-database/simpletest-has-race-full-slice.txt";
		Collection<PredicateProfile> colls = TraceAnalyzer.createProfiles(trace_file);
		System.out.println(colls.size());
		System.out.println("File length: " + Files.readWholeNoExp(trace_file).size());
	}
	
	public void testParseRandoopTraceFile() {
		String goodRandoopTrace = "./output/trace_dump_binarytree.txt";
		String badRandoopTrace = "./output/trace_dump_nanoxml.txt";
		TraceAnalyzer analyzer = new TraceAnalyzer(goodRandoopTrace, badRandoopTrace);
		Collection<PredicateProfile> goodProfiles = analyzer.getGoodProfiles();
		Collection<PredicateProfile> badProfiles = analyzer.getBadProfiles();
		
		ProfileComparator comparator = new ProfileComparator(goodProfiles, badProfiles);
//		comparator.findDeviatedProfiles();
		comparator.findProfilesByTfIdf();
	}
	
	public void testParseRandoopTraceFile_context2() {
		String goodRandoopTrace = "./output/trace_dump_binarytree_2.txt";
		String badRandoopTrace = "./output/trace_dump_nanoxml_2.txt";
		TraceAnalyzer analyzer = new TraceAnalyzer(goodRandoopTrace, badRandoopTrace);
		Collection<PredicateProfile> goodProfiles = analyzer.getGoodProfiles();
		Collection<PredicateProfile> badProfiles = analyzer.getBadProfiles();
		
		ProfileComparator comparator = new ProfileComparator(goodProfiles, badProfiles);
//		comparator.findDeviatedProfiles();
		comparator.findProfilesByTfIdf();
	}
	
	public void testParseWekaTraceFile() {
		String goodTrace = "./output/trace_dump_iris.txt";
		String badTrace = "./output/trace_dump_labor.txt";
		TraceAnalyzer analyzer = new TraceAnalyzer(goodTrace, badTrace);
		Collection<PredicateProfile> goodProfiles = analyzer.getGoodProfiles();
		Collection<PredicateProfile> badProfiles = analyzer.getBadProfiles();
		
		ProfileComparator comparator = new ProfileComparator(goodProfiles, badProfiles);
//		comparator.findDeviatedProfiles();
		comparator.findProfilesByTfIdf();
		
	}
	
	public void testParseWekaTraceFile_context2() {
		String goodTrace = "./output/trace_dump_iris_2.txt";
		String badTrace = "./output/trace_dump_labor_2.txt";
		TraceAnalyzer analyzer = new TraceAnalyzer(goodTrace, badTrace);
		Collection<PredicateProfile> goodProfiles = analyzer.getGoodProfiles();
		Collection<PredicateProfile> badProfiles = analyzer.getBadProfiles();
		
		ProfileComparator comparator = new ProfileComparator(goodProfiles, badProfiles);
//		comparator.findDeviatedProfiles();
		comparator.findProfilesByTfIdf();
	}
	
	public void testParseSynotpic2pc3nodes() {
		String goodTrace = "./output/trace_dump_2pc_3nodes_good.txt";
//		String badTrace = "./output/trace_dump_2pc_3nodes_bad.txt";
		String badTrace = "./output/trace_dump_2pc_3nodes_5x_bad.txt";
		TraceAnalyzer analyzer = new TraceAnalyzer(goodTrace, badTrace);
		Collection<PredicateProfile> goodProfiles = analyzer.getGoodProfiles();
		Collection<PredicateProfile> badProfiles = analyzer.getBadProfiles();
		
		ProfileComparator comparator = new ProfileComparator(goodProfiles, badProfiles);
//		comparator.findDeviatedProfiles();
		comparator.findProfilesByTfIdf();
	}
	
	public void testParseJChordRace() {
//		String goodTrace = "./output/chord_trace_dump_has_race.txt";
//		String badTrace = "./output/chord_trace_dump_no_race.txt";
		
		String goodTrace = "./output/chord_trace_dump_has_race_src_pre.txt";
		String badTrace = "./output/chord_trace_dump_no_race_src_pre.txt";
		
		TraceAnalyzer analyzer = new TraceAnalyzer(goodTrace, badTrace);
		Collection<PredicateProfile> goodProfiles = analyzer.getGoodProfiles();
		Collection<PredicateProfile> badProfiles = analyzer.getBadProfiles();
		
		ProfileComparator comparator = new ProfileComparator(goodProfiles, badProfiles);
//		comparator.findDeviatedProfiles();
		comparator.findProfilesByTfIdf();
	}
}