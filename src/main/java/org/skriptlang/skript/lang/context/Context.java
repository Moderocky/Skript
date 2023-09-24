package org.skriptlang.skript.lang.context;

import ch.njol.skript.lang.TriggerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

/**
 * A context for the execution of syntax.
 * This is like a map of where the program is and where it came from.
 *
 * @param <Source> the source of the entire process, e.g. a Minecraft event, a command or a function called by a plugin.
 */
public class Context<Source> {
	
	/**
	 * The stack of each place we have been.
	 * When we begin a section (e.g. a function body) we push it onto the stack, when we exit we pop.
	 */
	// todo should we stack TriggerSections instead?
	protected final Stack<TriggerItem> stack = new Stack<>();
	/**
	 * The thing that caused this process to run, e.g. the Bukkit event that was listened to.
	 */
	protected final @NotNull Source source;
	/**
	 * Occasionally, one process may start some kind of sub-process, with its own cause/event and stack.
	 */
	protected final @Nullable Context<?> parent;
	
	public Context(@NotNull Source source, @Nullable Context<?> parent) {
		this.source = source;
		this.parent = parent;
	}
	
	public Context(Source source) {
		this(source, null);
	}
	
	/**
	 * The very first source of a process, if it came from another Skript process.
	 */
	public @NotNull Context<?> getRootContext() {
		if (parent != null)
			return parent.getRootContext();
		else
			return this;
	}
	
	public @Nullable("if this context is the root context") Context<?> getParent() {
		return parent;
	}
	
	public boolean hasParent() {
		return parent != null;
	}
	
	/**
	 * @return the thing that caused this process to run, e.g. the Bukkit event that was listened to.
	 */
	public @NotNull Source getSource() {
		return source;
	}
	
}
