package edu.washington.cs.conf.analysis;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.LinkedList;

import edu.washington.cs.conf.instrument.InstrumentSchema;

public class ConfOutputSerializer {
	
	public static InstrumentSchema deserializeAsSchema(String fileName) {
		InstrumentSchema schema = null;
		FileInputStream fis;
		ObjectInputStream in;
		try {
		    fis = new FileInputStream(fileName);
		    in = new ObjectInputStream(fis);
		    schema = InstrumentSchema.readFromFile(in);
		    in.close();
			fis.close();
		    return schema;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
//	public static void serializeAsSchema(Collection<ConfPropOutput> outputs, String fileName) {
//		InstrumentSchema schema = new InstrumentSchema();
//		schema.addInstrumentationPoint(outputs);
//		serializeSchema(schema, fileName);
//	}
	
	public static void serializeSchema(InstrumentSchema schema, String fileName) {
		try {
			FileOutputStream fos = new FileOutputStream (fileName);
			ObjectOutputStream out = new ObjectOutputStream (fos);
			//write to the file
			schema.writeToFile(out);
			out.close();
			fos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void writeToFileAsText(InstrumentSchema schema, String fileName) {
		try {
			BufferedWriter writer= new BufferedWriter(new FileWriter(fileName));
			schema.saveToFileAsText(writer);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}