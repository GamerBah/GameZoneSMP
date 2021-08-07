package com.gamerbah.gamezonesmp.data.profile;
/* Created by GamerBah on 6/18/2016 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.util.EventSound;
import com.gamerbah.gamezonesmp.util.message.C;
import com.gamerbah.gamezonesmp.util.message.Hex;
import lombok.Data;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Data
public class GameProfile {

	private final GameZoneSMP plugin;

	private final int  id;
	private final UUID uuid;

	private String name;
	private Rank   rank;

	private int dollars, tokens, referredBy, referrals;

	private boolean messaging, silent, watchdog;

	private LocalDateTime rewardLast, lastSeen;

	public GameProfile(GameZoneSMP plugin, final int id, final UUID uuid) {
		this.plugin = plugin;
		this.id = id;
		this.uuid = uuid;
	}

	public boolean hasRank(Rank rank) {
		return this.rank.getLevel() >= rank.getLevel();
	}

	public void addDollars(int amount) {
		this.dollars += amount;
	}

	public void removeDollars(int amount) {
		this.dollars -= amount;
	}

	public void addTokens(int amount) {
		this.tokens += amount;
	}

	public void removeTokens(int amount) {
		this.tokens -= amount;
	}

	public String displayName() {
		return rank.toString() + C.RESET + (hasRank(Rank.OWNER) ? C.WHITE : Hex.LIGHT_GRAY) + " " + name;
	}

	public String coloredName() {
		return rank.getColor() + name;
	}

	public String displayNameStripped() {
		return rank.getName().toUpperCase() + " " + name;
	}

	public boolean isOnline() {
		Player player = plugin.getServer().getPlayer(uuid);
		return player != null && player.isOnline();
	}

	public GameProfile playSound(EventSound eventSound) {
		EventSound.playSound(plugin.getServer().getPlayer(this.uuid), eventSound);
		return this;
	}

	public GameProfile sendMessage(String message) {
		Objects.requireNonNull(plugin.getServer().getPlayer(this.uuid)).sendMessage(message);
		return this;
	}

	public void fullSync() {
		// TODO: Use partial sync once data exceeds what should be done in a simple sync
		// partialSync();
		plugin.getThreadPool().submit(() -> {
			String query = """
			               UPDATE GameProfiles SET
			               username = ?,
			               `rank` = ?,
			               dollars = ?,
			               tokens = ?,
			               referrals = ?,
			               accepting_messages = ?,
			               silent = ?, watchdog = ?
			               WHERE uuid = ?;
			               """;
			try (var sql = plugin.getDataManager().getDataSource().getConnection()) {
				var statement = sql.prepareStatement(query);
				statement.setString(1, this.name);
				statement.setInt(2, this.rank.getId());
				statement.setInt(3, this.dollars);
				statement.setInt(4, this.tokens);
				statement.setInt(5, this.referrals);
				statement.setBoolean(6, this.messaging);
				statement.setBoolean(7, this.silent);
				statement.setBoolean(8, this.watchdog);
				statement.setString(9, this.uuid.toString());
				statement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

	public void partialSync() {
		/*plugin.getSessionManager().getService().execute(() -> {
			Session session = plugin.getSessionManager().openSession();
			session.beginTransaction();
			entity.setRank(this.rank.getId());

			session.merge(entity);
			session.getTransaction().commit();
			plugin.getSessionManager().closeSession(session);
		});*/
	}

	public String[] logInfo() {
		return new String[]{"id = " + id, "uuid = " + uuid, "name = " + name};
	}

}
