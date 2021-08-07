package com.gamerbah.gamezonesmp.util.manager;
/* Created by GamerBah on 6/9/21 */

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.util.message.M;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;

import java.sql.SQLException;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class DataManager {

	private final GameZoneSMP      plugin;
	@Getter
	private final HikariDataSource dataSource;

	public DataManager(GameZoneSMP plugin) {
		this.plugin = plugin;
		this.dataSource = createDataSource();

		try {
			createTables();
		} catch (SQLException e) {
			M.log(Level.SEVERE, e, "Failed attempt to create tables.");
		}
	}

	private HikariDataSource createDataSource() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl(plugin.getConfig().getString("database.url"));
		config.setUsername(plugin.getConfig().getString("database.username"));
		config.setPassword(plugin.getConfig().getString("database.password"));
		config.setIdleTimeout(30000);
		config.setMaxLifetime(60000);
		config.setDriverClassName("org.mariadb.jdbc.Driver");

		return new HikariDataSource(config);
	}

	private void createTables() throws SQLException {
		var query = """
              CREATE TABLE IF NOT EXISTS GameProfiles (
                  id INT NOT NULL AUTO_INCREMENT,
                  uuid varchar(36) NOT NULL UNIQUE,
                  username varchar(16) NOT NULL,
                  `rank` int NOT NULL DEFAULT 0,
                  dollars int NOT NULL DEFAULT 100,
                  tokens int NOT NULL DEFAULT 0,
                  rewardAvailable tinyint NOT NULL DEFAULT 0,
                  rewardLast varchar(35) NOT NULL DEFAULT '2020-01-01T12:00:00.000',
                  lastSeen varchar(35) NOT NULL DEFAULT '2020-01-01T12:00:00.000',
                  referredBy int NOT NULL DEFAULT -1,
                  referrals int NOT NULL DEFAULT 0,
                  accepting_messages tinyint NOT NULL DEFAULT 1,
                  silent tinyint NOT NULL DEFAULT 0,
                  watchdog tinyint NOT NULL DEFAULT 0,
                  PRIMARY KEY (id));
              """;
		var statement = getDataSource().getConnection().prepareStatement(query);
		statement.executeUpdate();
	}

}
