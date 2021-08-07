package com.gamerbah.gamezonesmp.util.message;

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.EventSound;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.logging.Level;

public final class M {

	private static final GameZoneSMP PLUGIN = GameZoneSMP.getInstance();

	public static final String ARROW_SUCCESS = C.GREEN + " \u00BB  " + C.GRAY;
	public static final String ARROW_FAIL    = C.RED + " \u00BB  " + C.GRAY;
	public static final String ARROW_ERROR   = Hex.DARK_RED + " \u00BB  " + C.GRAY;
	public static final String ARROW_INFO    = C.BLUE + " \u00BB  " + C.GRAY;

	public static void incorrectUsage(CommandSender sender, String msg) {
		sender.sendMessage(ARROW_ERROR + "Usage: " + C.RED + msg);
		if (sender instanceof Player player) EventSound.playSound(player, EventSound.ACTION_FAIL);
	}

	public static void noPermission(CommandSender sender) {
		sender.sendMessage(ARROW_FAIL + "You don't have permission to use that command");
		if (sender instanceof Player player) EventSound.playSound(player, EventSound.ACTION_FAIL);
	}

	public static void error(CommandSender sender, String msg) {
		sender.sendMessage(ARROW_ERROR + msg);
		if (sender instanceof Player player) EventSound.playSound(player, EventSound.ACTION_FAIL);
	}

	public static void info(CommandSender sender, String msg) {
		sender.sendMessage(ARROW_INFO + C.GRAY + msg);
	}

	public static void success(CommandSender player, String msg) {
		player.sendMessage(ARROW_SUCCESS + C.GRAY + msg);
	}

	public static void invalidPlayer(Player player, String arg) {
		error(player, "Unable to find player " + C.RED + arg);
	}

	public static void staffMessage(final Rank requiredRank, boolean sound, final String... messages) {
		PLUGIN.getServer().getOnlinePlayers().stream().filter(player -> {
			var gameProfile = PLUGIN.getProfileManager().getCachedProfile(player.getUniqueId());
			return gameProfile != null && gameProfile.hasRank(requiredRank);
		}).forEach(staff -> {
			for (String message : messages) {
				staff.sendMessage(message);
			}
			if (sound) {
				EventSound.playSound(staff, EventSound.CLICK);
			}
		});
	}

	public static void staffMessage(final Rank requiredRank, final String... messages) {
		staffMessage(requiredRank, false, messages);
	}

	public static void staffMessage(final Rank requiredRank, boolean sound, final BaseComponent... baseComponents) {
		PLUGIN.getServer().getOnlinePlayers().stream().filter(player -> {
			GameProfile gameProfile = PLUGIN.getProfileManager().getCachedProfile(player.getUniqueId());
			return gameProfile != null && gameProfile.hasRank(requiredRank);
		}).forEach(staff -> {
			Arrays.asList(baseComponents).forEach(((staff.spigot()::sendMessage)));
			if (sound) {
				EventSound.playSound(staff, EventSound.CLICK);
			}
		});
	}

	public static void staffMessage(final Rank requiredRank, final BaseComponent... baseComponents) {
		staffMessage(requiredRank, false, baseComponents);
	}

	public static void staffMessage(boolean sound, final String... messages) {
		staffMessage(Rank.MODERATOR, sound, messages);
	}

	public static void staffMessage(final String... messages) {
		staffMessage(Rank.MODERATOR, messages);
	}

	public static void staffMessage(final boolean sound, final BaseComponent... baseComponents) {
		staffMessage(Rank.MODERATOR, sound, baseComponents);
	}

	public static void staffMessage(final BaseComponent... baseComponents) {
		staffMessage(Rank.MODERATOR, false, baseComponents);
	}


	public static void log(final Level level, final Throwable throwable, final String... messages) {
		PLUGIN.getLogger().log(level, throwable.getLocalizedMessage());
		Arrays.asList(messages).forEach(message -> PLUGIN.getLogger().log(level, message));
	}

	public static void log(final Level level, final String... messages) {
		Arrays.asList(messages).forEach(message -> PLUGIN.getLogger().log(level, message));
	}

	public static void log(final String... messages) {
		log(Level.INFO, messages);
	}

	public static void broadcast(final String message) {
		PLUGIN.getServer().broadcastMessage(message);
	}

}
