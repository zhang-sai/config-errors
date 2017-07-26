package edu.washington.cs.conf.mutation;

public 
class MessageAdequacy {
	
	private boolean containOptionName = false;
	private boolean containOptionValue = false;
	private boolean closeEnoughToUserManual = false;
	private boolean closestToAllUserDesc = false;
	
	//give some explanations of why this is sufficient
	
	//TODO analyze the text to decide its adequacy
	//more info, in particular, if the message is not enough, we need
	//to show why it is not enough, and which one should be improved
	public boolean isAdequate() {
		return containOptionName || containOptionValue
		    || closeEnoughToUserManual || closestToAllUserDesc;
	}
	
	public void setContainOptionName(boolean flag) {
		this.containOptionName = flag;
	}
	
	public void setContainOptionValue(boolean flag) {
		this.containOptionValue = flag;
	}
	
	public void setCloseEnoughToUserManual(boolean flag) {
		this.closeEnoughToUserManual = flag;
	}
	
	public void setClosestToAllUserDesc(boolean flag) {
		this.closeEnoughToUserManual = flag;
	}
	
	//TODO need to get more
	public String getExplanation() {
		return this.toString();
//		if(!isAdequate()) {
//			return "Not contain option values/names, nor not close enough to user description";
//		} else {
//			return "Adequate!";
//		}
	}
	
	public String toString() {
		return "Adequate: contain name:" + containOptionName + ", contain value: "
		    + containOptionValue + ", close to manual: "
		    + closeEnoughToUserManual + ", close to all user descs: " + closestToAllUserDesc;
	}
}