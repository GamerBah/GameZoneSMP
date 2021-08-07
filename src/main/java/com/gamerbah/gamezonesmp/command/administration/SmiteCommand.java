package com.gamerbah.gamezonesmp.command.administration;
/* Created by GamerBah on 6/5/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.M;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record SmiteCommand(GameZoneSMP plugin) implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		GameProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
		assert profile != null;

		if (!profile.hasRank(Rank.OWNER)) {
			player.sendMessage(M.ARROW_ERROR + "You don't have permission to use this command");
			EventSound.playSound(player, EventSound.ACTION_FAIL);
			return true;
		}

		if (args.length != 1) {
			M.incorrectUsage(player, "/" + label + " <player>");
			return true;
		}

		Player target = plugin.getServer().getPlayer(args[0]);
		if (target == null || !target.isOnline()) {
			player.sendMessage(M.ARROW_ERROR + "That player isn't online");
			return true;
		}

		GameProfile targetProfile = plugin.getProfileManager().getProfile(target.getUniqueId());
		assert targetProfile != null;

		target.getWorld().strikeLightningEffect(target.getLocation());
		target.damage(0.5, player);

		plugin.getServer().broadcastMessage(profile.coloredName() + C.GRAY + " smites " + targetProfile.coloredName());

		return false;
	}

}
