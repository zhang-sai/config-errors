package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.instrument.AbstractInstrumenter;

/**
 * The code for experimental comparison with statement-level profiling.
 * The idea is: first collect all statement executed by good run and bad
 * run, and rank thhose statements based on the Tarantula metrics. For
 * each ranked statement, find out which configuration may affect them.
 * */
public class StmtCoverageBasedDiagnoser extends AbstractBaselineDiagnoser {
	
    //all statement executed
//	public final Collection<StmtExecuted> stmts;
	
	public StmtCoverageBasedDiagnoser(Collection<ConfPropOutput> confs,
//			Collection<StmtExecuted> stmts, 
			Map<String, Float> sigMap) {
		super(confs, sigMap);
//		this.stmts = stmts;
	}

	@Override
	public List<ConfEntity> computeResponsibleOptions() {
		List<ConfEntity> entityList = new LinkedList<ConfEntity>();
		
		for(String stmtSig : this.sigMap.keySet()) {
			String[] splits = stmtSig.split(AbstractInstrumenter.SEP);
			String methodSig = splits[0];
			int instructionIndex = Integer.parseInt(splits[2]);
			//find all configuration that is responsible for it.
			List<ConfEntity> options = this.findConfEntities(methodSig, instructionIndex, this.confs);
			entityList.addAll(options);
			
			System.out.println("size: " + entityList.size());
			if(entityList.size() > 2600) {
				return entityList;
			}
		}
		
		return entityList;
	}
	
	private List<ConfEntity> findConfEntities(String methodSig, int instructionIndex,
			Collection<ConfPropOutput> confs) {
		List<ConfEntity> matched = new LinkedList<ConfEntity>();
		
		for(ConfPropOutput conf : confs) {
			if(conf.containStatement(methodSig, instructionIndex)) {
				matched.add(conf.getConfEntity());
			}
		}
		
		return matched;
	}
}
