package edu.washington.cs.conf.instrument;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfOutputSerializer;
import edu.washington.cs.conf.analysis.ShrikePoint;
import edu.washington.cs.conf.instrument.InstrumentSchema;
import junit.framework.TestCase;

public class TestInstrumentationSchema extends TestCase {
	
	InstrumentSchema schema = null;
	
	@Override
	public void setUp() {
		ConfEntity conf1 = new ConfEntity("Class1", "Field1", false);
		ConfEntity conf2 = new ConfEntity("Class2", "Field2", true);
		
		ShrikePoint p1 = ShrikePoint.createMockPoint(1, "method_sig1");
		ShrikePoint p2 = ShrikePoint.createMockPoint(2, "method_sig1");
		ShrikePoint p3 = ShrikePoint.createMockPoint(3, "method_sig1");
		ShrikePoint p4 = ShrikePoint.createMockPoint(1, "method_sig2");
		ShrikePoint p5 = ShrikePoint.createMockPoint(1, "method_sig1"); //same as p1
		ShrikePoint p6 = ShrikePoint.createMockPoint(2, "method_sig2");
		ShrikePoint p7 = ShrikePoint.createMockPoint(4, "method_sig1");
		ShrikePoint p8 = ShrikePoint.createMockPoint(1, "method_sig2"); //same as p4
		
		schema = new InstrumentSchema();
		
		Collection<ShrikePoint> coll1 = new LinkedHashSet<ShrikePoint>();
		coll1.add(p1);
		coll1.add(p2);
		coll1.add(p3);
		coll1.add(p4);
		
		Collection<ShrikePoint> coll2 = new LinkedHashSet<ShrikePoint>();
		coll2.add(p5);
		coll2.add(p6);
		coll2.add(p7);
		coll2.add(p8);
		
		schema.addInstrumentationPoint(conf1, coll1);
		schema.addInstrumentationPoint(conf2, coll2);
	}

	public void testGetInstrumentationPredicates() {
		String methodSig1 = "method_sig1";
		Map<String, Set<Integer>> map1 = schema.getInstrumentationPoints(methodSig1);
		System.out.println(map1);
		assertEquals(2, map1.size());
		assertEquals(map1.get("Class1.Field1").toString(), "[1, 2, 3]");
		assertEquals(map1.get("Class2.Field2").toString(), "[1, 4]");
		
		String methodSig2 = "method_sig2";
		Map<String, Set<Integer>> map2 = schema.getInstrumentationPoints(methodSig2);
		System.out.println(map2);
		assertEquals(2, map2.size());
		assertEquals(map2.get("Class1.Field1").toString(), "[1]");
		assertEquals(map2.get("Class2.Field2").toString(), "[2, 1]");
	}
	
	public void testSerialization() {
		String fileName = "./schema_tmp.txt";
		ConfOutputSerializer.serializeSchema(schema, fileName);
		
		InstrumentSchema recoverSchema = ConfOutputSerializer.deserializeAsSchema(fileName);
		
		System.out.println(schema);
		System.out.println(recoverSchema);
		
		//assertTrue(schema.equals(recoverSchema));
		assertEquals(schema.toString(), recoverSchema.toString());
	}
}
