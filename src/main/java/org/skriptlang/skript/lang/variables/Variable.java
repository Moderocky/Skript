package org.skriptlang.skript.lang.variables;

import java.util.concurrent.atomic.AtomicReference;

/**
 * A handle for a variable.
 *
 * @param <Value> the value type - in practice most variables will be Object,
 *                but in principle we can support high performance fixed-type handles
 *                (e.g. for known-type literal locals)
 * @author moderocky
 */
public interface Variable<Value> {
	
	String name();
	
	Value get();
	
	void set(Value value);
	
	void lazySet(Value value);
	
	/**
	 * @param value the new value
	 * @return the previous value
	 */
	Value getAndSet(Value value);
	
	boolean compareAndSet(Value expectedValue, Value newValue);
	
	void delete();
	
}

class AtomicVariable<Value> extends AtomicReference<Value> implements Variable<Value> {
	
	protected final String name;
	
	public AtomicVariable(String name, Value initialValue) {
		super(initialValue);
		this.name = name;
	}
	
	public AtomicVariable(String name) {
		super();
		this.name = name;
	}
	
	@Override
	public String toString() {
		return '{' + name + '}';
	}
	
	@Override
	public String name() {
		return name;
	}
	
	@Override
	public void delete() {
		this.set(null); // this should be setOpaque after we update java
	}
	
}
