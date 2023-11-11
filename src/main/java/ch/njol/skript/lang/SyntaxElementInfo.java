/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.lang;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * @author Peter Güttinger
 * @param <E> the syntax element this info is for
 */
public class SyntaxElementInfo<E extends SyntaxElement> {
	
	public final Class<E> type;
	public final @Deprecated(forRemoval = true) Class<E> c; // replaced by the 'type' field
	public final String[] patterns;
	public final String originClassPath;
	private @Nullable Constructor<E> constructor;
	
	public SyntaxElementInfo(final String[] patterns, final Class<E> type, final String originClassPath)
		throws IllegalArgumentException {
		this(patterns, type, originClassPath, true);
	}
	
	/**
	 * An alternative constructor for third-party addons that wish to create non-standard syntax,
	 * specifically where the constructor may not meet the requirements (or may not be used at all).
	 *
	 * In this case, the {@link SyntaxElementInfo#create()} method should be overridden.
     */
	protected SyntaxElementInfo(final String[] patterns, final Class<E> type, final String originClassPath, boolean checkConstructor)
		throws IllegalArgumentException {
		this.patterns = patterns;
		this.type = c = type;
		this.originClassPath = originClassPath;
		if (checkConstructor)
			this.checkConstructor();
	}
	
	private void checkConstructor() {
		if (constructor != null)
			return;
		try {
			this.constructor = type.getConstructor();
			if (!Modifier.isPublic(constructor.getModifiers()))
				throw new Error("The nullary constructor of class " + type.getName() + " is not public");
		} catch (final NoSuchMethodException e) {
			// throwing an Exception throws an (empty) ExceptionInInitializerError instead, thus an Error is used
			throw new Error(type + " does not have a public nullary constructor", e);
		} catch (final SecurityException e) {
			throw new IllegalStateException("Skript cannot run properly because a security manager is blocking it!");
		}
	}
	
	public E create()
		throws InstantiationException, IllegalAccessException {
		this.checkConstructor();
		assert constructor != null;
		try {
			return constructor.newInstance();
		} catch (InvocationTargetException ex) {
			throw new RuntimeException("Unable to create " + type.getName() + " instance", ex);
		} catch (IllegalAccessException ex) {
			// this should never occur since we checked it was accessible to us
			throw new Error(ex);
		}
	}
	
	/**
	 * Get the class that represents this element.
	 * @return The Class of the element
	 */
	public Class<E> getElementClass() {
		return type;
	}
	
	/**
	 * Get the patterns of this syntax element.
	 * @return Array of Skript patterns for this element
	 */
	public String[] getPatterns() {
		return Arrays.copyOf(patterns, patterns.length);
	}
	
	/**
	 * Get the original classpath for this element.
	 * @return The original ClassPath for this element
	 */
	public String getOriginClassPath() {
		return originClassPath;
	}
	
}
