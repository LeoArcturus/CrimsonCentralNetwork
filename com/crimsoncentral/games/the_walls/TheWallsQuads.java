package com.crimsoncentral.games.the_walls;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import com.crimsoncentral.Main;
import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.ArenaManager;
import com.crimsoncentral.arena.Team;
import com.crimsoncentral.arena.Team.PlayerStatus;
import com.crimsoncentral.arena.Team.TeamStatus;
import com.crimsoncentral.arena.game_events.ChestRefill;
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
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public final class TheWallsQuads extends Arena implements Cloneable {

	public TheWallsQuads(String arena_name, Double game_mode, int max_players, int min_players) {
		super(arena_name, game_mode, max_players, min_players);

		game_timer = 20;

	}

	public TheWallsQuads game = this;

	public int game_timer;

	public int game_stage;

	public int scoreboard_page;

	public ArrayList<Location> center_chests = new ArrayList<Location>();
	public HashMap<Location, Location> walls = new HashMap<Location, Location>();

	public int coins_for_final_kills = 80;
	public int coins_for_kills = 80;
	public int coins_for_eggs_broken = 0;
	public int coins_for_playing = 20;
	public int coins_for_winning = 0;

	public int xp_for_final_kills = 15;
	public int xp_for_kills = 15;
	public int xp_for_eggs_broken = 0;
	public int xp_for_playing = 5;
	public int xp_for_winning = 75;

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
						game.sendWorldSubTitle("" + ChatColor.DARK_GREEN + "Work together as a team!", 7, (int) 3.1, 7);
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

						centerRefill(center_chests, 8);
					} else if (game_timer == 0) {

						ArrayList<Player> kits = new ArrayList<Player>();
						game.playWorldSound(Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
						game.sendGameStartInfoMessage();
						game_stage = 3;
						game_timer = 1800;
						game.sendWorldTitle(ChatColor.RED + "" + ChatColor.BOLD + "The Walls", 0, (int) 3.1, 10);
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

							createBlankStats(p, "TheWallsQuads");

							p.setHealth(19);
							p.setHealth(20);
						}

						game.addCondition(Arena.Conditions.CAN_BLOCK_INTERACT);
						game.addCondition(Arena.Conditions.CAN_MOVE_ITEMS);
						game.addCondition(Arena.Conditions.CAN_PVP);
						game.addCondition(Arena.Conditions.SHOW_PLAYER_HEALTH);

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

					} else if (game_timer == 900) {
						addCondition(Arena.Conditions.CAN_PVP);

						game.sendWorldMessage(ChatColor.RED + "The Walls have fallen! " + ChatColor.DARK_PURPLE
								+ "Pvp has been enabled...");
						game.playWorldSound(Sound.ENTITY_LIGHTNING_THUNDER, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.RED + "THE WALLS HAVE FALLEN", 0, 2, 0);

						Iterator<Entry<Location, Location>> it = walls.entrySet().iterator();
						while (it.hasNext()) {
							Entry<Location, Location> pair1 = it.next();

							game.replaceBlockArea(Material.AIR, pair1.getKey(), pair1.getValue());
						}
					} else if (game_timer == 600) {

						centerRefill(center_chests, 10);

						game.sendWorldMessage(ChatColor.GREEN + "All chests have been refilled!");
						game.playWorldSound(Sound.BLOCK_CHEST_OPEN, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.GREEN + "All chests have been refilled!", 0, 2, 0);

					} else if (game_timer == 300) {

						game.sendWorldMessage(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + "5"
								+ ChatColor.RED + " Minutes");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + "5"
								+ ChatColor.RED + " minutes", 0, 2, 0);

					} else if (game_timer == 240) {

						centerRefill(center_chests, 10);

						game.sendWorldMessage(ChatColor.GREEN + "All chests have been refilled!");
						game.playWorldSound(Sound.BLOCK_CHEST_OPEN, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.GREEN + "All chests have been refilled!", 0, 2, 0);
					} else if (game_timer == 210) {

						game.sendWorldMessage(ChatColor.DARK_PURPLE + "The border has begun to shrink!");
						game.playWorldSound(Sound.ENTITY_LIGHTNING_IMPACT, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.DARK_PURPLE + "The border has begun to shrink", 0, 2, 0);

					} else if (game_timer == 175) {

						org.bukkit.WorldBorder border = game.getWorld().getWorldBorder();

						border.setWarningDistance(10);
						border.setDamageAmount(8);
						border.setCenter(0, 0);
						border.setSize(35, 75);
						border.setCenter(0, 0);

						game.sendWorldMessage(ChatColor.RED + "Sudden Death Has Begun! " + ChatColor.DARK_RED + "+"
								+ game.getAlivePlayers().size() * 2 + ChatColor.DARK_PURPLE + " Dragons");
						game.playWorldSound(Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.RED + "Sudden Death Has Begun!", 0, 2, 0);

					} else if (game_timer == 150) {

						centerRefill(center_chests, 12);

						game.sendWorldMessage(ChatColor.GREEN + "All chests have been refilled!");
						game.playWorldSound(Sound.BLOCK_CHEST_OPEN, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.GREEN + "All chests have been refilled!", 0, 2, 0);

					} else if (game_timer == 60) {
						game.sendWorldMessage(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + "1 "
								+ ChatColor.RED + " Minute");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + "1"
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
						game.sendWorldTitle("", 0, 1, 0);
						game.sendWorldSubTitle(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + game_timer
								+ ChatColor.RED + " seconds", 0, 1, 0);
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

		for (int i = 1; i <= 4; ++i) {
			game.createTeam("Team-" + i, 4, "", null, config.getLocation("Team-" + i + "-Spawn", game.getWorld()), false);

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

		for (String s : strings) {
			String lowercase = s.toLowerCase();
			if (lowercase.contains("chest") && !lowercase.contains("number") && !lowercase.contains("--")) {
				String name = Iterables.get(Splitter.on(':').split(s), 0).toLowerCase();

				Location loc = new Location(null, -10000, -10000, -10000);

				loc.setWorld(game.getWorld());

				StringTokenizer st = new StringTokenizer(s.substring(s.indexOf(":") + 1).trim());

				if (st.hasMoreTokens() == true) {

					String xs = st.nextToken(", ");

					Integer x = Integer.parseInt(xs);
					loc.setX(x);
					if (st.hasMoreTokens() == true) {
						String ys = st.nextToken(", ");
						Integer y = Integer.parseInt(ys);
						loc.setY(y);
						if (st.hasMoreTokens() == true) {
							String zs = st.nextToken(", ");
							Integer z = Integer.parseInt(zs);
							;
							loc.setZ(z);
						}
					}

				}
				if (loc.getWorld() != null && loc.getX() != -10000 && loc.getY() != -10000 && loc.getZ() != -10000)
					if (name.contains(("chest"))) {
						center_chests.add(loc);
					}

			} else if (lowercase.contains("wall-")) {

				Location l1 = config.getLocation(lowercase, game.getWorld());
				lowercase.replaceAll(".*| ", "");
				Location l2 = config.getLocation(lowercase, game.getWorld());

				walls.put(l1, l2);
			}
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
				.runCommand("/play thewalls normal quads").send(player);

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
			sw.setTitle(scoreboard_page, ChatColor.YELLOW + "" + ChatColor.BOLD + "THE WALLS");

			if (game_stage <= 2) {
				sw.setLine(scoreboard_page, 12, "" + ChatColor.GRAY + Main.date + " Quads");

				sw.setLine(scoreboard_page, 11, " ");
				sw.setLine(scoreboard_page, 10, ChatColor.WHITE + "Server: " + ChatColor.GOLD + Main.server_name);
				sw.setLine(scoreboard_page, 9, ChatColor.WHITE + "Map: " + ChatColor.GREEN + game.getMapName());
				sw.setLine(scoreboard_page, 8, ChatColor.WHITE + "Mode: " + ChatColor.RED + "Quads");
				sw.setLine(scoreboard_page, 7, "");
				sw.setLine(scoreboard_page, 6, ChatColor.WHITE + "Players: " + ChatColor.YELLOW + getPlayers().size()
						+ "/" + game.getMaxPlayers());
				sw.setLine(scoreboard_page, 5, ChatColor.RED + "(" + ChatColor.DARK_GREEN + getMinPlayers()
						+ ChatColor.WHITE + " required to start)");
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
				sw.setTitle(scoreboard_page, ChatColor.YELLOW + "" + ChatColor.BOLD + "THE WALLS");

				sw.setLine(scoreboard_page, 14, "" + ChatColor.GRAY + Main.date);
				sw.setLine(scoreboard_page, 13, "");
				sw.setLine(scoreboard_page, 12, ChatColor.WHITE + "Game Time Left", ChatColor.WHITE + ":");
				if (game_stage == 3) {
					if (game_timer % 60 < 10) {
						sw.setLine(scoreboard_page, 11, ChatColor.GREEN + "", game_timer / 60 + ":0" + game_timer % 60);
					} else {
						sw.setLine(scoreboard_page, 11, ChatColor.GREEN + "", game_timer / 60 + ":" + game_timer % 60);
					}
				} else if (game_stage == 4) {
					sw.setLine(scoreboard_page, 11, ChatColor.GREEN + "Game Ended!");
				} else {
					sw.setLine(scoreboard_page, 11, ChatColor.GREEN + "30:00");
				}
				sw.setLine(scoreboard_page, 10, ChatColor.DARK_PURPLE + "");

				if (game_stage == 3) {
					if (game_timer >= 900) {
						int i = game_timer / 60 - 15;
						if (game_timer % 60 < 10) {
							sw.setLine(scoreboard_page, 9, ChatColor.WHITE + "Walls Fall: ",
									ChatColor.GREEN + "" + i + ":0" + game_timer % 60);
						} else {
							sw.setLine(scoreboard_page, 9, ChatColor.WHITE + "Walls Fall: ",
									ChatColor.GREEN + "" + i + ":" + game_timer % 60);
						}
					} else if (game_timer >= 600) {
						int i = game_timer / 60 - 10;
						if (game_timer % 60 < 10) {
							sw.setLine(scoreboard_page, 9, ChatColor.WHITE + "Chest Refill: ",
									ChatColor.GREEN + "" + i + ":0" + game_timer % 60);
						} else {
							sw.setLine(scoreboard_page, 9, ChatColor.WHITE + "Chest Refill: ",
									ChatColor.GREEN + "" + i + ":" + game_timer % 60);
						}
					} else if (game_timer >= 240) {
						int i = game_timer / 60 - 4;
						if (game_timer % 60 < 10) {
							sw.setLine(scoreboard_page, 9, ChatColor.WHITE + "Next Refill: ",
									ChatColor.GREEN + "" + i + ":0" + game_timer % 60);
						} else {
							sw.setLine(scoreboard_page, 9, ChatColor.WHITE + "Next Refill: ",
									ChatColor.GREEN + "" + i + ":" + game_timer % 60);
						}
					} else if (game_timer >= 210) {
						int i = game_timer / 60 - 4;
						if (game_timer % 60 < 10) {
							sw.setLine(scoreboard_page, 9, ChatColor.WHITE + "Border Shrink: ",
									ChatColor.GREEN + "" + i + ":0" + game_timer % 60);
						} else {
							sw.setLine(scoreboard_page, 9, ChatColor.WHITE + "Border Shrink: ",
									ChatColor.GREEN + "" + i + ":" + game_timer % 60);
						}
					} else {
						if (game_timer % 60 < 10) {
							sw.setLine(scoreboard_page, 9, ChatColor.WHITE + "Game End: ",
									ChatColor.RED + "" + game_timer / 60 + ":0" + game_timer % 60);
						} else {
							sw.setLine(scoreboard_page, 9, ChatColor.WHITE + "Game End: ",
									ChatColor.DARK_RED + "" + game_timer / 60 + ":" + game_timer % 60);
						}
					}
				} else if (game_stage == 4) {
					sw.setLine(scoreboard_page, 9, ChatColor.WHITE + "Game End: ", ChatColor.GREEN + "0:00");
				} else {
					sw.setLine(scoreboard_page, 9, ChatColor.WHITE + "Next Refill: ", ChatColor.GREEN + "5:00");
				}

				sw.setLine(scoreboard_page, 8, ChatColor.GOLD + "");
				sw.setLine(scoreboard_page, 7, ChatColor.WHITE + "Players Left:",
						ChatColor.GREEN + " " + alive_players);
				sw.setLine(scoreboard_page, 6, ChatColor.GOLD + "");
				sw.setLine(scoreboard_page, 5, ChatColor.WHITE + "Kills: ",
						ChatColor.GREEN + "" + game.getStats(p).getFinalKills());
				sw.setLine(scoreboard_page, 4, ChatColor.GOLD + "");
				sw.setLine(scoreboard_page, 3, ChatColor.DARK_GREEN + "Map: ", ChatColor.GOLD + game.getMapName());
				sw.setLine(scoreboard_page, 2, ChatColor.DARK_GREEN + "Mode: ", ChatColor.RED + "Quads");

				sw.setLine(scoreboard_page, 1, ChatColor.DARK_PURPLE + "");
				sw.setLine(scoreboard_page, 0, ChatColor.RED + " crimson-centr", ChatColor.RED + "al.com");
			}
		}
	}

	public void sendGameStartInfoMessage() {

		ArenaUtil.sendCenteredWorldMessage(this,
				"" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.RED + ChatColor.BOLD + "THE WALLS");
		ArenaUtil.sendCenteredWorldMessage(this, "");
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.GREEN + ChatColor.BOLD + "Gear up by gathering");
		ArenaUtil.sendCenteredWorldMessage(this,
				"" + ChatColor.GREEN + ChatColor.BOLD + "materials and crafting items. After 15 minutes");
		ArenaUtil.sendCenteredWorldMessage(this,
				"" + ChatColor.GREEN + ChatColor.BOLD + "the walls will fall and you can kill other teams.");
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.GREEN + ChatColor.BOLD + "Last team standing wins!");

		ArenaUtil.sendCenteredWorldMessage(this,
				"" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
	}

	public void sendGameEndInfoMessage(Team t) {

		ArrayList<String> names = new ArrayList<String>();
		String winners = "";

		if (t == null) {

			winners = ChatColor.RED + "Game Was a Draw";

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
						"" + ChatColor.GRAY + ChatColor.BOLD + "WINNERS: " + winners);
				ArenaUtil.sendCenteredPlayerMessage(player, "");
				ArenaUtil.sendCenteredPlayerMessage(player,
						ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "REWARDS");
				// ArenaUtil.sendCenteredPlayerMessage(player, "");
				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.GOLD + "+" + coins + " Unusable Walls Coins");
				ArenaUtil.sendCenteredPlayerMessage(player, "" + ChatColor.AQUA + "+" + xp + " Unusable Network XP");
				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.GREEN + "+" + crates + " Unusable Loot Crates");

				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");

				JSONMessage.create("§lThe Game has ended! ").color(ChatColor.GOLD).then("§lWant to play again? ")
						.color(ChatColor.YELLOW).then("§l[CLICK HERE]").color(ChatColor.GREEN)
						.tooltip("Click to Play Again").runCommand("/play thewalls normal quads").send(player);
			}
		}

	}

	public void centerRefill(ArrayList<Location> chests, int slots_to_fill) {

		ChestRefill refill = new ChestRefill(1, 808, slots_to_fill);

		for (Location l : chests) {

			refill.addChest(l);

		}

		ItemStack enchanted_iron_sword = new ItemStack(Material.DIAMOND_SWORD, 1);
		enchanted_iron_sword.addEnchantment(Enchantment.FIRE_ASPECT, 1);
		enchanted_iron_sword.addEnchantment(Enchantment.DAMAGE_ALL, 2);

		ItemStack enchanted_bow = new ItemStack(Material.BOW, 1);
		enchanted_bow.addEnchantment(Enchantment.ARROW_DAMAGE, 2);

		ItemStack regen_potion = new ItemStack(Material.SPLASH_POTION, 1);
		PotionMeta regen_meta = (PotionMeta) regen_potion.getItemMeta();
		regen_meta.setBasePotionData(new PotionData(PotionType.REGEN));
		regen_potion.setItemMeta(regen_meta);

		ItemStack speed_potion = new ItemStack(Material.SPLASH_POTION, 1);
		PotionMeta speed_meta = (PotionMeta) speed_potion.getItemMeta();
		speed_meta.setBasePotionData(new PotionData(PotionType.SPEED));
		speed_potion.setItemMeta(speed_meta);

		ItemStack fire_potion = new ItemStack(Material.POTION, 1);
		PotionMeta fire_meta = (PotionMeta) fire_potion.getItemMeta();
		fire_meta.setBasePotionData(new PotionData(PotionType.FIRE_RESISTANCE));
		fire_potion.setItemMeta(fire_meta);

		refill.addItem(new ItemStack(Material.FISHING_ROD, 1), 1, 40);
		refill.addItem(new ItemStack(Material.DIAMOND_SWORD, 1), 41, 71);
		refill.addItem(enchanted_iron_sword, 72, 89);
		refill.addItem(new ItemStack(Material.DIAMOND_SWORD, 1), 90, 105);
		refill.addItem(new ItemStack(Material.SNOW_BALL, 64), 106, 48);
		refill.addItem(enchanted_bow, 149, 189);
		refill.addItem(new ItemStack(Material.ARROW, 32), 190, 204);
		refill.addItem(new ItemStack(Material.ARROW, 64), 205, 215);

		refill.addItem(new ItemStack(Material.DIAMOND_HELMET, 1), 206, 241);
		refill.addItem(new ItemStack(Material.DIAMOND_CHESTPLATE, 1), 242, 277);
		refill.addItem(new ItemStack(Material.DIAMOND_LEGGINGS, 1), 278, 303);
		refill.addItem(new ItemStack(Material.DIAMOND_BOOTS, 1), 304, 339);
		refill.addItem(new ItemStack(Material.DIAMOND_HELMET, 1), 340, 362);
		refill.addItem(new ItemStack(Material.DIAMOND_BOOTS, 1), 363, 385);

		refill.addItem(new ItemStack(Material.LOG, 16), 486, 435);
		refill.addItem(new ItemStack(Material.STONE, 32), 436, 475);

		refill.addItem(new ItemStack(Material.GOLDEN_APPLE, 5), 476, 520);
		refill.addItem(new ItemStack(Material.COOKED_BEEF, 16), 521, 542);

		refill.addItem(new ItemStack(Material.EGG, 16), 543, 593);
		refill.addItem(new ItemStack(Material.TNT, 15), 594, 629);
		refill.addItem(new ItemStack(Material.FLINT_AND_STEEL, 1), 630, 655);
		refill.addItem(new ItemStack(Material.LAVA_BUCKET, 1), 656, 700);
		refill.addItem(new ItemStack(Material.WATER_BUCKET, 1), 701, 745);

		refill.addItem(regen_potion, 746, 755);
		refill.addItem(speed_potion, 767, 787);
		refill.addItem(fire_potion, 788, 808);

		refill.refill();

	}

	@Override
	public final Arena clone() throws CloneNotSupportedException {
		return new TheWallsQuads("", getGamemode(), getMaxPlayers(), getMinPlayers());

	}
}
