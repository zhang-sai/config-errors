package edu.washington.cs.conf.analysis;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.slicer.Statement;

import edu.washington.cs.conf.util.Files;
import edu.washington.cs.conf.util.Globals;
import edu.washington.cs.conf.util.Utils;
import edu.washington.cs.conf.util.WALAUtils;

/**
 * Uniquely represent a point in Shrinke by ShrinkeMethod + instructionIndex
 * */
public class ShrikePoint implements Serializable {
	private static final long serialVersionUID = 3388049229274456039L;
	
	private final transient ShrikeBTMethod method;
	private final String methodSig; /*used for comparison after de-serialization*/
	
	public final int instructionIndex;
	
	//for debugging purpose
	public final int lineNum;
	public final int bcIndex;
	public final String instructStr;
	
	//keep the source text
	private String sourceText = null;
	
	public ShrikePoint(IRStatement ir) {
		Statement s = ir.getStatement();
		Utils.checkTrue(s.getNode().getMethod() instanceof ShrikeBTMethod);
		this.method = (ShrikeBTMethod) ir.getStatement().getNode().getMethod();
		this.methodSig = this.method.getSignature();
		this.instructionIndex = ir.getInstructionIndex();
		this.lineNum = ir.getLineNumber();
		this.bcIndex = ir.getBcIndex();
		instructStr = ir.getStatement().toString();
	}
	
	private ShrikePoint(int instructionIndex, int bcIndex,
			String methodSig) {
		this.instructionIndex = instructionIndex;
		this.bcIndex = bcIndex;
		this.methodSig = methodSig;
		//empty
		this.method = null;
		this.lineNum = -1;
		instructStr = null;
	}
	
	public static ShrikePoint createMockPoint(int instructionIndex,
			String methodSig) {
		return new ShrikePoint(instructionIndex, -1, methodSig);
	}
	
	public String getMethodSig() {
		return this.methodSig;
	}
	
	public String getInstructionStr() {
		return this.instructStr;
	}
	
	public int getInstructionIndex() {
		return this.instructionIndex;
	}
	
	public int getSourceLineNum() {
		return this.lineNum;
	}
	
	public void setSourceText(String sourceDir) {
		Utils.checkTrue(Files.checkDirExistence(sourceDir));
		String fullClassName = this.methodSig.substring(0, this.methodSig.lastIndexOf("."));
		String text = Files.fetchLineInFile(sourceDir, fullClassName, this.lineNum);
		this.sourceText = text;
	}
	
	public String getSourceText() {
		return this.sourceText;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ShrikePoint)) {
			return false;
		}
		ShrikePoint sp = (ShrikePoint)o;
		return this.methodSig.equals(sp.methodSig)
		    //this.method.equals(sp.method)
		    && this.instructionIndex == sp.instructionIndex;
	}
	
	@Override
	public int hashCode() {
//		return this.method.hashCode() + 13*this.instructionIndex;
		return this.methodSig.hashCode() + 13*this.instructionIndex;
	}
	
	@Override
	public String toString() {
		return "instruction index: " + this.instructionIndex
		    + "  line num: " + this.lineNum + " @ " + this.methodSig;
	}
}