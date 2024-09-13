package ch.njol.skript.expressions;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BossBar;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


@Name("Boss Bar Flags")
@Description("The flags of a boss bar. These flags control the behavior of the boss bar.")
@Examples({"add darken sky to flags of {_bar}t"})
@Since("INSERT VERSION")
public class ExprBossBarFlags extends PropertyExpression<BossBar, BarFlag> {

	static {
		register(ExprBossBarFlags.class, BarFlag.class, "flags", "bossbars");
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		this.setExpr((Expression<? extends BossBar>) exprs[0]);
		return true;
	}

	@Override
	protected BarFlag[] get(Event event, BossBar[] source) {
		List<BarFlag> flags = new ArrayList<>();
		for (BossBar bossBar : source)
			for (BarFlag flag : BarFlag.values())
				if (bossBar.hasFlag(flag)) // bukkit has no getter for flags, so we have to check like this...
					flags.add(flag);
		return flags.toArray(new BarFlag[0]);
	}


	@Override
	public Class<?> @Nullable [] acceptChange(ChangeMode mode) {
		return switch (mode) {
			case SET, ADD, REMOVE, DELETE -> new Class[] {BarFlag.class};
			default -> null;
		};
	}

	@Override
	public void change(Event event, Object[] delta, ChangeMode mode) {
		for (BossBar bar : this.getExpr().getArray(event))
			switch (mode) {
				case SET:
					this.clearFlags(bar);
				case ADD:
					for (Object flag : delta)
						bar.addFlag((BarFlag) flag);
					break;
				case REMOVE:
					for (Object flag : delta)
						bar.removeFlag((BarFlag) flag);
					break;
				case DELETE:
					this.clearFlags(bar);
			}
	}

	@Override
	public Class<? extends BarFlag> getReturnType() {
		return BarFlag.class;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "flags of " + getExpr().toString(event, debug);
	}

	private void clearFlags(BossBar bossBar) {
		for (BarFlag flag : BarFlag.values())
			if (bossBar.hasFlag(flag))
				bossBar.removeFlag(flag);
	}

}