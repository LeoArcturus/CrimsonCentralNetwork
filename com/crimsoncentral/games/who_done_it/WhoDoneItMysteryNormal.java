package com.crimsoncentral.games.who_done_it;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import com.crimsoncentral.Main;
import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.ArenaManager;
import com.crimsoncentral.arena.Team;
import com.crimsoncentral.arena.Team.PlayerStatus;
import com.crimsoncentral.arena.Team.TeamStatus;
import com.crimsoncentral.arena.util.ArenaUtil;
import com.crimsoncentral.arena.util.DeathMessages;
import com.crimsoncentral.arena.util.PlayerVisiblity;
import com.crimsoncentral.arena.util.PlayerVisiblity.Visiblity;
import com.crimsoncentral.cosmetics.LootCrateUtil;
import com.crimsoncentral.games.GameStats;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.util.Config;
import com.crimsoncentral.util.JSONMessage;

import com.crimsoncentral.util.ScoreboardWrapper;

public final class WhoDoneItMysteryNormal extends Arena implements Cloneable {

	public WhoDoneItMysteryNormal(String arena_name, Double game_mode, int max_players, int min_players) {
		super(arena_name, game_mode, max_players, min_players);

		game_timer = 20;

	}

	public WhoDoneItMysteryNormal game = this;

	public int game_timer;

	public int game_stage;

	public int scoreboard_page;

	public int coins_for_final_kills = 40;
	public int coins_for_kills = 40;
	public int coins_for_eggs_broken = 0;
	public int coins_for_playing = 20;
	public int coins_for_winning = 0;

	public int xp_for_final_kills = 15;
	public int xp_for_kills = 15;
	public int xp_for_eggs_broken = 0;
	public int xp_for_playing = 5;
	public int xp_for_winning = 75;

	public Player murder = null;
	public Player detective = null;
	public HashMap<Player, Double> innocents = new HashMap<Player, Double>();

	boolean detective_has_been_killed = false;
	boolean bow_dropped = false;

	int task;

	public void run() {
		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {

			@Override
			public void run() {

				--game_timer;
				game.getWorld().setTime(0);
				game.getWorld().setStorm(false);
				game.getWorld().setAutoSave(false);

				getPlayers();

				if (game_stage == 1) {

					sendPreGameScoreboard();

					if (game_timer == 0 && getPlayers().size() >= getMinPlayers()) {
						game_timer = 20;
						game_stage = 2;
						game.sendWorldMessage(ChatColor.YELLOW + "Game Starting in " + ChatColor.GOLD + "20 "
								+ ChatColor.YELLOW + "Seconds");
						game.playWorldSound(Sound.BLOCK_LEVER_CLICK, 1, 1);
						game.sendWorldTitle(ChatColor.YELLOW + "Game Starting in", 7, (int) 3.1, 7);
						game.sendWorldSubTitle("" + ChatColor.GOLD + game_timer + " seconds...", 7, (int) 3.1, 7);
					} else if (game_timer == 0 && getPlayers().size() <= getMinPlayers()) {
						game_timer = 15;

					} else if (getPlayers().size() == getMaxPlayers()) {
						game_timer = 10;
						game_stage = 2;
						game.sendWorldMessage(ChatColor.YELLOW + "Game Starting in " + ChatColor.DARK_PURPLE
								+ game_timer + ChatColor.YELLOW + " Seconds");
						game.playWorldSound(Sound.BLOCK_LEVER_CLICK, 1, 1);
						game.sendWorldTitle(ChatColor.YELLOW + "Game Starting in", 0, (int) 3.1, 0);
						game.sendWorldSubTitle("" + ChatColor.DARK_PURPLE + game_timer + " seconds...", 7, (int) 3.1,
								7);
					}
				} else if (game_stage == 2) {
					sendPreGameScoreboard();
					if (game_timer == 10) {
					
						for (Team t : getTeams()) {

							for (Player p : t.getPlayers()) {

								if (p.getWorld() == getWorld()) {
									p.teleport(t.team_spawn);
								}
							}

						}

						game.allowNewPlayers(false);
						game.deletePreGameLobby();
						game.sendWorldMessage(ChatColor.YELLOW + "" + "Game Starting in " + ChatColor.DARK_GREEN + "10 "
								+ ChatColor.YELLOW + "Seconds");
						game.playWorldSound(Sound.BLOCK_LEVER_CLICK, 1, 1);
						game.sendWorldTitle(ChatColor.YELLOW + "Game Tip:", 0, (int) 3.1, 0);
						game.sendWorldSubTitle("" + ChatColor.DARK_GREEN + "Stay Alive!", 7, (int) 3.1, 7);
					} else if (game_timer == 4 || game_timer == 5) {

						game.sendWorldMessage(ChatColor.YELLOW + "Game Starting in " + ChatColor.GREEN + game_timer
								+ ChatColor.YELLOW + " Seconds");
						game.playWorldSound(Sound.BLOCK_LEVER_CLICK, 1, 1);
						game.sendWorldTitle(ChatColor.YELLOW + "Game Starting in", 0, (int) 1.1, 0);
						game.sendWorldSubTitle("" + ChatColor.GREEN + game_timer + " seconds", 0, (int) 1.1, 0);
					} else if (game_timer == 3 || game_timer == 2) {

						game.sendWorldMessage(ChatColor.YELLOW + "Game Starting in " + ChatColor.RED + game_timer
								+ ChatColor.YELLOW + " Seconds");
						game.playWorldSound(Sound.BLOCK_LEVER_CLICK, 1, 1);
						game.sendWorldTitle(ChatColor.YELLOW + "Game Starting in", 0, (int) 1.1, 0);
						game.sendWorldSubTitle("" + ChatColor.RED + game_timer + " seconds", 0, (int) 1.1, 0);
					} else if (game_timer == 1) {
						game.sendWorldMessage(ChatColor.YELLOW + "Game Starting in " + ChatColor.GOLD + game_timer
								+ ChatColor.YELLOW + " Second");

						game.sendWorldTitle(ChatColor.YELLOW + "Game Starting in", 0, (int) 1.1, 0);
						game.sendWorldSubTitle("" + ChatColor.GOLD + game_timer + " second", 0, (int) 1.1, 0);
						game.playWorldSound(Sound.BLOCK_NOTE_HARP, 1, 1);
						

					} else if (game_timer == 0) {

						ArrayList<Player> kits = new ArrayList<Player>();
						game.playWorldSound(Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
						game.sendGameStartInfoMessage();
						game_stage = 3;
						game_timer = 900;
						game.sendWorldTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "Whone Done It!?", 0, (int) 3.1, 10);
						game.sendWorldSubTitle("" + ChatColor.GREEN + ChatColor.BOLD + "on " + ChatColor.DARK_RED
								+ ChatColor.BOLD + "Crimson " + ChatColor.GOLD + ChatColor.BOLD + "Central", 0,
								(int) 3.1, 10);

						

						for (Team t : getTeams()) {

							Iterator<Entry<Player, Team.PlayerStatus>> it = t.players.entrySet().iterator();
							while (it.hasNext()) {
								Entry<Player, Team.PlayerStatus> pair1 = it.next();

								if (t.players.size() > 0) {

									t.team_status = TeamStatus.ALIVE;
									t.colorizePlayerNames();
									t.showPlayerHealth();
								}

								t.players.replace(pair1.getKey(), pair1.getValue(), Team.PlayerStatus.ALIVE);

								kits.add(pair1.getKey());
							}
						}

						for (Player p : game.getPlayers()) {

							createBlankStats(p, "WhoDoneItMysteryNormal");

						}

						game.addCondition(Arena.Conditions.CAN_MOVE_ITEMS);
						game.addCondition(Arena.Conditions.CAN_PVP);

						org.bukkit.WorldBorder border = game.getWorld().getWorldBorder();

						border.setWarningDistance(10);
						border.setDamageAmount(0.25);
						border.setCenter(0, 0);
						border.setSize(512, 1);
						border.setCenter(0, 0);
					}

				} else if (game_stage == 3) {

					sendGameScoreboard();

					if (getWinningTeam() != null) {
						game_timer = 10;
						game_stage = 4;

					} else if (game_timer == 896) {
						addCondition(Arena.Conditions.CAN_TAKE_DAMAGE);

					} else if (game_timer == 300) {

						game.sendWorldMessage(ChatColor.YELLOW + "5" + ChatColor.RED + " Minutes remaining!");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.RED + "The Murder will loose in " + ChatColor.YELLOW + "5"
								+ ChatColor.RED + " minutes", 0, 2, 0);

					} else if (game_timer == 180) {

						game.sendWorldMessage(ChatColor.YELLOW + "3" + ChatColor.RED + " Minutes remaining!");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.RED + "The Murder will loose in " + ChatColor.YELLOW + "3"
								+ ChatColor.RED + " minutes", 0, 2, 0);

					} else if (game_timer == 60) {
						game.sendWorldMessage(ChatColor.YELLOW + "3" + ChatColor.RED
								+ " Minutes remaining! The Murder now has tracking capablities...");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.RED + "The Murder will loose in " + ChatColor.YELLOW + "1"
								+ ChatColor.RED + " minute", 0, 2, 0);
					} else if (game_timer == 30) {
						game.sendWorldMessage(ChatColor.YELLOW + "Game will be Ending in " + ChatColor.RED + game_timer
								+ ChatColor.YELLOW + " Seconds");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + game_timer
								+ ChatColor.RED + " seconds", 0, 2, 0);
					} else if (game_timer == 10) {
						game.sendWorldMessage(ChatColor.YELLOW + "Game will be Ending in " + ChatColor.RED + game_timer
								+ ChatColor.YELLOW + " Seconds");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);
						game.sendWorldTitle("", 0, 1, 0);
						game.sendWorldSubTitle(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + game_timer
								+ ChatColor.RED + " seconds", 0, 1, 0);
					} else if (game_timer <= 5 && game_timer > 0) {
						game.sendWorldMessage(ChatColor.YELLOW + "Game will be Ending in " + ChatColor.RED + game_timer
								+ ChatColor.YELLOW + " Seconds");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);

					} else if (game_timer == 0) {
						playWorldSound(Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
						game_stage = 4;
						game_timer = 10;
						sendGameEndInfoMessage(null);

					}

				} else if (game_stage == 4) {
					sendGameScoreboard();

					if (game_timer == 9) {
						sendGameEndInfoMessage(getWinningTeam());
					} else if (game_timer == 0) {

						deleteArena();

					}

				}

			}

		}, 0, 20);
	}

	int i;
	int s;

	@Override
	public void setup() {

		game = this;
		game.allowNewPlayers(true);

		game_stage = 1;

		File c = new File(getWorld().getWorldFolder().getName() + "/arena-config.yml");

		Config config = new Config(c);

		game.setArenaSpawn(config.getLocation("Arena-Spawn", game.getWorld()));

		for (int i = 1; i <= 16; ++i) {
			game.createTeam("Team-" + i, 16, "", null, config.getLocation("Team-" + i + "-Spawn", game.getWorld()), false);

		}

		ArrayList<String> strings = new ArrayList<String>();
		String line = null;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(config.arenaData.getAbsolutePath()));
			try {
				while ((line = reader.readLine()) != null) {
					strings.add(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void join(Player player) {

		player.getInventory().clear();
		player.setHealth(20);
		player.setSaturation(20);
		ScoreboardWrapper sw = new ScoreboardWrapper(player.getUniqueId());
		PlayerVisiblity.setPlayerVisiblity(player, Visiblity.VISIBLE);
		PlayerVisiblity.updateWorldVisiblity(game.getWorld());

		player.setAllowFlight(false);
		player.setFlying(false);

		player.teleport(game.getArenaSpawn());

		for (Team t : game.getTeams()) {

			if (t.players.size() == 0) {

				t.players.put(player, Team.PlayerStatus.NA);
				break;
			}

		}

		game.giveSpawnItems(player);
		ArenaManager.scoreboards.remove(player);
		ArenaManager.scoreboards.put(player, sw);

		ChatColor color = ChatColor.RED;

		if (game.getPlayers().size() / game.getMinPlayers() <= 0.5) {
			color = ChatColor.RED;

		} else if (game.getPlayers().size() / game.getMinPlayers() > 0.5
				&& game.getPlayers().size() / game.getMinPlayers() < 1) {
			color = ChatColor.YELLOW;
		} else if (game.getPlayers().size() >= game.getMinPlayers()) {
			color = ChatColor.DARK_GREEN;
		} else if (game.getPlayers().size() == game.getMaxPlayers()) {

			color = ChatColor.GOLD;
		}

		game.sendWorldMessage(PlayerManager.getServerPlayer(player).getRank().getRankColor() + player.getName()
				+ ChatColor.GREEN + " has joined the game! (" + color + game.getPlayers().size() + color + "/" + color
				+ game.getMaxPlayers() + ChatColor.GREEN + ")");

	}

	public void playerDeath(Player player, Player killer, DamageCause cause) {

		player.setHealth(20);
		player.setSaturation(20);
		player.setAllowFlight(true);
		player.setFlying(true);
		DeathMessages.broadcastDeathMessage(player, cause);
		ArenaUtil.dropPlayerInventory(player);
		PlayerVisiblity.setPlayerVisiblity(player, PlayerVisiblity.Visiblity.SPECTATOR);
		PlayerVisiblity.updateWorldVisiblity(game.getWorld());
		player.teleport(game.getPlayerTeam(player).team_spawn);
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASEDRUM, 1, 1);
		ArenaUtil.sendTitle(player, ChatColor.RED + "YOU DIED!", 1, 4, 1);
		ArenaUtil.sendSubTitle(player, ChatColor.GRAY + "You are now spectating", 1, 4, 1);

		JSONMessage.create("§lYou Died! ").color(ChatColor.RED).then("§lWant to play again? ").color(ChatColor.YELLOW)
				.then("§l[CLICK HERE]").color(ChatColor.GREEN).tooltip("Click to Play Again")
				.runCommand("/play whodonteit mystery normal").send(player);

	}

	public void giveSpawnItems(Player player) {

	}

	public void quit(Player player) {

		playerDeath(player, null, null);

		for (Team t : game.getTeams()) {

			if (t.players.containsKey(player)) {

				t.players.remove(player, t.players.get(player).hashCode());
				t.players.put(player, Team.PlayerStatus.DEAD);
			}

		}

	}

	public void deleteArena() {
		Bukkit.getScheduler().cancelTask(task);

		ArenaManager.local_arenas.remove(this, game.getGamemode());

		for (Player p : getWorld().getPlayers()) {

			p.teleport(Bukkit.getWorld("MainLobby").getSpawnLocation());
		}

		File f = getWorld().getWorldFolder();

		Bukkit.unloadWorld(Bukkit.getWorld(getWorld().getName()), true);

		Bukkit.getWorlds().remove(Bukkit.getWorld(getWorld().getName()));

		try {
			org.apache.commons.io.FileUtils.cleanDirectory(f);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendPreGameScoreboard() {

		for (Player p : getWorld().getPlayers()) {

			ScoreboardWrapper sw = ArenaManager.scoreboards.get(p);
			p.setScoreboard(sw.getScoreboard());
			sw.changePage(scoreboard_page);
			sw.setTitle(scoreboard_page, ChatColor.YELLOW + "" + ChatColor.BOLD + "WHO DONE IT");

			if (game_stage <= 2) {
				sw.setLine(scoreboard_page, 11, "" + ChatColor.GRAY + Main.date);

				sw.setLine(scoreboard_page, 10, " ");
				sw.setLine(scoreboard_page, 9, ChatColor.WHITE + "Server: " + ChatColor.GOLD + Main.server_name);
				sw.setLine(scoreboard_page, 8, ChatColor.WHITE + "Map: " + ChatColor.GREEN + game.getMapName());
				sw.setLine(scoreboard_page, 7, ChatColor.WHITE + "Mode: " + ChatColor.RED + "Mystery");
				sw.setLine(scoreboard_page, 6, "");
				sw.setLine(scoreboard_page, 5, ChatColor.WHITE + "Players: " + ChatColor.YELLOW + getPlayers().size()
						+ "/" + game.getMaxPlayers());
				sw.setLine(scoreboard_page, 4, "");
				sw.setLine(scoreboard_page, 3, ChatColor.WHITE + "Time Until Start: ");

				if (game_stage == 1) {
					sw.setLine(scoreboard_page, 2, ChatColor.DARK_RED + "waiting for players...");
				} else if (game_stage == 2) {

					sw.setLine(scoreboard_page, 2, ChatColor.GOLD + "" + game_timer + " seconds");
				}
				sw.setLine(scoreboard_page, 1, " ");

			}
			sw.setLine(scoreboard_page, 0, ChatColor.RED + " crimson-centr", ChatColor.RED + "al.com");
		}

	}

	public void sendGameScoreboard() {

		int alive_players = 0;
		if (getWorld().getPlayers().size() > 0) {

			for (Team t : getTeams()) {

				for (@SuppressWarnings("unused") Player p : t.getPlayers()) {

					Iterator<Entry<Player, PlayerStatus>> it1 = t.players.entrySet().iterator();
					while (it1.hasNext()) {
						Entry<Player, PlayerStatus> pair = it1.next();

						if (pair.getValue() == Team.PlayerStatus.ALIVE) {

							alive_players = alive_players + 1;
						}

					}
				}

			}

			for (Player p : getWorld().getPlayers()) {

				com.crimsoncentral.util.ScoreboardWrapper sw = ArenaManager.scoreboards.get(p);

				p.setScoreboard(sw.getScoreboard());
				sw.changePage(scoreboard_page);

				sw.setTitle(scoreboard_page, ChatColor.YELLOW + "" + ChatColor.BOLD + "WHO DONE IT");
				sw.setLine(scoreboard_page, 14, "" + ChatColor.GRAY + Main.date);
				sw.setLine(scoreboard_page, 13, "");
				sw.setLine(scoreboard_page, 12, ChatColor.WHITE + "Role: " + getPlayerRoleAsName(p));
				sw.setLine(scoreboard_page, 11, "");
				sw.setLine(scoreboard_page, 10, ChatColor.WHITE + "Time Left", ChatColor.WHITE + ":");
				if (game_stage == 3) {
					if (game_timer % 60 < 10) {
						sw.setLine(scoreboard_page, 11, ChatColor.GREEN + "", game_timer / 60 + ":0" + game_timer % 60);
					} else {
						sw.setLine(scoreboard_page, 11, ChatColor.GREEN + "", game_timer / 60 + ":" + game_timer % 60);
					}
				} else if (game_stage == 4) {
					sw.setLine(scoreboard_page, 11, ChatColor.GREEN + "Game Ended!");
				} else {
					sw.setLine(scoreboard_page, 11, ChatColor.GREEN + "15:00");
				}
				sw.setLine(scoreboard_page, 10, ChatColor.DARK_PURPLE + "");

				String status = "";
				if (detective_has_been_killed == false) {

					status = ChatColor.GRAY + "Detective: " + ChatColor.AQUA + "Alive";
				} else if (bow_dropped == true) {

					status = ChatColor.RED + "Dropped";

				} else {

					status = ChatColor.GREEN + "Bow Not Dropped";
				}
				sw.setLine(scoreboard_page, 9, status);
				sw.setLine(scoreboard_page, 8, ChatColor.GOLD + "");
				sw.setLine(scoreboard_page, 7, ChatColor.WHITE + "Innocents Left:",
						ChatColor.GREEN + " " + innocents.size());
				sw.setLine(scoreboard_page, 6, ChatColor.GOLD + "");
				sw.setLine(scoreboard_page, 5, ChatColor.AQUA + ": ", ChatColor.GREEN + "" + status);
				sw.setLine(scoreboard_page, 4, ChatColor.GOLD + "");
				sw.setLine(scoreboard_page, 3, ChatColor.DARK_GREEN + "Map: ", ChatColor.GOLD + game.getMapName());
				sw.setLine(scoreboard_page, 2, ChatColor.DARK_GREEN + "Mode: ", ChatColor.RED + "Mystery, Normal");

				sw.setLine(scoreboard_page, 1, ChatColor.DARK_PURPLE + "");
				sw.setLine(scoreboard_page, 0, ChatColor.RED + " crimson-centr", ChatColor.RED + "al.com");
			}

		}
	}

	public String getPlayerRoleAsName(Player p) {
		String role = ChatColor.GRAY + "Spectator";

		if (murder == p) {

			role = ChatColor.RED + "Murder";
		} else if (detective == p) {

			role = ChatColor.AQUA + "Detective";
		} else if(innocents.containsKey(p)) {
			
			role = ChatColor.GREEN + "Innocent";
		}
		return role;
	}

	public void sendGameStartInfoMessage() {

		ArenaUtil.sendCenteredWorldMessage(this,
				"" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.YELLOW + ChatColor.BOLD + "WHO DONE IT");
		ArenaUtil.sendCenteredWorldMessage(this, "");
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.GREEN + ChatColor.BOLD + "14 Innocents need to");
		ArenaUtil.sendCenteredWorldMessage(this,
				"" + ChatColor.GREEN + ChatColor.BOLD + "survive a night in the manor. ");
		ArenaUtil.sendCenteredWorldMessage(this,
				"" + ChatColor.GREEN + ChatColor.BOLD + "map or use your pvp ''skill'' to win.");
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.GREEN + ChatColor.BOLD + "Last man standing wins!");

		ArenaUtil.sendCenteredWorldMessage(this,
				"" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
	}

	public void sendGameEndInfoMessage(Team t) {

		ArrayList<String> names = new ArrayList<String>();
		String winners = "";

		if (t == null) {

			winners = ChatColor.GREEN + "Innocents";

		} else if (t != null) {

			Iterator<Entry<Player, Team.PlayerStatus>> it = t.players.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Player, Team.PlayerStatus> pair1 = it.next();

				names.add(PlayerManager.getServerPlayer(pair1.getKey()).getRank().getPrefix()
						+ PlayerManager.getServerPlayer(pair1.getKey()).getRank().getRankColor()
						+ pair1.getKey().getName());
			}

			winners = String.join(ChatColor.GRAY + ", ", names.stream().map(String::toString).toArray(String[]::new));

		}

		for (Player player : game.getPlayers()) {

			if (player.getWorld() == game.getWorld()) {
				GameStats gs = game.getStats(player);

				int coins = (gs.getKills() * coins_for_kills) + (gs.getEggsBroken() * coins_for_eggs_broken)
						+ (gs.getFinalKills() * coins_for_eggs_broken) + coins_for_playing;
				int xp = (gs.getKills() * 7) + (gs.getEggsBroken() * 15) + (gs.getFinalKills() * 10) + 20;
				int crates = LootCrateUtil.randomizeForLootCrate(player);

				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");

				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.GRAY + ChatColor.BOLD + "WINNER: " + winners);
				ArenaUtil.sendCenteredPlayerMessage(player, "");
				ArenaUtil.sendCenteredPlayerMessage(player,
						ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "REWARDS");
				// ArenaUtil.sendCenteredPlayerMessage(player, "");
				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.GOLD + "+" + coins + " Unusable Mystery Coins");
				ArenaUtil.sendCenteredPlayerMessage(player, "" + ChatColor.AQUA + "+" + xp + " Unusable Network XP");
				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.GREEN + "+" + crates + " Unusable Loot Crates");

				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");

				JSONMessage.create("§lThe Game has ended! ").color(ChatColor.GOLD).then("§lWant to play again? ")
						.color(ChatColor.YELLOW).then("§l[CLICK HERE]").color(ChatColor.GREEN)
						.tooltip("Click to Play Again").runCommand("/play whodoneit mystery normal").send(player);
			}
		}

	}


	public void deletePreGameLobby() {

		Location l = game.getArenaSpawn();

		for (int x = l.getBlockX() - 15; x <= l.getBlockX() + 15; x++) {
			for (int y = l.getBlockY() - 5; y <= l.getBlockY() + 5; y++) {
				for (int z = l.getBlockZ() - 15; z <= l.getBlockZ() + 15; z++) {

					Location loc = new Location(game.getWorld(), x, y, z);
					if (!loc.getBlock().getType().equals(Material.AIR)) {
						loc.getBlock().setType(Material.AIR);
					}
				}
			}

		}

	}

	

	@Override
	public final Arena clone() throws CloneNotSupportedException {
		return new WhoDoneItMysteryNormal("", getGamemode(), getMaxPlayers(), getMinPlayers());

	}
}
