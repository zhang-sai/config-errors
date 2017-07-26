package edu.washington.cs.conf.experiments;

import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;

public class WekaExpUtils {
	
	public static ConfEntityRepository getWekaRepository() {
		List<ConfEntity> wekaConfList = WekaExpUtils.getWekaConfList();
		ConfEntityRepository repo = new ConfEntityRepository(wekaConfList);
		return repo;
	}
	
	public static List<ConfEntity> getWekaConfList() {
		ConfEntity entity2 = new ConfEntity("weka.classifiers.trees.J48", "m_unpruned", false);
		ConfEntity entity3 = new ConfEntity("weka.classifiers.trees.J48", "m_CF", false);
		ConfEntity entity4 = new ConfEntity("weka.classifiers.trees.J48", "m_minNumObj", false);
		ConfEntity entity5 = new ConfEntity("weka.classifiers.trees.J48", "m_useLaplace", false);
		ConfEntity entity6 = new ConfEntity("weka.classifiers.trees.J48", "m_reducedErrorPruning", false);
		ConfEntity entity7 = new ConfEntity("weka.classifiers.trees.J48", "m_numFolds", false);
		ConfEntity entity8 = new ConfEntity("weka.classifiers.trees.J48", "m_binarySplits", false);
		ConfEntity entity9 = new ConfEntity("weka.classifiers.trees.J48", "m_subtreeRaising", false);
		ConfEntity entity10 = new ConfEntity("weka.classifiers.trees.J48", "m_noCleanup", false);
		ConfEntity entity11 = new ConfEntity("weka.classifiers.trees.J48", "m_Seed", false);
		
		ConfEntity entity12 = new ConfEntity("weka.classifiers.trees.j48.C45PruneableClassifierTree", "m_pruneTheTree", false);
		ConfEntity entity13 = new ConfEntity("weka.classifiers.trees.j48.C45PruneableClassifierTree", "m_CF", false);
		ConfEntity entity14 = new ConfEntity("weka.classifiers.trees.j48.C45PruneableClassifierTree", "m_subtreeRaising", false);
		ConfEntity entity15 = new ConfEntity("weka.classifiers.trees.j48.C45PruneableClassifierTree", "m_cleanup", false);
//		ConfEntity entity13 = new ConfEntity("weka.classifiers.trees.J48", "m_noCleanup", false);
		
		
		List<ConfEntity> list = new LinkedList<ConfEntity>();
//		list.add(entity1);
		list.add(entity2);
		list.add(entity3);
		list.add(entity4);
		list.add(entity5);
		list.add(entity6);
		list.add(entity7);
		list.add(entity8);
		list.add(entity9);
		list.add(entity10);
		list.add(entity11);
		list.add(entity12);
		list.add(entity13);
		list.add(entity14);
		list.add(entity15);
		
		return list;
	}
	
}