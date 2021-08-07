package com.gamerbah.gamezonesmp.util.integration;
/* Created by GamerBah on 4/29/20 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePostProcessEvent;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class DiscordSRVListener {

	private final GameZoneSMP plugin;

	public DiscordSRVListener(GameZoneSMP plugin) {
		this.plugin = plugin;
	}

	@Subscribe(priority = ListenerPriority.MONITOR)
	public void discordMessageReceived(DiscordGuildMessageReceivedEvent event) {
		User        user    = event.getAuthor();
		TextChannel channel = event.getChannel();
		String      message = event.getMessage().getContentStripped();

		if (event.getMessage().getContentStripped().startsWith("!link") && channel.getName().equals("bot-spam")) {
			String[] split = message.split(" ");
			if (split.length > 2) {
				event.getChannel().sendMessage("You need to enter the token given when you join the server: `!link <token>`").queue();
			} else {
				int token;
				try {
					token = Integer.parseInt(split[1]);
					if (plugin.getDiscordLink().containsKey(token)) {
						UUID uuid = plugin.getDiscordLink().get(token);
						DiscordSRV.getPlugin().getAccountLinkManager().link(user.getId(), plugin.getDiscordLink().get(token));
						plugin.getLinkMap().remove(uuid);
						plugin.getDiscordLink().remove(token);
						event.getChannel().sendMessage("Account successfully linked to Minecraft!").queue();
						Player player = Objects.requireNonNull(plugin.getServer().getPlayer(uuid));
						player.sendMessage(C.GREEN + "Account successfully linked to Discord!");
						EventSound.playSound(player, EventSound.ACTION_SUCCESS);
					} else {
						event.getChannel()
						     .sendMessage("Token is unavailable or may have expired! Try rejoining the server to get a new key.")
						     .queue();
					}
				} catch (NumberFormatException e) {
					event.getChannel().sendMessage("Invalid token. Your token should only contain numbers.").queue();
				}
			}
		}

		if (event.getMessage().getContentStripped().startsWith("!whitelist") && channel.getName().equals("bot-spam")) {
			String[] split = message.split(" ");
			if (split.length > 2) {
				event.getChannel().sendMessage("Enter only your username to be whitelisted! `!whitelist <username>`").queue();
			} else {
				String username = split[1];
				OfflinePlayer player = plugin.getServer().getOfflinePlayer(username);
				if (!player.isWhitelisted()) {
					player.setWhitelisted(true);
					event.getChannel().sendMessage("Account added to the Minecraft whitelist!").queue();
				} else {
					event.getChannel().sendMessage("That username already on the whitelist!").queue();
				}
				plugin.getServer().reloadWhitelist();
			}
		}
	}

	@Subscribe
	public void discordMessageProcessed(DiscordGuildMessagePostProcessEvent event) {
		User   user    = event.getAuthor();
		String message = event.getMessage().getContentStripped();

		UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(user.getId());
		if (uuid != null) {
			GameProfile profile = plugin.getProfileManager().getProfile(uuid);
			assert profile != null;
			event.setMinecraftMessage(Component.text(C.DARK_GRAY + "Discord> " + profile.displayName() + C.GRAY + " \u00BB " + C.GRAY + message));
			event.getMessage().delete().queue();
			event.getChannel().sendMessage(profile.displayNameStripped() + " \u00BB " + message).queue();
		}
	}

}
