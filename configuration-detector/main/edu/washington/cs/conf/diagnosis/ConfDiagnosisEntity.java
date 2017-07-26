package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;

public class ConfDiagnosisEntity {
	
	public enum RawDataType{GOOD_EVAL_COUNT, GOOD_ENTER_COUNT, GOOD_IMPORT, GOOD_RATIO, GOOD_RATIO_ABS, GOOD_IMPORT_ABS,
		BAD_EVAL_COUNT, BAD_ENTER_COUNT, BAD_IMPORT, BAD_RATIO, BAD_RATIO_ABS, BAD_IMPORT_ABS,
		GOOD_RANK, BAD_RANK};
		
	//ratio only calcuates the percentage of passing / all
	//import metric only balances sensitivty and specificity
	public enum ScoreType {RATIO_DELTA, IMPORT_DELTA, IMPORT_RANK_CHANGE, RATIO_RANK_CHANGE}
	
	private final String configFullName;
	private final String context;
	//the following two are for displaying to users
	private final int srcLineNumber;
	private final String predicateText;
	//store different scoring criteria and the corresponding score
	private final Map<RawDataType, Object> rawData = new LinkedHashMap<RawDataType, Object>();
	//all computed scores
	private final Map<ScoreType, Float> scores = new LinkedHashMap<ScoreType, Float>();
	//keep the original data of a score
	private final Map<ScoreType, String> scoreProvenance = new LinkedHashMap<ScoreType, String>();
	
	private ConfEntity entity = null;

	public ConfDiagnosisEntity(PredicateProfile profile) {
		String configFullName = profile.getConfigFullName();
		String context = profile.getContext();
		Utils.checkNotNull(configFullName);
    	Utils.checkNotNull(context);
    	this.configFullName = configFullName;
    	this.context = context;
    	//current empty
    	this.srcLineNumber = profile.getSourceLineNumber();
    	this.predicateText = profile.getPredicateInSource();
	}
    
    public String getConfigFullName() {
    	return this.configFullName;
    }
    
    public String getContext() {
    	return this.context;
    }

    public void setConfEntity(ConfEntityRepository repo) {
    	this.entity = repo.lookupConfEntity(this.configFullName);
    }
    
    public ConfEntity getConfEntity() {
    	return this.entity;
    }
    
    public void saveRawData(RawDataType criteria, Object score) {
    	Utils.checkTrue(!this.rawData.containsKey(criteria));
    	this.rawData.put(criteria, score);
    }
    
    public Float getRawData(RawDataType criteria) {
    	return Float.parseFloat(rawData.get(criteria).toString());
    }
    
    public boolean hasRawData(RawDataType critera) {
    	return rawData.containsKey(critera);
    }
    
    public int getLineNumber() {
    	return this.srcLineNumber;
    }
    
    public String getPredicateText() {
    	return this.predicateText;
    }
    
    public Float getScore(ScoreType type) {
    	return this.scores.get(type);
    }
    
    public boolean hasScore(ScoreType type) {
    	return this.scores.containsKey(type);
    }
    
    //experimental, only in rank change
    public void saveScore(ScoreType t, Float score) {
    	Utils.checkTrue(!this.scores.containsKey(t));
    	this.scores.put(t, score);
    }
    
    public String getScoreProvenance(ScoreType type) {
    	return this.scoreProvenance.get(type);
    }
    
    public boolean hasScoreProvenence(ScoreType type) {
    	return this.scoreProvenance.containsKey(type);
    }
    
    public void setScoreProvence(ScoreType type, String provenance) {
    	Utils.checkTrue(!this.scoreProvenance.containsKey(type));
    	this.scoreProvenance.put(type, provenance);
    }
    
    public String createErrorReport() {
    	//based on the current observation
    	String report = ExplanationGenerator.createWellFormattedExpanation(this.configFullName,
    			this.context, this.predicateText, this.srcLineNumber,
    			getGoodEvalCount(),
    			//this.rawData.containsKey(RawDataType.GOOD_EVAL_COUNT) ? (Integer)this.rawData.get(RawDataType.GOOD_EVAL_COUNT) : 0,
    			getGoodEnterCount(),
    			//this.rawData.containsKey(RawDataType.GOOD_ENTER_COUNT) ? (Integer)this.rawData.get(RawDataType.GOOD_ENTER_COUNT) : 0,
    			this.rawData.containsKey(RawDataType.BAD_EVAL_COUNT) ? (Integer)this.rawData.get(RawDataType.BAD_EVAL_COUNT) : 0,
    			this.rawData.containsKey(RawDataType.BAD_ENTER_COUNT) ? (Integer)this.rawData.get(RawDataType.BAD_ENTER_COUNT) : 0);
    	
    	return report;
    }
    
    public int getGoodEvalCount() {
    	return this.rawData.containsKey(RawDataType.GOOD_EVAL_COUNT) ? (Integer)this.rawData.get(RawDataType.GOOD_EVAL_COUNT) : 0;
    }
    
    public int getGoodEnterCount() {
    	return this.rawData.containsKey(RawDataType.GOOD_ENTER_COUNT) ? (Integer)this.rawData.get(RawDataType.GOOD_ENTER_COUNT) : 0;
    }
    
    public void computeAllScores() {
    	this.computeScore(ScoreType.RATIO_DELTA);
    	this.computeScore(ScoreType.IMPORT_DELTA);
    }
    
    public void computeScore(ScoreType type) {
    	if(type.equals(ScoreType.RATIO_DELTA)) {
    		boolean hasGoodRatio = this.rawData.containsKey(RawDataType.GOOD_RATIO);
    		boolean hasBadRatio = this.rawData.containsKey(RawDataType.BAD_RATIO);
    		Utils.checkTrue( hasGoodRatio || hasBadRatio );
    		if(hasGoodRatio && hasBadRatio ) {
    			float score = Math.abs(this.getRawData(RawDataType.GOOD_RATIO) - this.getRawData(RawDataType.BAD_RATIO));
    			this.scores.put(ScoreType.RATIO_DELTA, score);
    			this.scoreProvenance.put(ScoreType.RATIO_DELTA, "Good ratio: "
    					+ this.getRawData(RawDataType.GOOD_RATIO) + ", bad ratio: " + this.getRawData(RawDataType.BAD_RATIO)
    					+ ", computed by, good-evaluation: " + this.getRawData(RawDataType.GOOD_EVAL_COUNT)
    					+ ",  good-entering: " + this.getRawData(RawDataType.GOOD_ENTER_COUNT)
    					+ ", also bad-evaluation: " + this.getRawData(RawDataType.BAD_EVAL_COUNT)
    					+ ", bad-entering: " + this.getRawData(RawDataType.BAD_ENTER_COUNT)
    					+ ",  line num: " + getLineNumber() + ", text: " + getPredicateText());
    		} else if (hasGoodRatio && !hasBadRatio) {
    			float score = Math.abs(this.getRawData(RawDataType.GOOD_RATIO_ABS));
    			this.scores.put(ScoreType.RATIO_DELTA, score);
    			this.scoreProvenance.put(ScoreType.RATIO_DELTA,
    					"Good absolute ratio: " + this.getRawData(RawDataType.GOOD_RATIO_ABS)
    					+ ", computed by, good-evaluation: " + this.getRawData(RawDataType.GOOD_EVAL_COUNT)
    					+ ", good-entering: " + this.getRawData(RawDataType.GOOD_ENTER_COUNT)
    					+ ",  line num: " + getLineNumber() + ", text: " + getPredicateText());
    		} else if (!hasGoodRatio && hasBadRatio) {
    			float score = Math.abs(this.getRawData(RawDataType.BAD_RATIO_ABS));
    			this.scores.put(ScoreType.RATIO_DELTA, score);
    			this.scoreProvenance.put(ScoreType.RATIO_DELTA,
    					"Bad absolute ratio: " + this.getRawData(RawDataType.BAD_RATIO_ABS)
    					+ ", computed by, bad-evaluation: " + this.getRawData(RawDataType.BAD_EVAL_COUNT)
    					+ ", bad-entering: " + this.getRawData(RawDataType.BAD_ENTER_COUNT)
    					+ ",  line num: " + getLineNumber() + ", text: " + getPredicateText());
    		} else {
    			throw new Error();
    		}
    	} else if (type.equals(ScoreType.IMPORT_DELTA)) {
    		boolean hasGoodImport = this.rawData.containsKey(RawDataType.GOOD_IMPORT);
    		boolean hasBadImport = this.rawData.containsKey(RawDataType.BAD_IMPORT);
            if(hasGoodImport && hasBadImport ) {
    			float score = Math.abs(this.getRawData(RawDataType.GOOD_IMPORT) - this.getRawData(RawDataType.BAD_IMPORT));
    			this.scores.put(ScoreType.IMPORT_DELTA, score);
    			this.scoreProvenance.put(ScoreType.IMPORT_DELTA, "Good import: "
    					+ this.getRawData(RawDataType.GOOD_IMPORT) + ", bad import: " + this.getRawData(RawDataType.BAD_IMPORT)
    					+ ",  computed by: good-evaluation: " + this.getRawData(RawDataType.GOOD_EVAL_COUNT)
    					+ ",  good ratio: " + this.getRawData(RawDataType.GOOD_RATIO)
    					+ ",  bad-evaluation: " + this.getRawData(RawDataType.BAD_EVAL_COUNT)
    					+ ",  bad ratio:  " + this.getRawData(RawDataType.BAD_RATIO)
    					+ ",  line num: " + getLineNumber() + ", text: " + getPredicateText());
    		} else if (hasGoodImport && !hasBadImport) {
    			float score = Math.abs(this.getRawData(RawDataType.GOOD_IMPORT_ABS));
    			this.scores.put(ScoreType.IMPORT_DELTA, score);
    			this.scoreProvenance.put(ScoreType.IMPORT_DELTA,
    					"Good absolute import: " + this.getRawData(RawDataType.GOOD_IMPORT_ABS)
    					+ ", computed by, good-evaluation: " + this.getRawData(RawDataType.GOOD_EVAL_COUNT)
    					+ ", good absolute ratio: " + this.getRawData(RawDataType.GOOD_RATIO_ABS)
    					+ ",  line num: " + getLineNumber() + ", text: " + getPredicateText());
    		} else if (!hasGoodImport && hasBadImport) {
    			float score = Math.abs(this.getRawData(RawDataType.BAD_IMPORT_ABS));
    			this.scores.put(ScoreType.IMPORT_DELTA, score);
    			this.scoreProvenance.put(ScoreType.IMPORT_DELTA,
    					"Bad absolute import: " + this.getRawData(RawDataType.BAD_IMPORT_ABS)
    					+ ",  computed by, bad-evaluation: " + this.getRawData(RawDataType.BAD_EVAL_COUNT)
    					+ ",  bad absolute ratio: " + this.getRawData(RawDataType.BAD_RATIO_ABS)
    					+ ",  line num: " + getLineNumber() + ", text: " + getPredicateText());
    		} else {
    			throw new Error();
    		}
    	} else {
    		throw new Error("Unrecognized: " + type);
    	}
    }
    
    public boolean missedByOneRun() {
    	boolean hasGoodEval = this.hasRawData(RawDataType.GOOD_EVAL_COUNT);
    	boolean hasBadEval = this.hasRawData(RawDataType.BAD_EVAL_COUNT);
    	Utils.checkTrue(hasGoodEval || hasBadEval);
    	if(hasGoodEval && hasBadEval) {
    		return false;
    	}
    	return true;
    }
    
    public boolean isSingleOccurance() {
    	boolean hasGoodEval = this.hasRawData(RawDataType.GOOD_EVAL_COUNT);
    	boolean hasBadEval = this.hasRawData(RawDataType.BAD_EVAL_COUNT);
    	Utils.checkTrue(hasGoodEval || hasBadEval);
    	if(hasGoodEval && hasBadEval) {
    		return false;
    	}
    	if(hasGoodEval && ! hasBadEval) {
    		float goodCount = this.getRawData(RawDataType.GOOD_EVAL_COUNT);
    		return ((int)goodCount) == 1;
    	}
    	if(!hasGoodEval && hasBadEval) {
    		float badCount = this.getRawData(RawDataType.BAD_EVAL_COUNT);
    		return ((int)badCount) == 1;
    	}
    	return false;
    }
    
    public boolean isSingleOccuranceInBothRuns() {
    	boolean hasGoodEval = this.hasRawData(RawDataType.GOOD_EVAL_COUNT);
    	boolean hasBadEval = this.hasRawData(RawDataType.BAD_EVAL_COUNT);
    	Utils.checkTrue(hasGoodEval || hasBadEval);
    	if(hasGoodEval && hasBadEval) {
    		float goodCount = this.getRawData(RawDataType.GOOD_EVAL_COUNT);
    		float badCount = this.getRawData(RawDataType.BAD_EVAL_COUNT);
    		return (int)goodCount == 1 && (int)badCount ==1;
    	} else {
    		return false;
    	}
    }
    
    public boolean hasSameRatio() {
    	boolean hasGoodEval = this.hasRawData(RawDataType.GOOD_EVAL_COUNT);
    	boolean hasBadEval = this.hasRawData(RawDataType.BAD_EVAL_COUNT);
    	Utils.checkTrue(hasGoodEval || hasBadEval);
    	if(hasGoodEval && hasBadEval) {
    		float goodRatio = this.getRawData(RawDataType.GOOD_RATIO);
    		float badRatio = this.getRawData(RawDataType.BAD_RATIO);
    		return goodRatio == badRatio;
    	} 
    	return false;
    }
    
    public boolean hasSameCountDelta() {
    	boolean hasGoodEval = this.hasRawData(RawDataType.GOOD_EVAL_COUNT);
    	boolean hasBadEval = this.hasRawData(RawDataType.BAD_EVAL_COUNT);
    	Utils.checkTrue(hasGoodEval || hasBadEval);
    	if(hasGoodEval && hasBadEval) {
    		float goodEnterCount = this.getRawData(RawDataType.GOOD_ENTER_COUNT);
    		float goodEvalCount = this.getRawData(RawDataType.GOOD_EVAL_COUNT);
    		float badEnterCount = this.getRawData(RawDataType.BAD_ENTER_COUNT);
    		float badEvalCount = this.getRawData(RawDataType.BAD_EVAL_COUNT);
    		return (goodEvalCount - goodEnterCount) == (badEvalCount - badEnterCount);  //filter cases like time
    	} 
    	return false;
    }
    
    @Override
    public boolean equals(Object obj) {
    	if(!(obj instanceof ConfDiagnosisEntity)) {
    		return false;
    	}
    	ConfDiagnosisEntity confE = (ConfDiagnosisEntity)obj;
    	return this.configFullName.equals(confE.configFullName)
    	    && this.context.equals(confE.context)
    	    && this.rawData.equals(confE.rawData)
    	    && this.scores.equals(confE.scores);
    }
    
    @Override
    public int hashCode() {
    	return this.configFullName.hashCode() + 13*this.context.hashCode() + 29*this.rawData.hashCode()
    	     + 31*this.scores.hashCode();
    }
    
    @Override
    public String toString() {
    	return this.configFullName + "@" + this.context + Globals.lineSep + "   " + this.rawData
    	    + Globals.lineSep + "   " + this.scores
    	    + Globals.lineSep + "   config entity: " + this.entity;
    }
}