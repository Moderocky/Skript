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
package ch.njol.skript.conditions;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.skriptlang.skript.lang.script.Script;

@Name("Is Loaded")
@Description({
		"Checks whether a world, chunk or script is loaded.",
		"'chunk at 1, 1' uses chunk coordinates, which are location coords divided by 16."
})
@Examples({"if chunk at {home::%player's uuid%} is loaded:",
		"if chunk 1, 10 in world \"world\" is loaded:",
		"if world(\"lobby\") is loaded:",
		"if script named \"MyScript.sk\" is loaded:"
})
@Since("2.3, 2.5 (revamp with chunk at location/coords), INSERT VERSION (Scripts)")
@SuppressWarnings({"unchecked", "NotNullFieldNotInitialized"})
public class CondIsLoaded extends Condition {

	static {
		Skript.registerCondition(CondIsLoaded.class,
			"chunk[s] %directions% [%locations%] (is|are)[(1¦(n't| not))] loaded",
			"chunk [at] %number%, %number% (in|of) [world] %world% is[(1¦(n't| not))] loaded",
			"%scripts/worlds% (is|are)[(1¦(n't| not))] loaded",
			"script[s] %scripts% (is|are)[(1¦(n't| not))] loaded",
			"world[s] %worlds% (is|are)[(1¦(n't| not))] loaded");
	}

	private Expression<Location> locations;
	private Expression<Number> x, z;
	private Expression<?> something;
	private int pattern;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		pattern = matchedPattern;
		switch (pattern) {
			case 0:
				locations = Direction.combine((Expression<? extends Direction>) exprs[0], (Expression<? extends Location>) exprs[1]);
				break;
			case 1:
				x = (Expression<Number>) exprs[0];
				z = (Expression<Number>) exprs[1];
				something = exprs[2];
				break;
			case 2:
			case 3:
			case 4:
				something = exprs[0];
		}
		setNegated(parseResult.mark == 1);
		return true;
	}

	@SuppressWarnings({"null", "PatternVariableCanBeUsed"})
	@Override
	public boolean check(Event e) {
		switch (pattern) {
			case 0:
				return locations.check(e, location -> {
					World world = location.getWorld();
					if (world != null)
						return world.isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4);
					return false;
				}, isNegated());
			case 1:
				return something.check(e, object -> {
					if (!(object instanceof World))
						return false;
					World world = (World) object;
					Number x = this.x.getSingle(e);
					Number z = this.z.getSingle(e);
					if (x == null || z == null)
						return false;
					return world.isChunkLoaded(x.intValue(), z.intValue());
				}, isNegated());
			case 2:
			case 4:
				return something.check(e, object -> {
					if (object instanceof World) {
						World world = (World) object;
						return Bukkit.getWorld(world.getName()) != null;
					} else if (object instanceof Script) {
						Script script = (Script) object;
						return ScriptLoader.getLoadedScripts().contains(script);
					}
					return false;
				}, isNegated());
			case 3:
				return something.check(e, ScriptLoader.getLoadedScripts()::contains, isNegated());
			default:
				return false;
		}
	}

	@SuppressWarnings("null")
	@Override
	public String toString(@Nullable Event e, boolean d) {
		String neg = isNegated() ? " not " : " ";
		switch (pattern) {
			case 0:
				return "chunk[s] at " + locations.toString(e, d) + (locations.isSingle() ? " is" : " are") + neg + "loaded";
			case 1:
				return "chunk at " + x.toString(e, d) + ", " + z.toString(e, d) + " in " + something.toString(e,d) + ") is" + neg + "loaded";
			case 3:
				return "scripts " + this.something.toString(e, d) + (this.something.isSingle() ? " is" : " are") + neg + "loaded";
			case 4:
				return "worlds " + this.something.toString(e, d) + (this.something.isSingle() ? " is" : " are") + neg + "loaded";
			default:
				return this.something.toString(e, d) + (this.something.isSingle() ? " is" : " are") + neg + "loaded";
		}
	}

}
