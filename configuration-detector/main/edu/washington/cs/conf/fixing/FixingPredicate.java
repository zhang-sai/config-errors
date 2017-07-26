package edu.washington.cs.conf.fixing;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.util.Utils;

/**
 * The place to fix
 * */
public class FixingPredicate {
	
	private static int ID = 0;
	private static int getID() {
		ID++;
		return ID;
	}

	private final int id;
	private final String methodSig;
	private final int instructionIndex;
	private final List<Float> trueRatios;
	
	public FixingPredicate(String methodSig, int index, float trueRatio) {
		this(methodSig, index, Collections.singleton(trueRatio));
	}
	
	public FixingPredicate(String methodSig, int index, Collection<Float> trueRatios) {
		Utils.checkNotNull(methodSig);
		Utils.checkTrue(index > -1);
		Utils.checkNotNull(trueRatios);
		Utils.checkTrue(!trueRatios.isEmpty());
		for(Float f : trueRatios) {
			Utils.checkTrue(f >= 0.0f);
		}
		this.id = getID();
		this.methodSig = methodSig;
		this.instructionIndex = index;
		this.trueRatios = new LinkedList<Float>();
		this.trueRatios.addAll(trueRatios);
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getMethodSig() {
		return this.methodSig;
	}
	
	public int getInstructionIndex() {
		return this.instructionIndex;
	}
	
	public List<Float> getTrueRatios() {
		return this.trueRatios;
	}
	
	public Float getFirstTrueRatio() {
		return this.trueRatios.get(0);
	}
	
	public Float getTrueRatio(int index) {
		Utils.checkTrue(index >= 0 && index < this.trueRatios.size());
		return this.trueRatios.get(index);
	}
}
