package com.gamerbah.gamezonesmp.command;
/* Created by GamerBah on 4/26/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.integration.twitch.TwitchBot;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.M;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record TwitchCommand(GameZoneSMP plugin) implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;

		if (args.length == 0 || args.length > 2) {
			M.incorrectUsage(player, "/twitch <watch/disable/oauth>");
			return true;
		}

		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("watch")) {
				M.incorrectUsage(player, "/twitch watch <channel name>");
				return true;
			} else if (args[0].equalsIgnoreCase("disable")) {
				if (!plugin.getTwitchWatchers().containsKey(player.getUniqueId())) {
					M.error(player, "You're not watching any Twitch chat!");
					return true;
				} else {
					String value = plugin.getTwitchWatchers().remove(player.getUniqueId());
					player.sendMessage(C.GREEN + " \u00BB  " + C.GRAY + "Successfully stopped watching " + value +
					                   "'s Twitch chat");
					EventSound.playSound(player, EventSound.ACTION_SUCCESS);
				}
			} else if (args[0].equalsIgnoreCase("oauth")) {
				M.incorrectUsage(player, "/twitch oauth <token>");
				return true;
			} else {
				M.incorrectUsage(player, "/twitch <watch/disable>");
				return true;
			}
		} else {
			if (args[0].equalsIgnoreCase("watch")) {
				if (plugin.getTwitchWatchers().containsKey(player.getUniqueId())) {
					M.error(player, "You're already watching a Twitch chat!");
					player.sendMessage(C.RED + " \u00BB  " + C.GRAY + "Use " + C.YELLOW + "/twitch disable" + C.GRAY +
					                   " to disable watching your current channel");
					return true;
				}

				String token = plugin.getConfig().getString("OAuthToken." + player.getUniqueId());
				if (token == null) {
					player.sendMessage(" ");
					M.error(player, "You haven't set up your OAuth Token yet!");
					player.sendMessage(C.GRAY +
					                   " In order to watch a Twitch chat, you must use a Twitch-generated authentication token.");
					player.sendMessage(" ");

					TextComponent a = new TextComponent(" \u00BB  ");
					TextComponent b = new TextComponent("Click here to get your OAuth Token from Twitch");
					a.setColor(ChatColor.of("#C8C8C8"));
					b.setColor(ChatColor.of("#9147FF"));
					a.addExtra(b);

					Text c = new Text(
							C.GRAY + "Click to open a link to" + C.PINK + " https://twitchapps.com/tmi/");
					a.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://twitchapps.com/tmi/"));
					a.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, c));
					player.spigot().sendMessage(a);
					player.sendMessage(" ");
				} else {
					if (plugin.getTwitchIntegration()
					          .getBots()
					          .stream()
					          .noneMatch(twitchBot -> twitchBot.getChannel().equalsIgnoreCase(args[1]))) {

						TwitchBot bot = new TwitchBot(plugin, "GamerBot", token, args[1]);
						try {
							bot.start();
							plugin.getTwitchIntegration().getBots().add(bot);
							plugin.getTwitchWatchers().put(player.getUniqueId(), args[1]);
							player.sendMessage(
									C.GREEN + " \u00BB  " + C.GRAY + "Successfully watching " + bot.getChannel() +
									"'s Twitch chat");
							EventSound.playSound(player, EventSound.ACTION_SUCCESS);
							System.out.println("STARTING TWITCH INTEGRATION...");
						} catch (Throwable e) {
							M.error(player, "Unable to start watching Twitch chat!");
							player.sendMessage(C.RED + " \u00BB  " + C.GRAY + "Make sure the channel name is correct?");
						}
					} else {
						plugin.getTwitchWatchers().put(player.getUniqueId(), args[1]);
						player.sendMessage(
								C.GREEN + " \u00BB  " + C.GRAY + "Successfully watching " + args[1].toLowerCase() +
								"'s Twitch chat");
						EventSound.playSound(player, EventSound.ACTION_SUCCESS);
					}
				}
			} else if (args[0].equalsIgnoreCase("disable")) {
				M.incorrectUsage(player, "/twitch disable");
				return true;
			} else if (args[0].equalsIgnoreCase("oauth")) {
				if (!args[1].startsWith("oauth:")) {
					M.error(player, "Invalid OAuth Token. Make sure you copy the whole string.");
					return true;
				}
				plugin.getConfig().set("OAuthToken." + player.getUniqueId(), args[1]);
				plugin.saveConfig();
				player.sendMessage(C.GREEN + " \u00BB  " + C.GRAY + "OAuth Token successfully saved!");
				EventSound.playSound(player, EventSound.ACTION_SUCCESS);
				return true;
			}
		}


		return false;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (command.getName().equalsIgnoreCase("twitch")) {
			if (args.length == 1) {
				ArrayList<String> options = new ArrayList<>();
				options.add("disable");
				options.add("oauth");
				options.add("watch");
				return options;
			}
		}
		return null;
	}

}
