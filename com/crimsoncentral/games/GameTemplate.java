package com.crimsoncentral.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import com.crimsoncentral.Main;
import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.ArenaManager;
import com.crimsoncentral.arena.Team;
import com.crimsoncentral.arena.util.ArenaUtil;
import com.crimsoncentral.arena.util.DeathMessages;
import com.crimsoncentral.arena.util.PlayerVisiblity;
import com.crimsoncentral.cosmetics.LootCrateUtil;
import com.crimsoncentral.games.GameStats;
import com.crimsoncentral.games.sky_wars.SkyWarsKits;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.util.Config;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public final class GameTemplate extends Arena implements Cloneable {

	public GameTemplate(String arena_name, Double game_mode, int max_players, int min_players) {
		super(arena_name, game_mode, max_players, min_players);

		setGameTimer(20);

	}

	public GameTemplate game = this;

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

	int task;

	public void run() {
		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {

			@Override
			public void run() {

				setGameTimer(getGameTimer() - 1);
				game.getWorld().setTime(0);
				game.getWorld().setStorm(false);
				game.getWorld().setAutoSave(false);

				getPlayers();

				preGame("GAME NAME", "PLAYER AMMOUNT", "MODE", "GAME TIP");
				SkyWarsKits.setupSoloKitSelector();
				if (getStage() == ArenaStage.COUNT_DOWN) {

					if (getGameTimer() == 0) {
						addCondition(Arena.Conditions.CAN_MOVE_ITEMS);
						addCondition(Arena.Conditions.CAN_PVP);
						addCondition(Arena.Conditions.SHOW_PLAYER_HEALTH);
						addCondition(Conditions.CAN_TAKE_DAMAGE);
						addCondition(Conditions.CAN_BLOCK_INTERACT);
						addCondition(Conditions.ELIMINATE_ON_LOG_OUT);

						sendGameStartInfoMessage();

						for (Player p : getAlivePlayers()) {

							p.teleport(getPlayerTeam(p).team_spawn);

						}

					}

				} else if (getStage() == ArenaStage.GAME_TIME) {

					sendGameScoreboard();

					if (getWinningTeam() != null) {
						setGameTimer(10);
						setStage(ArenaStage.AFTER_GAME);

					} else if (getGameTimer() == getMaxGameTime() - 4) {
						addCondition(Arena.Conditions.CAN_TAKE_DAMAGE);

					} else if (getGameTimer() == 300) {

						game.sendWorldMessage(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + "5"
								+ ChatColor.RED + " Minutes");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + "5"
								+ ChatColor.RED + " minutes", 0, 2, 0);

					} else if (getGameTimer() == 175) {

						WorldBorder border = game.getWorld().getWorldBorder();

						border.setWarningDistance(10);
						border.setDamageAmount(8);
						border.setCenter(0, 0);
						border.setSize(35, 75);
						border.setCenter(0, 0);

						for (int i = 1; i <= game.getAlivePlayers().size() * 2; ++i) {

							EnderDragon dragon = (EnderDragon) getWorld().spawnEntity(getArenaSpawn(),
									EntityType.ENDER_DRAGON);

							dragon.setInvulnerable(true);
							dragon.setSilent(true);

							dragon.setCustomName(ChatColor.DARK_PURPLE + "Demogorgan " + ChatColor.DARK_RED + i);
							dragon.setCustomNameVisible(true);
						}

						game.sendWorldMessage(ChatColor.RED + "Sudden Death Has Begun! " + ChatColor.DARK_RED + "+"
								+ game.getAlivePlayers().size() * 2 + ChatColor.DARK_PURPLE + " Dragons");
						game.playWorldSound(Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, 1, 1);
						game.sendWorldTitle(ChatColor.RED + "Sudden Death Has Begun!", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.DARK_RED + "+" + game.getAlivePlayers().size() * 2
								+ ChatColor.DARK_PURPLE + " Dragon", 0, 2, 0);

					} else if (getGameTimer() == 60) {
						game.sendWorldMessage(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + "1 "
								+ ChatColor.RED + " Minute");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + "1"
								+ ChatColor.RED + " minute", 0, 2, 0);
					} else if (getGameTimer() == 30) {
						game.sendWorldMessage(ChatColor.YELLOW + "Game will be Ending in " + ChatColor.RED
								+ getGameTimer() + ChatColor.YELLOW + " Seconds");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW
								+ getGameTimer() + ChatColor.RED + " seconds", 0, 2, 0);
					} else if (getGameTimer() == 10) {
						game.sendWorldMessage(ChatColor.YELLOW + "Game will be Ending in " + ChatColor.RED
								+ getGameTimer() + ChatColor.YELLOW + " Seconds");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);
						game.sendWorldTitle("", 0, 1, 0);
						game.sendWorldSubTitle(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW
								+ getGameTimer() + ChatColor.RED + " seconds", 0, 1, 0);
					} else if (getGameTimer() <= 5 && getGameTimer() > 0) {
						game.sendWorldMessage(ChatColor.YELLOW + "Game will be Ending in " + ChatColor.RED
								+ getGameTimer() + ChatColor.YELLOW + " Seconds");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);
						game.sendWorldTitle("", 0, 1, 0);
						game.sendWorldSubTitle(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW
								+ getGameTimer() + ChatColor.RED + " seconds", 0, 1, 0);
					} else if (getGameTimer() == 0) {
						playWorldSound(Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
						setStage(ArenaStage.AFTER_GAME);
						setGameTimer(10);
						sendGameEndInfoMessage(null);

					}

				} else if (getStage() == ArenaStage.AFTER_GAME) {
					sendGameScoreboard();

					if (getGameTimer() == 9) {
						sendGameEndInfoMessage(getWinningTeam());
					} else if (getGameTimer() == 0) {

						delete();
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
		setBallistic(true);
		makeArenaWorld();
		setStage(ArenaStage.PRE_COUNT_DOWN);
		setMaxGameTime(900);

		File c = new File(getWorld().getWorldFolder().getName() + "/arena-config.yml");

		Config config = new Config(c);

		game.setArenaSpawn(config.getLocation("Arena-Spawn", game.getWorld()));

		for (int i = 1; i <= 1; ++i) {
			game.createTeam("Team-" + i, 1, "", null, config.getLocation("Team-" + i + "-Spawn", game.getWorld()),
					false);

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
					if (name.contains(("name of something like a chest (ex: tier-1-chest)"))) {
						// do something
					}
			}
		}

	}

	public void join(Player player) {

		resetPlayer(player);
		managePlayerScoreboard(player);
		joinTeam(player);
		joinMessage(player);

		player.teleport(game.getArenaSpawn());

		game.giveSpawnItems(player);

	}

	public void playerDeath(Player player, Player killer, DamageCause cause) {
		if (cause != null) {
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

			setSpectating(player, true, true, true, true, true);
		}
	}

	public void giveSpawnItems(Player player) {

		// give items

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

	public void sendGameScoreboard() {

		int alive_players = getAlivePlayers().size();
		if (getWorld().getPlayers().size() > 0) {

			for (Player p : getWorld().getPlayers()) {

				if (p.getWorld() == getWorld()) {

					com.crimsoncentral.util.ScoreboardWrapper sw = ArenaManager.scoreboards.get(p);

					p.setScoreboard(sw.getScoreboard());
					sw.changePage(getArenaId());
					sw.setTitle(getArenaId(), ChatColor.YELLOW + "" + ChatColor.BOLD + "GAME NAME");

					sw.setLine(getArenaId(), 14, "" + ChatColor.GRAY + Main.date + " PLAYER AMOUNT");
					sw.setLine(getArenaId(), 13, "");
					sw.setLine(getArenaId(), 12, ChatColor.WHITE + "Game Time Left", ChatColor.WHITE + ":");
					if (getStage() == ArenaStage.GAME_TIME) {
						if (getGameTimer() % 60 < 10) {
							sw.setLine(getArenaId(), 11, ChatColor.GREEN + "",
									getGameTimer() / 60 + ":0" + getGameTimer() % 60);
						} else {
							sw.setLine(getArenaId(), 11, ChatColor.GREEN + "",
									getGameTimer() / 60 + ":" + getGameTimer() % 60);
						}
					} else if (getStage() == ArenaStage.AFTER_GAME) {
						sw.setLine(getArenaId(), 11, ChatColor.GREEN + "Game Ended!");
					} else {
						sw.setLine(getArenaId(), 11, ChatColor.GREEN + "15:00");
					}
					sw.setLine(getArenaId(), 10, ChatColor.DARK_PURPLE + "");

					if (getStage() == ArenaStage.GAME_TIME) {
						if (getGameTimer() >= 600) {
							int i = getGameTimer() / 60 - 10;
							if (getGameTimer() % 60 < 10) {
								sw.setLine(getArenaId(), 9, ChatColor.WHITE + "Next Refill: ",
										ChatColor.GREEN + "" + i + ":0" + getGameTimer() % 60);
							} else {
								sw.setLine(getArenaId(), 9, ChatColor.WHITE + "Next Refill: ",
										ChatColor.GREEN + "" + i + ":" + getGameTimer() % 60);
							}
						} else if (getGameTimer() >= 240) {
							int i = getGameTimer() / 60 - 4;
							if (getGameTimer() % 60 < 10) {
								sw.setLine(getArenaId(), 9, ChatColor.WHITE + "Next Refill: ",
										ChatColor.GREEN + "" + i + ":0" + getGameTimer() % 60);
							} else {
								sw.setLine(getArenaId(), 9, ChatColor.WHITE + "Next Refill: ",
										ChatColor.GREEN + "" + i + ":" + getGameTimer() % 60);
							}
						} else {
							if (getGameTimer() % 60 < 10) {
								sw.setLine(getArenaId(), 9, ChatColor.WHITE + "Game End: ",
										ChatColor.GREEN + "" + getGameTimer() / 60 + ":0" + getGameTimer() % 60);
							} else {
								sw.setLine(getArenaId(), 9, ChatColor.WHITE + "Game End: ",
										ChatColor.GREEN + "" + getGameTimer() / 60 + ":" + getGameTimer() % 60);
							}
						}
					} else if (getStage() == ArenaStage.AFTER_GAME) {
						sw.setLine(getArenaId(), 9, ChatColor.WHITE + "Game End: ", ChatColor.GREEN + "0:00");
					} else {
						sw.setLine(getArenaId(), 9, ChatColor.WHITE + "Next Refill: ", ChatColor.GREEN + "5:00");
					}

					sw.setLine(getArenaId(), 8, ChatColor.GOLD + "");
					sw.setLine(getArenaId(), 7, ChatColor.WHITE + "Players Left:",
							ChatColor.GREEN + " " + alive_players);
					sw.setLine(getArenaId(), 6, ChatColor.GOLD + "");
					sw.setLine(getArenaId(), 5, ChatColor.WHITE + "Kills: ",
							ChatColor.GREEN + "" + game.getStats(p).getFinalKills());
					sw.setLine(getArenaId(), 4, ChatColor.GOLD + "");
					sw.setLine(getArenaId(), 3, ChatColor.DARK_GREEN + "Map: ", ChatColor.GOLD + game.getMapName());
					sw.setLine(getArenaId(), 2, ChatColor.DARK_GREEN + "Mode: ", ChatColor.RED + "MODE");

					sw.setLine(getArenaId(), 1, ChatColor.DARK_PURPLE + "");
					sw.setLine(getArenaId(), 0, ChatColor.RED + " crimson-centr", ChatColor.RED + "al.com");
				}
			}
		}
	}

	public void sendGameStartInfoMessage() {

		ArenaUtil.sendCenteredWorldMessage(this,
				"" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.AQUA + ChatColor.BOLD + "GAME NAME");
		ArenaUtil.sendCenteredWorldMessage(this, "");
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.GREEN + ChatColor.BOLD + "Description line 1");
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.GREEN + ChatColor.BOLD + "Description line 2");
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.GREEN + ChatColor.BOLD + "Description line 3");
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.GREEN + ChatColor.BOLD + "Description line 4");

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
						"" + ChatColor.GRAY + ChatColor.BOLD + "WINNER: " + winners);
				ArenaUtil.sendCenteredPlayerMessage(player, "");
				ArenaUtil.sendCenteredPlayerMessage(player,
						ChatColor.DARK_GREEN + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "REWARDS");
				// ArenaUtil.sendCenteredPlayerMessage(player, "");
				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.GOLD + "+" + coins + " Unusable Game Name Coins");
				ArenaUtil.sendCenteredPlayerMessage(player, "" + ChatColor.AQUA + "+" + xp + " Unusable Network XP");
				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.GREEN + "+" + crates + " Unusable Loot Crates");

				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");

				getJsonMessage().send((Player[]) getWorld().getPlayers().toArray());
			}
		}

	}

	@Override
	public final Arena clone() throws CloneNotSupportedException {
		return new GameTemplate("", getGamemode(), getMaxPlayers(), getMinPlayers());

	}
}
