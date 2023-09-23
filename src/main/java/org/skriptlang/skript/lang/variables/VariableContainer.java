package org.skriptlang.skript.lang.variables;

/**
 * Represents a single variable storage
 */
public interface VariableContainer extends VariableHolder {
	
	boolean isPresent(VariablePath path);
	
	boolean isLoaded(VariablePath path);
	
	<Value> void setVariable(VariablePath path, Value value);
	
	<Value> Value getVariable(VariablePath path);
	
	<Value> Variable<Value> getVariableHandle(VariablePath path);
	
	@Override
	default <Value> void setVariable(Object context, VariablePath path, Value value) {
		this.setVariable(path, value);
	}
	
	@Override
	default <Value> Value getVariable(Object context, VariablePath path) {
		return this.getVariable(path);
	}
	
	@Override
	default <Value> Variable<Value> getVariableHandle(Object context, VariablePath path) {
		return this.getVariableHandle(path);
	}
	
}
