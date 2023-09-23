package org.skriptlang.skript.lang.variables;

import ch.njol.skript.Skript;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.lang.variables.storage.VariableStorage;

/**
 * A variable instance manager. This is to be obtained via Skript.
 * There is typically expected to be one instance of a manager.
 *
 * @author moderocky
 */
public class VariableManager implements VariableHolder {
	
	protected @NotNull Phase phase = Phase.END;
	
	/**
	 * Registers a variable storage option with a given key.
	 * This key can then be used in a variable configuration.
	 *
	 * @param storage the storage handler
	 */
	public void registerVariableStorage(VariableStorage storage) {
		if (storage == null)
			return;
		if (phase.stage > 0) {
			Skript.error("'" + storage.getProvider()
				.getName() + "' attempted to register variable storage mode after initialisation.");
        }
		// todo
	}
	
	/**
	 * Gets the container responsible for a variable in a given context.
	 * This will typically fall back to the global variable container.
	 *
	 * @param context the context of our request, provided by the trigger we're asking from
	 * @param path    the path of the variable
	 * @return the container responsible for holding this variable
	 */
	protected VariableContainer getContainer(@Deprecated Object context, VariablePath path) { // todo need context from walrus
		// todo
		return null;
	}
	
	@Override
	public <Value> void setVariable(@Deprecated Object context, VariablePath path, Value value) {
		// todo we can shortcut this later
		VariableContainer container = this.getContainer(context, path);
		container.setVariable(path, value);
	}
	
	@Override
	public <Value> Value getVariable(Object context, VariablePath path) {
		VariableContainer container = this.getContainer(context, path);
		return container.getVariable(path);
	}
	
	@Override
	public <Value> Variable<Value> getVariableHandle(Object context, VariablePath path) {
		VariableContainer container = this.getContainer(context, path);
		return container.getVariableHandle(path);
	}
	
	/**
	 * The phase of the variable manager, used to prevent things happening at times they shouldn't.
	 * The cycle starts in END and must go in order (e.g. END -> LOAD -> RUN -> SHUTDOWN -> END).
	 * <p>
	 * The only exception to this is when we move from LOAD -> END, i.e. Skript is terminated while loading,
	 * in which case we don't need to bother with saving or file checks and can simply drop everything.
	 */
	public enum Phase {
		LOAD(1),
		RUN(2),
		SHUTDOWN(3),
		END(0);
		
		public final int stage;
		
		Phase(int stage) {this.stage = stage;}
	}
	
}
