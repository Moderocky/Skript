package org.skriptlang.skript.lang.variables.storage;

import ch.njol.skript.SkriptAddon;

public abstract class VariableStorage {
	
	protected final SkriptAddon provider;
	protected final String key;
	
	protected VariableStorage(SkriptAddon provider, String key) {
		this.provider = provider;
		this.key = key;
		// todo validate key contains no whitespace looks ok
	}
	
	public String getKey() {
		return key;
	}
	
	public SkriptAddon getProvider() {
		return provider;
	}
	
}
