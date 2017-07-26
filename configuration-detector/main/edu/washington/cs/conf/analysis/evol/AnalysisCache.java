package edu.washington.cs.conf.analysis.evol;

import java.util.LinkedHashSet;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;

import edu.washington.cs.conf.util.Utils;

/**
 * To cache some useful fact for the analysis.
 * */
public class AnalysisCache {

	public final CodeAnalyzer oldAnalyzer;
	public final CodeAnalyzer newAnalyzer;
	public final AnalysisScope scope;
	
	public static AnalysisCache createCache(CodeAnalyzer oldAnalyzer, CodeAnalyzer newAnalyzer, AnalysisScope scope) {
		return new AnalysisCache(oldAnalyzer, newAnalyzer, scope);
	}
	
	AnalysisCache(CodeAnalyzer oldAnalyzer, CodeAnalyzer newAnalyzer, AnalysisScope scope) {
		Utils.checkNotNull(oldAnalyzer);
		Utils.checkNotNull(newAnalyzer);
		Utils.checkNotNull(scope);
		this.oldAnalyzer = oldAnalyzer;
		this.newAnalyzer = newAnalyzer;
		this.scope = scope;
	}
	
	private Set<String> unmatchedMethodSigsInOldVersion = null;
	private Set<String> unmatchedMethodSigsInNewVersion = null;
	
	public boolean isUnmatchedInNewVersion(String methodSig) {
		if(this.unmatchedMethodSigsInNewVersion == null) {
			this.buildUnmatchedMethods();
		}
		return this.unmatchedMethodSigsInNewVersion.contains(methodSig);
	}
	
	public boolean isUnmatchedInOldVersion(String methodSig) {
		if(this.unmatchedMethodSigsInOldVersion == null) {
			this.buildUnmatchedMethods();
		}
		return this.unmatchedMethodSigsInOldVersion.contains(methodSig);
	}
	
	private void buildUnmatchedMethods() {
		this.unmatchedMethodSigsInNewVersion = new LinkedHashSet<String>();
		this.unmatchedMethodSigsInOldVersion = new LinkedHashSet<String>();
		//go through the call graphs
		Set<String> allMethodsNewVersion = new LinkedHashSet<String>();
		Set<String> allMethodsOldVersion = new LinkedHashSet<String>();
		for(CGNode oldNode : this.oldAnalyzer.getCallGraph()) {
			if(this.scope.isInScope(oldNode.getMethod().getDeclaringClass())) {
				allMethodsOldVersion.add(oldNode.getMethod().getSignature());
			}
		}
		for(CGNode newNode : this.newAnalyzer.getCallGraph()) {
			if(this.scope.isInScope(newNode.getMethod().getDeclaringClass())) {
				allMethodsNewVersion.add(newNode.getMethod().getSignature());
			}
		}
		//compute the unmatched methods
		for(String oldMethodSig : allMethodsOldVersion) {
			if(!allMethodsNewVersion.contains(oldMethodSig)) {
				this.unmatchedMethodSigsInOldVersion.add(oldMethodSig);
			}
		}
		for(String newMethodSig : allMethodsNewVersion) {
			if(!allMethodsOldVersion.contains(newMethodSig)) {
				this.unmatchedMethodSigsInNewVersion.add(newMethodSig);
			}
		}
		
		//reclaim memory
		allMethodsOldVersion.clear();
		allMethodsNewVersion.clear();
	}
}
