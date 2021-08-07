package com.gamerbah.gamezonesmp.util.manager;

import com.gamerbah.gamezonesmp.GameZoneSMP;
import com.gamerbah.gamezonesmp.data.Rank;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Locale;

public class ScoreboardManager {

	private final GameZoneSMP         plugin;
	private final HashMap<Rank, Team> teams;

	private Scoreboard scoreboard;

	public ScoreboardManager(GameZoneSMP plugin) {
		this.plugin = plugin;
		this.teams = new HashMap<>();

		createScoreboard();
	}

	private void createScoreboard() {
		var boardManager = plugin.getServer().getScoreboardManager();

		Scoreboard board;
		if (boardManager != null) {
			board = boardManager.getNewScoreboard();
			var healthObj = board.registerNewObjective("showHealth", Criterias.HEALTH, "Health", RenderType.HEARTS);
			healthObj.setDisplaySlot(DisplaySlot.BELOW_NAME);
			this.scoreboard = board;
			createTeams();
		}
	}

	private void createTeams() {
		for (Rank rank : Rank.values()) {
			Team team = this.scoreboard.registerNewTeam(rank.getName().toLowerCase(Locale.ROOT));
			team.setPrefix(rank + " ");
			team.setAllowFriendlyFire(true);
			team.setCanSeeFriendlyInvisibles(false);
			team.setDisplayName(rank.toString());
			teams.put(rank, team);
		}
	}

	public void assign(Player player) {
		var profile = plugin.getProfileManager().getProfile(player.getUniqueId());
		assert profile != null;
		teams.get(profile.getRank()).addEntry(player.getName());
		player.setScoreboard(this.scoreboard);
	}

}
