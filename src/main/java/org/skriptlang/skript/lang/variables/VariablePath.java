package org.skriptlang.skript.lang.variables;

public class VariablePath {
	
	private final String path;
	
	public VariablePath(String path) {this.path = path;}
	
	@Override
	public int hashCode() {
		return path.hashCode();
	}
	
}
