package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.ISSABasicBlock;

import edu.washington.cs.conf.util.WALAUtils;
import junit.framework.TestCase;

public class TestFindPostDominator extends TestCase {
	
	public void testPostDomExampleFoo() {
		PostDominatorFinder.post_dom_debug = false;
		this.showPostDomExample("test.evol.examples.PostDominate.foo");
	}

    public void testPostDomExampleBar() {
    	PostDominatorFinder.post_dom_debug = false;
		this.showPostDomExample("test.evol.examples.PostDominate.bar");
	}
    
    public void testPostDomExampleLoop() {
    	PostDominatorFinder.post_dom_debug = false;
		this.showPostDomExample("test.evol.examples.PostDominate.loop");
	}
	
	private void showPostDomExample(String methodName) {
		String classpath = "./bin/test/evol/examples";
		String mainMethod = "Ltest/evol/examples/PostDominate";
		CodeAnalyzer coder = new CodeAnalyzer(classpath, mainMethod);
		coder.buildAnalysis();
		
		Collection<CGNode> nodes = WALAUtils.lookupCGNode(coder.getCallGraph(), methodName);
		System.out.println(nodes);
		
		CGNode node = nodes.iterator().next();
		
		WALAUtils.printCFG(node);
		
		WALAUtils.printAllIRs(node);
		
		for(ISSABasicBlock bb : WALAUtils.getAllBasicBlocks(node)) {
			ISSABasicBlock postbb = PostDominatorFinder.computeImmediatePostDominator(node, bb);
			System.out.println("The immediate post of: " + bb.getNumber() + " is: " + postbb);
			System.out.println("------------");
		}
	}
	
}