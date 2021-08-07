package com.gamerbah.gamezonesmp.command;

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.gui.shop.ShopManagerGUI;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.M;
import com.gamerbah.gamezonesmp.util.shop.ItemShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public record ShopCommand(GameZoneSMP plugin) implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player player)) {
			M.error(sender, "This command must be executed by a player");
			return false;
		}

		var profile = plugin.getProfileManager().getProfile(player.getUniqueId());
		assert profile != null;

		if (args.length != 2) {
			if (profile.hasRank(Rank.OWNER)) {
				M.incorrectUsage(sender, "/shop <create/manage/view> <shop>");
			} else {
				M.incorrectUsage(sender, "/shop <manage/view> <shop>");
			}
			return false;
		}

		// TODO: Player Shop Handling

		if (profile.hasRank(Rank.ADMIN)) {
			if (plugin.getShopManager().getServerShops().containsKey(args[1])) {
				var shop = plugin.getShopManager().getServerShops().get(args[1]);
				switch (args[0].toLowerCase(Locale.ROOT)) {
					case "manage" -> new ShopManagerGUI(plugin, player, shop).build(player).open();
					case "view" -> shop.build(player).open();
					case "create" -> M.error(sender, "There is already a shop named " + C.GOLD + args[1].toLowerCase());
				}
			} else {
				if ("create".equals(args[0].toLowerCase(Locale.ROOT))) {
					var shop = new ItemShop(plugin, args[1].toLowerCase(), null, new ArrayList<>());
					plugin.getShopManager().getServerShops().put(args[1].toLowerCase(), shop);
					M.success(sender, "Created new shop named " + C.GOLD + args[1].toLowerCase());
					new ShopManagerGUI(plugin, player, shop).build(player).open();
					return true;
				} else {
					M.error(sender, "Unable to find shop with name " + C.GOLD + args[1]);
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		var options = new ArrayList<String>();
		if (sender instanceof Player player) {
			var profile = plugin.getProfileManager().getProfile(player.getUniqueId());
			assert profile != null;
			if (args.length == 1) {
				if (profile.hasRank(Rank.OWNER)) options.add("create");
				options.add("manage");
				options.add("view");
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("manage")) {
					if (profile.hasRank(Rank.ADMIN)) {
						options.addAll(plugin.getShopManager().getServerShops().keySet());
						options.sort(String::compareTo);
					}
				}
			}
		}
		return options;
	}

}
