package edu.washington.cs.conf.analysis.evol;

import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAInvokeInstruction;
import com.ibm.wala.ssa.SSANewInstruction;

public class SSAFilter {

	/**
	 * True means filter this instruction. Used to compute
	 * iterative slicing, deciding whether an instruction should
	 * be a seed statement or not.
	 * */
	public static boolean filterSSAForSlicing(SSAInstruction ssa) {
		if(ssa instanceof SSAInvokeInstruction) {
			SSAInvokeInstruction ssaInvoke = (SSAInvokeInstruction)ssa;
			String signature = ssaInvoke.getDeclaredTarget().getSignature();
			if(signature.startsWith("java.lang.StringBuilder")
					|| signature.startsWith("java.lang.System")
					|| signature.startsWith("java.lang.Class")
					|| signature.startsWith("java.lang.Object")
					|| signature.startsWith("java.lang.Method")) {
				return true;
			}
		} else if (ssa instanceof SSANewInstruction) {
			SSANewInstruction newSSA = (SSANewInstruction)ssa;
			String typeName = newSSA.getConcreteType().getName().toString();
			if(typeName.equals("Ljava/lang/StringBuilder")
					|| typeName.equals("Ljava/lang/String")) {
				return true;
			}
		} else if (ssa instanceof SSAGetInstruction) {
			SSAGetInstruction ssaGet = (SSAGetInstruction)ssa;
//			if(ssaGet.getDeclaredField().getDeclaringClass())
			String declareClass = ssaGet.getDeclaredField().getDeclaringClass().getName().toString();
			if(declareClass.equals("Ljava/lang/System")) {
				return true;
			}
		}
		return false;
	}
	
}