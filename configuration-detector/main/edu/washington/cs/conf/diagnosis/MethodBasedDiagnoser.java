package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;

/**
 * This class is for experimental comparison with method-level invariant
 * analysis. The basic idea is: observe the invariant difference between
 * a good run and a bad run, and find out all methods that have different
 * invariants. Then, identify configurations that could affect such methods.
 * 
 * Compared to the configuration profiling, this method is at a coarser
 * granularity.
 * */
public class MethodBasedDiagnoser extends AbstractBaselineDiagnoser {

	public MethodBasedDiagnoser(Collection<ConfPropOutput> confs,
			Map<String, Float> sigMap) {
		super(confs, sigMap);
	}

	@Override
	public List<ConfEntity> computeResponsibleOptions() {
		List<ConfEntity> entityList = new LinkedList<ConfEntity>();
		
		for(String daikonMethod : this.sigMap.keySet()) {
			List<ConfEntity> confs = this.findConfEntities(daikonMethod, this.confs);
			entityList.addAll(confs);
		}
		
		return entityList;
	}
	
	private List<ConfEntity> findConfEntities(String daikonMethod,
			Collection<ConfPropOutput> confs) {
		List<ConfEntity> matched = new LinkedList<ConfEntity>();
		
		for(ConfPropOutput conf : confs) {
			if(conf.findStatementByDaikonStyleMethod(daikonMethod)) {
				matched.add(conf.getConfEntity());
			}
		}
		
		return matched;
	}
	
	/**
	 * integrate with Daikon
	 * */
	public static List<ConfEntity> computeResponsibleOptions(Collection<String> goodInvFiles, String badInvFile,
			Collection<ConfPropOutput> confs) {
		//get method scores
		InvariantDiffAnalyzer analyzer = new InvariantDiffAnalyzer(goodInvFiles, badInvFile);
		Map<String, Float> scores = analyzer.getMethodsWithDiffInvariants();
		
		for(String s : scores.keySet()) {
			System.out.println(" = " + s + ", " + scores.get(s));
		}
		
		//find responsbile options
		MethodBasedDiagnoser diagnoser = new MethodBasedDiagnoser(confs, scores);
		List<ConfEntity> entityList = diagnoser.computeResponsibleOptions();
		
		List<ConfEntity> finalList = new LinkedList<ConfEntity>();
		Set<String> entityStrs = new LinkedHashSet<String>();
		for(ConfEntity e : entityList) {
			if(entityStrs.contains(e.toString())) {
				continue;
			}
			finalList.add(e);
			entityStrs.add(e.toString());
		}
		
		return finalList;
	}
}
