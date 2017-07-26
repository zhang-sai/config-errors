package edu.washington.cs.conf.diagnosis;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.washington.cs.conf.analysis.ConfEntity;
import edu.washington.cs.conf.analysis.ConfPropOutput;
import edu.washington.cs.conf.util.Utils;

/**
 * An abstract class for baseline comparison. Not for our own technique.
 * */
public abstract class AbstractBaselineDiagnoser {
    public final Collection<ConfPropOutput> confs;
	
	//this can be a statement signature map or a method signature map
    //note it contains daikon-style method:
    //    randoop.util.ListOfLists.ListOfLists(randoop.util.SimpleList[])
    //    randoop.util.MethodReflectionCode.MethodReflectionCode(java.lang.reflect.Method, java.lang.Object, java.lang.Object[])
	public final Map<String, Float> sigMap;
	
	public AbstractBaselineDiagnoser(Collection<ConfPropOutput> confs,
			Map<String, Float> sigMap) {
		this.confs = confs;
//		this.stmts = stmts;
		this.sigMap = Utils.sortByValue(sigMap, false);
	}
	
	public abstract List<ConfEntity> computeResponsibleOptions();
}
