package edu.washington.cs.conf.experiments.soot;

import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;
import edu.washington.cs.conf.experiments.CommonUtils;
import junit.framework.TestCase;

public class TestComparingSootTraces extends TestCase {

	public static String baf_string = "./experiments/soot-database/baf_string.txt";
	public static String jimple_use_cg_spark = "./experiments/soot-database/jimple_use_cg_spark.txt";
	public static String jimple_use_original_name = "./experiments/soot-database/jimple_use_original_name.txt";
	public static String optimize_helloworld = "./experiments/soot-database/optimize_helloworld.txt";
	public static String optimize_jimple_helloworld = "./experiments/soot-database/optimize_jimple_helloworld.txt";
	public static String parse_jimple = "./experiments/soot-database/parse_jimple.txt"; //?
	public static String produce_shimpe = "./experiments/soot-database/produce_shimpe.txt";
	public static String soot_helloworld_with_keepline = "./experiments/soot-database/soot_helloworld_with_keepline.txt"; //?
	public static String soot_number_with_keepline = "./experiments/soot-database/soot_example_line_number.txt";
	public static String soot_main_allowphan = "./experiments/soot-database/soot_main_allowphantom.txt";
	public static String soot_main_help = "./experiments/soot-database/soot_main_help.txt";
	public static String soot_main_no_args = "./experiments/soot-database/soot_main_no_args.txt";
//	public static String soot_main_pp_helloworld = "./experiments/soot-database/soot_main_pp_helloworld.txt"; //?
//	public static String soot_main_process_dir = "./experiments/soot-database/soot_main_process_dir.txt";//?
//	public static String soot_main_redict_dir = "./experiments/soot-database/soot_main_redict_dir.txt"; //?
	public static String static_method_inline_optimization = "./experiments/soot-database/static_method_inline_optimization.txt";
	public static String whole_program_optimization = "./experiments/soot-database/whole_program_optimization.txt";
    public static String ann_null_pointer = "./experiments/soot-database/ann_null_pointer.txt";
    public static String array_bound_ann = "./experiments/soot-database/array_bound_ann.txt";
    public static String side_effect_ann = "./experiments/soot-database/side-effect-ann.txt";
    public static String side_effect_spark_ann = "./experiments/soot-database/side-effect-spark-enabled.txt";
	
	public static String soot_helloworld_no_keepline = "./experiments/soot-database/soot_helloworld_no_keepline.txt";
	
	public static String[] db = new String[]{
		baf_string, 
		jimple_use_cg_spark, 
		jimple_use_original_name, 
		optimize_helloworld,
		optimize_jimple_helloworld, 
		produce_shimpe,
		soot_number_with_keepline,
		soot_main_allowphan,
		soot_main_help,
		soot_main_no_args, 
		static_method_inline_optimization,
		whole_program_optimization,
		ann_null_pointer,
		array_bound_ann,
		side_effect_ann,
		side_effect_spark_ann
	};
	
	public void test1() {
	    String goodRunTrace = "./experiments/soot-database/soot_helloworld_with_keepline.txt";
	    String badRunTrace = "./experiments/soot-database/soot_helloworld_no_keepline.txt";
//	    CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.SUBTRACTION, 0.0f);
	    CommonUtils.compareTraceDistance(goodRunTrace, badRunTrace, DistanceType.INTERPRODUCT, 0.10196239f);
	}
	
	public void testAllDistance() {
		String badRun = soot_helloworld_no_keepline;
		for(String goodRun : db) {
			CommonUtils.compareTraceDistance(goodRun, badRun, DistanceType.INTERPRODUCT, null, false);
		}
	}
	
}