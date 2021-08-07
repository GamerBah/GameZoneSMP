package com.gamerbah.gamezonesmp.event.player;
/* Created by GamerBah on 8/15/2016 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.task.player.AFKRunnable;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public record PlayerCommandPreProcess(GameZoneSMP plugin) implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player player  = event.getPlayer();
		String command = event.getMessage();

		GameProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
		assert profile != null;

		if (plugin.getUpdateManager().isUpdating()) {
			event.setCancelled(true);
			player.sendMessage(C.RED + "Commands aren't available during an update!");
			EventSound.playSound(player, EventSound.ACTION_FAIL);
			return;
		}

		if (plugin.isFrozen()) {
			if (!profile.hasRank(Rank.MODERATOR)) {
				event.setCancelled(true);
				player.sendMessage(C.RED + "You can't use commands while frozen!");
				EventSound.playSound(player, EventSound.ACTION_FAIL);
				return;
			}
		}

		if (StringUtils.startsWithIgnoreCase(command, "/me") || StringUtils.startsWithIgnoreCase(command, "/minecraft:")
		    || StringUtils.startsWithIgnoreCase(command, "/bukkit:") ||
		    StringUtils.startsWithIgnoreCase(command, "/spigot:") || StringUtils
				    .startsWithIgnoreCase(command, "/GameZoneSMP:")) {
			event.setCancelled(true);
			return;
		}

		if (StringUtils.equalsIgnoreCase(command, "/help")) {
			event.setCancelled(true);
			player.performCommand("GameZoneSMP:help");
			return;
		}
		if (StringUtils.equalsIgnoreCase(command, "/help staff")) {
			event.setCancelled(true);
			player.performCommand("GameZoneSMP:help staff");
			return;
		}
		if (StringUtils.equalsIgnoreCase(command, "/reload config")) {
			event.setCancelled(true);
			player.performCommand("GameZoneSMP:reload config");
			return;
		}
		if (StringUtils.equalsIgnoreCase(command, "/reload plugin")) {
			event.setCancelled(true);
			player.performCommand("GameZoneSMP:reload plugin");
			return;
		}
		if (StringUtils.equalsIgnoreCase(command, "/reload")) {
			event.setCancelled(true);
			player.performCommand("GameZoneSMP:reload");
			return;
		}
		if (StringUtils.equalsIgnoreCase(command, "/gamemode")) {
			event.setCancelled(true);
			player.performCommand("GameZoneSMP:gamemode");
			return;
		}
		if (StringUtils.equalsIgnoreCase(command, "/gamemode creative")) {
			event.setCancelled(true);
			player.performCommand("GameZoneSMP:gamemode creative");
			return;
		}
		if (StringUtils.equalsIgnoreCase(command, "/gamemode survival")) {
			event.setCancelled(true);
			player.performCommand("GameZoneSMP:gamemode survival");
			return;
		}
		if (StringUtils.equalsIgnoreCase(command, "/gamemode adventure")) {
			event.setCancelled(true);
			player.performCommand("GameZoneSMP:gamemode adventure");
			return;
		}
		if (StringUtils.equalsIgnoreCase(command, "/gamemode spectator")) {
			event.setCancelled(true);
			player.performCommand("GameZoneSMP:gamemode spectator");
			return;
		}
		if (StringUtils.equalsIgnoreCase(command, "/npc")) {
			event.setCancelled(true);
			player.performCommand("GameZoneSMP:npc");
			return;
		}

		if (plugin.getAfk().contains(player.getUniqueId()) && !StringUtils.startsWithIgnoreCase(command, "/afk")
		    && !StringUtils.startsWithIgnoreCase(command, "/spawn")) {
			player.chat("/afk");
		}

		if (plugin.getAfkTimer().containsKey(player.getUniqueId())) {
			AFKRunnable.reset(player);
		}

		plugin.getServer()
		      .getOnlinePlayers()
		      .stream()
		      .filter(p -> {
			      GameProfile gp = plugin.getProfileManager().getProfile(p.getUniqueId());
			      return gp != null && gp.isWatchdog();
		      })
		      .forEach(staff -> player.sendMessage(
				      C.GRAY + "[Watchdog] \u00BB " + C.DARK_GRAY + player.getName() + ": " + command));

	}

}
