package com.gamerbah.gamezonesmp.util.task.player;
/* Created by GamerBah on 8/24/2016 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SleepTimer implements Runnable {

	private final GameZoneSMP plugin;

	public SleepTimer(GameZoneSMP plugin) {
		this.plugin = plugin;
	}

	public static void reset(final Player player) {
		GameZoneSMP.getInstance().getAfkTimer().put(player.getUniqueId(), 0);
	}

	@Override
	public void run() {
		World world = plugin.getServer().getWorld("GZS-S1");
		if (world != null) {
			if (plugin.getSleeping() > 0) {
				long time    = world.getTime();
				long newTime = time + (50L * plugin.getSleeping());
				if (newTime > 24000) {
					world.setTime(0);
					world.setWeatherDuration(0);
					world.setThundering(false);
					world.setGameRule(GameRule.RANDOM_TICK_SPEED, 3);
				} else {
					world.setTime(newTime);
					world.setGameRule(GameRule.RANDOM_TICK_SPEED, 3 * (50 * plugin.getSleeping()));
				}
			} else {
				world.setGameRule(GameRule.RANDOM_TICK_SPEED, 3);
			}
		}
	}

}