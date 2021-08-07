package com.gamerbah.gamezonesmp.command.administration;
/* Created by GamerBah on 4/22/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.M;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public record GameModeCommand(GameZoneSMP plugin) implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		GameProfile profile = plugin.getProfileManager().getCachedProfile(player.getUniqueId());
		assert profile != null;

		if (profile.hasRank(Rank.ADMIN)) {
			if (args.length == 0) {
				M.incorrectUsage(player, "/gamemode <mode> [player]");
				return false;
			} else {
				var target = player;
				var self   = true;
				if (args.length == 2) {
					target = plugin.getServer().getPlayer(args[1]);
					if (target == null) {
						M.error(player, "Unable to find player " + C.RED + args[1]);
						return false;
					}
					self = target.getUniqueId() == player.getUniqueId();
				}
				var playing = M.ARROW_SUCCESS + "Game mode changed to ";
				var changed = M.ARROW_SUCCESS + "Changed " + C.YELLOW + target.getName() + "'s game mode to ";
				switch (args[0].toLowerCase()) {
					case "survival" -> {
						if (target.getGameMode() == GameMode.SURVIVAL) {
							if (self) M.error(player, "You are already playing in survival");
							else M.error(player, "That player is already playing in survival");
							return false;
						}
						if (plugin.storeCreativeData(target)) {
							if (plugin.loadSurvivalData(target)) {
								plugin.getCreative().remove(target.getUniqueId());
								target.setGameMode(GameMode.SURVIVAL);
								target.sendMessage(playing + C.RED + "survival");
								if (!self) player.sendMessage(changed + C.RED + "survival");
								return true;
							} else {
								if (self) M.error(player, "Unable to load your survival data");
								else M.error(player, "Unable to load that player's survival data");
								return false;
							}
						} else {
							if (self) M.error(player, "Unable to save your creative data");
							else M.error(player, "Unable to save that player's creative data");
							return false;
						}
					}
					case "creative" -> {
						if (target.getGameMode() == GameMode.CREATIVE) {
							if (self) M.error(player, "You are already playing in creative");
							else M.error(player, "That player is already playing in creative");
							return false;
						}
						if (plugin.storeSurvivalData(target)) {
							if (plugin.loadCreativeData(target)) {
								togglePvp(target);
								target.setGameMode(GameMode.CREATIVE);
								target.sendMessage(playing + C.GREEN + "creative");
								if (!self) player.sendMessage(changed + C.GREEN + "creative");
								return true;
							} else {
								if (self) M.error(player, "Unable to load your creative data");
								else M.error(player, "Unable to load that player's creative data");
								return false;
							}
						} else {
							if (self) M.error(player, "Unable to save your survival data");
							else M.error(player, "Unable to save that player's survival data");
							return false;
						}
					}
					case "spectator" -> {
						if (target.getGameMode() == GameMode.SPECTATOR) {
							if (self) M.error(player, "You are already spectating");
							else M.error(player, "That player is already spectating");
							return false;
						}
						if (plugin.storeSurvivalData(target)) {
							if (plugin.loadCreativeData(target)) {
								togglePvp(target);
								target.setGameMode(GameMode.SPECTATOR);
								target.sendMessage(playing + C.GOLD + "spectator");
								if (!self) player.sendMessage(changed + C.GOLD + "spectator");
								return true;
							} else {
								if (self) M.error(player, "Unable to load your creative data");
								else M.error(player, "Unable to load that player's creative data");
								return false;
							}
						} else {
							if (self) M.error(player, "Unable to save your survival data");
							else M.error(player, "Unable to save that player's survival data");
							return false;
						}
					}
					default -> {
						if (self) M.error(player, "Invalid game mode " + C.GRAY + "\"" + args[0] + "\"");
						else M.error(player, "Invalid game mode ");
						return false;
					}
				}
			}
		} else {
			M.noPermission(player);
			return true;
		}
	}

	private void togglePvp(Player player) {
		if (plugin.getPvp().contains(player.getUniqueId())) {
			player.sendMessage(C.GRAY + "Player combat toggled " + C.RED + C.BOLD + "OFF");
			plugin.getServer()
			      .getOnlinePlayers()
			      .stream()
			      .filter(p -> p != player)
			      .forEach(p -> p.sendMessage(C.YELLOW + player.getName() + C.GRAY + " turned PVP " + C.RED + "off"));
			plugin.getPvp().remove(player.getUniqueId());
		}
		plugin.getCreative().add(player.getUniqueId());
	}

}
