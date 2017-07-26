package edu.washington.cs.conf.mutation;

import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.ibm.wala.shrikeBT.ConstantInstruction;
import com.ibm.wala.shrikeBT.Constants;
import com.ibm.wala.shrikeBT.IInstruction;
import com.ibm.wala.shrikeBT.MethodData;
import com.ibm.wala.shrikeBT.MethodEditor;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;

import edu.washington.cs.conf.instrument.AbstractInstrumenter;
import edu.washington.cs.conf.util.Utils;

//this class inspects the bytecode of a jar file, and
//outputs all string constants in each class
public class ConstantMsgFetcher {

	public Collection<String> findAllStringMsg(String inputJar) throws Exception {
		StringFetcher fetcher = new StringFetcher();
		fetcher.simpleTraverse(inputJar);
		Set<String> msgs = fetcher.allStrings;
		return msgs;
	}
	
}

class StringFetcher extends AbstractInstrumenter {
	
	Set<String> allStrings = new HashSet<String>();
	
	public Set<String> getAllStrings() {
		return this.allStrings;
	}

	@Override
	protected void doClass(ClassInstrumenter ci, Writer w) throws Exception {
		// TODO Auto-generated method stub
		for (int m = 0; m < ci.getReader().getMethodCount(); m++) {
		      MethodData d = ci.visitMethod(m);
		      // d could be null, e.g., if the method is abstract or native
		      if (d != null) {

		        MethodEditor me = new MethodEditor(d);
		        me.beginPass();
		        
		        int length = me.getInstructions().length;
		        for(int i = 0; i < length; i++) {
		        	IInstruction inst = me.getInstructions()[i];
		        	if(inst instanceof ConstantInstruction) {
		        		ConstantInstruction instruction = (ConstantInstruction)inst;
		        		String typeName = instruction.getType();
		        		if(typeName.equals(Constants.TYPE_String)) {
		        		    Object value = instruction.getValue();
		        		    if(value != null) {
//		        		    	System.out.println(value);
		        		        Utils.checkTrue(value instanceof String);
		        		        if(addStringConst(value.toString())) {
		        		            allStrings.add(value.toString().trim());
		        		        }
		        		    }
		        		}
		        	}
		        }
		    }
	    }
	}
	
	private boolean addStringConst(String str) {
		if(str.trim().equals("") || str.length() < 2) {
			return false;
		}
		if(noAlphaChar(str)) {
			return false;
		}
		return true;
	}
	
	private boolean noAlphaChar(String str) {
		for(char c : str.toCharArray()) {
			if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args) throws Exception {
		ConstantMsgFetcher fetcher = new ConstantMsgFetcher();
		String inputJar = "./evoltracer.jar";
		Collection<String> set = fetcher.findAllStringMsg(inputJar);
		for(String str : set) {
			System.out.println(str);
		}
	}
}