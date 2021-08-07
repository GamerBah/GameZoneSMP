package com.gamerbah.gamezonesmp.command.administration;
/* Created by GamerBah on 8/30/19 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.M;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class WorldCommand implements CommandExecutor {

	private final GameZoneSMP plugin;

	private HashMap<Player, String> confirmation = new HashMap<>();

	public WorldCommand(GameZoneSMP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player      player  = (Player) sender;
		GameProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
		if (profile != null) {
			if (args.length == 0) {
				String s;
				if (profile.hasRank(Rank.OWNER)) {
					s = "create/delete/go/list";
				} else {
					s = "go/list";
				}
				M.incorrectUsage(player, "/world <" + s + ">");
				return true;
			}

			if (args[0].equalsIgnoreCase("list")) {
				StringBuilder worlds = new StringBuilder();
				Bukkit.getServer()
				      .getWorlds()
				      .stream()
				      .filter(world -> !player.getWorld().equals(world) && world.getEnvironment() != World.Environment.NETHER && world.getEnvironment() != World.Environment.THE_END)
				      .forEach(world -> worlds.append(world.getName()).append(", "));
				if (worlds.toString().isEmpty()) {
					player.sendMessage(C.PINK + "There are no currently available worlds to go to");
				} else {
					player.sendMessage(C.PINK + "Available Worlds: " + C.GRAY + worlds.toString().trim().substring(0, worlds.length() - 2));
				}
				return true;
			}

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("create")) {
					if (!profile.hasRank(Rank.OWNER)) {
						M.noPermission(player);
					} else {
						M.incorrectUsage(player, "/world create <name> [seed]");
					}
				} else if (args[0].equalsIgnoreCase("delete")) {
					if (!profile.hasRank(Rank.OWNER)) {
						M.noPermission(player);
					} else {
						M.incorrectUsage(player, "/world delete <name>");
					}
				} else if (args[0].equalsIgnoreCase("go")) {
					M.incorrectUsage(player, "/world go <name>");
				} else {
					String s;
					if (profile.hasRank(Rank.OWNER)) {
						s = "create/delete/go/list";
					} else {
						s = "go/list";
					}
					M.incorrectUsage(player, "/world <" + s + ">");
					return true;
				}
				return true;
			}

			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("create")) {
					if (!profile.hasRank(Rank.OWNER)) {
						M.noPermission(player);
					} else {
						String name = args[1];
						if (name.trim().isEmpty()) {
							player.sendMessage(C.RED + "Name cannot be empty!");
							EventSound.playSound(player, EventSound.ACTION_FAIL);
						} else {
							player.sendMessage(C.PINK + "Creating new world \"" + C.GRAY + args[1] + C.PINK + "\"...");
							Bukkit.getServer().createWorld(WorldCreator.name(args[1]));
							player.sendMessage(C.PINK + "World \"" + C.GRAY + args[1] + C.PINK + "\" successfully created!");
							player.sendMessage(C.GRAY + "Travel there with " + C.RED + "/world go " + args[1]);
							EventSound.playSound(player, EventSound.ACTION_SUCCESS);
						}
					}
				} else if (args[0].equalsIgnoreCase("delete")) {
					if (!profile.hasRank(Rank.OWNER)) {
						M.noPermission(player);
					} else {
						String name = args[1];
						if (name.trim().isEmpty()) {
							player.sendMessage(C.RED + "Name cannot be empty!");
							EventSound.playSound(player, EventSound.ACTION_FAIL);
						} else {
							if (!confirmation.containsKey(player)) {
								World world = Bukkit.getWorld(args[1]);
								if (world == null) {
									player.sendMessage(C.RED + "Unable to find world \"" + C.GRAY + args[1] + C.RED + "\"");
									EventSound.playSound(player, EventSound.ACTION_FAIL);
								} else if (world.equals(player.getWorld())) {
									player.sendMessage(C.RED + "You can't delete a world you are currently in!");
									player.sendMessage(C.GRAY + "Go to an available world first, then delete this one");
									EventSound.playSound(player, EventSound.ACTION_FAIL);
								} else if (!world.getPlayers().isEmpty()) {
									player.sendMessage(C.RED + "There are currently players in \"" + C.GRAY + args[1] + C.RED + "\"");
									player.sendMessage(C.GRAY + "Make sure all players have left the world before deleting!");
									EventSound.playSound(player, EventSound.ACTION_FAIL);
								} else {
									player.sendMessage(C.GOLD + "Are you sure you want to delete \"" + C.GRAY + world.getName() + C.GOLD + "\"?");
									player.sendMessage(C.RED + "This can't be undone!");
									player.sendMessage("");
									player.sendMessage(C.YELLOW + "Run the command again to confirm and delete " + "the world");
									EventSound.playSound(player, EventSound.CLICK);
									confirmation.put(player, world.getName());
									Bukkit.getScheduler().runTaskLater(plugin, () -> {
										if (confirmation.containsKey(player)) {
											confirmation.remove(player);
											player.sendMessage(C.RED + "Deletion of world \"" + C.GRAY + world.getName() + C.RED + "\" canceled");
											EventSound.playSound(player, EventSound.COMMAND_NEEDS_CONFIRMATION);
										}
									}, 200L);
								}
							} else {
								if (confirmation.get(player).equals(args[1])) {
									confirmation.remove(player);
									World world = Bukkit.getWorld(args[1]);
									player.sendMessage(C.PINK + "Deleting world...");
									Bukkit.unloadWorld(world, false);
									File worldData = world.getWorldFolder();
									try {
										FileUtils.deleteDirectory(worldData);
									} catch (IOException e) {
										e.printStackTrace();
									}
									player.sendMessage(C.PINK + "World \"" + C.GRAY + args[1] + C.PINK + "\" successfully deleted!");
									EventSound.playSound(player, EventSound.ACTION_SUCCESS);
								}
							}
						}
					}
				} else if (args[0].equalsIgnoreCase("go")) {
					World world = Bukkit.getWorld(args[1]);
					if (world == null) {
						player.sendMessage(C.RED + "Unable to find that world!");
						player.sendMessage(C.GRAY + "Use " + C.PINK + "/world list" + C.GRAY + " to show available worlds");
						EventSound.playSound(player, EventSound.ACTION_FAIL);
					} else {
						if (world.equals(player.getWorld())) {
							player.sendMessage(C.RED + "You are currently in that world!");
							player.sendMessage(C.GRAY + "Use " + C.PINK + "/world list" + C.GRAY + " to show available worlds");
							EventSound.playSound(player, EventSound.ACTION_FAIL);
						} else {
							// Save data from current world
							String currentPath = "worlds." + player.getWorld().getName() + "." + player.getUniqueId() + ".";
							plugin.getConfig().set(currentPath + "data.health", player.getHealth());
							plugin.getConfig().set(currentPath + "data.hunger", player.getFoodLevel());
							plugin.getConfig().set(currentPath + "data.saturation", player.getSaturation());
							plugin.getConfig().set(currentPath + "data.level", player.getLevel());
							plugin.getConfig().set(currentPath + "data.exp", player.getExp());
							plugin.getConfig().set(currentPath + "location.x", player.getLocation().getX());
							plugin.getConfig().set(currentPath + "location.y", player.getLocation().getY());
							plugin.getConfig().set(currentPath + "location.z", player.getLocation().getZ());
							plugin.getConfig().set(currentPath + "location.pitch", player.getLocation().getPitch());
							plugin.getConfig().set(currentPath + "location.yaw", player.getLocation().getYaw());
							for (int i = 0; i < 45; i++) {
								ItemStack item = player.getInventory().getItem(i) == null ? new ItemStack(Material.AIR) : player.getInventory().getItem(i);
								plugin.getConfig().set(currentPath + "inventory." + i, item);
							}
							plugin.getConfig().set(currentPath + "inventory.offhand", player.getInventory().getItemInOffHand());
							plugin.saveConfig();

							// Load data from new world
							String toPath = "worlds." + world.getName() + "." + player.getUniqueId() + ".";
							if (plugin.getConfig().get(toPath.substring(0, toPath.length() - 1)) == null) {
								// Player hasn't been to this world yet, spawn them at the world spawn point
								player.teleport(world.getSpawnLocation());
								player.setHealth(20);
								player.setFoodLevel(20);
								player.setSaturation(20);
								player.setLevel(0);
								player.setExp(0F);
								player.getInventory().clear();
								player.setFireTicks(0);
								player.setNoDamageTicks(60);
							} else {
								double x     = plugin.getConfig().getDouble(toPath + "location.x");
								double y     = plugin.getConfig().getDouble(toPath + "location.y");
								double z     = plugin.getConfig().getDouble(toPath + "location.z");
								float  pitch = (float) plugin.getConfig().getDouble(toPath + "location.pitch");
								float  yaw   = (float) plugin.getConfig().getDouble(toPath + "location.yaw");
								player.teleport(new Location(world, x, y, z, yaw, pitch));
								player.setHealth(plugin.getConfig().getDouble(toPath + "data.health"));
								player.setFoodLevel(plugin.getConfig().getInt(toPath + "data.hunger"));
								player.setSaturation((float) plugin.getConfig().getDouble(toPath + "data" + ".saturation"));
								player.setLevel(plugin.getConfig().getInt(toPath + "data.level"));
								player.setExp((float) plugin.getConfig().getDouble(toPath + "data.exp"));
								player.getInventory().clear();
								for (int i = 0; i < 45; i++) {
									player.getInventory().setItem(i, (ItemStack) plugin.getConfig().get(toPath + "inventory." + i));
								}
								player.getInventory().setItemInOffHand((ItemStack) plugin.getConfig().get(toPath + "inventory.offhand"));
								player.setNoDamageTicks(60);
							}
						}
					}
				} else {
					String s;
					if (profile.hasRank(Rank.OWNER)) {
						s = "create/delete/go/list";
					} else {
						s = "go/list";
					}
					M.incorrectUsage(player, "/world <" + s + ">");
					return true;
				}
				return true;
			}

			return false;
		}
		return false;
	}

}
