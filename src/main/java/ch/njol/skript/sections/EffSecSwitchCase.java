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
package ch.njol.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.skriptlang.skript.lang.experiment.Feature;

import java.util.List;

public class EffSecSwitchCase extends EffectSection {

	static {
		Skript.registerSection(EffSecSwitchCase.class, "[check] <.+>");
	}

	private @UnknownNullability Condition condition;
	private boolean section;

	@Override
	public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed,
                        SkriptParser.ParseResult parseResult, @Nullable SectionNode sectionNode,
                        @Nullable List<TriggerItem> triggerItems) {
		if (!this.hasExperiment(Feature.SWITCH_SECTIONS))
			return false;
		if (!SecSwitch.isInSwitch(this))
			return false;
		this.section = sectionNode != null;
		String text = parseResult.regexes.get(0).group();
		this.condition = Condition.parse(text, null);
		if (section) {
			this.loadOptionalCode(sectionNode);
		}
		return condition != null;
	}

	protected Condition getCondition() {
		return condition;
	}

	@Override
	protected @Nullable TriggerItem walk(Event event) {
		if (this.checkCondition(event)) {
			TriggerItem skippedNext = this.getNormalNext();
			if (this.last != null)
				this.last.setNext(skippedNext);
			return this.first != null ? this.first : skippedNext;
		} else {
			return this.getNextCondition();
		}
	}

	private @UnknownNullability TriggerItem getNextCondition() {
		return this.getSkippedNext();
	}

	private TriggerItem getSwitch() {
		return SecSwitch.getSwitch(this);
	}

	@Nullable
	private TriggerItem getSkippedNext() {
		TriggerItem next = this.getNormalNext();
		if (section)
			return next;
		if (next instanceof SecSwitch)
			return next;
		while (next != null) {
			if (next instanceof EffSecSwitchCase && ((EffSecSwitchCase) next).section) {
				return ((EffSecSwitchCase) next).getNormalNext();
			} else if (next instanceof EffSecSwitchCase) {
				next = ((EffSecSwitchCase) next).getNormalNext();
			} else if (next instanceof MetaSyntaxElement) {
				next = next.getNext();
			} else {
				break;
			}
		}
		return next;
	}

	@Nullable
	public TriggerItem getNormalNext() {
		return super.getNext();
	}

	private boolean checkCondition(Event event) {
		return condition.run(event);
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "check " + condition.toString(event, debug);
	}

}
