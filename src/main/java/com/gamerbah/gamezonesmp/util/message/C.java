package com.gamerbah.gamezonesmp.util.message;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class C {

	private static final Pattern hexPattern = Pattern.compile("<#([A-Fa-f0-9]){6}>");

	private static final char CHAR = '\u00a7';

	public static final String BLACK      = CHAR + "0";
	public static final String DARK_BLUE  = CHAR + "1";
	public static final String DARK_GREEN = CHAR + "2";
	public static final String DARK_AQUA  = CHAR + "3";
	public static final String DARK_RED   = CHAR + "4";
	public static final String PURPLE     = CHAR + "5";
	public static final String GOLD       = CHAR + "6";
	public static final String GRAY       = CHAR + "7";
	public static final String DARK_GRAY  = CHAR + "8";
	public static final String BLUE       = CHAR + "9";
	public static final String GREEN      = CHAR + "a";
	public static final String AQUA       = CHAR + "b";
	public static final String RED        = CHAR + "c";
	public static final String PINK       = CHAR + "d";
	public static final String YELLOW     = CHAR + "e";
	public static final String WHITE      = CHAR + "f";

	public static final String RESET = CHAR + "r";

	public static final String OBFUSCATED    = CHAR + "k";
	public static final String BOLD          = CHAR + "l";
	public static final String STRIKETHROUGH = CHAR + "m";
	public static final String UNDERLINE     = CHAR + "n";
	public static final String ITALIC        = CHAR + "o";

	public static String getColor(String name) {
		for (Field field : C.class.getFields()) {
			if (field.getType().equals(String.class)) {
				if (field.getName().equalsIgnoreCase(name)) {
					if (Modifier.isStatic(field.getModifiers())) {
						try {
							return (String) field.get(null);
						} catch (IllegalAccessException e) {
							return WHITE;
						}
					}
				}
			}
		}

		return WHITE;
	}

	public static Color getColorFromCode(String colorCode) {
		if (colorCode.contains("1")) {
			return Color.AQUA;
		} else if (colorCode.contains("2")) {
			return Color.GREEN;
		} else if (colorCode.contains("3")) {
			return Color.AQUA;
		} else if (colorCode.contains("4")) {
			return Color.RED;
		} else if (colorCode.contains("5")) {
			return Color.PURPLE;
		} else if (colorCode.contains("6")) {
			return Color.ORANGE;
		} else if (colorCode.contains("7")) {
			return Color.GRAY;
		} else if (colorCode.contains("8")) {
			return Color.GRAY;
		} else if (colorCode.contains("9")) {
			return Color.AQUA;
		} else if (colorCode.contains("a")) {
			return Color.GREEN;
		} else if (colorCode.contains("b")) {
			return Color.AQUA;
		} else if (colorCode.contains("c")) {
			return Color.RED;
		} else if (colorCode.contains("d")) {
			return Color.PURPLE;
		} else if (colorCode.contains("e")) {
			return Color.YELLOW;
		}

		return Color.WHITE;
	}

	public static String hex(String hex) {
		return ChatColor.of(hex).toString();
	}

	public static String translateHex(String message) {
		Matcher matcher = hexPattern.matcher(message);
		while (matcher.find()) {
			final ChatColor hexColor = ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
			final String    before   = message.substring(0, matcher.start());
			final String    after    = message.substring(matcher.end());
			message = before + hexColor + after;
			matcher = hexPattern.matcher(message);
		}
		return ChatColor.translateAlternateColorCodes('&', message);
	}

}