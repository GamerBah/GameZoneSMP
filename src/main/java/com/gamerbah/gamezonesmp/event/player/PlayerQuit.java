package com.gamerbah.gamezonesmp.event.player;
/* Created by GamerBah on 8/7/2016 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.M;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerQuit(GameZoneSMP plugin) implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player      player  = event.getPlayer();
		GameProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

		plugin.getAfk().remove(player.getUniqueId());
		plugin.getInventoryCheck().remove(player.getUniqueId());
		plugin.getPvp().remove(player.getUniqueId());
		plugin.getCreative().remove(player.getUniqueId());
		plugin.getDiscordLink().remove(plugin.getLinkMap().remove(player.getUniqueId()));
		plugin.getTwitchWatchers().remove(player.getUniqueId());

		plugin.getAfkTimer().remove(player.getUniqueId());
		if (profile != null) {
			String tag = C.DARK_GRAY + C.BOLD + "[" + C.RED + C.BOLD + "-" + C.DARK_GRAY + C.BOLD + "] " + C.WHITE;
			System.out.println(profile.getId() + ", " + profile.getName());
			if (profile.isSilent()) {
				event.setQuitMessage("");
				M.staffMessage(tag + event.getPlayer().getName() + C.GRAY + " (Silent Leave)");
			} else {
				event.setQuitMessage(tag + event.getPlayer().getName());
			}
			plugin.getServer().getOnlinePlayers().forEach(p -> p.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 0.7F));

			profile.fullSync();
			plugin.getProfileManager().getProfiles().remove(profile);
		}
	}

}
