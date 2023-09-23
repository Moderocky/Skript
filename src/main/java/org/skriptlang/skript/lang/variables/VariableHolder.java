package org.skriptlang.skript.lang.variables;

/**
 * Something that contains or can refer to variables.
 *
 * @author moderocky
 */
/*
todo make script/trigger implement this for reflective modification of scoped variables ?
 */
public interface VariableHolder {
	
	/**
	 * Sets the value of a variable.
	 *
	 * @param context the context of our request, provided by the trigger we're asking from
	 * @param path    the variable path
	 * @param value   the new variable value
	 * @param <Value> the value type
	 */
	<Value> void setVariable(@Deprecated Object context, VariablePath path, Value value);
	
	/**
	 * Gets the handle reference to a variable.
	 *
	 * @param context the context of our request, provided by the trigger we're asking from
	 * @param path    the variable path
	 * @param <Value> the value type
	 * @return the current variable value
	 */
	<Value> Variable<Value> getVariableHandle(@Deprecated Object context, VariablePath path);
	
	/**
	 * Gets the value of a variable.
	 *
	 * @param context the context of our request, provided by the trigger we're asking from
	 * @param path    the variable path
	 * @param <Value> the value type
	 * @return the current variable value
	 */
	default <Value> Value getVariable(@Deprecated Object context, VariablePath path) {
		return this.<Value>getVariableHandle(context, path).get();
	}
	
}
