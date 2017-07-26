package edu.washington.cs.conf.analysis.evol.experiments;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashSet;

import daikon.util.UtilMDE;

import junit.framework.TestCase;

import edu.washington.cs.conf.analysis.evol.CodeAnalyzerRepository;
import edu.washington.cs.conf.util.Utils;

public class PlumeOptionFinder extends TestCase {

	public void printAllOptions(String path, String[] classNames) {
		Collection<Class<?>> allClasses = new LinkedHashSet<Class<?>>();
		for(String className : classNames) {
		    Class<?> c = Utils.loadclass(path, className);
		    Utils.checkNotNull(c, className);
		    allClasses.add(c);
		}
		System.out.println(allClasses);
		for(Class<?> c : allClasses) {
			Field[] fields = c.getDeclaredFields();
			for(Field f : fields) {
				Annotation[] anns = f.getAnnotations();
				boolean hasOption = false;
				boolean isUnpub = false;
				for(Annotation ann : anns) {
//					System.out.println(ann);
					if(ann.toString().startsWith(("@utilMDE.Option"))) {
						hasOption = true;
					}
//					if(ann.toString().startsWith("@utilMDE.Invisible")) {
//						isUnpub = true;
//					}
					if(ann.toString().startsWith("@plume.Option")) {
						hasOption = true;
					}
				}
				if(hasOption && !isUnpub) {
//					System.out.println(f);
					System.out.print(f.getDeclaringClass().getName());
					System.out.print("#");
					System.out.print(f.getName());
					System.out.print("#");
					System.out.print(Modifier.isStatic(f.getModifiers()));
					System.out.println();
				}
			}
		}
	}
	
	public void testShowRandoopOld() {
		this.printAllOptions(CodeAnalyzerRepository.randoop121Path,
				new String[]{
				"cov.CovUtil",
				"randoop.ForwardGenerator",
				"randoop.Globals",
				"randoop.SequenceGeneratorStats",
				"randoop.main.GenInputsAbstract",
				"randoop.main.GenTests",
				"randoop.main.PrintStats",
				"randoop.util.Log",
				"randoop.util.ReflectionExecutor"
		});
	}
	
	public void testShowRandoopNew() {
		this.printAllOptions(CodeAnalyzerRepository.randoop132Path,
				new String[]{
				"randoop.AbstractGenerator",
				"randoop.experiments.CodeCoverageTracker",
				"randoop.experiments.CombineCovMaps",
				"randoop.experiments.CreateCovReport",
				"randoop.experiments.MultiMachineRunner",
				"randoop.experiments.PreDF",
				"randoop.experiments.RandoopAllClasses",
				"randoop.experiments.StatsWriter",
				"randoop.instrument.Premain",
				"randoop.main.GenInputsAbstract",
				"randoop.main.GenTests",
				"randoop.main.Help",
				"randoop.util.ReflectionExecutor"
		});
	}
	
	public void testShowSynopticOld() {
		this.printAllOptions(CodeAnalyzerRepository.oldSynopticPath,
				new String[]{
				"synoptic.main.SynopticOptions"
		});
	}

	public void testShowSynopticNew() {
		this.printAllOptions(CodeAnalyzerRepository.newSynopticPath,
				new String[]{
				"synoptic.main.SynopticOptions"
		});
	}
	
	public static void main(String[] args) {
		System.out.println(System.getProperty("test.colon"));
		String[] splits = System.getProperty("test.colon").split(" |,|:|;");
		for(String split : splits) {
			System.out.println(split);
		}
	}
}