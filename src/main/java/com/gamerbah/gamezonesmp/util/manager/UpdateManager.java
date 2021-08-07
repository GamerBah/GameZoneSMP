package com.gamerbah.gamezonesmp.util.manager;
/* Created by GamerBah on 1/31/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.PluginUtil;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.M;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;

public class UpdateManager {

	private final GameZoneSMP plugin;
	private final String      updatePath;

	@Getter
	@Setter
	private boolean updating;
	@Getter
	@Setter
	private boolean developmentMode;
	@Getter
	@Setter
	private boolean awaitingUpdate;

	@Getter
	private File updateFile;
	@Getter
	private File currentFile;

	public UpdateManager(GameZoneSMP plugin) {
		this.plugin      = plugin;
		this.updatePath  = plugin.getServer().getUpdateFolderFile().getParentFile().getPath();
		this.currentFile = new File(updatePath + File.separator + "GameZoneSMP.jar");
	}

	public boolean checkForUpdate() {
		File updateFile = new File(plugin.getServer().getUpdateFolderFile().getPath() + File.separator + "GameZoneSMP.jar");
		if (updateFile.exists() && !plugin.getUpdateManager().isAwaitingUpdate() && !plugin.getUpdateManager().isUpdating()) {
			plugin.getUpdateManager().setAwaitingUpdate(true);
			return true;
		}
		return false;
	}

	public void update(final UpdateType type) {
		updateFile = new File(plugin.getServer().getUpdateFolderFile().getPath() + File.separator + "GameZoneSMP.jar");
		Throwable preUpdate = runPreUpdate();
		if (preUpdate == null) {
			plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
				try {
					plugin.getProfileManager().sync();
					plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
						Result result = Result.SUCCESS;
						switch (type) {
							case PLUGIN -> {
								PluginUtil.unload(plugin);
								if (updateFile.exists()) {
									try {
										Files.move(Paths.get(currentFile.getPath()), Paths.get(
												plugin.getDataFolder().getPath() + File.separator +
												"GameZoneSMP.jar"), StandardCopyOption.REPLACE_EXISTING);
										currentFile = new File(plugin.getDataFolder().getPath() + File.separator +
										                       "GameZoneSMP.jar");
										Files.move(Paths.get(updateFile.getPath()), Paths.get(
												plugin.getServer().getUpdateFolderFile().getParentFile().getPath() +
												File.separator + "GameZoneSMP.jar"),
										           StandardCopyOption.REPLACE_EXISTING);
										setAwaitingUpdate(false);
									} catch (IOException exception) {
										M.log(Level.SEVERE, exception, "Unable to move update file!");
										result = deleteQueuedUpdate(updateFile);
									}
								}

								try {
									PluginUtil.load("GameZoneSMP");
								} catch (Throwable throwable) {
									M.log(Level.SEVERE, throwable, "Unable to load update file!",
									           "Performing rollback, hang tight...");
									result = rollback();
								}
							}
							case SERVER -> {
								try {
									PluginUtil.unload(plugin);
									plugin.getServer().reload();
								} catch (Throwable throwable) {
									M.log(Level.SEVERE, throwable, "Unable to perform global reload!",
									           "Entering Maintenance Mode...");
									result = enterMaintenance();
								}
							}
						}
						runPostUpdate(result);
					}, 60L);
				} catch (Throwable throwable) {
					M.log(Level.WARNING, throwable, "Unable to perform full-sync!", "Entering Maintenance Mode...");
					runPostUpdate(enterMaintenance());
				}
			}, 100);
		} else {
			M.log(Level.SEVERE, preUpdate, "Unable to complete pre-update!", "Reload cancelled.");
			runPostUpdate(Result.FAIL);
		}
	}

	private Result deleteQueuedUpdate(File file) {
		try {
			Files.delete(file.toPath());
		} catch (IOException exception) {
			plugin.getLogger().severe("Unable to delete queued jarfile!");
			exception.printStackTrace();
		}
		return Result.FAIL;
	}

	private Throwable runPreUpdate() {
		updating = true;
		plugin.getServer().broadcastMessage(C.RED + C.BOLD + "SERVER: " + C.GRAY + "Reloading in 5 seconds for an update. Hang in there!");
		plugin.setFrozen(true);
		try {
			plugin.getServer().getOnlinePlayers().forEach(player -> {
				EventSound.playSound(player, EventSound.CLICK);
				if (player.getGameMode() != GameMode.CREATIVE) {
					player.setGameMode(GameMode.ADVENTURE);
				}
				player.closeInventory();
				player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -100, true, false));
				player.setAllowFlight(true);
				player.setFlying(true);
				player.teleport(player.getLocation().add(0, 0.1, 0));
				player.setWalkSpeed(0F);
				player.setFlySpeed(0F);
				player.setInvulnerable(true);
				player.sendMessage(" ");
				player.sendMessage(C.YELLOW + "To prevent data corruption, you've been frozen.");
				player.sendMessage(C.YELLOW + "You'll be unfrozen once the update is complete.\n ");
			});
			plugin.getServer().getWorlds().forEach(world -> {
				world.getLivingEntities()
				     .stream()
				     .filter(entity -> entity.getType() != EntityType.PLAYER)
				     .forEach(entity -> entity.setAI(false));
			});
		} catch (Throwable e) {
			e.printStackTrace();
			return e;
		}
		return null;
	}

	private void runPostUpdate(final Result result) {
		plugin.getServer().broadcastMessage(C.RED + C.BOLD + "SERVER: " + C.GRAY + "Update was a " + result.getString() + C.GRAY + "!");
		updating = false;
		plugin.setFrozen(false);
		plugin.getServer().getOnlinePlayers().forEach(player -> {
			if (result == Result.SUCCESS) {
				EventSound.playSound(player, EventSound.ACTION_SUCCESS);
			} else {
				EventSound.playSound(player, EventSound.ACTION_FAIL);
			}
			player.setWalkSpeed(0.2F);
			player.setFlySpeed(0.1F);
			player.setInvulnerable(false);
			player.setNoDamageTicks(60);
			player.removePotionEffect(PotionEffectType.JUMP);
			if (player.getGameMode() != GameMode.CREATIVE) {
				player.setGameMode(GameMode.SURVIVAL);
				player.setFlying(false);
				player.setAllowFlight(false);
			}

		});
		plugin.getServer().getWorlds().forEach(world -> {
			world.getLivingEntities()
			     .stream()
			     .filter(entity -> entity.getType() != EntityType.PLAYER)
			     .forEach(entity -> entity.setAI(true));
		});
	}

	public Result enterMaintenance() {
		plugin.getServer().getOnlinePlayers().forEach(player -> {
			GameProfile gameProfile = plugin.getProfileManager().getCachedProfile(player.getUniqueId());
			if (gameProfile != null && !gameProfile.hasRank(Rank.LEGEND)) {
				player.kickPlayer(
						C.RED + "You were kicked because the server was put into\n" + C.GOLD + C.BOLD + "MAINTENANCE MODE\n\n" + C.AQUA
						+ "This means that we are fixing bugs, or found another issue we needed to take care of\n\n" + C.GRAY
						+ "We put the server into Maintenance Mode in order to reduce the risk of\n§7corrupting player data, etc. Check back soon!");
			}
			EventSound.playSound(player, EventSound.ACTION_FAIL);
		});
		setDevelopmentMode(true);
		plugin.getServer().broadcastMessage(C.RED + C.BOLD + "\nSERVER HAS BEEN PUT INTO " + C.GOLD + C.BOLD + "MAINTENANCE MODE\n ");
		return Result.MAINTENANCE;
	}

	private Result rollback() {
		try {
			Files.move(currentFile.toPath(),
			           Paths.get(plugin.getServer().getUpdateFolderFile().getParentFile().getPath() + File.separator + "GameZoneSMP.jar"),
			           StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException exception) {
			M.log(Level.SEVERE, exception, "Unable to load rollback jar!", "Entering Maintenance Mode...");
			return enterMaintenance();
		}
		try {
			PluginUtil.load("GameZoneSMP");
		} catch (Throwable throwable) {
			M.log(Level.WARNING, throwable, "Rollback jar has errors, but it'll have to do...");
		}
		return Result.ROLLBACK;
	}

	public enum UpdateType {
		SERVER,
		PLUGIN,
		MODULES
	}

	@AllArgsConstructor
	@Getter
	public enum Result {
		SUCCESS(C.GREEN + C.BOLD + "success"),
		FAIL(C.RED + C.BOLD + "failure"),
		ROLLBACK(FAIL.getString()),
		MAINTENANCE(FAIL.getString());

		private final String string;
	}

}
