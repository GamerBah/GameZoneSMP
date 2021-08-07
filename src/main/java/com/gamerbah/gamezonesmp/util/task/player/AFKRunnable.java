package com.gamerbah.gamezonesmp.util.task.player;
/* Created by GamerBah on 8/24/2016 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import org.bukkit.entity.Player;

import java.util.Objects;

public class AFKRunnable implements Runnable {

	private final GameZoneSMP plugin;

	public AFKRunnable(GameZoneSMP plugin) {
		this.plugin = plugin;
	}

	public static void reset(final Player player) {
		GameZoneSMP.getInstance().getAfkTimer().put(player.getUniqueId(), 0);
	}

	@Override
	public void run() {
		plugin.getAfkTimer().forEach((uuid, time) -> {
			if (time < 600) {
				plugin.getAfkTimer().put(uuid, ++time);
			}
			if (time == 600) {
				if (!plugin.getAfk().contains(uuid)) {
					Objects.requireNonNull(plugin.getServer().getPlayer(uuid)).chat("/afk");
				}
			}
		});
	}

}