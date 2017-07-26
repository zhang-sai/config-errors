package edu.washington.cs.conf.diagnosis;

import java.io.PrintStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;

public class ConfDiagnosisOutput {

	private final ConfEntity conf;
	
	//need to keep some (meta) histrocal analysis information to
	//show users why it output the rank, this field is not used for comparison
	private List<String> explanations = new LinkedList<String>();
	
	private Float finalScore = Float.NaN; //A place holder
	
	public ConfDiagnosisOutput(ConfEntity conf) {
		Utils.checkNotNull(conf);
		this.conf = conf;
	}
	
	public ConfEntity getConfEntity() {
		return this.conf;
	}
	
	public void addExplain(String explanation) {
		this.explanations.add(explanation);
	}
	
	public void addAllExplain(Collection<String> explanations) {
		this.explanations.addAll(explanations);
	}
	
	public List<String> getExplanations() {
		return this.explanations;
	}
	
	public String getBriefExplanation() {
		return "Number of explanations: " + this.explanations.size()
		    + Globals.lineSep
		    + "     , with the first piece: "
		    + (explanations.isEmpty() ? "N/A" : explanations.get(0) )
		    + Globals.lineSep
		    + "     , with the last piece: "
		    + (explanations.isEmpty() ? "N/A" : explanations.get(explanations.size() - 1) );
	}
	
	//not used for comparison in equals
	//the fields used to generate error report, should be encapsulated
	//in some other places?
	//FIXME some dirty hack
	private String errorReport = "NOT-GENERATED-YET";
	private int total_enter = -1;
	private int total_eval = -1;
	public int getTotalEnter() {
		return total_enter;
	}
	public int getTotalEval() {
		return total_eval;
	}
	public void setTotalEnter(int count) {
		Utils.checkTrue(count >= 0);
		if(count != 0) {
		    this.total_enter = count;
		}
	}
	public void setTotalEval(int count) {
		Utils.checkTrue(count >= 0);
		if(count != 0) {
		    this.total_eval = count;
		}
	}
	public void incrTotalEnter(int count) {
		if(count > 0) {
			this.total_enter += count;
		}
	}
	public void incrTotalEval(int count) {
		if(count > 0) {
			this.total_eval += count;
		}
	}
	public void setErrorReport(String errorReport) {
		Utils.checkNotNull(errorReport);
		this.errorReport = errorReport;
	}
	public String getErrorReport() {
		return ExplanationGenerator.replaceWithGoodRunNum(errorReport, this.total_enter, this.total_eval);
	}
	
	//START: this section is for experimental purpose
	private List<String> reports = new LinkedList<String>();
	public void deleteReports() {
		this.reports.clear();
	}
	public void addReport(String report) {
		this.reports.add(report);
	}
	public void addReports(Collection<String> reports) {
		this.reports.addAll(reports);
	}
	public List<String> getReports() {
		return reports;
	}
	//END:
	
	/**
	 * a rough outline of recovery:
	 * 
	 * String confName = this.conf.getFullConfName();
		String context = null;
		String predicateText = null;
		int lineNum = -1;
		int goodRunNum = -1;
		int goodEnter = -1;
		int badRunNum = -1;
		int badEnter = -1;
		String expl = ExplanationGenerator.createWellFormattedExpanation(confName, context, predicateText, lineNum,
				goodRunNum, goodEnter, badRunNum, badEnter);
		return expl;
	 * */
	
	public Float getFinalScore() {
		return this.finalScore;
	}
	
	public void setFinalScore(Float score) {
		this.finalScore = score;
	}
	
	public void showExplanations(PrintStream out) {
		String s = "";
		for(String ex : this.getExplanations()) {
			s =  s + ex + Globals.lineSep;
		}
		out.println(s);
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ConfDiagnosisOutput)) {
			return false;
		}
		ConfDiagnosisOutput conf = (ConfDiagnosisOutput)o;
		return conf.conf.equals(this.conf);
	}
	
	@Override
	public int hashCode() {
		return this.conf.hashCode();
	}
	
	@Override
	public String toString() {
		return this.conf.toString();
	}
}