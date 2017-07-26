package edu.washington.cs.conf.fixing;

import java.util.Random;

import edu.washington.cs.conf.util.Utils;

public class PredicateAlter {
	
	public static PredicateAlter alter = new PredicateAlter();
	
	private static Random rand = new Random();
	
	public int returnIntThres() {
		//remove the hardcode here
		return 10;
//		System.out.println(20);
	}
	
	public int returnRandom() {
		//remove the hardcode here
		return rand.nextInt(100);
//		System.out.println(rand.nextInt(100));
	}
	
	private static boolean returnTrueWithProb(float r) {
		Utils.checkTrue(r >= 0.0f && r < 1.0f);
		float f = rand.nextFloat();
		return f <= r;
	}
	
	static FixingPlan plan = null;
	
	static {
		//load the plan from a file or some external sources
	}

	public static boolean evaluate(int id) {
		FixingPredicate predicate = plan.getFixingPredicate(id);
		float ratio = predicate.getFirstTrueRatio();
		return returnTrueWithProb(ratio);
	}
	
}