package com.gamerbah.gamezonesmp.command;
/* Created by GamerBah on 8/15/2016 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.task.player.AFKRunnable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record AFKCommand(GameZoneSMP plugin) implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		GameProfile profile = plugin.getProfileManager().getCachedProfile(player.getUniqueId());
		assert profile != null;

		if (plugin.getAfk().contains(player.getUniqueId())) {
			plugin.getAfk().remove(player.getUniqueId());
			player.getDisplayName();
			player.setPlayerListName(profile.displayName());
			player.sendMessage(C.GRAY + "You are no longer AFK");
			plugin.getServer()
			      .getOnlinePlayers()
			      .stream()
			      .filter(p -> p != player)
			      .forEach(p -> p.sendMessage(C.GRAY + player.getName() + " is no longer AFK"));
			EventSound.playSound(player, EventSound.CLICK);
			AFKRunnable.reset(player);
		} else {
			player.getDisplayName();
			player.setPlayerListName(C.GRAY + "AFK - " + player.getDisplayName());
			player.sendMessage(C.GRAY + "You are now AFK");
			plugin.getServer()
			      .getOnlinePlayers()
			      .stream()
			      .filter(p -> p != player)
			      .forEach(p -> p.sendMessage(C.GRAY + player.getName() + " is now AFK"));
			EventSound.playSound(player, EventSound.CLICK);
			plugin.getAfk().add(player.getUniqueId());
		}
		return false;
	}

}
