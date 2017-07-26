package edu.washington.cs.conf.analysis;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import edu.washington.cs.conf.util.Utils;


public class ConfEntityRepository {

	public final Collection<ConfEntity> entities = new LinkedHashSet<ConfEntity>();
	
	public ConfEntityRepository(ConfEntity[] entities) {
		this(Arrays.asList(entities));
	}
	
	public ConfEntityRepository(Collection<ConfEntity> entities) {
		this.entities.addAll(entities);
		if(this.entities.size() != entities.size()) {
			System.err.println("Warning, size not equal. Given: "
					+ entities.size() + ", but result in: " + this.entities.size());
		}
	}
	
	public int size() {
		return entities.size();
	}
	
	public List<ConfEntity> getConfEntityList() {
		List<ConfEntity> list = new LinkedList<ConfEntity>();
		list.addAll(entities);
		return list;
	}
	
	public ConfEntity lookupConfEntity(String fullName) {
		for(ConfEntity entity : entities) {
			if(entity.getFullConfName().equals(fullName)) {
				return entity;
			}
		}
		return null;
	}
	
	public void initializeTypesInConfEntities(String path) {
		for(ConfEntity entity : entities) {
			String fullClassName = entity.getClassName();
			String fieldName = entity.getConfName();
			boolean isStatic = entity.isStatic();
			
			Class<?> clz = Utils.loadclass(path, fullClassName);
			Utils.checkNotNull(clz, "full class name: " + fullClassName);
			Field f = Utils.lookupField(clz, fieldName);
			Utils.checkNotNull(f);
			
			boolean isFieldStatic = Modifier.isStatic(f.getModifiers());
			Utils.checkTrue(isStatic == isFieldStatic, "f is: " + f);
			
			entity.setType(f.getType().getName());
		}
	}
	
	public void showAll() {
		System.out.println(this.size() + " options:");
		for(ConfEntity entity : this.getConfEntityList()) {
			System.out.println("  " + entity.toString());
		}
	}
}
