package com.gamerbah.gamezonesmp.event.player;
/* Created by GamerBah on 8/9/2016 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.DeathChest;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.Hex;
import com.gamerbah.gamezonesmp.util.message.M;
import com.gamerbah.gamezonesmp.util.task.player.AFKRunnable;
import com.gamerbah.gamezonesmp.util.task.player.RespawnTimer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public record PlayerDeath(GameZoneSMP plugin) implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		var player   = event.getEntity();
		var killer   = player.getKiller();
		var location = player.getLocation();

		if (plugin.getPvp().remove(player.getUniqueId())) {
			player.sendMessage(M.ARROW_SUCCESS + "Player combat toggled " + C.RED + C.BOLD + "OFF");
		}

		if (plugin.getCreative().remove(player.getUniqueId())) {
			player.sendMessage(M.ARROW_SUCCESS + "Now playing in " + C.RED + "survival" + C.GRAY + " mode!");
			plugin.getDeaths().put(player.getUniqueId(), GameMode.CREATIVE);
		} else {
			plugin.getDeaths().put(player.getUniqueId(), GameMode.SURVIVAL);
		}

		if (player.getLastDamageCause() != null) {
			var voidDamage = player.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID;
			var totemMain  = player.getInventory().getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING;
			var totemOff   = player.getInventory().getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING;
			if (voidDamage && (totemMain || totemOff)) {
				player.setHealth(20.0);
				plugin.getRespawning().add(player.getUniqueId());
				plugin.getServer()
				      .getScheduler()
				      .runTaskLater(plugin, () -> plugin.getRespawning().remove(player.getUniqueId()), 40L);
				player.getActivePotionEffects().clear();
				plugin.respawn(player, true);
				player.setHealth(1.0);
				player.setVelocity(new Vector(0, 0, 0));
				var loc = location.clone().add(0, 0.1, 0);
				player.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc, 10, 0.2, 0.2, 0.2, 0.01);
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2400, 2, true, false));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 2400, 2, true, false));
				player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 2400, 1, true, false));
				player.playEffect(EntityEffect.TOTEM_RESURRECT);
			} else {
				if (player.getLastDamageCause().getCause() != EntityDamageEvent.DamageCause.VOID) {
					if (!event.getDrops().isEmpty()) {
						var drops = new ArrayList<>(event.getDrops());
						int exp   = 0;
						if (player.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.LAVA) {
							drops.removeIf(item -> {
								boolean netherite = !item.getType().toString().startsWith("NETHERITE");
								boolean shulker   = !item.getType().toString().contains("SHULKER_BOX");
								return netherite && shulker;
							});
						} else {
							exp = event.getDroppedExp();
						}
						int blockX     = player.getLocation().getBlockX();
						int blockY     = player.getLocation().getBlockY();
						int blockZ     = player.getLocation().getBlockZ();
						var deathLoc   = new Location(player.getWorld(), blockX, blockY, blockZ);
						var deathChest = new DeathChest(plugin, player.getUniqueId(), deathLoc, drops, exp);
						deathChest.spawn();
						plugin.getDeathChests().add(deathChest);
						event.setDroppedExp(0);
						event.getDrops().clear();
					}
				} else {
					player.teleport(new Location(player.getWorld(), location.getX(), 0, location.getZ()));
				}

				player.setHealth(20.0);
				player.setSaturation(20.0F);
				player.setFoodLevel(20);
				player.setLevel(0);
				player.setExp(0);
				player.setGameMode(GameMode.SPECTATOR);
				player.getActivePotionEffects().clear();

				player.sendTitle(Hex.DARK_RED + C.BOLD + "You died!", "", 10, 60, 0);

				new RespawnTimer(plugin, player).start();

				if (location.getWorld() != null) {
					String world;
					switch (location.getWorld().getEnvironment()) {
						case NETHER -> world = Hex.DARK_RED + "Nether";
						case THE_END -> world = C.PINK + "End";
						default -> world = C.GREEN + "Overworld";
					}

					String message = C.RED + player.getName() + C.GRAY + " ";
					String type    = "";
					if (killer == null) {
						String path = "deaths.";
						switch (player.getLastDamageCause().getCause()) {
							case FALL -> type = "fall";
							case LAVA -> type = "lava";
							case DROWNING -> type = "drowning";
							case FIRE, FIRE_TICK -> type = "fire";
							case VOID -> type = "void";
							case BLOCK_EXPLOSION -> type = "blockExplosion";
							case DRAGON_BREATH -> type = "dragon";
							case ENTITY_EXPLOSION -> type = "entityExplosion";
							case FLY_INTO_WALL -> type = "flying";
							case HOT_FLOOR -> type = "hotFloor";
							case MAGIC -> type = "magic";
							case STARVATION -> type = "starving";
							case SUFFOCATION -> type = "suffocation";
							case WITHER -> type = "wither";
							case FALLING_BLOCK -> type = "fallingBlock";
							case LIGHTNING -> type = "lightning";
							case CRAMMING -> type = "cramming";
							case FREEZE -> type = "freezing";
							case POISON -> type = "poison";
						}
						if (!type.isEmpty()) {
							var list   = plugin.getPluginMessages().getStringList(path + type);
							int random = ThreadLocalRandom.current().nextInt(list.size());
							message += list.get(random);
						} else {
							if (event.getDeathMessage() != null) {
								String sub = event.getDeathMessage().substring(player.getName().length());
								message = event.getDeathMessage()
								               .replace(player.getName(), C.RED + player.getName())
								               .replace(sub, C.GRAY + sub);
							}
						}
					}
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						var lString = location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
						var comp    = new ComponentBuilder(message);
						var hover   = new Text(C.WHITE + lString + C.GRAY + " in the " + world);
						p.spigot().sendMessage(comp.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover)).create());
					}
					Bukkit.getServer().getLogger().log(Level.INFO, message);
				}
			}
		}
		AFKRunnable.reset(player);
		event.setDeathMessage(null);
	}

}
