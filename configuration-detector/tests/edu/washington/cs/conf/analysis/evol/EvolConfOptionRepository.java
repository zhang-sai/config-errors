package edu.washington.cs.conf.analysis.evol;

import java.util.Collection;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfEntityRepository;

public class EvolConfOptionRepository {
	
	private static final String optionDir = "./tests/edu/washington/cs/conf/analysis/evol/experiments/";
	private static final String sliceCacheDir = "./evol-experiments/slicing-cache/";
	
	public static final String randoopOldOptionFile = optionDir + "randoop-old-options.txt";
	public static String randoopOldCacheFile = sliceCacheDir + "randoop-old-slice.dat";
	public static ConfEntityRepository randoopOldConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(randoopOldOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String randoopNewOptionFile = optionDir + "randoop-new-options.txt";
	public static String randoopNewCacheFile = sliceCacheDir + "randoop-new-slice.dat";
	public static ConfEntityRepository randoopNewConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(randoopNewOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String wekaOldOptionFile = optionDir + "weka-old-options.txt";
	public static String wekaOldCacheFile = sliceCacheDir + "weka-old-slice.dat";
	public static ConfEntityRepository wekaOldConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(wekaOldOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String wekaNewOptionFile = optionDir + "weka-new-options.txt";
	public static String wekaNewCacheFile = sliceCacheDir + "weka-new-slice.dat";
	public static ConfEntityRepository wekaNewConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(wekaNewOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String synopticOldOptionFile = optionDir + "synoptic-old-options.txt";
	public static String synopticOldCacheFile = sliceCacheDir + "synoptic-old-slice.dat";
	public static ConfEntityRepository synopticOldConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(synopticOldOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String synopticNewOptionFile = optionDir + "synoptic-new-options.txt";
	public static String synopticNewCacheFile = sliceCacheDir + "synoptic-new-slice.dat";
	public static ConfEntityRepository synopticNewConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(synopticNewOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String jmeterOldOptionFile = optionDir + "jmeter-old-options.txt";
	public static final String jmeterOldCacheFile = sliceCacheDir + "jmeter-old-slice.dat";
	public static ConfEntityRepository jmeterOldConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(jmeterOldOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String jmeterNewOptionFile = optionDir + "jmeter-new-options.txt";
	public static final String jmeterNewCacheFile = sliceCacheDir + "jmeter-new-slice.dat";
	public static ConfEntityRepository jmeterNewConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(jmeterNewOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String jchordOldOptionFile = optionDir + "jchord-old-options.txt";
	public static String jchordOldCacheFile = sliceCacheDir + "jchord-old-slice.dat";
	public static ConfEntityRepository jchordOldConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(jchordOldOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String jchordNewOptionFile = optionDir + "jchord-new-options.txt";;
	public static String jchordNewCacheFile = sliceCacheDir + "jchord-new-slice.dat";
	public static ConfEntityRepository jchordNewConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(jchordNewOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String javalancheOldOptionFile = optionDir + "javalanche-old-options.txt";
	public static final String javalancheOldCacheFile = sliceCacheDir + "javalanche-old-slices.dat";
	public static ConfEntityRepository javalancheOldConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(javalancheOldOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	public static final String javalancheNewOptionFile = optionDir + "javalanche-new-options.txt";
	public static final String javalancheNewCacheFile = sliceCacheDir + "javalanche-new-slices.dat";
	public static ConfEntityRepository javalancheNewConfs() {
		Collection<ConfEntity> entities = ConfEntity.readConfigOptionsFromFile(javalancheNewOptionFile);
		return new ConfEntityRepository(entities);
	}
	
	//the following is for experimental uses
	public static final String randoopOldVersionFullSlicing = sliceCacheDir + "randoop-old-full-slices.dat";
	public static final String randoopNewVersionFullSlicing = sliceCacheDir + "randoop-new-full-slices.dat";
	public static final String wekaOldVersionFullSlicing = sliceCacheDir + "weka-old-full-slices.dat";
	public static final String wekaNewVersionFullSlicing = sliceCacheDir + "weka-new-full-slices.dat";
	public static final String jchordOldVersionFullSlicing = sliceCacheDir + "jchord-old-full-slices.dat";
	public static final String jchordNewVersionFullSlicing = sliceCacheDir + "jchord-new-full-slices.dat";
	public static final String synopticOldVersionFullSlicing = sliceCacheDir + "synoptic-old-full-slices.dat";
	public static final String synopticNewVersionFullSlicing = sliceCacheDir + "synoptic-new-full-slices.dat";
	public static final String jmeterOldVersionFullSlicing = sliceCacheDir + "jmeter-old-full-slices.dat";
	public static final String jmeterNewVersionFullSlicing = sliceCacheDir + "jmeter-new-full-slices.dat";
	public static final String javalancheOldVersionFullSlicing = sliceCacheDir + "javalanche-old-full-slices.dat";
	public static final String javalancheNewVersionFullSlicing = sliceCacheDir + "javalanche-new-full-slices.dat";
}
