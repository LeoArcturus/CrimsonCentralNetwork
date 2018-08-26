package com.crimsoncentral.games.duels.doubles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import com.crimsoncentral.Main;
import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.ArenaManager;
import com.crimsoncentral.arena.ArenaRegister;
import com.crimsoncentral.arena.Team;
import com.crimsoncentral.arena.Team.PlayerStatus;
import com.crimsoncentral.arena.util.ArenaUtil;
import com.crimsoncentral.arena.util.DeathMessages;
import com.crimsoncentral.cosmetics.LootCrateUtil;
import com.crimsoncentral.games.GameStats;
import com.crimsoncentral.games.duels.solos.DuelsUtil;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.util.Config;
import com.crimsoncentral.util.JSONMessage;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public final class DuelsOverpoweredDoubles extends Arena implements Cloneable {

	public DuelsOverpoweredDoubles(String arena_name, Double game_mode, int max_players, int min_players) {
		super(arena_name, game_mode, max_players, min_players);

	}

	public DuelsOverpoweredDoubles game = this;

	public int game_stage;

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

	public Team team_one;
	public Team team_two;

	public String game_name = "";

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

				game_name = ArenaRegister.getGameProfile(Integer.valueOf(new DecimalFormat("0").format(getGamemode())))
						.getMode(getGamemode()).getName().replace(" Original-Copy", "").replace("-", "")
						.replace("Solo", "").replace("Doubles", "");

				preGame("Duels", "Doubles",  game_name, "Kill your Opponet");
				if (getStage() == ArenaStage.COUNT_DOWN) {

					if (getGameTimer() == 0) {
						addCondition(Arena.Conditions.CAN_MOVE_ITEMS);
						addCondition(Arena.Conditions.CAN_PVP);
						addCondition(Arena.Conditions.SHOW_PLAYER_HEALTH);
						addCondition(Conditions.CAN_TAKE_DAMAGE);
						addCondition(Conditions.ELIMINATE_ON_LOG_OUT);

						DuelsUtil.setUpKits();
						sendGameStartInfoMessage();

						for (Player p : getAlivePlayers()) {

							p.teleport(getPlayerTeam(p).team_spawn);
							DuelsUtil.duels_modes.playerSelectKit(p, DuelsUtil.kits.get(getGamemode()));
						}
							DuelsUtil.duels_modes.givePlayerKits(game, game.getAlivePlayers());
					}

				} else if (getStage() == ArenaStage.GAME_TIME) {

					sendGameScoreboard();

					if (getWinningTeam() != null) {
						setGameTimer(10);
						setStage(ArenaStage.AFTER_GAME);

					} else if (getGameTimer() == 300) {

						game.sendWorldMessage(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + "5"
								+ ChatColor.RED + " Minutes");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + "5"
								+ ChatColor.RED + " minutes", 0, 2, 0);

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
						setGameTimer(10);
						setStage(ArenaStage.AFTER_GAME);
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
		makeArenaWorld();
		setStage(ArenaStage.PRE_COUNT_DOWN);
		setMaxGameTime(600);

		File c = new File(getWorld().getWorldFolder().getName() + "/arena-config.yml");

		Config config = new Config(c);

		game.setArenaSpawn(config.getLocation("Arena-Spawn", game.getWorld()));

		setJsonMessage(JSONMessage.create("You Died! ").color(ChatColor.RED).then("Want to play again? ")
				.color(ChatColor.YELLOW).then("§l[CLICK HERE]").color(ChatColor.GREEN).tooltip("Click to Play Again")
				.runActionItem(
						ArenaRegister.getGameProfile(Integer.valueOf(new DecimalFormat("0").format(getGamemode())))
								.getMode(getGamemode()).getActionIitem()));

		for (int i = 1; i <= 2; ++i) {
			game.createTeam("Team-" + i, 2, "", null, config.getLocation("Team-" + i + "-Spawn", game.getWorld()),
					false);

		}

		for (Team t : getTeams()) {
			if (t.team_name.contains("1")) {
				team_one = t;

			} else {

				team_two = t;
			}

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
				Iterables.get(Splitter.on(':').split(s), 0).toLowerCase();

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
			}
		}

	}

	public void join(Player player) {

		resetPlayer(player);

		managePlayerScoreboard(player);
		joinTeam(player);
		joinMessage(player);

		player.teleport(game.getArenaSpawn());

	}

	public void playerDeath(Player player, Player killer, DamageCause cause) {

		setSpectating(player, true, true, true, true, true);
		DeathMessages.broadcastDeathMessage(player, cause);

		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASEDRUM, 1, 1);
		ArenaUtil.sendTitle(player, ChatColor.RED + "YOU DIED!", 1, 4, 1);
		ArenaUtil.sendSubTitle(player, ChatColor.GRAY + "Game Over!", 1, 4, 1);

	}

	public void quit(Player player) {

		playerDeath(player, null, null);

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
				sw.changePage(getArenaId());
				sw.setTitle(getArenaId(), ChatColor.YELLOW + "" + ChatColor.BOLD + "DUELS");

				sw.setLine(getArenaId(), 11, "" + ChatColor.GRAY + Main.date);
				sw.setLine(getArenaId(), 10, "");
				sw.setLine(getArenaId(), 9, ChatColor.WHITE + "Game Time Left", ChatColor.WHITE + ":");
				if (getStage() == ArenaStage.GAME_TIME) {
					if (getGameTimer() % 60 < 10) {
						sw.setLine(getArenaId(), 8, ChatColor.GREEN + "",
								getGameTimer() / 60 + ":0" + getGameTimer() % 60);
					} else {
						sw.setLine(getArenaId(), 8, ChatColor.GREEN + "",
								getGameTimer() / 60 + ":" + getGameTimer() % 60);
					}
				} else if (game_stage == 4) {
					sw.setLine(getArenaId(), 8, ChatColor.GREEN + "Game Ended!");
				} else {
					sw.setLine(getArenaId(), 8, ChatColor.GREEN + "10:00");
				}
				sw.setLine(getArenaId(), 7, ChatColor.DARK_PURPLE + "");

				DecimalFormat f = new DecimalFormat("0.0");

				sw.setLine(getArenaId(), 6, ChatColor.WHITE + "Opponets:");

				for (Team t : getTeams()) {

					if (t != getPlayerTeam(p)) {

						Player pp1 = null;
						Player pp2 = null;

						String p1 = ChatColor.RED + "DEAD";
						String p2 = ChatColor.RED + "DEAD";

						for (Player ps : t.getPlayers()) {

							if (pp1 == null) {

								pp1 = ps;

								if (t.players.get(pp1) == PlayerStatus.ALIVE) {

									p1 = PlayerManager.getServerPlayer(pp1).getRank().getRankColor() + pp1.getName()
											+ " " + ChatColor.GRAY + f.format(pp1.getHealth()) + "§c❤";
								}

							} else {

								pp2 = ps;

								if (t.players.get(pp2) == PlayerStatus.ALIVE) {

									p1 = PlayerManager.getServerPlayer(pp2).getRank().getRankColor() + pp2.getName()
											+ " " + ChatColor.GRAY + f.format(pp2.getHealth()) + "§c❤";
								}

							}

						}

						sw.setLine(getArenaId(), 3, p1);
						sw.setLine(getArenaId(), 4, p2);
						break;

					}
				}
				sw.setLine(getArenaId(), 2, ChatColor.DARK_GREEN + "Map: ", ChatColor.GOLD + game.getMapName());

				sw.setLine(getArenaId(), 1, ChatColor.DARK_PURPLE + "");
				sw.setLine(getArenaId(), 0, ChatColor.RED + " crimson-centr", ChatColor.RED + "al.com");
			}
		}
	}

	public void sendGameStartInfoMessage() {

		Player player_one = null;
		Player player_two = null;
		Player player_three = null;
		Player player_four = null;
		int i = 0;
		for (Team t : getTeams()) {

			for (Player p : t.getPlayers()) {

				i = i + 1;

				if (i == 1) {

					player_one = p;
				} else if (i == 2) {

					player_two = p;
				} else if (i == 3) {

					player_three = p;
				} else if (i == 4) {

					player_four = p;
					break;
				}

			}

		}

		ArenaUtil.sendCenteredWorldMessage(this,
				"" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.DARK_RED + ChatColor.BOLD + "DUELS");
		ArenaUtil.sendCenteredWorldMessage(this, "");
		ArenaUtil.sendCenteredWorldMessage(this,
				"" + PlayerManager.getServerPlayer(player_one).getRank().getPlayerFullName(player_one) + ChatColor.WHITE
						+ ", " + PlayerManager.getServerPlayer(player_two).getRank().getPlayerFullName(player_two));
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.WHITE + ChatColor.BOLD + "vs");
		ArenaUtil.sendCenteredWorldMessage(this,
				"" + PlayerManager.getServerPlayer(player_three).getRank().getPlayerFullName(player_three)
						+ ChatColor.WHITE + ", "
						+ PlayerManager.getServerPlayer(player_four).getRank().getPlayerFullName(player_four));
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.GREEN + ChatColor.BOLD + " ");

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

				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.GOLD + "+" + coins + " Unusable Duels Coins");
				ArenaUtil.sendCenteredPlayerMessage(player, "" + ChatColor.AQUA + "+" + xp + " Unusable Network XP");
				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.GREEN + "+" + crates + " Unusable Loot Crates");

				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");

			}
		}

	}

	@Override
	public final Arena clone() throws CloneNotSupportedException {
		return new DuelsOverpoweredDoubles("", getGamemode(), getMaxPlayers(), getMinPlayers());

	}

}
