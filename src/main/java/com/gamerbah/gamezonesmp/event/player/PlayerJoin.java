package com.gamerbah.gamezonesmp.event.player;
/* Created by GamerBah on 8/7/2016 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.manager.DataManager;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.Hex;
import com.gamerbah.gamezonesmp.util.message.M;
import com.gamerbah.gamezonesmp.util.task.player.AFKRunnable;
import github.scarsz.discordsrv.DiscordSRV;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;
import java.util.UUID;

public record PlayerJoin(GameZoneSMP plugin) implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(AsyncPlayerPreLoginEvent event) {
		if (!plugin.getServer().getWhitelistedPlayers().contains(Bukkit.getOfflinePlayer(event.getUniqueId()))) {
			String message = C.RED + C.BOLD + "You're not on the whitelist!\n\n" + C.YELLOW +
			                 "If you believe this is an error, please\n" + C.YELLOW +
			                 "contact an Administrator via Discord.";
			event.setKickMessage(message);
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, message);
		} else {
			if (plugin.getDataManager().getDataSource().isClosed()) {
				plugin.setDataManager(new DataManager(plugin));
			}
			GameProfile profile = plugin.getProfileManager().getProfile(event.getUniqueId());
			if (profile == null) {
				profile = plugin.getProfileManager().createProfile(event.getName(), event.getUniqueId());
				// TODO
				// plugin.getGlobalStats().setTotalUniqueJoins(plugin.getGlobalStats().getTotalUniqueJoins() + 1);
			}

			if (profile == null) {
				event.setKickMessage(C.RED + C.BOLD + "Unable to join!\n\n" + C.GRAY + C.ITALIC +
				                     "\"Someone's poisoned the water hole!\"\n\n" + C.GRAY +
				                     "In other words, something went wrong on our end, and we\n" + C.GRAY +
				                     "weren't able to create your server data correctly!\n\n" + C.GOLD +
				                     "Server admins have been notified of the issue!");
				event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, event.getKickMessage());

				M.staffMessage(true, C.GRAY + "[" + C.DARK_AQUA + "STAFF" + C.GRAY + "] " + C.RED + C.BOLD + "Error! " +
				                     C.GRAY + "Unable to create profile data for " + C.GOLD + event.getName());
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player      player  = event.getPlayer();
		GameProfile profile = plugin.getProfileManager().getCachedProfile(player.getUniqueId());

		event.setJoinMessage(null);

		if (!player.hasPlayedBefore()) {
			plugin.respawn(player, false);
		}

		if (!plugin.isFrozen()) {
			player.setWalkSpeed(0.2F);
			player.setFlySpeed(0.1F);
		}

		if (player.getGameMode() == GameMode.CREATIVE) {
			plugin.getCreative().add(player.getUniqueId());
		}

		if (profile != null) {
			var join    = C.DARK_GRAY + "[" + C.GREEN + "+" + C.DARK_GRAY + "] " + profile.getRank().getColor();
			var newJoin = C.DARK_GRAY + "[" + C.PINK + "+" + C.DARK_GRAY + "] " + profile.getRank().getColor();

			String joinMessage = "";

			if (profile.isSilent()) {
				M.staffMessage(Rank.MODERATOR, join + event.getPlayer().getName() + C.GRAY + " (Silent Join)");
			} else {
				if (!player.hasPlayedBefore()) {
					joinMessage = newJoin + event.getPlayer().getName();
				} else {
					joinMessage = join + event.getPlayer().getName();
				}
				String finalJoinMessage = joinMessage;
				M.broadcast(finalJoinMessage);
				plugin.getServer()
				      .getOnlinePlayers()
				      .forEach(p -> p.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 0.7F));
			}
			AFKRunnable.reset(player);
			player.setPlayerListName(profile.displayName());
			player.setCustomName(profile.displayName());
			player.setCustomNameVisible(true);
			player.setInvisible(false);

			plugin.getScoreboardManager().assign(player);

			if (!plugin.isDevMode()) {
				plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
					String id = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player.getUniqueId());
					if (id == null) {
						int token = Math.abs(UUID.randomUUID().hashCode() + new Random().nextInt(100));
						plugin.getDiscordLink().put(token, player.getUniqueId());
						plugin.getLinkMap().put(player.getUniqueId(), token);

						EventSound.playSound(player, EventSound.CHAT_TAGGED);
						player.sendMessage(" ");
						player.sendMessage(M.ARROW_INFO + C.GOLD + "You haven't linked your Discord account yet!");

						var c = new TextComponent();

						String link = String.format("Use %s!link %sin %s#bot-spam %sto link your account!", C.WHITE,
						                            C.GOLD, C.GRAY, C.GOLD);

						var hover   = new Text("Click to go to" + C.GRAY + " #bot-spam");
						var discord = "https://discord.com/channels/814683619217244170/825887002418216960";

						c.setText(M.ARROW_INFO + C.GOLD + link);
						c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover));
						c.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, discord));
						player.spigot().sendMessage(c);
						player.sendMessage(" ");

						c = new TextComponent();
						c.setText(M.ARROW_INFO + C.AQUA + "Click here to copy the command to your clipboard!");
						c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to copy!")));
						c.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "!link " + token));
						player.spigot().sendMessage(c);
						player.sendMessage(" ");
					}
				}, 60L);
			} else {
				var m1 = Hex.SLATE + "This server is for " + Hex.DARK_RED + C.BOLD + "development" + Hex.SLATE + ".";
				var m2 = Hex.SLATE + "Frequent server reloads will happen for changes.";
				var m3 = Hex.ORANGE + "This is not a production environment.";

				player.sendMessage(" ");
				player.sendMessage(m1);
				player.sendMessage(m2);
				player.sendMessage(" ");
				player.sendMessage(m3);
				player.sendMessage(" ");
			}

			player.sendMessage(M.ARROW_INFO + "Your current balance is " + Hex.GREEN + "$" + profile.getDollars());
		}
	}

}
