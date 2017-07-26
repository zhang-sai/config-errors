package edu.washington.cs.conf.experiments;

import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;

//java -cp ./randoop-instrumented.jar;../bin;../subjects/plume.jar;
//../subjects/binarytree.jar randoop.main.Main gentests
//--testclass=binarytree.BinaryTreeTest --timelimit=100 --dont-output-tests=true

//
//java -cp ./randoop-instrumented.jar;../bin;../subjects/plume.jar;
//../subjects/nanoxml.jar randoop.main.Main gentests
//--classlist=../subjects/nano-classlist.txt --timelimit=100 --dont-output-tests=true
//--test-classes=gco.DumpXmlRandoop

public class RandoopExpUtils {
	
	public static String getRandoopSrcDir() {
		return "./subjects/randoop/randoop-src";
	}
	
	public static ConfEntityRepository getRandoopConfRepository(){
		ConfEntityRepository repo = new ConfEntityRepository(getRandoopConfList());
		return repo;
	}
	
	public static List<ConfEntity> getLargeSliceConfList() {
		List<ConfEntity> list = new LinkedList<ConfEntity>();
		ConfEntity entity6 = new ConfEntity("randoop.main.GenInputsAbstract", "init_routine", true);
		ConfEntity entity7 = new ConfEntity("randoop.main.GenInputsAbstract", "junit_classname", true);
//		list.add(entity6);
		list.add(entity7);
		return list;
	}
	
	public static List<ConfEntity> getFakeOptions() {
		List<ConfEntity> list = new LinkedList<ConfEntity>();
		ConfEntity entity6 = new ConfEntity("randoop.main.GenInputsAbstract", "fakeoption", true);
		list.add(entity6);
		return list;
	}
	
	public static List<ConfEntity> getSampleList() {
		List<ConfEntity> list = new LinkedList<ConfEntity>();
		ConfEntity entity6 = new ConfEntity("randoop.main.GenInputsAbstract", "init_routine", true);
		ConfEntity entity7 = new ConfEntity("randoop.main.GenInputsAbstract", "maxsize", true);
		list.add(entity6);
		list.add(entity7);
		return list;
	}
	
	public static List<ConfEntity> getReorderedList() {
		ConfEntity entity1 = new ConfEntity("randoop.main.GenInputsAbstract", "classlist", true);
		ConfEntity entity2 = new ConfEntity("randoop.main.GenInputsAbstract", "testclass", true);
		ConfEntity entity3 = new ConfEntity("randoop.main.GenInputsAbstract", "methodlist", true);
		ConfEntity entity4 = new ConfEntity("randoop.main.GenInputsAbstract", "omitmethods", true);
		ConfEntity entity5 = new ConfEntity("randoop.main.GenInputsAbstract", "public_only", true);
		ConfEntity entity6 = new ConfEntity("randoop.main.GenInputsAbstract", "init_routine", true);
		ConfEntity entity7 = new ConfEntity("randoop.main.GenInputsAbstract", "maxsize", true);
		ConfEntity entity8 = new ConfEntity("randoop.main.GenInputsAbstract", "silently_ignore_bad_class_names", true);
		ConfEntity entity9 = new ConfEntity("randoop.main.GenInputsAbstract", "literals_file", true);
		ConfEntity entity10 = new ConfEntity("randoop.main.GenInputsAbstract", "literals_level", true);
		ConfEntity entity11 = new ConfEntity("randoop.main.GenInputsAbstract", "randomseed", true);
		ConfEntity entity12 = new ConfEntity("randoop.main.GenInputsAbstract", "timelimit", true);
		ConfEntity entity13 = new ConfEntity("randoop.main.GenInputsAbstract", "inputlimit", true);
		ConfEntity entity14 = new ConfEntity("randoop.main.GenInputsAbstract", "outputlimit", true);
		ConfEntity entity15 = new ConfEntity("randoop.main.GenInputsAbstract", "forbid_null", true);
		ConfEntity entity16 = new ConfEntity("randoop.main.GenInputsAbstract", "string_maxlen", true);
		ConfEntity entity17 = new ConfEntity("randoop.main.GenInputsAbstract", "null_ratio", true);
		ConfEntity entity18 = new ConfEntity("randoop.main.GenInputsAbstract", "alias_ratio", true);
		ConfEntity entity19 = new ConfEntity("randoop.main.GenInputsAbstract", "small_tests", true);
		ConfEntity entity20 = new ConfEntity("randoop.main.GenInputsAbstract", "clear", true);
		ConfEntity entity21 = new ConfEntity("randoop.main.GenInputsAbstract", "observers", true);
		ConfEntity entity22 = new ConfEntity("randoop.main.GenInputsAbstract", "check_object_contracts", true);
		ConfEntity entity23 = new ConfEntity("randoop.main.GenInputsAbstract", "testsperfile", true);
		ConfEntity entity24 = new ConfEntity("randoop.main.GenInputsAbstract", "junit_classname", true);
		ConfEntity entity25 = new ConfEntity("randoop.main.GenInputsAbstract", "junit_package_name", true);
		ConfEntity entity26 = new ConfEntity("randoop.main.GenInputsAbstract", "junit_output_dir", true);
		ConfEntity entity27 = new ConfEntity("randoop.main.GenInputsAbstract", "dont_output_tests", true);
		ConfEntity entity28 = new ConfEntity("randoop.main.GenInputsAbstract", "output_nonexec", true);
		ConfEntity entity29 = new ConfEntity("randoop.main.GenInputsAbstract", "test_classes", true);
		ConfEntity entity30 = new ConfEntity("randoop.main.GenInputsAbstract", "system_props", true);
		ConfEntity entity31 = new ConfEntity("randoop.main.GenInputsAbstract", "agent", true);
		ConfEntity entity32 = new ConfEntity("randoop.main.GenInputsAbstract", "mem_megabytes", true);
		ConfEntity entity33 = new ConfEntity("randoop.main.GenInputsAbstract", "capture_output", true);
		ConfEntity entity34 = new ConfEntity("randoop.main.GenInputsAbstract", "componentfile_ser", true);
		ConfEntity entity35 = new ConfEntity("randoop.main.GenInputsAbstract", "componentfile_txt", true);
		ConfEntity entity36 = new ConfEntity("randoop.main.GenInputsAbstract", "output_components", true);
		ConfEntity entity37 = new ConfEntity("randoop.main.GenInputsAbstract", "output_tests_serialized", true);
		ConfEntity entity38 = new ConfEntity("randoop.main.GenInputsAbstract", "comm_port", true);
		ConfEntity entity39 = new ConfEntity("randoop.main.GenInputsAbstract", "progressinterval", true);
		ConfEntity entity40 = new ConfEntity("randoop.main.GenInputsAbstract", "visitor", true);
		ConfEntity entity41 = new ConfEntity("randoop.main.GenInputsAbstract", "debug_checks", true);
		ConfEntity entity42 = new ConfEntity("randoop.main.GenInputsAbstract", "log", true);
		ConfEntity entity43 = new ConfEntity("randoop.main.GenInputsAbstract", "dontexecute", true);
		ConfEntity entity44 = new ConfEntity("randoop.main.GenInputsAbstract", "long_format", true);
		ConfEntity entity45 = new ConfEntity("randoop.main.GenInputsAbstract", "output_covmap", true);
		ConfEntity entity46 = new ConfEntity("randoop.main.GenInputsAbstract", "output_cov_witnesses", true);
		ConfEntity entity47 = new ConfEntity("randoop.main.GenInputsAbstract", "always_use_ints_as_objects", true);
		ConfEntity entity48 = new ConfEntity("randoop.main.GenInputsAbstract", "coverage_instrumented_classes", true);
		ConfEntity entity49 = new ConfEntity("randoop.main.GenInputsAbstract", "output_branches", true);
		ConfEntity entity50 = new ConfEntity("randoop.main.GenInputsAbstract", "remove_subsequences", true);
		ConfEntity entity51 = new ConfEntity("randoop.main.GenInputsAbstract", "compare_checks", true);
		ConfEntity entity52 = new ConfEntity("randoop.main.GenInputsAbstract", "clean_checks", true);
		ConfEntity entity53 = new ConfEntity("randoop.main.GenInputsAbstract", "print_diff_obs", true);
		ConfEntity entity54 = new ConfEntity("randoop.main.GenInputsAbstract", "expfile", true);
		ConfEntity entity55 = new ConfEntity("randoop.main.GenInputsAbstract", "offline", true);
		ConfEntity entity56 = new ConfEntity("randoop.main.GenInputsAbstract", "repeat_heuristic", true);
		ConfEntity entity57 = new ConfEntity("randoop.main.GenInputsAbstract", "use_object_cache", true);
		
		
		List<ConfEntity> list = new LinkedList<ConfEntity>();
//		list.add(entity1);
//		list.add(entity2);
//		list.add(entity3);
//		list.add(entity4);
//		list.add(entity5);
//		list.add(entity6);
//		list.add(entity7);
//		list.add(entity8);
//		list.add(entity9);
//		list.add(entity10);
//		list.add(entity11);
//		list.add(entity12);
//		list.add(entity13);
//		list.add(entity14);
		list.add(entity15);
		list.add(entity16);
		list.add(entity17);
		list.add(entity18);
		list.add(entity19);
		list.add(entity20);
		list.add(entity21);
		list.add(entity22);
		list.add(entity23);
		list.add(entity24);
		list.add(entity25);
		list.add(entity26);
		list.add(entity27);
		list.add(entity28);
		list.add(entity29);
		list.add(entity30);
		list.add(entity31);
		list.add(entity32);
		list.add(entity33);
		list.add(entity34);
		list.add(entity35);
		list.add(entity36);
		list.add(entity37);
		list.add(entity38);
		list.add(entity39);
		list.add(entity40);
		list.add(entity41);
		list.add(entity42);
		list.add(entity43);
		list.add(entity44);
		list.add(entity45);
		list.add(entity46);
		list.add(entity47);
		list.add(entity48);
		list.add(entity49);
		list.add(entity50);
		list.add(entity51);
		list.add(entity52);
		list.add(entity53);
		list.add(entity54);
		list.add(entity55);
		list.add(entity56);
		list.add(entity57);
		
		return list;
	}

	public static List<ConfEntity> getRandoopConfList() {
		ConfEntity entity1 = new ConfEntity("randoop.main.GenInputsAbstract", "classlist", true);
		ConfEntity entity2 = new ConfEntity("randoop.main.GenInputsAbstract", "testclass", true);
		ConfEntity entity3 = new ConfEntity("randoop.main.GenInputsAbstract", "methodlist", true);
		ConfEntity entity4 = new ConfEntity("randoop.main.GenInputsAbstract", "omitmethods", true);
		ConfEntity entity5 = new ConfEntity("randoop.main.GenInputsAbstract", "public_only", true);
		ConfEntity entity6 = new ConfEntity("randoop.main.GenInputsAbstract", "init_routine", true);
		ConfEntity entity7 = new ConfEntity("randoop.main.GenInputsAbstract", "maxsize", true);
		ConfEntity entity8 = new ConfEntity("randoop.main.GenInputsAbstract", "silently_ignore_bad_class_names", true);
		ConfEntity entity9 = new ConfEntity("randoop.main.GenInputsAbstract", "literals_file", true);
		ConfEntity entity10 = new ConfEntity("randoop.main.GenInputsAbstract", "literals_level", true);
		ConfEntity entity11 = new ConfEntity("randoop.main.GenInputsAbstract", "randomseed", true);
		ConfEntity entity12 = new ConfEntity("randoop.main.GenInputsAbstract", "timelimit", true);
		ConfEntity entity13 = new ConfEntity("randoop.main.GenInputsAbstract", "inputlimit", true);
		ConfEntity entity14 = new ConfEntity("randoop.main.GenInputsAbstract", "outputlimit", true);
		ConfEntity entity15 = new ConfEntity("randoop.main.GenInputsAbstract", "forbid_null", true);
		ConfEntity entity16 = new ConfEntity("randoop.main.GenInputsAbstract", "string_maxlen", true);
		ConfEntity entity17 = new ConfEntity("randoop.main.GenInputsAbstract", "null_ratio", true);
		ConfEntity entity18 = new ConfEntity("randoop.main.GenInputsAbstract", "alias_ratio", true);
		ConfEntity entity19 = new ConfEntity("randoop.main.GenInputsAbstract", "small_tests", true);
		ConfEntity entity20 = new ConfEntity("randoop.main.GenInputsAbstract", "clear", true);
		ConfEntity entity21 = new ConfEntity("randoop.main.GenInputsAbstract", "observers", true);
		ConfEntity entity22 = new ConfEntity("randoop.main.GenInputsAbstract", "check_object_contracts", true);
		ConfEntity entity23 = new ConfEntity("randoop.main.GenInputsAbstract", "testsperfile", true);
		ConfEntity entity24 = new ConfEntity("randoop.main.GenInputsAbstract", "junit_classname", true);
		ConfEntity entity25 = new ConfEntity("randoop.main.GenInputsAbstract", "junit_package_name", true);
		ConfEntity entity26 = new ConfEntity("randoop.main.GenInputsAbstract", "junit_output_dir", true);
		ConfEntity entity27 = new ConfEntity("randoop.main.GenInputsAbstract", "dont_output_tests", true);
		ConfEntity entity28 = new ConfEntity("randoop.main.GenInputsAbstract", "output_nonexec", true);
		ConfEntity entity29 = new ConfEntity("randoop.main.GenInputsAbstract", "test_classes", true);
		ConfEntity entity30 = new ConfEntity("randoop.main.GenInputsAbstract", "system_props", true);
		ConfEntity entity31 = new ConfEntity("randoop.main.GenInputsAbstract", "agent", true);
		ConfEntity entity32 = new ConfEntity("randoop.main.GenInputsAbstract", "mem_megabytes", true);
		ConfEntity entity33 = new ConfEntity("randoop.main.GenInputsAbstract", "capture_output", true);
		ConfEntity entity34 = new ConfEntity("randoop.main.GenInputsAbstract", "componentfile_ser", true);
		ConfEntity entity35 = new ConfEntity("randoop.main.GenInputsAbstract", "componentfile_txt", true);
		ConfEntity entity36 = new ConfEntity("randoop.main.GenInputsAbstract", "output_components", true);
		ConfEntity entity37 = new ConfEntity("randoop.main.GenInputsAbstract", "output_tests_serialized", true);
		ConfEntity entity38 = new ConfEntity("randoop.main.GenInputsAbstract", "comm_port", true);
		ConfEntity entity39 = new ConfEntity("randoop.main.GenInputsAbstract", "progressinterval", true);
		ConfEntity entity40 = new ConfEntity("randoop.main.GenInputsAbstract", "visitor", true);
		ConfEntity entity41 = new ConfEntity("randoop.main.GenInputsAbstract", "debug_checks", true);
		ConfEntity entity42 = new ConfEntity("randoop.main.GenInputsAbstract", "log", true);
		ConfEntity entity43 = new ConfEntity("randoop.main.GenInputsAbstract", "dontexecute", true);
		ConfEntity entity44 = new ConfEntity("randoop.main.GenInputsAbstract", "long_format", true);
		ConfEntity entity45 = new ConfEntity("randoop.main.GenInputsAbstract", "output_covmap", true);
		ConfEntity entity46 = new ConfEntity("randoop.main.GenInputsAbstract", "output_cov_witnesses", true);
		ConfEntity entity47 = new ConfEntity("randoop.main.GenInputsAbstract", "always_use_ints_as_objects", true);
		ConfEntity entity48 = new ConfEntity("randoop.main.GenInputsAbstract", "coverage_instrumented_classes", true);
		ConfEntity entity49 = new ConfEntity("randoop.main.GenInputsAbstract", "output_branches", true);
		ConfEntity entity50 = new ConfEntity("randoop.main.GenInputsAbstract", "remove_subsequences", true);
		ConfEntity entity51 = new ConfEntity("randoop.main.GenInputsAbstract", "compare_checks", true);
		ConfEntity entity52 = new ConfEntity("randoop.main.GenInputsAbstract", "clean_checks", true);
		ConfEntity entity53 = new ConfEntity("randoop.main.GenInputsAbstract", "print_diff_obs", true);
		ConfEntity entity54 = new ConfEntity("randoop.main.GenInputsAbstract", "expfile", true);
		ConfEntity entity55 = new ConfEntity("randoop.main.GenInputsAbstract", "offline", true);
		ConfEntity entity56 = new ConfEntity("randoop.main.GenInputsAbstract", "repeat_heuristic", true);
		ConfEntity entity57 = new ConfEntity("randoop.main.GenInputsAbstract", "use_object_cache", true);
		
		
		List<ConfEntity> list = new LinkedList<ConfEntity>();
		list.add(entity1);
		list.add(entity2);
		list.add(entity3);
		list.add(entity4);
		list.add(entity5);
		list.add(entity6);
		list.add(entity7);
		list.add(entity8);
		list.add(entity9);
		list.add(entity10);
		list.add(entity11);
		list.add(entity12);
		list.add(entity13);
		list.add(entity14);
		list.add(entity15);
		list.add(entity16);
		list.add(entity17);
		list.add(entity18);
		list.add(entity19);
		list.add(entity20);
		list.add(entity21);
		list.add(entity22);
		list.add(entity23);
		list.add(entity24);
		list.add(entity25);
		list.add(entity26);
		list.add(entity27);
		list.add(entity28);
		list.add(entity29);
		list.add(entity30);
		list.add(entity31);
		list.add(entity32);
		list.add(entity33);
		list.add(entity34);
		list.add(entity35);
		list.add(entity36);
		list.add(entity37);
		list.add(entity38);
		list.add(entity39);
		list.add(entity40);
		list.add(entity41);
		list.add(entity42);
		list.add(entity43);
		list.add(entity44);
		list.add(entity45);
		list.add(entity46);
		list.add(entity47);
		list.add(entity48);
		list.add(entity49);
		list.add(entity50);
		list.add(entity51);
		list.add(entity52);
		list.add(entity53);
		list.add(entity54);
		list.add(entity55);
		list.add(entity56);
		list.add(entity57);
		
		return list;
	}
	
}