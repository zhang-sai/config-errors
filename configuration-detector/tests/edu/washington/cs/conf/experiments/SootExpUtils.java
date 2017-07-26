package edu.washington.cs.conf.experiments;

import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;

public class SootExpUtils {
	
	public static String getSootSourceDir() {
		return "D:\\research\\configurations\\workspace\\soot-2.5\\src";
	}
	
	public static ConfEntityRepository getConfEntityRepository() {
		List<ConfEntity> sootConfList = getSootConfList();
		ConfEntityRepository repo = new ConfEntityRepository(sootConfList);
		return repo;
	}
	
	public static List<ConfEntity> getSootConfList() {
		ConfEntity entity1 = new ConfEntity("soot.options.Options", "help", false);
		ConfEntity entity2 = new ConfEntity("soot.options.Options", "phase_list", false);
		ConfEntity entity3 = new ConfEntity("soot.options.Options", "phase_help", false);
		ConfEntity entity4 = new ConfEntity("soot.options.Options", "version", false);
		ConfEntity entity5 = new ConfEntity("soot.options.Options", "verbose", false);
		ConfEntity entity6 = new ConfEntity("soot.options.Options", "interactive_mode", false);
		ConfEntity entity7 = new ConfEntity("soot.options.Options", "unfriendly_mode", false);
		ConfEntity entity8 = new ConfEntity("soot.options.Options", "app", false);
		ConfEntity entity9 = new ConfEntity("soot.options.Options", "whole_program", false);
		ConfEntity entity10 = new ConfEntity("soot.options.Options", "whole_shimple", false);
		ConfEntity entity11 = new ConfEntity("soot.options.Options", "validate", false);
		ConfEntity entity12 = new ConfEntity("soot.options.Options", "debug", false);
		ConfEntity entity13 = new ConfEntity("soot.options.Options", "debug_resolver", false);
		ConfEntity entity14 = new ConfEntity("soot.options.Options", "soot_classpath", false);
		ConfEntity entity15 = new ConfEntity("soot.options.Options", "prepend_classpath", false);
		ConfEntity entity16 = new ConfEntity("soot.options.Options", "process_dir", false);
		ConfEntity entity17 = new ConfEntity("soot.options.Options", "ast_metrics", false);
		ConfEntity entity18 = new ConfEntity("soot.options.Options", "src_prec", false);
		ConfEntity entity19 = new ConfEntity("soot.options.Options", "full_resolver", false);
		ConfEntity entity20 = new ConfEntity("soot.options.Options", "allow_phantom_refs", false);
		ConfEntity entity21 = new ConfEntity("soot.options.Options", "no_bodies_for_excluded", false);
		ConfEntity entity22 = new ConfEntity("soot.options.Options", "j2me", false);
		ConfEntity entity23 = new ConfEntity("soot.options.Options", "main_class", false);
		ConfEntity entity24 = new ConfEntity("soot.options.Options", "polyglot", false);
		ConfEntity entity25 = new ConfEntity("soot.options.Options", "output_dir", false);
		ConfEntity entity26 = new ConfEntity("soot.options.Options", "output_format", false);
		ConfEntity entity27 = new ConfEntity("soot.options.Options", "output_jar", false);
		ConfEntity entity28 = new ConfEntity("soot.options.Options", "xml_attributes", false);
		ConfEntity entity29 = new ConfEntity("soot.options.Options", "print_tags_in_output", false);
		ConfEntity entity30 = new ConfEntity("soot.options.Options", "no_output_source_file_attribute", false);
		ConfEntity entity31 = new ConfEntity("soot.options.Options", "no_output_inner_classes_attribute", false);
		ConfEntity entity32 = new ConfEntity("soot.options.Options", "dump_body", false);
		ConfEntity entity33 = new ConfEntity("soot.options.Options", "dump_cfg", false);
		ConfEntity entity34 = new ConfEntity("soot.options.Options", "show_exception_dests", false);
		ConfEntity entity35 = new ConfEntity("soot.options.Options", "gzip", false);
		ConfEntity entity36 = new ConfEntity("soot.options.Options", "via_grimp", false);
		ConfEntity entity37 = new ConfEntity("soot.options.Options", "via_shimple", false);
		ConfEntity entity38 = new ConfEntity("soot.options.Options", "throw_analysis", false);
		ConfEntity entity39 = new ConfEntity("soot.options.Options", "omit_excepting_unit_edges", false);
		ConfEntity entity40 = new ConfEntity("soot.options.Options", "include", false);
		ConfEntity entity41 = new ConfEntity("soot.options.Options", "exclude", false);
		ConfEntity entity42 = new ConfEntity("soot.options.Options", "include_all", false);
		ConfEntity entity43 = new ConfEntity("soot.options.Options", "dynamic_class", false);
		ConfEntity entity44 = new ConfEntity("soot.options.Options", "dynamic_dir", false);
		ConfEntity entity45 = new ConfEntity("soot.options.Options", "dynamic_package", false);
		ConfEntity entity46 = new ConfEntity("soot.options.Options", "keep_line_number",  false); //change it to set, some set method is not reachable
		ConfEntity entity47 = new ConfEntity("soot.options.Options", "keep_offset", false);
		ConfEntity entity48 = new ConfEntity("soot.options.Options", "time", false);
		ConfEntity entity49 = new ConfEntity("soot.options.Options", "subtract_gc", false);
		
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
		
		return list;
	}
}
