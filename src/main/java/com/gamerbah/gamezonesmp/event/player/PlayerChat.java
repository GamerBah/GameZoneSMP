package com.gamerbah.gamezonesmp.event.player;
/* Created by GamerBah on 8/9/2016 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.Hex;
import com.gamerbah.gamezonesmp.util.task.player.AFKRunnable;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Pattern;

public record PlayerChat(GameZoneSMP plugin) implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);

		Player      player  = event.getPlayer();
		GameProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

		AFKRunnable.reset(player);
		if (profile != null) {
			if (profile.hasRank(Rank.MODERATOR)) {
				event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
			}

			plugin.getServer().getOnlinePlayers().forEach(p -> {
				if (Pattern.compile(Pattern.quote("@" + p.getName()), Pattern.CASE_INSENSITIVE)
				           .matcher(event.getMessage())
				           .find()) {
					event.setMessage(
							event.getMessage().replaceAll("@(?i)" + p.getName(), C.AQUA + "@" + p.getName() + C.GRAY));
					p.sendMessage(profile.displayName() + C.GRAY + " \u00BB " + Hex.LIGHT_GRAY + event.getMessage());
					EventSound.playSound(p, EventSound.CHAT_TAGGED);
				} else {
					p.sendMessage(profile.displayName() + C.GRAY + " \u00BB " + Hex.LIGHT_GRAY + event.getMessage());
				}
			});
			System.out.println(profile.displayName() + C.GRAY + " \u00BB " + Hex.LIGHT_GRAY + event.getMessage());

			if (!plugin().isDevMode()) {
				DiscordSRV.getPlugin()
				          .getMainTextChannel()
				          .sendMessage(profile.displayNameStripped() + " \u00BB " + event.getMessage())
				          .queue();
			}
		}
	}

}
