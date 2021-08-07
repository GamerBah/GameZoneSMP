package com.gamerbah.gamezonesmp.util.task.player;
/* Created by GamerBah on 8/23/2017 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.message.C;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RespawnTimer implements Runnable {

	private final GameZoneSMP plugin;

	private Player player;
	private Thread thread;
	private String threadName;

	public RespawnTimer(GameZoneSMP plugin, Player player) {
		this.plugin     = plugin;
		this.player     = player;
		this.threadName = player.getName() + "-RepawnTimer";
	}

	public void run() {
		try {
			Thread.sleep(3000);
			player.sendTitle(C.RED + C.BOLD + "You died!", C.GRAY + "Respawning in 5", 5, 25, 0);
			for (int i = 4; i > 0; i--) {
				player.sendTitle(C.RED + C.BOLD + "You died!", C.GRAY + "Respawning in " + i, 0, 25, 0);
				Thread.sleep(1000);
			}
		} catch (InterruptedException exception) {
			exception.printStackTrace();
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> plugin.respawn(player, true));
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this, threadName);
			thread.start();
		}
	}

}
