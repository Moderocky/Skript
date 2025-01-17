package ch.njol.skript.util;

import ch.njol.skript.localization.Adjective;
import ch.njol.skript.localization.Language;
import ch.njol.skript.variables.Variables;
import ch.njol.yggdrasil.Fields;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("null")
public enum SkriptColor implements Color {

	BLACK(DyeColor.BLACK, ChatColor.BLACK, null),
	DARK_GREY(DyeColor.GRAY, ChatColor.DARK_GRAY, null),
	// DyeColor.LIGHT_GRAY on 1.13, DyeColor.SILVER on earlier (dye colors were changed in 1.12)
	LIGHT_GREY(DyeColor.LIGHT_GRAY, ChatColor.GRAY, null),
	WHITE(DyeColor.WHITE, ChatColor.WHITE, BarColor.WHITE),
	
	DARK_BLUE(DyeColor.BLUE, ChatColor.DARK_BLUE, BarColor.BLUE),
	BROWN(DyeColor.BROWN, ChatColor.BLUE, null),
	DARK_CYAN(DyeColor.CYAN, ChatColor.DARK_AQUA, BarColor.BLUE),
	LIGHT_CYAN(DyeColor.LIGHT_BLUE, ChatColor.AQUA, BarColor.BLUE),
	
	DARK_GREEN(DyeColor.GREEN, ChatColor.DARK_GREEN, BarColor.GREEN),
	LIGHT_GREEN(DyeColor.LIME, ChatColor.GREEN, BarColor.GREEN),
	
	YELLOW(DyeColor.YELLOW, ChatColor.YELLOW, BarColor.YELLOW),
	ORANGE(DyeColor.ORANGE, ChatColor.GOLD, BarColor.YELLOW),
	
	DARK_RED(DyeColor.RED, ChatColor.DARK_RED, BarColor.RED),
	LIGHT_RED(DyeColor.PINK, ChatColor.RED, BarColor.RED),
	
	DARK_PURPLE(DyeColor.PURPLE, ChatColor.DARK_PURPLE, BarColor.PURPLE),
	LIGHT_PURPLE(DyeColor.MAGENTA, ChatColor.LIGHT_PURPLE, BarColor.PURPLE),

	PINK(DyeColor.PINK, ChatColor.LIGHT_PURPLE, BarColor.PINK);

	private final static Map<String, SkriptColor> names = new HashMap<>();
	private final static Set<SkriptColor> colors = new HashSet<>();
	private final static String LANGUAGE_NODE = "colors";
	
	static {
		colors.addAll(Arrays.asList(values()));
		Language.addListener(() -> {
			names.clear();
			for (SkriptColor color : values()) {
				String node = LANGUAGE_NODE + "." + color.name();
				color.setAdjective(new Adjective(node + ".adjective"));
				for (String name : Language.getList(node + ".names"))
					names.put(name.toLowerCase(Locale.ENGLISH), color);
			}
		});
	}
	
	private ChatColor chat;
	private DyeColor dye;
	private @Nullable BarColor bar;

	@Nullable
	private Adjective adjective;
	
	SkriptColor(DyeColor dye, ChatColor chat, @Nullable BarColor bar) {
		this.chat = chat;
		this.dye = dye;
		this.bar = bar;
	}
	
	@Override
	public org.bukkit.Color asBukkitColor() {
		return dye.getColor();
	}

	@Override
	public int getAlpha() {
		return dye.getColor().getAlpha();
	}

	@Override
	public int getRed() {
		return dye.getColor().getRed();
	}

	@Override
	public int getGreen() {
		return dye.getColor().getGreen();
	}

	@Override
	public int getBlue() {
		return dye.getColor().getBlue();
	}

	@Override
	public DyeColor asDyeColor() {
		return dye;
	}

	@Override
	public @Nullable BarColor asBossBarColor() {
		return bar;
	}

	@Override
	public String getName() {
		assert adjective != null;
		return adjective.toString();
	}
	
	@Override
	public Fields serialize() throws NotSerializableException {
		return new Fields(this, Variables.yggdrasil);
	}
	
	@Override
	public void deserialize(@NotNull Fields fields) throws StreamCorruptedException {
		dye = fields.getObject("dye", DyeColor.class);
		chat = fields.getObject("chat", ChatColor.class);
		bar = fields.getObject("bar", BarColor.class);
		try {
			adjective = fields.getObject("adjective", Adjective.class);
		} catch (StreamCorruptedException ignored) {}
	}
	
	public String getFormattedChat() {
		return "" + chat;
	}
	
	@Nullable
	public Adjective getAdjective() {
		return adjective;
	}
	
	public ChatColor asChatColor() {
		return chat;
	}

	@Deprecated
	public byte getWoolData() {
		return dye.getWoolData();
	}
	
	@Deprecated
	public byte getDyeData() {
		return (byte) (15 - dye.getWoolData());
	}
	
	private void setAdjective(@Nullable Adjective adjective) {
		this.adjective = adjective;
	}
	
	
	/**
	 * @param name The String name of the color defined by Skript's .lang files.
	 * @return Skript Color if matched up with the defined name
	 */
	@Nullable
	public static SkriptColor fromName(String name) {
		return names.get(name);
	}
	
	/**
	 * @param dye DyeColor to match against a defined Skript Color.
	 * @return Skript Color if matched up with the defined DyeColor
	 */
	public static SkriptColor fromDyeColor(DyeColor dye) {
		for (SkriptColor color : colors) {
			DyeColor c = color.asDyeColor();
			assert c != null;
			if (c.equals(dye))
				return color;
		}
		assert false;
		return null;
	}
	
	public static SkriptColor fromBukkitColor(org.bukkit.Color color) {
		for (SkriptColor c : colors) {
			if (c.asBukkitColor().equals(color) || c.asDyeColor().getFireworkColor().equals(color))
				return c;
		}
		return null;
	}

	public static SkriptColor fromBossBarColor(BarColor color) {
		for (SkriptColor skriptColor : colors)
			if (skriptColor.bar == color)
				return skriptColor;
		assert false;
		return null;
	}

	/**
	 * @deprecated Magic numbers
	 * @param data short to match against a defined Skript Color.
	 * @return Skript Color if matched up with the defined short
	 */
	@Deprecated
	@Nullable
	public static SkriptColor fromDyeData(short data) {
		if (data < 0 || data >= 16)
			return null;
		
		for (SkriptColor color : colors) {
			DyeColor c = color.asDyeColor();
			assert c != null;
			if (c.getDyeData() == data)
				return color;
		}
		return null;
	}
	
	/**
	 * @deprecated Magic numbers
	 * @param data short to match against a defined Skript Color.
	 * @return Skript Color if matched up with the defined short
	 */
	@Deprecated
	@Nullable
	public static SkriptColor fromWoolData(short data) {
		if (data < 0 || data >= 16)
			return null;
		for (SkriptColor color : colors) {
			DyeColor c = color.asDyeColor();
			assert c != null;
			if (c.getWoolData() == data)
				return color;
		}
		return null;
	}

	/**
	 * Replace chat color character '§' with '&'
	 * This is an alternative method to {@link ChatColor#stripColor(String)}
	 * But does not strip the color code.
	 * @param s string to replace chat color character of.
	 * @return String with replaced chat color character
	 */
	public static String replaceColorChar(String s) {
		return s.replace('\u00A7', '&');
	}

	@Override
	public String toString() {
		return adjective == null ? "" + name() : adjective.toString(-1, 0);
	}
}
