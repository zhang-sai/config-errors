package edu.washington.cs.conf.analysis;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.slicer.NormalStatement;
import com.ibm.wala.ipa.slicer.Statement;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAInstruction;

import edu.washington.cs.conf.diagnosis.PredicateProfile;
import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

public class ConfUtils {

	
	public static String sep = ";";
	
	public static Collection<ConfEntity> parseConfEntities(String fileName) {
		Collection<ConfEntity> all = new LinkedHashSet<ConfEntity>();
		List<String> lines = Files.readWholeNoExp(fileName);
		for(String line : lines) {
			if(line.trim().isEmpty()) {
				continue;
			}
			ConfEntity entity = parseConfEntity(line);
			if(all.contains(entity)) {
				System.err.println("Duplicated entity: " + entity);
			} else {
				all.add(entity);
			}
		}
		return all;
	}
	
	public static ConfEntity parseConfEntity(String line) {
		String[] splits = line.split(sep);
		Utils.checkTrue(splits.length == 4, "Incorrect length: " + splits.length);
		String className = splits[0].trim();
		String confName = splits[1].trim();
		String affMethod = splits[2].trim().isEmpty() ? null : splits[2].trim();
		boolean isStatic = Boolean.parseBoolean(splits[3].trim());
		return new ConfEntity(className, confName, affMethod, isStatic);
	}
	
	//all this method does is to set the field source line number / source text in each PredicateProfile
	public static void setUpLineNumberAndSource(String sourceDir,
			Collection<ConfPropOutput> propOutputs, Collection<PredicateProfile> profiles) {
		for(PredicateProfile profile : profiles) {
//			int srcLineNum = -1;
//			String srcText = "NOT_SET_UP_IN_ConfUtils";
			
			String configName = profile.getConfigFullName();
			ConfPropOutput output = findConfPropOutputByConfName(propOutputs, configName);
			
			if(output == null) {
				continue;
			}
			
			Utils.checkNotNull(output, "configName: " + configName);
			
			IRStatement irs = output.getStatement(profile.getMethodSig(), profile.getInstructionIndex());
			
			if(irs == null) {
				profile.setSourceLineNumber(-1);
				profile.setPredicateInSource("Unavailable from source: " + profile.getMethodSig() + ", " + profile.getInstructionIndex());
				continue;
			}
			
			Utils.checkNotNull(irs, "profile: " + profile.getMethodSig());
			String fullClassName = irs.getDeclaringFullClassName();
			
			int srcLineNum = irs.getLineNumber();
			String srcText = Files.fetchLineInFile(sourceDir, fullClassName, srcLineNum);
			
			profile.setSourceLineNumber(srcLineNum);
			profile.setPredicateInSource(srcText);
		}
	}
	
	public static ConfPropOutput findConfPropOutputByConfName(Collection<ConfPropOutput> propOutputs,
			String fullConfigName) {
		for(ConfPropOutput output : propOutputs) {
			if(output.getConfEntity().getFullConfName().equals(fullConfigName)) {
				return output;
			}
		}
		return null;
	}
	
	/**
	 * FIXME
	 * may not be a right place
	 * */
	public static String extractFullClassName(String methodSig) {
		return methodSig.substring(0, methodSig.lastIndexOf("."));
	}
	
	/**
	 * Removes all statements corresponding to the same statement
	 * */
	public static Set<IRStatement> removeSameStmtsInDiffContexts(Set<IRStatement> stmts) {
		Set<String> existed = new LinkedHashSet<String>();
		Set<IRStatement> filtered = new LinkedHashSet<IRStatement>();
		for(IRStatement stmt : stmts) {
			String sig = stmt.getUniqueSignature(); //method name + instruction string + instruction index
			if(existed.contains(sig)) {
				continue;
			}
			existed.add(sig);
			filtered.add(stmt);
		}
		return filtered;
	}
	
	private static Map<ConfEntity, Collection<Statement>> cachedStmts = new LinkedHashMap<ConfEntity, Collection<Statement>>();
	public static void buildCachedStatements(Collection<ConfEntity> entities, CallGraph cg, String[] pkgs) {
		Utils.checkTrue(pkgs.length > 0);
		
		Map<ConfEntity, Collection<Statement>> confStmts = new LinkedHashMap<ConfEntity, Collection<Statement>>();
		for(ConfEntity entity : entities) {
			confStmts.put(entity, new LinkedHashSet<Statement>());
		}
		//build the cache
		for(CGNode node : cg) {
			if(node.getIR() == null) {
				continue;
			}
			String fullClassName = WALAUtils.getFullMethodName(node.getMethod());
			if(!Utils.startWith(fullClassName, pkgs)) {
				continue;
			}
			Iterator<SSAInstruction> iter = node.getIR().iterateAllInstructions();
			while(iter.hasNext()) {
				SSAInstruction ssa = iter.next();
				if(ssa instanceof SSAGetInstruction) {
					SSAGetInstruction ssaGet = (SSAGetInstruction)ssa;
					//quick and dirty hack
					String sig = ssaGet.toString();
					//check each conf entity
					for(ConfEntity entity : entities) {
						String confClassName = entity.getClassName();
						String confName = entity.getConfName();
						String jvmClassName = Utils.translateDotToSlash(confClassName);
						if(sig.indexOf(confName) != -1 && sig.indexOf(jvmClassName) != -1) {
							Statement s = new NormalStatement(node, WALAUtils.getInstructionIndex(node, ssa));
							confStmts.get(entity).add(s);
						}
					}
				}
			}
		}
		//add to the cache
		cachedStmts.putAll(confStmts);
	}
	
	public static Collection<Statement> getextractAllGetStatements(ConfEntity entity, CallGraph cg) {
		if(cachedStmts.containsKey(entity)) {
			return cachedStmts.get(entity);
		}
		throw new Error("Not built: " + entity);
	}
}