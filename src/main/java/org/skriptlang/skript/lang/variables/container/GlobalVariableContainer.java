package org.skriptlang.skript.lang.variables.container;

import org.skriptlang.skript.lang.variables.Variable;
import org.skriptlang.skript.lang.variables.VariablePath;
import org.skriptlang.skript.lang.variables.cache.LazyVariableMap;
import org.skriptlang.skript.lang.variables.storage.VariableStorage;

import java.util.HashMap;
import java.util.Map;

public class GlobalVariableContainer implements VariableContainer {
	
	protected final VariableStorage storage;
	protected Map<VariablePath, Variable<?>> cache;
	
	public GlobalVariableContainer(VariableStorage storage) {
		this.storage = storage;
		this.cache = new LazyVariableMap(new HashMap<>());
	}
	
	@Override
	public boolean isPresent(VariablePath path) {
		return cache.containsKey(path) || storage.knowsAbout(path);
	}
	
	@Override
	public boolean isLoaded(VariablePath path) {
		return false;
	}
	
	@Override
	public <Value> void setVariable(VariablePath path, Value value) {
	
	}
	
	@Override
	public <Value> Value getVariable(VariablePath path) {
		return null;
	}
	
	@Override
	public <Value> Variable<Value> getVariableHandle(VariablePath path) {
		return null;
	}
	
}
