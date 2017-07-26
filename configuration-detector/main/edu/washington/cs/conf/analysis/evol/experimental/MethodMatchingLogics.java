package edu.washington.cs.conf.analysis.evol.experimental;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.wala.ipa.callgraph.CGNode;

import edu.washington.cs.conf.analysis.evol.AnalysisCache;
import edu.washington.cs.conf.analysis.evol.AnalysisScope;
import edu.washington.cs.conf.analysis.evol.CodeAnalyzer;
import edu.washington.cs.conf.analysis.evol.MethodMatcher;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

//the strategy taken to match methods
@Deprecated
public class MethodMatchingLogics {
	
	public static boolean USE_FUZZING_MATCHING = false;
	
	public final CodeAnalyzer oldAnalyzer;
	public final CodeAnalyzer newAnalyzer;
	
	private float threshold = 0.6f;
	private int lookahead = 5;
	
	public final MethodMatcher matcher;
	public final AnalysisScope scope;
	public final AnalysisCache cache;
	
	public MethodMatchingLogics(CodeAnalyzer oldAnalyzer, CodeAnalyzer newAnalyzer,
			AnalysisScope scope, AnalysisCache cache) {
		Utils.checkNotNull(oldAnalyzer);
		Utils.checkNotNull(newAnalyzer);
		Utils.checkNotNull(scope);
		Utils.checkNotNull(cache);
		this.oldAnalyzer = oldAnalyzer;
		this.newAnalyzer = newAnalyzer;
		this.matcher = new MethodMatcher(oldAnalyzer.getCallGraph(), newAnalyzer.getCallGraph(),
				scope, cache);
		this.scope = scope;
		this.cache = cache;
	}
	
	public void setThreshold(float d) {
		Utils.checkTrue(d >= 0f && d <=1f );
		this.threshold = d;
	}
	
	public void setLookahead(int la) {
		Utils.checkTrue(la >= 0);
		this.lookahead = la;
	}
	
	public List<CGNode> getMatchedMethods(String methodSig) {
		CGNode oldNode = WALAUtils.lookupMatchedCGNode(this.oldAnalyzer.getCallGraph(), methodSig);
		Utils.checkNotNull(oldNode);
		return this.getMatchedMethods(oldNode);
	}
	
	//from an old node to a list of new nodes
	public Map<CGNode, List<CGNode>> getAllMatchedMethods() {
		Map<CGNode, List<CGNode>> resultMap = new LinkedHashMap<CGNode, List<CGNode>>();
		for(CGNode oldNode : this.oldAnalyzer.getCallGraph()) {
			if(!this.scope.isInScope(oldNode.getMethod().getDeclaringClass())) {
				continue;
			}
			List<CGNode> matchedMethods = this.getMatchedMethods(oldNode);
			resultMap.put(oldNode, matchedMethods);
		}
		return resultMap;
	}
	
	public List<CGNode> getMatchedMethods(CGNode oldNode) {
		Utils.checkTrue(WALAUtils.containNode(this.oldAnalyzer.getCallGraph(), oldNode),
				"Old node is: " + oldNode.getMethod().getSignature());
		List<CGNode> nodeList = new LinkedList<CGNode>();
		
		CGNode exactMatchedNode = this.matcher.getMethodInNewCG(oldNode.getMethod().getSignature());
		if(exactMatchedNode != null) {
			nodeList.add(exactMatchedNode);
		} else {
			//if there is no exact matching
			if(USE_FUZZING_MATCHING) {
			    //use the fuzzing matching
			    List<CGNode> fuzzMatchedNodes
			        = this.matcher.getFuzzMatchedNodes(oldNode, threshold, lookahead);
			    nodeList.addAll(fuzzMatchedNodes);
			}
		}
		
		return nodeList;
	}
}