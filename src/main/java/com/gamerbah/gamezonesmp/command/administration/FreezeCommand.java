package com.gamerbah.gamezonesmp.command.administration;
/* Created by GamerBah on 1/31/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.M;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record FreezeCommand(GameZoneSMP plugin) implements CommandExecutor, Listener, TabCompleter {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		if (!(sender instanceof Player player)) {
			return true;
		}

		GameProfile gameProfile = plugin.getProfileManager().getProfile(player.getUniqueId());

		if (gameProfile != null) {
			if (!gameProfile.hasRank(Rank.MODERATOR) && !player.isOp()) {
				M.noPermission(player);
				return true;
			}

			if (args.length == 0) {
				player.sendMessage(C.RED + "/freeze <@a/[username]>");
				return true;
			}

			if (args[0].equalsIgnoreCase("@a")) {
				if (!plugin.isFrozen()) {
					plugin.setFrozen(true);
					Bukkit.getServer()
					      .broadcastMessage(
							      C.RED + "" + C.BOLD + "All players have been frozen by " + player.getName() + "!");
					for (Player p : Bukkit.getOnlinePlayers()) {
						GameProfile profile = plugin.getProfileManager().getProfile(p.getUniqueId());
						EventSound.playSound(player, EventSound.ACTION_SUCCESS);
						assert profile != null;
						if (!profile.hasRank(Rank.MODERATOR)) {
							p.setWalkSpeed(0F);
							p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, -50, true, false));
							p.setFoodLevel(6);
							p.setSaturation(0);
						}
					}
				} else {
					plugin.setFrozen(false);
					Bukkit.getServer().broadcastMessage(C.RED + "" + C.BOLD + "Movement has been re-enabled!");
					for (Player p : Bukkit.getOnlinePlayers()) {
						GameProfile pData = plugin.getProfileManager().getProfile(p.getUniqueId());
						if (pData != null && !pData.hasRank(Rank.MODERATOR)) {
							p.setWalkSpeed(0.2F);
						}
						EventSound.playSound(player, EventSound.ACTION_SUCCESS);
					}
					return true;
				}
			} else {
				Player target = plugin.getServer().getPlayer(args[0]);
				if (target == player) {
					player.sendMessage(C.RED + "You want to freeze yourself? But... why?");
					EventSound.playSound(player, EventSound.ACTION_FAIL);
					return true;
				}
				if (target == null) {
					player.sendMessage(C.RED + "That player isn't online!");
					EventSound.playSound(player, EventSound.ACTION_FAIL);
					return true;
				}

				if (plugin.getFrozenPlayers().contains(target.getUniqueId())) {
					plugin.getFrozenPlayers().remove(target.getUniqueId());
					target.setWalkSpeed(0.2F);
					target.removePotionEffect(PotionEffectType.JUMP);
					target.setFoodLevel(20);
					player.setSaturation(20);
					target.sendMessage(C.RED + "Your movement has been re-enabled!");
					EventSound.playSound(player, EventSound.ACTION_SUCCESS);

					player.sendMessage(C.GREEN + "You unfroze " + target.getName());
					EventSound.playSound(player, EventSound.ACTION_SUCCESS);
				} else {
					plugin.getFrozenPlayers().add(target.getUniqueId());
					target.setWalkSpeed(0F);
					target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 6000, -10, true, false));
					target.setFoodLevel(6);
					player.setSaturation(0);
					target.sendMessage(C.RED + "You have been frozen by " + player.getName() + "!");
					target.sendMessage(C.RED + "You will automatically be unfrozen in 5 minutes.");
					EventSound.playSound(player, EventSound.ACTION_SUCCESS);

					player.sendMessage(C.GREEN + "You have successfully frozen " + target.getName() + "!");
					player.sendMessage(C.RED + "They will automatically be unfrozen in 5 minutes.");
					EventSound.playSound(player, EventSound.ACTION_SUCCESS);


					plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
						if (plugin.getFrozenPlayers().contains(target.getUniqueId())) {
							plugin.getFrozenPlayers().remove(target.getUniqueId());
							target.setWalkSpeed(0.2F);
							target.removePotionEffect(PotionEffectType.JUMP);
							target.setFoodLevel(20);
							player.setSaturation(20);
							target.sendMessage(C.RED + "Your movement has been automatically re-enabled!");
							EventSound.playSound(player, EventSound.ACTION_SUCCESS);
						}
					}, 6000L);
				}
			}
			return true;
		}
		return false;
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		if (!(event.getEntityType().equals(EntityType.PLAYER))) {
			return;
		}

		if (plugin.isFrozen() || plugin.getFrozenPlayers().contains(event.getEntity().getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerHurtEntity(EntityDamageByEntityEvent event) {
		if (!(event.getEntityType().equals(EntityType.PLAYER) && event.getDamager().getType().equals(EntityType.PLAYER))) {
			return;
		}

		var damager = (Player) event.getDamager();
		var damaged = (Player) event.getEntity();

		if (plugin.isFrozen()) {
			damager.sendMessage(C.RED + "All players are frozen. You are unable to attack!");
			event.setCancelled(true);
		} else if (plugin.getFrozenPlayers().contains(damaged.getUniqueId())) {
			damager.sendMessage(C.RED + "That player was frozen by a Staff member. You are unable to attack them.");
			event.setCancelled(true);
		} else if (plugin.getFrozenPlayers().contains(damager.getUniqueId())) {
			damager.sendMessage(C.RED + "You are unable to attack since you are frozen.");
			event.setCancelled(true);
		}
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (command.getName().equalsIgnoreCase("freeze")) {
			if (args.length == 1) {
				ArrayList<String> options = new ArrayList<>();
				options.add("@a");
				plugin.getServer().getOnlinePlayers().forEach(player -> options.add(player.getName()));
				return options;
			}
		}
		return null;
	}

}
