package com.gamerbah.gamezonesmp.command.administration;

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.M;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenInvCommand implements CommandExecutor, TabCompleter {

	// openinv cosmetic
	// openinv blacksmith
	// openinv marketplace
	// openinv construction
	// openinv fishing
	// openinv library
	// openinv farming
	// openinv realestate

	private final GameZoneSMP plugin;

	public OpenInvCommand(GameZoneSMP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (sender instanceof Player player) {
			if (!player.isOp()) {
				return false;
			}
		}

		if (args.length != 2) {
			M.incorrectUsage(sender, "/openinv <inventory> <player>");
			return false;
		}

		var target = plugin.getServer().getPlayerExact(args[1]);
		if (target == null) {
			M.error(sender, "Unable to find player " + C.GOLD + args[1]);
			return false;
		}

		switch (args[0]) {
			case "cosmetic" -> {
				var shop = plugin.getShopManager().getServerShops().get("cosmetic");
				if (shop == null) {
					M.error(sender, "Unable to find shop named " + C.GOLD + args[0]);
				} else {
					shop.create();
					shop.build(target).open();
				}
			}
		}

		return false;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (sender instanceof Player) {
			if (args.length == 1) {
				var arguments = new String[]{
						"cosmetic", "blacksmith", "marketplace", "construction", "fishing", "library", "farming", "realestate"
				};
				Arrays.sort(arguments);
				return Arrays.asList(arguments);
			}
		}
		return new ArrayList<>();
	}

}
