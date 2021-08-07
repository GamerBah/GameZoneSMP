package com.gamerbah.gamezonesmp.util.task.server;
/* Created by GamerBah on 1/31/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;

import java.io.File;

public class UpdateRunnable implements Runnable {

	private final GameZoneSMP plugin;

	public UpdateRunnable(GameZoneSMP plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		if (plugin.getUpdateManager().getUpdateFile() == null || !plugin.getUpdateManager().getUpdateFile().exists()) {
			File updateFile = new File(plugin.getServer().getUpdateFolderFile().getPath() + File.separator + "GameZoneSMP.jar");
			if (updateFile.exists() && !plugin.getUpdateManager().isAwaitingUpdate() && !plugin.getUpdateManager().isUpdating()) {
				plugin.getUpdateManager().setAwaitingUpdate(true);
				plugin.getProfileManager()
				      .getProfiles()
				      .stream()
				      .distinct()
				      .filter(profile -> profile.hasRank(Rank.MODERATOR) && profile.isOnline())
				      .forEach(profile -> {
					      profile.sendMessage(" ");
					      profile.sendMessage(C.GREEN + "GameZoneSMP is ready to update!");
					      profile.sendMessage(C.GRAY + "Use " + C.RED + "/reload plugin" + C.GRAY + " to start the update");
					      profile.sendMessage(" ");
					      profile.playSound(EventSound.ACTION_SUCCESS);
				      });
				plugin.getLogger().info("GameZoneSMP is ready to update!");
				plugin.getLogger().info("Use '/reload plugin' to start the update");
			}
		}
	}

}
