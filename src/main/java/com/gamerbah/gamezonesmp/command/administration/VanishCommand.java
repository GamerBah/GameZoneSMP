package com.gamerbah.gamezonesmp.command.administration;

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.M;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VanishCommand implements CommandExecutor {

	private final GameZoneSMP plugin;

	public VanishCommand(GameZoneSMP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!(sender instanceof Player player)) {
			return false;
		}

		var profile = plugin.getProfileManager().getProfile(player.getUniqueId());
		assert profile != null;

		if (profile.hasRank(Rank.ADMIN)) {
			player.setInvisible(!player.isInvisible());
			var visible = player.isInvisible() ? C.GREEN + "invisible" : C.RED + "visible";
			M.success(player, "You are now " + visible);
		}

		return false;
	}

}
