package com.crimsoncentral.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class Team {

	public Arena arena;
	public String team_name;
	public String prefix;
	private int maxPlayers;

	public ChatColor team_color;
	public Location team_spawn;
	public boolean can_respawn;
	public boolean color_code_other_players;
	public TeamStatus team_status;

	public HashMap<Player, PlayerStatus> players = new HashMap<Player, PlayerStatus>();

	public enum PlayerStatus {

		DEAD, ALIVE, RESPAWNING, NA
	}

	public enum TeamStatus {

		DEAD, ALIVE, NA
	}

	public Team(Arena arena, String team_name, int max_players, String prefix, ChatColor team_color,
			Location team_spawn, boolean can_respawn, boolean color_code_other_players) {
		this.arena = arena;
		this.team_name = team_name;
		this.setMaxPlayers(max_players);
		this.prefix = prefix;
		this.team_color = team_color;
		this.team_spawn = team_spawn;
		this.can_respawn = can_respawn;
		this.color_code_other_players = color_code_other_players;

	}

	public ArrayList<Player> getPlayers() {
		ArrayList<Player> ps = new ArrayList<Player>();

		Iterator<Entry<Player, PlayerStatus>> it1 = players.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<Player, PlayerStatus> pair = it1.next();

			ps.add(pair.getKey());
		}

		return ps;
	}

	@SuppressWarnings("deprecation")
	public void colorizePlayerNames() {

		if (team_color == null) {
			ArrayList<Player> ps = new ArrayList<Player>();

			Iterator<Entry<Player, PlayerStatus>> it1 = players.entrySet().iterator();
			while (it1.hasNext()) {
				Entry<Player, PlayerStatus> pair = it1.next();

				ps.add(pair.getKey());
			}

			for (Player player : ps) {
				if (player.getWorld() == arena.getWorld()) {
					Scoreboard s = player.getScoreboard();
					org.bukkit.scoreboard.Team team = s.registerNewTeam("team" + randomWord());
					org.bukkit.scoreboard.Team you = s.registerNewTeam("you" + randomWord());
					org.bukkit.scoreboard.Team enemy = s.registerNewTeam("enemy" + randomWord());

					team.setPrefix("§a");
					you.setPrefix("§2");
					enemy.setPrefix("§c");

					for (Player p : ps) {

						if (p != player) {
							team.addPlayer(p);
						} else {
							you.addPlayer(p);

						}
					}

					for (Player p : player.getWorld().getPlayers()) {

						if (!team.getPlayers().contains(p) && !you.getPlayers().contains(p)) {

							enemy.addPlayer(p);
						}

					}
				}
			}

		} else {

			ArrayList<Player> ps = new ArrayList<Player>();

			Iterator<Entry<Player, PlayerStatus>> it1 = players.entrySet().iterator();
			while (it1.hasNext()) {
				Entry<Player, PlayerStatus> pair = it1.next();

				ps.add(pair.getKey());
			}

			for (Player p : arena.getWorld().getPlayers()) {

				Scoreboard s = p.getScoreboard();

				org.bukkit.scoreboard.Team team = s.registerNewTeam(team_name + randomWord());
				team.setPrefix(prefix);

				for (Player p2 : ps) {

					team.addPlayer(p2);
				}

			}

		}
	}

	public void showPlayerHealth() {

		ArrayList<Player> ps = new ArrayList<Player>();

		Iterator<Entry<Player, PlayerStatus>> it1 = players.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<Player, PlayerStatus> pair = it1.next();

			ps.add(pair.getKey());
		}

		for (Player player : ps) {
			if (arena.getWorld() != player.getWorld()) {
				Scoreboard board = player.getScoreboard();

				Objective objective = board.registerNewObjective("showhealth" + randomWord(), "health");
				objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
				objective.setDisplayName("§c❤");

				for (Player p : ps) {

					p.setScoreboard(board);

				}

			}
		}
	}

	int i = 0;

	public void refreshTeamStatus() {
		i = 0;
		Iterator<Entry<Player, PlayerStatus>> it1 = players.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<Player, PlayerStatus> pair = it1.next();
			if (pair.getValue() == PlayerStatus.ALIVE) {

				++i;
			}

		}

		if (i == 0) {

			team_status = TeamStatus.DEAD;

		}

	}

	public void setTeamName(String name) {

		this.team_name = name;
	}

	public String getTeamName() {

		return this.team_name;
	}

	public void setPrefix(String prefix) {

		this.prefix = prefix;
	}

	public String getPrefix() {

		return this.prefix;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public void addTeamMember(Player p, PlayerStatus status) {

		players.put(p, status);

	}

	public PlayerStatus getPlayerStatus(Player p) {
		PlayerStatus status = PlayerStatus.NA;

		Iterator<Entry<Player, PlayerStatus>> it1 = players.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<Player, PlayerStatus> pair = it1.next();
			if (pair.getKey() == p) {

				status = pair.getValue();
				break;
			}

		}

		return status;

	}

	public void respawn(Player player) {

	}

	public void removeTeamMember(Player p) {

		PlayerStatus status = null;

		Iterator<Entry<Player, PlayerStatus>> it1 = players.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<Player, PlayerStatus> pair = it1.next();
			if (pair.getKey() == p) {

				status = pair.getValue();
				break;
			}

		}

		if (status != null) {
			players.remove(status, p);
		}
	}

	public ArrayList<Player> getAlivePlayers() {

		ArrayList<Player> ps = new ArrayList<Player>();

		Iterator<Entry<Player, PlayerStatus>> it1 = players.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<Player, PlayerStatus> pair = it1.next();
			if (pair.getValue() == PlayerStatus.ALIVE) {

				ps.add(pair.getKey());
			}

		}

		return ps;
	}

	public String randomWord() {

		char letter1 = 0;
		char letter2 = 0;
		char letter3 = 0;
		char letter4 = 0;

		final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final int N = alphabet.length();

		Random r = new Random();

		for (int i = 0; i < 26; i++) {
			letter1 = alphabet.charAt(r.nextInt(N));
		}

		for (int i = 0; i < 26; i++) {
			letter2 = alphabet.charAt(r.nextInt(N));
		}

		for (int i = 0; i < 26; i++) {
			letter3 = alphabet.charAt(r.nextInt(N));
		}

		for (int i = 0; i < 26; i++) {
			letter4 = alphabet.charAt(r.nextInt(N));
		}

		return "" + letter1 + letter2 + letter3 + letter4;

	}

}
