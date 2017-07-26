package edu.washington.cs.conf.instrument;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ShrikePoint;
import junit.framework.TestCase;

public class TestConfInstrumenter extends TestCase {

	public void testSimpleInstrumenter() throws Exception {
		ConfInstrumenter instrumenter = new ConfInstrumenter(null);
		instrumenter.instrument("./subjects/testdata.jar", "./output.jar");
	}
	
	public void testInstrumenterWithMockData() throws Exception {
		InstrumentSchema schema = new InstrumentSchema();
		ConfEntity conf1 = new ConfEntity("test.slice.depfield.FieldDeps", "f_value", null, false);
		ShrikePoint p11 = ShrikePoint.createMockPoint(16, "test.slice.depfield.FieldDeps.compute_result2()I");
		List<ShrikePoint> pList1 = Collections.singletonList(p11);
		
		ConfEntity conf2 = new ConfEntity("test.slice.depfield.FieldDeps", "field_value", null, true);
		ShrikePoint p21 = ShrikePoint.createMockPoint(4, "test.slice.depfield.FieldDeps.compute_result1(I)I");
		ShrikePoint p22 = ShrikePoint.createMockPoint(3, "test.slice.depfield.FieldDeps.compute_final_r()V");
		List<ShrikePoint> pList2 = new LinkedList<ShrikePoint>();
		pList2.add(p21);
		pList2.add(p22);
		
		schema.addInstrumentationPoint(conf1, pList1);
		schema.addInstrumentationPoint(conf2, pList2);
		
		//test.slice.depfield.FieldDeps : f_value, 
		//16  line num: 33 @ test.slice.depfield.FieldDeps.compute_result2()
		
		//test.slice.depfield.FieldDeps : field_value, null, static: true
		//instruction index: 4  line num: 20 @ test.slice.depfield.FieldDeps.compute_result1(I)I
		//instruction index: 3  line num: 44 @ test.slice.depfield.FieldDeps.compute_final_r()V
		
		ConfInstrumenter instrumenter = new ConfInstrumenter(schema);
		instrumenter.instrument("./subjects/testdata.jar", "./output.jar");
	}
	
}

/**
kept in ShrikePoint:
test.slice.depfield.FieldDeps.<init>(Ljava/lang/String;)V

kept in MethodData:
()V--<clinit>--<clinit>--Ltest/slice/depfield/FieldDeps;
()V--<init>--<init>--Ltest/slice/depfield/FieldDeps;
(Ljava/lang/String;)V--<init>--<init>--Ltest/slice/depfield/FieldDeps;
(I)I--compute_result1--compute_result1--Ltest/slice/depfield/FieldDeps;
(Ljava/lang/String;)I--getValue--getValue--Ltest/slice/depfield/FieldDeps;
([Ljava/lang/String;)V--main--main--Ltest/slice/depfield/FieldDeps;
 * */