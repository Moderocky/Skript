package org.skriptlang.skript.lang.variables.container;

import org.skriptlang.skript.lang.context.Context;
import org.skriptlang.skript.lang.variables.Variable;
import org.skriptlang.skript.lang.variables.VariableHolder;
import org.skriptlang.skript.lang.variables.VariablePath;

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
	default <Value> void setVariable(Context<?> context, VariablePath path, Value value) {
		this.setVariable(path, value);
	}
	
	@Override
	default <Value> Value getVariable(Context<?> context, VariablePath path) {
		return this.getVariable(path);
	}
	
	@Override
	default <Value> Variable<Value> getVariableHandle(Context<?> context, VariablePath path) {
		return this.getVariableHandle(path);
	}
	
}
