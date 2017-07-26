package edu.washington.cs.conf.analysis.evol;

public class AnalysisScopeRepository {

	public static AnalysisScope createRandoopScore() {
		return createScopeOfPackages("randoop");
	}
	
	public static AnalysisScope createSynopticScope() {
		return createScopeOfPackages("synoptic");
	}
	
	public static AnalysisScope createWekaScope() {
		return createScopeOfPackages("weka");
	}
	
	public static AnalysisScope createJMeterScope() {
		return createScopeOfPackages("org.apache.jmeter");
	}
	
	public static AnalysisScope createJChordScope() {
		return createScopeOfPackages("chord");
	}
	
	public static AnalysisScope createScopeOfPackages(String packName) {
		AnalysisScope scope = new AnalysisScope();
		scope.setScopePackages(new String[]{packName});
		return scope;
	}
}