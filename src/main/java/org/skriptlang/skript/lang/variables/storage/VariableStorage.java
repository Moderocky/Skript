package org.skriptlang.skript.lang.variables.storage;

import ch.njol.skript.SkriptAddon;
import org.skriptlang.skript.lang.variables.VariablePath;

import java.util.HashSet;
import java.util.Set;

public abstract class VariableStorage {
	
	protected final SkriptAddon provider;
	protected final String key;
	private final Set<Integer> memory;
	
	protected VariableStorage(SkriptAddon provider, String key) {
		this.provider = provider;
		this.key = key;
		this.memory = new HashSet<>();
		// todo validate key contains no whitespace looks ok
	}
	
	public String getKey() {
		return key;
	}
	
	public SkriptAddon getProvider() {
		return provider;
	}
	
	public boolean knowsAbout(VariablePath path) {
		return memory.contains(path.hashCode());
	}
	
}
