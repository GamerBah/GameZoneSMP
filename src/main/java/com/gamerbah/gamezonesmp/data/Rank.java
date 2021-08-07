package com.gamerbah.gamezonesmp.data;
/* Created by GamerBah on 8/7/2016 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.message.C;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Rank {
	OWNER(9, "Owner", C.hex("#A900FF") + C.BOLD, 100),
	ADMIN(8, "Admin", C.hex("#D41D24") + C.BOLD, 75),
	MODERATOR(7, "Moderator", C.hex("#E600F4") + C.BOLD, 50),
	LEGEND(5, "Legend", C.hex("#E91E63") + C.BOLD, 15),
	DIAMOND(3, "Diamond", C.hex("#00E2DC") + C.BOLD, 8),
	PLATINUM(2, "Platinum", C.hex("#95BBC7") + C.BOLD, 6),
	GOLD(1, "Gold", C.hex("#D2B123") + C.BOLD, 4),
	GAMER(0, "Gamer", C.hex("#3498DB") + C.BOLD, 0);

	private final int    id;
	private final String name;
	private final String color;
	private final int    level;

	public static Rank fromString(final String name) {
		return Arrays.stream(Rank.values())
		             .filter(rank -> name.trim().equalsIgnoreCase(rank.toString()))
		             .findFirst()
		             .orElse(GAMER);
	}

	public static Rank fromId(final int id) {
		return Arrays.stream(Rank.values()).filter(rank -> id == rank.getId()).findFirst().orElse(GAMER);
	}

	@Override
	public String toString() {
		return color + name.toUpperCase();
	}

	public static boolean validate(CommandSender sender, Rank requiredRank) {
		if (sender instanceof Player player) {
			var profile = GameZoneSMP.getInstance().getProfileManager().getProfile(player.getUniqueId());
			assert profile != null;
			return profile.hasRank(requiredRank);
		} else {
			return true;
		}
	}

}
