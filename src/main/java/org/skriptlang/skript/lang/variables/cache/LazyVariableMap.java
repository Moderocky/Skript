package org.skriptlang.skript.lang.variables.cache;

import mx.kenzie.clockwork.collection.ReferenceMap;
import org.skriptlang.skript.lang.variables.Variable;
import org.skriptlang.skript.lang.variables.VariablePath;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;

public class LazyVariableMap extends ReferenceMap<VariablePath, Variable<?>> {
	
	public LazyVariableMap(Map<VariablePath, Reference<Variable<?>>> map) {
		super(map, SoftReference::new);
	}
	
}
