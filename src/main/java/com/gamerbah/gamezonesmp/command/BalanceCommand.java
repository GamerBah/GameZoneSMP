package com.gamerbah.gamezonesmp.command;

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.Hex;
import com.gamerbah.gamezonesmp.util.message.M;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

public record BalanceCommand(GameZoneSMP plugin, String name) {

	// balance
	// balance top
	// balance position
	// balance <player>
	// balance modify <player> <amount>

	public String register() {
		var command  = new CommandAPICommand("balance");
		var top      = new CommandAPICommand("top");
		var position = new CommandAPICommand("position");
		var modify   = new CommandAPICommand("modify");

		command.withAliases("bal", "money")
		       .withSubcommand(top.executesPlayer((sender, args) -> {
			       getTopBalances(sender);
		       }))
		       .withSubcommand(position.executesPlayer((sender, args) -> {
			       getPosition(sender);
		       }))
		       .withSubcommand(modify.withRequirement(sender -> Rank.validate(sender, Rank.ADMIN))
		                             .withArguments(new EntitySelectorArgument("target",
		                                                                       EntitySelectorArgument.EntitySelector.ONE_PLAYER))
		                             .withArguments(new IntegerArgument("amount"))
		                             .executes((sender, args) -> {
			                             modifyBalance(sender, (Player) args[0], (Integer) args[1]);
		                             }))
		       .executesPlayer((sender, args) -> {
			       var profile = plugin.getProfileManager().getProfile(sender.getUniqueId());
			       assert profile != null;
			       M.info(sender, "Your current balance is " + Hex.GREEN + "$" + profile.getDollars());
		       })
		       .register();

		var withPlayer = new CommandAPICommand("balance");
		withPlayer.withRequirement(sender -> Rank.validate(sender, Rank.ADMIN))
		          .withAliases("bal", "money")
		          .withArguments(new EntitySelectorArgument("target", EntitySelectorArgument.EntitySelector.ONE_PLAYER))
		          .executes((sender, args) -> {
			          var target  = (Player) args[0];
			          var profile = plugin.getProfileManager().getProfile(target.getUniqueId());
			          assert profile != null;
			          M.info(sender, target.getName() + "'s balance is " + Hex.GREEN + "$" + profile.getDollars());
		          })
		          .register();

		return name;
	}

	private void getTopBalances(Player player) {
		var future = plugin.getThreadPool().submit(() -> {
			try (var sql = plugin.getDataManager().getDataSource().getConnection()) {
				var query     = "SELECT username, dollars FROM GameProfiles ORDER BY dollars DESC LIMIT 10;";
				var statement = sql.prepareStatement(query);

				var list = new ArrayList<String>();
				try (statement) {
					var result = statement.executeQuery();
					int p      = 1;
					while (result.next()) {
						var name    = result.getString("username");
						var balance = result.getInt("dollars");

						String color;
						switch (p) {
							case 1 -> color = Hex.GOLD + C.BOLD;
							case 2 -> color = Hex.SILVER + C.BOLD;
							case 3 -> color = Hex.BRONZE + C.BOLD;
							default -> color = Hex.SLATE;
						}
						p++;
						list.add(C.GRAY + "  - " + color + name + C.GRAY + " \u00BB " + Hex.GREEN + "$" + balance);
					}
					return list;
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		ArrayList<String> list = null;
		try {
			list = future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			M.log(Level.SEVERE, e, "Unable to get balance top");
		}

		if (list == null) {
			M.error(player, "Unable to get balance top");
		} else {
			M.info(player, "Top server balances:");
			list.forEach(player::sendMessage);
		}
	}

	private void getPosition(Player player) {
		Future<Integer> future = plugin.getThreadPool().submit(() -> {
			try (var sql = plugin.getDataManager().getDataSource().getConnection()) {
				var query = """
				            SELECT GP.position
				            FROM (SELECT ROW_NUMBER() OVER (ORDER BY dollars DESC)
				                             AS position,
				                         uuid,
				                         dollars
				                  FROM GameProfiles
				                  ORDER BY dollars DESC)
				                     as GP
				            WHERE uuid = ?;
				            """;
				var statement = sql.prepareStatement(query);
				statement.setString(1, player.getUniqueId().toString());

				try (statement) {
					var result = statement.executeQuery();
					result.next();
					return result.getInt("position");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
		int position;
		try {
			position = future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			M.log(Level.SEVERE, e, "Unable to get balance position");
			return;
		}

		if (position == 0) {
			M.error(player, "Unable to get your balance position");
		} else {
			M.info(player, "Your balance is " + C.GREEN + "#" + position);
		}
	}

	private void modifyBalance(CommandSender sender, Player target, int amount) {
		var profile = plugin.getProfileManager().getProfile(target.getUniqueId());
		assert profile != null;

		if (profile.getDollars() + amount < 0) {
			if (amount < 0) M.error(sender, "New balance cannot be less than 0");
			if (amount > 0) M.error(sender, "New balance is cannot be greater than 2,147,483,647");
		} else {

			profile.addDollars(amount);

			String amountString;
			if (amount < 0) amountString = C.RED + "-$" + (amount * -1);
			else amountString = C.GREEN + "+$" + amount;

			if (target != sender) {
				M.info(sender, "Modified " + profile.displayName() + C.GRAY + "'s balance " + amountString);
			}

			if (target.isOnline()) M.info(target, "Your balance has been modified " + amountString);

			plugin.getThreadPool().submit(() -> {
				String query = "UPDATE GameProfiles SET dollars = ? WHERE uuid = ?;";
				try (var sql = plugin.getDataManager().getDataSource().getConnection()) {
					var statement = sql.prepareStatement(query);
					statement.setInt(1, profile.getDollars());
					statement.setString(2, profile.getUuid().toString());
					statement.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			});
		}
	}

}
