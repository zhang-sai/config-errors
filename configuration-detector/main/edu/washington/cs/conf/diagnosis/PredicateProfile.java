package edu.washington.cs.conf.diagnosis;

import edu.washington.cs.conf.analysis.ConfUtils;
import edu.washington.cs.conf.instrument.AbstractInstrumenter;
import edu.washington.cs.conf.util.Utils;

/**
 * It represents a single predicate profile datapoint, such as
 *   configuration option: maxsize
 *   context: createNewSequence
 *   evaluation count:  100
 *   entering count: 20
 * */
public class PredicateProfile {
	
//	enum POINT{EVALUATING, ENTERING};
	
	private final String confId; //a full name
	private final String context;
//	public final POINT p;
	
	private int evaluating_count = 0;
	private int entering_count = 0;
	
	//for displaying to users
	private int srcLineNumber = -1;
	private final int instructionIndex;
	private final String methodSig; /**the declaring method sig*/
	private String textPredicate = "NOT_SET";
	
	//avoid use this constructor, just for experiment
	PredicateProfile(String confId, String context) {
		Utils.checkTrue(confId != null && !confId.trim().isEmpty());
		Utils.checkTrue(context != null && !context.trim().isEmpty());
		this.confId = confId;
		this.context = context;
//		this.setInstructionIndex(context);
		this.instructionIndex = this.parseInstructionIndex(context);
		this.methodSig = this.parseMethodSig(context);
	}
	
	public PredicateProfile(String confId, String context, 
			int evaluating_count, int entering_count) {
		this(confId, context);
		Utils.checkTrue(evaluating_count > -1);
		Utils.checkTrue(entering_count > -1);
		Utils.checkTrue(evaluating_count >= entering_count);
		this.evaluating_count = evaluating_count;
		this.entering_count = entering_count;
		//parse the context text to get the instruction index
//		this.setInstructionIndex(context);
		//this.instructionIndex = this.parseInstructionIndex(context);
	}
	
//	private void setInstructionIndex(String context) {
//		int instIndex = parseInstructionIndex(context);
//		if(instIndex != -1) {
//			this.instructionIndex = instIndex;
//		}
//	}
	
	//looks like: methodSig + INDEX_SEP + i + SUB_SEP ... ;
	//we want to fetch: i
	private int parseInstructionIndex(String context) {
		int index = context.indexOf(AbstractInstrumenter.INDEX_SEP);
		int sepIndex = context.indexOf(AbstractInstrumenter.SUB_SEP);
		Utils.checkTrue(index != -1, "invalid: " + context);
		if(sepIndex == -1) {
			String str = context.substring(index + AbstractInstrumenter.INDEX_SEP.length());
			return Integer.parseInt(str);
		} else {
			String str = context.substring(index + AbstractInstrumenter.INDEX_SEP.length(), sepIndex);
			return Integer.parseInt(str);
		}
	}
	
	private String parseMethodSig(String context) {
		int index = context.indexOf(AbstractInstrumenter.INDEX_SEP);
		Utils.checkTrue(index != -1, "invalid: " + context);
		return context.substring(0, index);
	}
	
	/**
	 * the source line number and the predicate text are used to display
	 * for users.
	 * */
	public int getInstructionIndex() {
		return this.instructionIndex;
	}
	public String getMethodSig() {
		return this.methodSig;
	}
	public String getFullDeclaringClassName() {
		return ConfUtils.extractFullClassName(this.methodSig);
		//this.methodSig.substring(0, this.methodSig.lastIndexOf("."));
	}
	public int getSourceLineNumber() {
		return srcLineNumber;
	}
	public void setSourceLineNumber(int lineNum) {
//		Utils.checkTrue(lineNum > 0);
		this.srcLineNumber = lineNum;
	}
	public String getPredicateInSource() {
		return textPredicate;
	}
	public void setPredicateInSource(String src) {
		Utils.checkNotNull(src);
		this.textPredicate = src;
	}
	
	public void setEvaluatingCount(int count) {
		Utils.checkTrue(count > -1);
		this.evaluating_count = count;
	}
	
	public int getEvaluatingCount() {
		return this.evaluating_count;
	}
	
	public void setEnteringCount(int count) {
		Utils.checkTrue(count > -1);
		this.entering_count = count;
	}
	
	public int getEnteringCount() {
		return this.entering_count;
	}
	
	public float getRatio() {
		Utils.checkTrue(this.evaluating_count >= this.entering_count);
		return (float)this.entering_count/(float)this.evaluating_count;
	}
	
	/**
	 * return a default value when comparing to Zero, for example,
	 * when comparing:
	 * p1: evaluating count: 80
	 *     entering count: 20
	 * to p2, which even does not evaluate the predicate
	 * */
	public float absoluteRatio() {
		float r = this.getRatio();
		return Math.max(r, 1 - r);
	}

	/**
	 * return a default value when comparing to zero using importance value:
	 * how about reflecting the importance of comparing:
	 * p1: evaluation count 100
	 *     entering count: 0
	 * to p2
	 * 
	 * p1: evaluating count 1
	 *     entering count 0
	 * to p2
	 * */
	public float absImportanceValue() {
		float ratio = this.absoluteRatio();
		return importanceValue(ratio);
	}
	
	public float importanceValue() {
		float ratio = this.getRatio();
//		ratio = (ratio == 0.0f) ? 1/(float)this.evaluating_count : ratio;
//		float importance = 2 / ( (1/(float)ratio) + (1/(float)this.evaluating_count));
//		return importance;
		return importanceValue(ratio);
	}
	
	public float logRunImportanceValue() {
		float ratio = this.getRatio();
		ratio = (ratio == 0.0f) ? 1/(float)this.evaluating_count : ratio;
		float logRun = (float) Math.log(this.evaluating_count);
		if(logRun < 0) {
			logRun = 1.0f;
		}
		float importance = 2 / ( (1/(float)ratio) + (1/logRun));
		return importance;
	}
	
	
	public float importanceValue(float ratio) {
		ratio = (ratio == 0.0f) ? 1/(float)this.evaluating_count : ratio;
		float importance = 2 / ( (1/(float)ratio) + (1/(float)this.evaluating_count));
		return importance;
	}
	
	public String getUniqueKey() {
		return confId + "@" + context;
	}
	
	public String getConfigFullName() {
		return this.confId;
	}
	
	public String getContext() {
		return this.context;
	}
	
	public static String[] parseKey(String key) {
		return key.split("@");
	}
	
	public static String getConfig(String key) {
		return parseKey(key)[0];
	}
	
	public static String getContext(String key) {
		return parseKey(key)[1];
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof PredicateProfile)) {
			return false;
		}
		PredicateProfile p = (PredicateProfile)o;
		return this.confId.equals(p.confId)
		    && this.context.equals(p.context)
		    && this.evaluating_count == p.evaluating_count
		    && this.entering_count == p.entering_count;
	}
	
	@Override
	public int hashCode() {
		return this.confId.hashCode() + 13*this.context.hashCode()
		    + 29*this.evaluating_count + 101*this.entering_count;
	}
	
	@Override
	public String toString() {
		return confId + " @ " + context + ", evaluating: " + evaluating_count
		    + ",  entering: " + entering_count;
	}
	
}