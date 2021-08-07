package com.gamerbah.gamezonesmp.util.manager;
/* Created by GamerBah on 7/9/2018 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import com.gamerbah.gamezonesmp.data.profile.GameProfile;
import com.gamerbah.gamezonesmp.util.message.M;
import lombok.Getter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

public final class GameProfileManager {

	private final GameZoneSMP plugin;

	@Getter
	private final ArrayList<GameProfile> profiles = new ArrayList<>();

	public GameProfileManager(GameZoneSMP plugin) {
		this.plugin = plugin;
	}

	public void addProfile(final GameProfile gameProfile) {
		//plugin.log("added profile: " + gameProfile.getName());
		getProfiles().add(gameProfile);
	}

	public GameProfile createProfile(String name, UUID uuid) {
		Future<GameProfile> future = plugin.getThreadPool().submit(() -> {
			try (var sql = plugin.getDataManager().getDataSource().getConnection()) {
				var query     = "INSERT INTO GameProfiles (uuid, username) VALUES (?, ?)";
				var statement = sql.prepareStatement(query);
				statement.setString(1, uuid.toString());
				statement.setString(2, name);
				statement.executeUpdate();

				query = "SELECT * FROM GameProfiles WHERE uuid = ? LIMIT 1";
				statement = sql.prepareStatement(query);
				statement.setString(1, uuid.toString());
				var result = statement.executeQuery();

				GameProfile profile = null;
				try {
					result.next();
					int id = result.getInt("id");

					profile = new GameProfile(plugin, id, uuid);
					profile.setName(result.getString("username"));
					profile.setRank(Rank.fromId(result.getInt("rank")));
					profile.setDollars(result.getInt("dollars"));
					profile.setTokens(result.getInt("tokens"));
					profile.setReferrals(result.getInt("referrals"));
					profile.setMessaging(result.getBoolean("accepting_messages"));
					profile.setSilent(result.getBoolean("silent"));
					profile.setWatchdog(result.getBoolean("watchdog"));
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					statement.close();
				}
				return profile;
			}
		});
		GameProfile profile = null;
		try {
			profile = future.get();
		} catch (InterruptedException | ExecutionException ex) {
			M.log(Level.WARNING, ex, "Unable to create new GameProfile", "Username: " + name,
			           "UUID: " + uuid.toString());
			ex.printStackTrace();
		}
		if (profile != null) {
			plugin.getServer()
			      .getLogger()
			      .info("GameProfile created for user " + name + " with UUID " + uuid.toString());
		}
		return profile;
	}

	public GameProfile getCachedProfile(final int id) {
		Optional<GameProfile> stream = profiles.stream().filter(gameProfile -> gameProfile.getId() == id).findFirst();
		return stream.orElse(null);
	}

	public GameProfile getCachedProfile(final UUID uuid) {
		Optional<GameProfile> stream = profiles.stream()
		                                       .filter(gameProfile -> gameProfile.getUuid().equals(uuid))
		                                       .findFirst();
		return stream.orElse(null);
	}

	public GameProfile getProfile(final int id) {
		Optional<GameProfile> stream = profiles.stream().filter(gameProfile -> gameProfile.getId() == id).findFirst();
		if (stream.isPresent()) {
			return stream.get();
		} else {
			Future<GameProfile> future = plugin.getThreadPool().submit(() -> {
				try (var sql = plugin.getDataManager().getDataSource().getConnection()) {
					String query     = "SELECT * FROM GameProfiles WHERE id = ? LIMIT 1";
					var    statement = sql.prepareStatement(query);
					statement.setInt(1, id);
					var result = statement.executeQuery();

					GameProfile profile = null;
					try {
						result.next();
						UUID uuid = UUID.fromString(result.getString("uuid"));

						profile = new GameProfile(plugin, id, uuid);
						profile.setName(result.getString("username"));
						profile.setRank(Rank.fromId(result.getInt("rank")));
						profile.setDollars(result.getInt("dollars"));
						profile.setTokens(result.getInt("tokens"));
						profile.setReferrals(result.getInt("referrals"));
						profile.setMessaging(result.getBoolean("accepting_messages"));
						profile.setSilent(result.getBoolean("silent"));
						profile.setWatchdog(result.getBoolean("watchdog"));
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						statement.close();
					}
					if (profile != null) {
						profiles.add(profile);
						return profile;
					} else {
						return null;
					}
				}
			});

			try {
				return future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public GameProfile getProfile(final String name) {
		Optional<GameProfile> stream = profiles.stream()
		                                       .filter(gameProfile -> gameProfile.getName().equalsIgnoreCase(name))
		                                       .findFirst();
		if (stream.isPresent()) {
			return stream.get();
		} else {
			Future<GameProfile> future = plugin.getThreadPool().submit(() -> {
				try (var sql = plugin.getDataManager().getDataSource().getConnection()) {
					String query     = "SELECT * FROM GameProfiles WHERE username = ? LIMIT 1";
					var    statement = sql.prepareStatement(query);
					statement.setString(1, name);
					var result = statement.executeQuery();

					GameProfile profile = null;
					try {
						result.next();
						int  id   = result.getInt("id");
						UUID uuid = UUID.fromString(result.getString("uuid"));

						profile = new GameProfile(plugin, id, uuid);
						profile.setName(name);
						profile.setRank(Rank.fromId(result.getInt("rank")));
						profile.setDollars(result.getInt("dollars"));
						profile.setTokens(result.getInt("tokens"));
						profile.setReferrals(result.getInt("referrals"));
						profile.setMessaging(result.getBoolean("accepting_messages"));
						profile.setSilent(result.getBoolean("silent"));
						profile.setWatchdog(result.getBoolean("watchdog"));
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						statement.close();
					}
					if (profile != null) {
						profiles.add(profile);
						return profile;
					} else {
						return null;
					}
				}
			});

			try {
				return future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public GameProfile getProfile(final UUID uuid) {
		Optional<GameProfile> stream = profiles.stream()
		                                       .filter(gameProfile -> gameProfile != null &&
		                                                              gameProfile.getUuid().equals(uuid))
		                                       .findFirst();

		if (stream.isPresent()) {
			return stream.get();
		} else {
			Future<GameProfile> future = plugin.getThreadPool().submit(() -> {
				try (var sql = plugin.getDataManager().getDataSource().getConnection()) {
					String query     = "SELECT * FROM GameProfiles WHERE uuid = ? LIMIT 1";
					var    statement = sql.prepareStatement(query);
					statement.setString(1, uuid.toString());
					var result = statement.executeQuery();

					GameProfile profile = null;
					try {
						result.next();
						int id = result.getInt("id");

						profile = new GameProfile(plugin, id, uuid);
						profile.setName(result.getString("username"));
						profile.setRank(Rank.fromId(result.getInt("rank")));
						profile.setDollars(result.getInt("dollars"));
						profile.setTokens(result.getInt("tokens"));
						profile.setReferrals(result.getInt("referrals"));
						profile.setMessaging(result.getBoolean("accepting_messages"));
						profile.setSilent(result.getBoolean("silent"));
						profile.setWatchdog(result.getBoolean("watchdog"));
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						statement.close();
					}
					if (profile != null) {
						profiles.add(profile);
						return profile;
					} else {
						return null;
					}
				}
			});

			try {
				return future.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/*@NotNull
	public CompletableFuture<ArrayList<GameProfile>> getAllProfiles() {
		ArrayList<GameProfile>                    result = new ArrayList<>(this.profiles);
		CompletableFuture<ArrayList<GameProfile>> future = new CompletableFuture<>();
		plugin.getThreadPool().execute(() -> {
			Session session = plugin.getSessionManager().openSession();
			session.beginTransaction();
			List<GameProfilesEntity> entities = session.createQuery("from GameProfilesEntity", GameProfilesEntity.class).getResultList();
			session.getTransaction().commit();
			plugin.getSessionManager().closeSession(session);
			if (entities != null && !entities.isEmpty()) {
				entities.forEach(entity -> {
					if (getCachedProfile(entity.getId()) == null) {
						result.add(new GameProfile(plugin, entity));
						//plugin.log("id = " + entity.getId(), "uuid = " + entity.getUuid(), "name = " + entity.getUsername());
					}
				});
			}
			future.complete(result);
		});
		return future;
	}*/

	public int getProfileCount() {
		Future<Integer> future = plugin.getThreadPool().submit(() -> {
			try (var sql = plugin.getDataManager().getDataSource().getConnection()) {
				String query     = "SELECT COUNT(DISTINCT uuid) AS 'count' FROM GameProfiles";
				var    statement = sql.prepareStatement(query);
				var    result    = statement.executeQuery();

				int count = 0;
				try {
					result.next();
					count = result.getInt("count");
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					statement.close();
				}
				return count;
			}
		});
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void sync() {
		profiles.forEach(GameProfile::fullSync);
		// TODO: Server stat sync
	}

}
