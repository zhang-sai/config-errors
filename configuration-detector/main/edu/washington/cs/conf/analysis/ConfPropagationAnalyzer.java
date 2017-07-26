package edu.washington.cs.conf.analysis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.ipa.cha.ClassHierarchy;

import edu.washington.cs.conf.analysis.ConfigurationSlicer.CG;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

/**
 * Take a list of configuration options in fields, and compute
 * its propagated places.
 * 
 * Output the affected statements of each configuration
 * */
public class ConfPropagationAnalyzer {
	
	public static boolean verbose = true;

	public final List<ConfEntity> confs;
	public final String analysisPath;
	public final String mainClass;
	
	private CG type = null;
	private String exclusionFile = null;
	
	/**
	 * Options are in the form of fullclassname.fieldname
	 * */
	public ConfPropagationAnalyzer(List<String> options, List<String> assignMethods,
			List<Boolean> isStatics, String analysisPath, String mainClass) {
		this.confs = new LinkedList<ConfEntity>();
		if(assignMethods != null) {
			Utils.checkTrue(options.size() == assignMethods.size());
		}
		Utils.checkNotNull(isStatics);
		Utils.checkTrue(options.size() == isStatics.size());
		//create the ConfEntity
		for(int i = 0; i < options.size(); i++) {
			String option = options.get(i);
			Boolean isStatic = isStatics.get(i);
			String className = Utils.extractClassName(option);
			String elementName = Utils.extractElementName(option);
			String affMethod = assignMethods == null ? null : assignMethods.get(i);
			ConfEntity conf = new ConfEntity(className, elementName, affMethod, isStatic);
			this.confs.add(conf);
		}
		//init app path and main class
		this.analysisPath = analysisPath;
		this.mainClass = mainClass;
	}
	
	public void setCGType(CG t) {
		this.type = t;
	}
	
	public void setExclusionFile(String file) {
		this.exclusionFile = file;
	}
	
	public List<ConfPropOutput> doAnalysis() {
		List<ConfPropOutput> outputList = new LinkedList<ConfPropOutput>();
		
		ConfigurationSlicer helper = new ConfigurationSlicer(this.analysisPath, this.mainClass);
		if(this.type != null) {
			helper.setCGType(type);
		}
		if(this.exclusionFile != null) {
			helper.setExclusionFile(exclusionFile);
		}
		//build call graph and pointer to analysis
		long startTime = System.currentTimeMillis();
		helper.buildAnalysis();
		if(verbose) {
			System.out.println("Time used in building CG: "
					+ (System.currentTimeMillis() - startTime)/1000 + " seconds.");
		}
		//check the existence of conf
		this.checkConfigExistence(helper.getClassHierarchy(), this.confs);
		//analyze each conf one by one
		for(ConfEntity e : this.confs) {
			ConfPropOutput output = helper.outputSliceConfOption(e);
			outputList.add(output);
		}
		//output the list
		return outputList;
	}
	
	private void checkConfigExistence(ClassHierarchy cha, List<ConfEntity> confs) {
		for(ConfEntity conf : confs) {
			String className = conf.getClassName();
			String fieldName = conf.getConfName();
			IClass c = WALAUtils.lookupClass(cha, className);
			Utils.checkNotNull(c, "Wrong conf: " + conf);
			Collection<IField> fields = c.getAllFields();
			boolean exist = false;
			for(IField f : fields) {
				if(f.getName().toString().equals(fieldName)) {
					exist = true;
					break;
				}
			}
			Utils.checkTrue(exist, "Field not exist in: " + conf);
		}
	}
	
}
