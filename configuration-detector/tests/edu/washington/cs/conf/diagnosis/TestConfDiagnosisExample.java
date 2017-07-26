package edu.washington.cs.conf.diagnosis;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntityRepository;
import edu.washington.cs.conf.analysis.TestConfEntityRepository;
import edu.washington.cs.conf.diagnosis.ConfDiagnosisEntity.RawDataType;
import junit.framework.TestCase;


/***
 * Good run            evaluation       enter
 * -------------------------------------------
 * c1@context1         100              80
 * c2@context2         5                4
 * c2@context4         40               20
 * c3@context3         1                0
 * c3@context4         25               5
 * c4@cotnext4         25               20
 * c4@context1         50               15
 * 
 * 
 * Bad run
 * --------------------------------------------
 * c1@context1         100              30
 * c2@context2         5                0
 * c4@context4         25               10
 * c4@context6         6                5
 * c5@context5         4                0
 * c6@context2         30               15
 * c6@context3         80               60
 * c6@cotnext6         1                0
 * 
 * */

public class TestConfDiagnosisExample extends TestCase {
	
	public void testGoodRun() {
		assertNotNull(getGoodRun());
	}
	
	public void testBadRun() {
		assertNotNull(getBadRun());
	}
	
	public void testCreateDiagnosisEntity() {
		getCreateDiagnosisEntity();
	}
	
	public void testRatioRank() {
		List<ConfDiagnosisEntity> entityList = getCreateDiagnosisEntity();
		Collection<List<ConfDiagnosisEntity>> singleColl = Arrays.asList(entityList);
		List<ConfDiagnosisOutput> rankedList = PredicateProfileBasedDiagnoser.rankOptionsByRatio(singleColl);
		
		System.out.println("----- rank ------");
		for(ConfDiagnosisOutput rankedOutput : rankedList) {
			System.out.println(rankedOutput);
			System.out.println(rankedOutput.getExplanations());
			System.out.println();
		}
	}
	
	public void testImportRank() {
		List<ConfDiagnosisEntity> entityList = getCreateDiagnosisEntity();
		Collection<List<ConfDiagnosisEntity>> singleColl = Arrays.asList(entityList);
		List<ConfDiagnosisOutput> rankedList = PredicateProfileBasedDiagnoser.rankOptionsByImportance(singleColl);
		
		System.out.println("----- rank ------");
		for(ConfDiagnosisOutput rankedOutput : rankedList) {
			System.out.println(rankedOutput);
			System.out.println(rankedOutput.getExplanations());
			System.out.println();
		}
	}
	
	public void testRatioSumRank() {
		List<ConfDiagnosisEntity> entityList = getCreateDiagnosisEntity();
		Collection<List<ConfDiagnosisEntity>> singleColl = Arrays.asList(entityList);
		List<ConfDiagnosisOutput> rankedList = PredicateProfileBasedDiagnoser.rankOptionsByRatioSum(singleColl);
		
		System.out.println("----- rank ------");
		for(ConfDiagnosisOutput rankedOutput : rankedList) {
			System.out.println(rankedOutput);
			System.out.println(rankedOutput.getExplanations());
			System.out.println();
		}
	}
	
	public void testImportSumRank() {
		List<ConfDiagnosisEntity> entityList = getCreateDiagnosisEntity();
		Collection<List<ConfDiagnosisEntity>> singleColl = Arrays.asList(entityList);
		List<ConfDiagnosisOutput> rankedList = PredicateProfileBasedDiagnoser.rankOptionsByImportanceSum(singleColl);
		
		System.out.println("----- rank ------");
		for(ConfDiagnosisOutput rankedOutput : rankedList) {
			System.out.println(rankedOutput);
			System.out.println(rankedOutput.getExplanations());
			System.out.println();
		}
	}
	
	public void testRatioTfidfRank() {
		List<ConfDiagnosisEntity> entityList = getCreateDiagnosisEntity();
		Collection<List<ConfDiagnosisEntity>> singleColl = Arrays.asList(entityList);
		List<ConfDiagnosisOutput> rankedList = PredicateProfileBasedDiagnoser.rankOptionsByTfidfRatio(singleColl);
		
		System.out.println("----- rank ------");
		for(ConfDiagnosisOutput rankedOutput : rankedList) {
			System.out.println(rankedOutput);
			System.out.println(rankedOutput.getExplanations());
			System.out.println();
		}
	}
	
	public void testImportTfidfRank() {
		List<ConfDiagnosisEntity> entityList = getCreateDiagnosisEntity();
		Collection<List<ConfDiagnosisEntity>> singleColl = Arrays.asList(entityList);
		List<ConfDiagnosisOutput> rankedList = PredicateProfileBasedDiagnoser.rankOptionsByTfidfImportance(singleColl);
		
		System.out.println("----- rank ------");
		for(ConfDiagnosisOutput rankedOutput : rankedList) {
			System.out.println(rankedOutput);
			System.out.println(rankedOutput.getExplanations());
			System.out.println();
		}
	}
	
	public List<ConfDiagnosisEntity> getCreateDiagnosisEntity() {
		PredicateProfileTuple goodRun = getGoodRun();
		PredicateProfileTuple badRun = getBadRun();
		ConfEntityRepository repo = TestConfEntityRepository.getSampleConfEntityRepository();
		List<ConfDiagnosisEntity>  entities = PredicateProfileBasedDiagnoser.summarizeDiagnosisEntity(goodRun, badRun, repo);
		for(ConfDiagnosisEntity entity : entities) {
			entity.computeAllScores();
			System.out.println(entity);
		}
		assertEquals(12, entities.size());
		return entities;
	}

	public PredicateProfileTuple getGoodRun() {
		ConfEntityRepository repo = TestConfEntityRepository.getSampleConfEntityRepository();
		
		PredicateProfile p1 = new PredicateProfile("class-name1.conf-option1", "context1", 100, 80);
		ConfDiagnosisEntity e1
		    = new ConfDiagnosisEntity(p1);
		PredicateProfile p2 = new PredicateProfile("class-name2.conf-option2", "context2", 5, 4);
		ConfDiagnosisEntity e2
	        = new ConfDiagnosisEntity(p2);
		PredicateProfile p3 = new PredicateProfile("class-name3.conf-option3", "context3", 1, 0);
		ConfDiagnosisEntity e3
	        = new ConfDiagnosisEntity(p3);
		PredicateProfile p4 = new PredicateProfile("class-name4.conf-option4", "context4", 25, 20);
		ConfDiagnosisEntity e4
	        = new ConfDiagnosisEntity(p4);
		
		PredicateProfile p7 = new PredicateProfile("class-name2.conf-option2", "context4", 40, 20);
		ConfDiagnosisEntity e7
	        = new ConfDiagnosisEntity(p7);
		PredicateProfile p8 = new PredicateProfile("class-name3.conf-option3", "context1", 25, 5);
		ConfDiagnosisEntity e8
	        = new ConfDiagnosisEntity(p8);
		PredicateProfile p9 = new PredicateProfile("class-name4.conf-option4", "context1", 50, 15);
		ConfDiagnosisEntity e9
	        = new ConfDiagnosisEntity(p9);
		
		e1.setConfEntity(repo);
		e2.setConfEntity(repo);
		e3.setConfEntity(repo);
		e4.setConfEntity(repo);
		e7.setConfEntity(repo);
		e8.setConfEntity(repo);
		e9.setConfEntity(repo);
		
		assertNotNull(e1.getConfEntity());
		assertNotNull(e2.getConfEntity());
		assertNotNull(e3.getConfEntity());
		assertNotNull(e4.getConfEntity());
		assertNotNull(e7.getConfEntity());
		assertNotNull(e8.getConfEntity());
		assertNotNull(e9.getConfEntity());
		
		System.out.println(e1.toString());
		System.out.println(e2.toString());
		System.out.println(e3.toString());
		System.out.println(e4.toString());
		System.out.println(e7.toString());
		System.out.println(e8.toString());
		System.out.println(e9.toString());
		
		PredicateProfileTuple goodTuple = PredicateProfileTuple.createGoodRun("good-run", Arrays.asList(p1, p2, p3, p4, p7, p8, p9));
		
		return goodTuple;
	}
	
	public PredicateProfileTuple getBadRun() {
		ConfEntityRepository repo = TestConfEntityRepository.getSampleConfEntityRepository();
		
		PredicateProfile p1 = new PredicateProfile("class-name1.conf-option1", "context1", 100, 30);
		ConfDiagnosisEntity e1
		    = new ConfDiagnosisEntity(p1);
		PredicateProfile p2 = new PredicateProfile("class-name2.conf-option2", "context2", 5, 0);
		ConfDiagnosisEntity e2
	        = new ConfDiagnosisEntity(p2);
		
		PredicateProfile p4 = new PredicateProfile("class-name4.conf-option4", "context4", 25, 20);
		ConfDiagnosisEntity e4
	        = new ConfDiagnosisEntity(p4);
		PredicateProfile p5 = new PredicateProfile("class-name5.conf-option5", "context5", 4, 0);
		ConfDiagnosisEntity e5
	        = new ConfDiagnosisEntity(p5);
		PredicateProfile p6 = new PredicateProfile("class-name6.conf-option6", "context6", 1, 0);
		ConfDiagnosisEntity e6
	        = new ConfDiagnosisEntity(p6);
		
		PredicateProfile p10 = new PredicateProfile("class-name4.conf-option4", "context6", 6, 5);
		ConfDiagnosisEntity e10
	        = new ConfDiagnosisEntity(p10);
		PredicateProfile p11 = new PredicateProfile("class-name6.conf-option6", "context2", 30, 15);
		ConfDiagnosisEntity e11
	        = new ConfDiagnosisEntity(p11);
		PredicateProfile p12 = new PredicateProfile("class-name6.conf-option6", "context3", 80, 60);
		ConfDiagnosisEntity e12
	        = new ConfDiagnosisEntity(p12);
		
		e1.setConfEntity(repo);
		e2.setConfEntity(repo);
		e4.setConfEntity(repo);
		e5.setConfEntity(repo);
		e6.setConfEntity(repo);
		e10.setConfEntity(repo);
		e11.setConfEntity(repo);
		e12.setConfEntity(repo);
		
		assertNotNull(e1.getConfEntity());
		assertNotNull(e2.getConfEntity());
		assertNotNull(e4.getConfEntity());
		assertNotNull(e5.getConfEntity());
		assertNotNull(e6.getConfEntity());
		assertNotNull(e10.getConfEntity());
		assertNotNull(e11.getConfEntity());
		assertNotNull(e12.getConfEntity());
		
		System.out.println(e1.toString());
		System.out.println(e2.toString());
		System.out.println(e4.toString());
		System.out.println(e5.toString());
		System.out.println(e6.toString());
		System.out.println(e10.toString());
		System.out.println(e11.toString());
		System.out.println(e12.toString());
		
		PredicateProfileTuple badTuple = PredicateProfileTuple.createBadRun("bad-run", Arrays.asList(p1, p2, p4, p5, p6, p10, p11, p12));
		
		return badTuple;
	}
	
	public static void saveRawData(ConfDiagnosisEntity e, PredicateProfile goodProfile, PredicateProfile badProfile) {
		if(goodProfile != null) {
			e.saveRawData(RawDataType.GOOD_EVAL_COUNT, goodProfile.getEvaluatingCount());
			e.saveRawData(RawDataType.GOOD_ENTER_COUNT, goodProfile.getEnteringCount());
			e.saveRawData(RawDataType.GOOD_IMPORT, goodProfile.importanceValue());
			e.saveRawData(RawDataType.GOOD_IMPORT_ABS, goodProfile.absImportanceValue());
			e.saveRawData(RawDataType.GOOD_RATIO, goodProfile.getRatio());
			e.saveRawData(RawDataType.GOOD_RATIO_ABS, goodProfile.absoluteRatio());
		}
		if(badProfile != null) {
			e.saveRawData(RawDataType.BAD_EVAL_COUNT, badProfile.getEvaluatingCount());
			e.saveRawData(RawDataType.BAD_ENTER_COUNT, badProfile.getEnteringCount());
			e.saveRawData(RawDataType.BAD_IMPORT, badProfile.importanceValue());
			e.saveRawData(RawDataType.BAD_IMPORT_ABS, badProfile.absImportanceValue());
			e.saveRawData(RawDataType.BAD_RATIO, badProfile.getRatio());
			e.saveRawData(RawDataType.BAD_RATIO_ABS, badProfile.absoluteRatio());
		}
		
//		public enum RawDataType{GOOD_EVAL_COUNT, GOOD_ENTER_COUNT, GOOD_IMPORT, GOOD_RATIO, GOOD_RATIO_ABS, GOOD_IMPORT_ABS,
//			BAD_EVAL_COUNT, BAD_ENTER_COUNT, BAD_IMPORT, BAD_RATIO, BAD_RATIO_ABS, BAD_IMPORT_ABS};
		
	}
	
}
