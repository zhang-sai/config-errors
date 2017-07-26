package edu.washington.cs.conf.fixing;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import edu.washington.cs.conf.util.Utils;

/**
 * Records how a predicate should be evaluated.
 * */
public class FixingPlan {

	private final Collection<FixingPredicate> fixes;
	
	public FixingPlan(FixingPredicate fix) {
		this(Collections.singletonList(fix));
	}
	
	public FixingPlan(Collection<FixingPredicate> fixes) {
		Utils.checkNotNull(fixes);
		this.fixes = new LinkedList<FixingPredicate>();
		this.fixes.addAll(fixes);
	}
	
	public Collection<FixingPredicate> getFixes() {
		return this.fixes;
	}
	
	public FixingPredicate getFixingPredicate(int id) {
		for(FixingPredicate f : fixes) {
			if(f.getId() == id) {
				return f;
			}
		}
		return null;
	}
}