package edu.washington.cs.conf.diagnosis;

import java.util.Arrays;

import edu.washington.cs.conf.diagnosis.ProfileDistanceCalculator.DistanceType;

import junit.framework.TestCase;

public class TestProfileDistanceCalculator extends TestCase {
	
	PredicateProfileTuple t1 = null;
	PredicateProfileTuple t2 = null;
	
	@Override
	public void setUp() {
		PredicateProfile p1 = new PredicateProfile("conf1", "context1_index_1", 100, 50);
		PredicateProfile p2 = new PredicateProfile("conf2", "context2_index_1", 100, 60);
		PredicateProfile p3 = new PredicateProfile("conf3", "context3_index_1", 100, 70);
		PredicateProfile p4 = new PredicateProfile("conf4", "context4_index_1", 100, 80);
		PredicateProfile p5 = new PredicateProfile("conf5", "context5_index_1", 100, 90);
		t1 = PredicateProfileTuple.createGoodRun("tuple1", Arrays.asList(p1, p2, p3, p4, p5));
		
		PredicateProfile p11 = new PredicateProfile("conf1", "context1_index_1", 100, 50);
		PredicateProfile p21 = new PredicateProfile("conf21", "context2_index_1", 100, 90);
		PredicateProfile p31 = new PredicateProfile("conf3", "context3_index_1", 100, 70);
		PredicateProfile p41 = new PredicateProfile("conf4", "context4_index_1", 100, 50);
		PredicateProfile p51 = new PredicateProfile("conf51", "context5_index_1", 100, 90);
		t2 = PredicateProfileTuple.createGoodRun("tuple2", Arrays.asList(p11, p21, p31, p41, p51));
	}
	
	public void test1() {
		//0.6 + 0.3 + 0.9 + 0.9 + 0.9
		float manhanttanDistance = ProfileDistanceCalculator.computeDistance(t1, t2, DistanceType.MANHATTAN);
		System.out.println("manhanttan distance: " + manhanttanDistance);
		assertEquals(3.6f, manhanttanDistance);
		
		//sqrt(0.6^2 + 0.3^2 + 0.9^2 + 0.9^2 + 0.9^2)
		float eucDistance = ProfileDistanceCalculator.computeDistance(t1, t2, DistanceType.EUCLIDEAN);
		System.out.println("Euclidean distance: " + eucDistance);
		assertEquals(1.6970563f, eucDistance);
		
		//1- ( 0.5 + 0.7 + 0.5 / 0.5 + 0.6 + 0.9 + 0.7 + 0.8 + 0.9 + 0.9)
		float jaccardDistance = ProfileDistanceCalculator.computeDistance(t1, t2, DistanceType.JACCARD);
		System.out.println("Jaccard distance: " + jaccardDistance);
		assertEquals(0.65999997f, jaccardDistance);
	}
	
	public void test2() {
		float interprodDistance = ProfileDistanceCalculator.computeDistance(t1, t2, DistanceType.INTERPRODUCT);
		System.out.println("interproduction distance: " + interprodDistance);
		assertEquals(0.65848213f, interprodDistance);
		
		float substractDistance = ProfileDistanceCalculator.computeDistance(t1, t2, DistanceType.SUBTRACTION);
		System.out.println("substraction distance: " + substractDistance);
		assertEquals(0.5142857f, substractDistance);
		
		float cosineDistance = ProfileDistanceCalculator.computeDistance(t1, t2, DistanceType.COSINE);
		System.out.println("cosine distance: " + cosineDistance);
	}
	
	public void testCosineDistance_Zero() {
		PredicateProfile p1 = new PredicateProfile("conf1", "context1_index_1", 100, 50);
		PredicateProfile p2 = new PredicateProfile("conf2", "context2_index_1", 100, 60);
		PredicateProfile p3 = new PredicateProfile("conf3", "context3_index_1", 100, 70);
		PredicateProfile p4 = new PredicateProfile("conf4", "context4_index_1", 100, 80);
		PredicateProfile p5 = new PredicateProfile("conf5", "context5_index_1", 100, 90);
		t1 = PredicateProfileTuple.createGoodRun("tuple1", Arrays.asList(p1, p2, p3, p4, p5));
		
		PredicateProfile p11 = new PredicateProfile("conf1", "context1_index_1", 100, 50);
		PredicateProfile p21 = new PredicateProfile("conf2", "context2_index_1", 100, 60);
		PredicateProfile p31 = new PredicateProfile("conf3", "context3_index_1", 100, 70);
		PredicateProfile p41 = new PredicateProfile("conf4", "context4_index_1", 100, 80);
		PredicateProfile p51 = new PredicateProfile("conf5", "context5_index_1", 100, 90);
		t2 = PredicateProfileTuple.createGoodRun("tuple2", Arrays.asList(p11, p21, p31, p41, p51));
		
		float cosineDistance = ProfileDistanceCalculator.computeDistance(t1, t2, DistanceType.COSINE);
		System.out.println("cosine distance: " + cosineDistance);
		
		assertTrue(cosineDistance < 0.00001f);
	}
	
	public void testCosineDistance_One() {
		PredicateProfile p1 = new PredicateProfile("conf1", "context1_index_1", 100, 0);
		PredicateProfile p2 = new PredicateProfile("conf2", "context2_index_1", 100, 0);
		PredicateProfile p3 = new PredicateProfile("conf3", "context3_index_1", 100, 0);
		PredicateProfile p4 = new PredicateProfile("conf4", "context4_index_1", 100, 0);
		PredicateProfile p5 = new PredicateProfile("conf5", "context5_index_1", 100, 0);
		t1 = PredicateProfileTuple.createGoodRun("tuple1", Arrays.asList(p1, p2, p3, p4, p5));
		
		PredicateProfile p11 = new PredicateProfile("conf1", "context1_index_1", 100, 50);
		PredicateProfile p21 = new PredicateProfile("conf2", "context2_index_1", 100, 60);
		PredicateProfile p31 = new PredicateProfile("conf3", "context3_index_1", 100, 70);
		PredicateProfile p41 = new PredicateProfile("conf4", "context4_index_1", 100, 80);
		PredicateProfile p51 = new PredicateProfile("conf5", "context5_index_1", 100, 90);
		t2 = PredicateProfileTuple.createGoodRun("tuple2", Arrays.asList(p11, p21, p31, p41, p51));
		
		float cosineDistance = ProfileDistanceCalculator.computeDistance(t1, t2, DistanceType.COSINE);
		System.out.println("cosine distance: " + cosineDistance);
		
		assertTrue(cosineDistance == 1.0f);
	}
	
	public void testCosineDistance_NOTEEXE() {
		PredicateProfile p1 = new PredicateProfile("conf11", "context1_index_1", 100, 10);
		PredicateProfile p2 = new PredicateProfile("conf21", "context2_index_1", 100, 10);
		PredicateProfile p3 = new PredicateProfile("conf31", "context3_index_1", 100, 10);
		PredicateProfile p4 = new PredicateProfile("conf41", "context4_index_1", 100, 10);
		PredicateProfile p5 = new PredicateProfile("conf51", "context5_index_1", 100, 10);
		t1 = PredicateProfileTuple.createGoodRun("tuple1", Arrays.asList(p1, p2, p3, p4, p5));
		
		PredicateProfile p11 = new PredicateProfile("conf1", "context1_index_1", 100, 50);
		PredicateProfile p21 = new PredicateProfile("conf2", "context2_index_1", 100, 60);
		PredicateProfile p31 = new PredicateProfile("conf3", "context3_index_1", 100, 70);
		PredicateProfile p41 = new PredicateProfile("conf4", "context4_index_1", 100, 80);
		PredicateProfile p51 = new PredicateProfile("conf5", "context5_index_1", 100, 90);
		t2 = PredicateProfileTuple.createGoodRun("tuple2", Arrays.asList(p11, p21, p31, p41, p51));
		
		float cosineDistance = ProfileDistanceCalculator.computeDistance(t1, t2, DistanceType.COSINE);
		System.out.println("cosine distance: " + cosineDistance);
		
		assertTrue(cosineDistance == 1.0f);
	}
}
