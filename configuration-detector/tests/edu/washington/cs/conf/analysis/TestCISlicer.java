package edu.washington.cs.conf.analysis;

import java.io.PrintWriter;
import java.util.Collection;

import com.ibm.wala.core.tests.slicer.SlicerTest;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.slicer.Slicer.ControlDependenceOptions;
import com.ibm.wala.ipa.slicer.Slicer.DataDependenceOptions;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ipa.slicer.thin.CISlicer;
import com.ibm.wala.ipa.slicer.thin.ThinSlicer;

import edu.washington.cs.conf.util.WALAUtils;

import junit.framework.TestCase;

public class TestCISlicer extends TestCase {

	public void testField() {
		String path = "D:\\research\\configurations\\workspace\\configuration-detector\\bin\\test\\slice\\depfield";
		String mainClass = "Ltest/slice/depfield/FieldDeps";
		ConfigurationSlicer helper = new ConfigurationSlicer(path, mainClass);
		helper.buildAnalysis();
		
		CISlicer slicer = new ThinSlicer(helper.getCallGraph(), helper.getPointerAnalysis());
		ConfEntity entity = new ConfEntity("test.slice.depfield.FieldDeps", "field_value", true);
		entity = new ConfEntity("test.slice.depfield.FieldDeps", "f_value", false);
		Statement seed = helper.extractConfStatement(entity);
		
		CGNode callerNode = SlicerTest.findMethod(helper.getCallGraph(),
				"compute_result2");
	    Statement s = SlicerTest.findCallTo(callerNode, "getValue");
	    System.err.println("Statement: " + s);
		
		Collection<Statement> slice = null;
		System.out.println("--- forward ---");
		
		slice = slicer.computeForwardThinSlice(seed);
		WALAUtils.dumpSlice(slice, new PrintWriter(System.out));
	}
	
}
