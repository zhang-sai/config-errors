package edu.washington.cs.conf.analysis.evol;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.analysis.evol.experimental.PredicateExecInfo;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class PredicateBehaviorAcrossVersions {

	/**
	 * If the predicate is executed on both versions, use
	 * the methodSig and index on the new version here.
	 * */
	public final String oldMethodSig;
	public final int oldIndex;
	
	public final String newMethodSig;
	public final int newIndex;
	
	private SSAInstruction oldSSA = null;
	private CGNode oldNode = null;
	private SSAInstruction newSSA = null;
	private CGNode newNode = null;
	
	private int execFreqInOld = -1;
	private int evalResultInOld = -1;
	private int execFreqInNew = -1;
	private int evalResultInNew = -1;
	
	//XXX what do these two fields used for?
	private int monitoredExec = -1;
	private int monitoredEval = -1;
	
	//the old version
	public PredicateBehaviorAcrossVersions(String oldMethodSig, int oldIndex, String newMethodSig, int newIndex) {
		Utils.checkTrue(oldMethodSig != null || newMethodSig != null);
		if(oldMethodSig != null) {
			Utils.checkTrue(oldIndex >= 0);
		} else {
			Utils.checkTrue(oldIndex == -1);
		}
		if(newMethodSig != null) {
			Utils.checkTrue(newIndex >= 0);
		} else {
			Utils.checkTrue(newIndex == -1);
		}
		this.oldMethodSig = oldMethodSig;
		this.oldIndex = oldIndex;
		this.newMethodSig = newMethodSig;
		this.newIndex = newIndex;
	}
	
	public SSAInstruction getOldInstruction(CodeAnalyzer oldCoder) {
		Utils.checkTrue(this.oldMethodSig != null);
		if(this.oldSSA != null) {
			return this.oldSSA;
		}
		this.oldNode = WALAUtils.lookupMatchedCGNode(oldCoder.getCallGraph(), this.oldMethodSig);
		this.oldSSA = WALAUtils.getInstruction(this.oldNode, this.oldIndex);
		return this.oldSSA;
	}
	
	public SSAInstruction getNewInstruction(CodeAnalyzer newCoder) {
		Utils.checkTrue(this.newMethodSig != null);
		if(this.newSSA != null) {
			return this.newSSA;
		}
		this.newNode = WALAUtils.lookupMatchedCGNode(newCoder.getCallGraph(), this.newMethodSig);
		this.newSSA = WALAUtils.getInstruction(this.newNode, this.newIndex);
		return this.newSSA;
	}
	
	public CGNode getOldNode(CodeAnalyzer oldCoder) {
		Utils.checkTrue(this.oldMethodSig != null);
		if(this.oldNode != null) {
			return this.oldNode;
		}
		this.oldNode = WALAUtils.lookupMatchedCGNode(oldCoder.getCallGraph(), this.oldMethodSig);
		return this.oldNode;
	}
	
	public CGNode getNewNode(CodeAnalyzer newCoder) {
		Utils.checkTrue(this.newMethodSig != null);
		if(this.newNode != null) {
			return this.newNode;
		}
		this.newNode = WALAUtils.lookupMatchedCGNode(newCoder.getCallGraph(), this.newMethodSig);
		return this.newNode;
	}
	
	public void setOldExecutionInfo(int oldFreq, int oldResult) {
		if(oldFreq > 0) {
			Utils.checkTrue(this.oldMethodSig != null);
		}
		Utils.checkTrue(this.oldMethodSig != null);
		Utils.checkTrue(oldFreq >= 0);
		Utils.checkTrue(oldResult >= 0);
		Utils.checkTrue(oldFreq >= oldResult);
		this.execFreqInOld = oldFreq;
		this.evalResultInOld = oldResult;
	}
	
	public void setNewExecutionInfo(int newFreq, int newResult) {
		if(newFreq >0 ) {
		    Utils.checkTrue(this.newMethodSig != null);
		}
		Utils.checkTrue(newFreq >= 0);
		Utils.checkTrue(newResult >= 0);
		Utils.checkTrue(newFreq >= newResult);
		this.execFreqInNew = newFreq;
		this.evalResultInNew = newResult;
	}
	
	//XXX forget what is this method going to do?
	public void setMonitoredInfo(int freq, int result) {
		Utils.checkTrue(freq >= 0);
		Utils.checkTrue(result >= 0);
		this.monitoredExec = freq;
		this.monitoredEval = result;
	}
	
	public int getMonitorFreq() {
		return this.monitoredExec;
	}
	
	public int getMonitorEval() {
		return this.monitoredEval;
	}
	
	public boolean isExecutedOnOldVersion() {
		if(!this.isValid()) {
			Utils.fail("should set the execution info first");
		}
		return this.execFreqInOld > 0;
	}
	
	public boolean isExecutedOnNewVersion() {
		if(!this.isValid()) {
			Utils.fail("should set the execution info first");
		}
		return this.execFreqInNew > 0;
	}
	
	public PredicateExecInfo createOldPredicateExecInfo() {
		Utils.checkNotNull(this.oldMethodSig);
		return new PredicateExecInfo(this.oldMethodSig, this.oldIndex + "",
				this.execFreqInOld, this.evalResultInOld);
	}
	
	public PredicateExecInfo createNewPredicateExecInfo() {
		Utils.checkNotNull(this.newMethodSig);
		return new PredicateExecInfo(this.newMethodSig, this.newIndex + "",
				this.execFreqInNew, this.evalResultInNew);
	}
	
	public boolean isBehaviorChanged() {
		if(this.isBehaviorSame()) {
			return false;
		}
		//check the execution frequency
		if(this.execFreqInNew != 0 && this.evalResultInNew != 0
			&& this.execFreqInOld != 0 && this.evalResultInOld != 0) {
			return this.compareBehaviors() > this.delta;
		}
		//some value must be zero, so need to check manually
		//a heuristic: no matter how many times it gets evalauted
		//if the results are always false, there is actually no difference
		if(this.evalResultInNew == this.evalResultInOld && this.evalResultInNew == 0) {
			return false;
		}
		return true; //yes, behavior has been changed
	}
	
	private boolean isBehaviorSame() {
		if(this.execFreqInOld == this.execFreqInNew 
			    & this.evalResultInOld == this.evalResultInNew) {
		    return true;
		} else if(this.evalResultInNew == this.evalResultInOld
					&& this.evalResultInOld == 0) {
				return true;
		} else {
			//check the ratio
			if(this.execFreqInNew != 0 && this.execFreqInOld != 0) {
				return Math.abs(((float)this.evalResultInOld/(float)this.execFreqInOld)
						- ((float)this.evalResultInNew/(float)this.execFreqInNew)) < 0.05;
			}
		}
		return false;
	}
	
	private float delta = 0.1f;
	public void setDelta(float d) {
		this.delta = d;
	}
	
	public float getDifferenceDegree() {
		//a special case
		//1:1 ==> 1:0 or 1:0 ==> 1:1
		if(this.execFreqInOld == 1 && this.evalResultInOld == 1 && this.execFreqInNew == 1 && this.evalResultInNew == 0) {
			return 0.5f;
		}
		if(this.execFreqInOld == 1 && this.evalResultInOld == 0 && this.execFreqInNew == 1 && this.evalResultInNew == 1) {
			return 0.5f;
		}
		if(this.execFreqInNew != 0 && this.evalResultInNew != 0
				&& this.execFreqInOld != 0 && this.evalResultInOld != 0) {
				return this.compareBehaviors();
		}
		//executed on both versions, the evaluation result is ZERO
		else if(this.execFreqInNew !=0 && this.execFreqInOld != 0) {
			return Math.abs(this.harmonicMean(this.execFreqInNew, this.evalResultInNew)
					- this.harmonicMean(this.execFreqInOld, this.evalResultInOld));
		}
		
		//executed on the old or new version
		if(this.execFreqInOld == 0) {
			return this.harmonicMean(this.execFreqInNew, this.evalResultInNew);
		}
		
		if(this.execFreqInNew == 0) {
			return this.harmonicMean(this.execFreqInOld, this.evalResultInOld);
		}
		
		return 0.0f; //XXX NOTE
	}
	
	private float compareBehaviors() {
		//check the exec freq
		float oldValue = 2 / ((1/(float)this.execFreqInOld) + (1/((float)this.evalResultInOld/(float)this.execFreqInOld)));
		float newValue = 2 / ((1/(float)this.execFreqInNew) + (1/((float)this.evalResultInNew/(float)this.execFreqInNew)));
		float d = Math.abs(oldValue - newValue);
		return d;
	}
	
	private float harmonicMean(float freq, float eval) {
		Utils.checkTrue(freq >= eval);
		Utils.checkTrue(freq > 0);
		float trueRatio = eval == 0.0f ? 1/freq : eval/freq;
		return 2 / ((1/freq) + (1/trueRatio));
	}
	
	private boolean isValid() {
		return this.execFreqInOld >= 0 && this.evalResultInOld >= 0
		    && this.execFreqInNew >= 0 && this.evalResultInNew >= 0;
	}
	
	@Override
	public String toString() {
		return this.oldMethodSig + "@" + this.oldIndex + " => " + this.newMethodSig + "@" + this.newIndex +
		    "\n      in old: " + this.evalResultInOld + "/" + this.execFreqInOld
		    + "\n      in new: " + this.evalResultInNew + "/" + this.execFreqInNew;
	}
}