package edu.washington.cs.conf.analysis.evol;

import com.ibm.wala.classLoader.IClass;

import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class AnalysisScope {

	private String[] pkgs = null;
	
	public void setScopePackages(String[] pkgs) {
		Utils.checkNotNull(pkgs);
		this.pkgs = pkgs;
	}
	
	public boolean isInScope(IClass clz) {
		String pkgName = WALAUtils.getJavaPackageName(clz);
		if(pkgs == null) {
			return true;
		}
		if(Utils.startWith(pkgName, this.pkgs)) {
			return true;
		}
		return false;
	}
	
}