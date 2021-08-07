package com.gamerbah.gamezonesmp.command.administration;
/* Created by GamerBah on 1/31/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.manager.UpdateManager;
import com.gamerbah.gamezonesmp.util.message.M;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record ReloadCommand(GameZoneSMP plugin) implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player player)) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("plugin")) {
					if (plugin.getUpdateManager().isAwaitingUpdate()) {
						if (plugin.getUpdateManager().isUpdating()) {
							sender.sendMessage("Another player is already running the update");
						}

						plugin.getUpdateManager().update(UpdateManager.UpdateType.PLUGIN);
						return true;
					} else {
						if (plugin.getUpdateManager().checkForUpdate()) {
							return true;
						} else {
							sender.sendMessage("There is no update available");
							return false;
						}
					}
				} else if (args[0].equalsIgnoreCase("config")) {
					plugin.reloadConfig();
					sender.sendMessage("Configs have been reloaded");
					return true;
				} else {
					sender.sendMessage("Usage: /reload <plugin/config>");
					return false;
				}
			} else {
				sender.sendMessage("Usage: /reload <plugin/config>");
				return false;
			}
		} else {
			GameProfile gameProfile = plugin.getProfileManager().getProfile(player.getUniqueId());
			if (gameProfile != null) {
				if (!gameProfile.hasRank(Rank.OWNER)) {
					M.noPermission(player);
				} else {
					if (args.length == 1) {
						if (args[0].equalsIgnoreCase("plugin")) {
							if (plugin.getUpdateManager().isAwaitingUpdate()) {
								if (plugin.getUpdateManager().isUpdating()) {
									player.sendMessage(M.ARROW_ERROR + "Another player is already running the update");
								}
								plugin.getUpdateManager().update(UpdateManager.UpdateType.PLUGIN);
								return true;
							} else {
								player.sendMessage(M.ARROW_ERROR +  "There is no update available");
								return false;
							}
						} else if (args[0].equalsIgnoreCase("config")) {
							plugin.reloadConfig();
							player.sendMessage(M.ARROW_SUCCESS + "Configs have been reloaded");
							return true;
						} else {
							M.incorrectUsage(player, "/reload <plugin/config>");
							return false;
						}
					} else {
						M.incorrectUsage(player, "/reload <plugin/config>");
						return false;
					}
				}
			}
		}
		return false;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (command.getName().equalsIgnoreCase("reload")) {
			if (args.length == 1) {
				ArrayList<String> options = new ArrayList<>();
				options.add("config");
				options.add("plugin");
				return options;
			}
		}
		return null;
	}

}
