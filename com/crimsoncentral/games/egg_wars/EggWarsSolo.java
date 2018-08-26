package com.crimsoncentral.games.egg_wars;

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

import org.apache.commons.lang.math.IntRange;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import com.crimsoncentral.Main;
import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.ArenaManager;
import com.crimsoncentral.arena.Team;
import com.crimsoncentral.arena.util.ArenaUtil;
import com.crimsoncentral.arena.util.DeathMessages;
import com.crimsoncentral.arena.util.Generator;
import com.crimsoncentral.arena.util.PlayerVisiblity;
import com.crimsoncentral.arena.util.PlayerVisiblity.Visiblity;
import com.crimsoncentral.cosmetics.LootCrateUtil;
import com.crimsoncentral.games.GameStats;
import com.crimsoncentral.games.duels.solos.DuelsUtil;
import com.crimsoncentral.games.sky_wars.SkyWarsKits;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.util.Config;
import com.crimsoncentral.util.JSONMessage;
import com.crimsoncentral.util.ScoreboardWrapper;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import net.minecraft.server.v1_12_R1.DragonControllerPhase;
import net.minecraft.server.v1_12_R1.EntityEnderDragon;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.World;

public final class EggWarsSolo extends Arena implements Cloneable {

	public EggWarsSolo(String arena_name, Double game_mode, int max_players, int min_players) {
		super(arena_name, game_mode, max_players, min_players);

		setGameTimer(20);

	}

	public EggWarsSolo game = this;

	public HashMap<Generator, Team> player_generators = new HashMap<Generator, Team>();
	public ArrayList<Generator> lapis_generators = new ArrayList<Generator>();
	public ArrayList<Generator> diamond_generators = new ArrayList<Generator>();
	public ArrayList<Generator> emerald_generators = new ArrayList<Generator>();

	public Location copia;

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

	public HashMap<Player, Integer> players_respawning = new HashMap<Player, Integer>();

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

				preGame("Egg Wars", "Solo", "", "Defend Your Egg!");
				if (getStage() == ArenaStage.COUNT_DOWN) {

					if (getGameTimer() == 0) {
						addCondition(Arena.Conditions.CAN_MOVE_ITEMS);
						addCondition(Arena.Conditions.CAN_PVP);
						addCondition(Arena.Conditions.SHOW_PLAYER_HEALTH);
						addCondition(Conditions.CAN_TAKE_DAMAGE);
						addCondition(Conditions.ELIMINATE_ON_LOG_OUT);

						DuelsUtil.setUpKits();
						sendGameStartInfoMessage();

					
					}

				} else if (getStage() == ArenaStage.GAME_TIME) {

					for (Entry<Player, Integer> e : players_respawning.entrySet()) {

						Player p = e.getKey();
						Integer time = e.getValue();

						players_respawning.put(e.getKey(), e.getValue() - 1);

						if (e.getValue() == 0) {
							game.sendTitle(p, ChatColor.GREEN + "RESPAWNED!", 1, 3, 1);
							p.sendMessage(ChatColor.GREEN + "RESPAWNED!");
							p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_TWINKLE, (float) 2, (float) 2);
							p.teleport(getPlayerTeam(p).team_spawn);
							PlayerVisiblity.setPlayerVisiblity(p, Visiblity.VISIBLE);
							PlayerVisiblity.updateWorldVisiblity(game.getWorld());
							p.setAllowFlight(false);
							p.setFlying(false);

							players_respawning.remove(p, time);

						} else {

							game.sendTitle(p, ChatColor.YELLOW + "You will respawn in", 0, 1, 0);
							game.sendSubTitle(p, "" + ChatColor.GREEN + time + ChatColor.YELLOW + " seconds...", 0, 1,
									0);

							p.sendMessage(ChatColor.YELLOW + "You will respawn in " + ChatColor.GREEN + time
									+ ChatColor.YELLOW + " seconds...");

							p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, (float) 1, (float) 1);
							PlayerVisiblity.setPlayerVisiblity(p, Visiblity.SPECTATOR);
							p.setAllowFlight(true);
							p.setFlying(true);

						}
					}

					sendGameScoreboard();

					if (getWinningTeam() != null) {
						setGameTimer(10);
						setStage(ArenaStage.AFTER_GAME);

					} else if (getGameTimer() == 2996) {
						addCondition(Arena.Conditions.CAN_TAKE_DAMAGE);

					} else if (getGameTimer() == 2580) {

						upgradeGeneratorsOfType(GeneratorType.LAPIS, 2, 45);

					} else if (getGameTimer() == 2100) {

						upgradeGeneratorsOfType(GeneratorType.DIAMOND, 2, 45);

					} else if (getGameTimer() == 1680) {

						upgradeGeneratorsOfType(GeneratorType.EMERALD, 2, 60);

					} else if (getGameTimer() == 1200) {

						upgradeGeneratorsOfType(GeneratorType.LAPIS, 3, 30);
					} else if (getGameTimer() == 900) {

						upgradeGeneratorsOfType(GeneratorType.DIAMOND, 3, 30);
					} else if (getGameTimer() == 600) {

						upgradeGeneratorsOfType(GeneratorType.EMERALD, 3, 40);
					} else if (getGameTimer() == 480) {

						for (int i = 1; i <= game.getAlivePlayers().size() * 2 - 1; ++i) {
							World w = ((CraftWorld) game.getWorld()).getHandle();
							EntityEnderDragon dragon = new EntityEnderDragon(w);

							dragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.a);
							dragon.setLocation(game.getArenaSpawn().getX(), game.getArenaSpawn().getY(),
									game.getArenaSpawn().getZ(), 0, 0);

							w.addEntity(dragon);
							dragon.setSilent(true);

							dragon.setCustomName(ChatColor.DARK_PURPLE + "Demogorgan " + ChatColor.DARK_RED + i);
							dragon.setCustomNameVisible(true);
						}

						game.sendWorldMessage(ChatColor.RED + "Sudden Death Has Begun! " + ChatColor.DARK_RED + "+"
								+ (game.getAlivePlayers().size() * 2 - 1) + ChatColor.DARK_PURPLE + " Dragons");
						game.playWorldSound(Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE, 1, 1);
						game.sendWorldTitle(ChatColor.RED + "Sudden Death Has Begun!", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.DARK_RED + "+" + (game.getAlivePlayers().size() * 2 - 1)
								+ ChatColor.DARK_PURPLE + " Dragon", 0, 2, 0);

					} else if (getGameTimer() == 180) {

						game.sendWorldMessage(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + "3 "
								+ ChatColor.RED + " Minutes");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + "3"
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

	@SuppressWarnings("deprecation")
	@Override
	public void setup() {

		game = this;
		game.allowNewPlayers(true);

		setStage(ArenaStage.PRE_COUNT_DOWN);

		File c = new File(getWorld().getWorldFolder().getName() + "/arena-config.yml");

		Config config = new Config(c);

		game.setArenaSpawn(config.getLocation("Arena-Spawn", game.getWorld()));

		game.createTeam("Red-Team", 1, "§c[RED] ", ChatColor.RED, config.getLocation("Red-Team-Spawn", game.getWorld()),
				true);
		game.createTeam("Blue-Team", 1, "§3[BLUE] ", ChatColor.DARK_AQUA,
				config.getLocation("Blue-Team-Spawn", game.getWorld()), true);
		game.createTeam("Yellow-Team", 1, "§e[YELLOW] ", ChatColor.YELLOW,
				config.getLocation("Yellow-Team-Spawn", game.getWorld()), true);
		game.createTeam("Green-Team", 1, "§a[GREEN] ", ChatColor.GREEN,
				config.getLocation("Green-Team-Spawn", game.getWorld()), true);
		game.createTeam("Purple-Team", 1, "§5[PURPLE] ", ChatColor.DARK_PURPLE,
				config.getLocation("Purple-Team-Spawn", game.getWorld()), true);
		game.createTeam("Orange-Team", 1, "§6[ORANGE] ", ChatColor.GOLD,
				config.getLocation("Orange-Team-Spawn", game.getWorld()), true);
		game.createTeam("Black-Team", 1, "§7[BLACK] ", ChatColor.GRAY,
				config.getLocation("Black-Team-Spawn", game.getWorld()), true);
		game.createTeam("White-Team", 1, "§f[WHITE] ", ChatColor.WHITE,
				config.getLocation("White-Team-Spawn", game.getWorld()), true);

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
			if (lowercase.contains("generator") || lowercase.contains("shop")
					|| lowercase.contains("egg") && !lowercase.contains("number") && !lowercase.contains("--")) {
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
					if (name.contains(("team-generator"))) {

						Team t = null;
						for (Team t1 : getTeams()) {

							if (distance(loc, t1.team_spawn) <= 30) {

								t = t1;
								break;
							}
						}
						if (t != null) {
							player_generators.put(
									new Generator(loc, new ItemStack(Material.IRON_INGOT, 1), 1, 3, false, "", null),
									t);
							player_generators.put(
									new Generator(loc, new ItemStack(Material.GOLD_INGOT, 1), 1, 6, false, "", null),
									t);
						}
					} else if (name.contains(("lapis-generator"))) {
						lapis_generators.add(new Generator(
								loc, new ItemStack(351, 1, (short) 4), 1, 60, true, ChatColor.BLUE + "" + ChatColor.BOLD
										+ "Lapis " + ChatColor.WHITE + ChatColor.BOLD + "Generator ",
								EnumParticle.WATER_DROP));
					} else if (name.contains(("diamond-generator"))) {
						diamond_generators.add(new Generator(
								loc, new ItemStack(Material.DIAMOND, 1), 1, 60, true, ChatColor.AQUA + ""
										+ ChatColor.BOLD + "Diamond " + ChatColor.WHITE + ChatColor.BOLD + "Generator ",
								EnumParticle.FALLING_DUST));
					} else if (name.contains(("emerald-generator"))) {
						emerald_generators.add(new Generator(
								loc, new ItemStack(Material.EMERALD, 1), 1, 70, true, ChatColor.DARK_GREEN + ""
										+ ChatColor.BOLD + "Emerald " + ChatColor.WHITE + ChatColor.BOLD + "Generator ",
								EnumParticle.VILLAGER_HAPPY));
					} else if (name.contains("-egg")) {
						Bukkit.broadcastMessage("good-1");
						for (Team t : getTeams()) {

							if (t.team_name.equalsIgnoreCase(name.replace("-egg", ""))) {

								Bukkit.broadcastMessage("good-2");
								EggWarsListeners.createNewEgg(loc, t);

							}

						}

					}

			}
		}

	}

	public double distance(Location l1, Location l2) {

		return (Math.pow(Math.pow(Math.abs(l2.getX() - l1.getX()), 2) + Math.pow(Math.abs(l2.getY() - l1.getY()), 2)
				+ Math.pow(Math.abs(l2.getZ() - l1.getZ()), 2), 0.5));

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

	@SuppressWarnings("deprecation")
	public void playerDeath(Player player, Player killer, DamageCause cause) {

		ArrayList<Material> materials_dropped = new ArrayList<Material>();
		ArrayList<Integer> materials_dropped_id = new ArrayList<Integer>();
		materials_dropped.add(Material.IRON_INGOT);
		materials_dropped.add(Material.GOLD_INGOT);
		materials_dropped.add(Material.DIAMOND);
		materials_dropped.add(Material.EMERALD);

		materials_dropped_id.add(351);

		player.setHealth(20);
		player.setSaturation(20);
		player.setAllowFlight(true);
		player.setFlying(true);
		DeathMessages.broadcastDeathMessage(player, cause);

		for (ItemStack is : player.getInventory().getContents()) {

			if (is != null && !materials_dropped.contains(is.getType())
					&& !materials_dropped_id.contains(is.getTypeId())) {

				is.setAmount(0);
				is.setType(Material.AIR);

			}
		}

		ArenaUtil.dropPlayerInventory(player);
		PlayerVisiblity.setPlayerVisiblity(player, PlayerVisiblity.Visiblity.SPECTATOR);
		PlayerVisiblity.updateWorldVisiblity(game.getWorld());

		if (game.getPlayerTeam(player).can_respawn == true) {
			players_respawning.put(player, 5);
			player.teleport(game.getArenaSpawn());
			player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASEDRUM, 1, 1);
			game.sendTitle(player, ChatColor.YELLOW + "You will respawn in", 0, 1, 0);
			game.sendSubTitle(player, "" + ChatColor.GREEN + "5" + ChatColor.YELLOW + " seconds...", 0, 1, 0);
		} else {
			game.sendTitle(player, ChatColor.RED + "You have been", 1, 5, 1);
			game.sendSubTitle(player, ChatColor.GRAY + "Eliminated!", 1, 5, 1);
			JSONMessage.create("§lYou Died! ").color(ChatColor.RED).then("§lWant to play again? ")
					.color(ChatColor.YELLOW).then("§l[CLICK HERE]").color(ChatColor.GREEN)
					.tooltip("Click to Play Again").runCommand("/play skywars ballistic solo").send(player);
		}
	}

	public void giveSpawnItems(Player player) {

		player.getInventory().setItem(0, SkyWarsKits.solo_kit_selector.opener);

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

	

	public void sendGameScoreboard() {

		String timer = "";
		if (getGameTimer() % 60 < 10) {
			timer = (getGameTimer() / 60) + ":0" + (getGameTimer() % 60);
		} else {
			timer = (getGameTimer() / 60) + ":" + (getGameTimer() % 60);
		}

		String next_event = "";

		int event_time = 0;
		String time_display = "";

		if (new IntRange(2580, 3000).containsInteger(getGameTimer())) {

			next_event = ChatColor.DARK_BLUE + "Lapis " + ChatColor.WHITE + "Upgrade";
			event_time = 2580;
		} else if (new IntRange(2100, 2579).containsInteger(getGameTimer())) {
			next_event = ChatColor.AQUA + "Diamond " + ChatColor.WHITE + "Upgrade";
			event_time = 2100;

		} else if (new IntRange(1680, 2099).containsInteger(getGameTimer())) {
			next_event = ChatColor.DARK_GREEN + "Emerald " + ChatColor.WHITE + "Upgrade";
			event_time = 1680;

		} else if (new IntRange(1200, 1679).containsInteger(getGameTimer())) {
			next_event = ChatColor.DARK_BLUE + "Lapis " + ChatColor.WHITE + "Upgrade";
			event_time = 1200;

		} else if (new IntRange(900, 1199).containsInteger(getGameTimer())) {
			next_event = ChatColor.AQUA + "Diamond " + ChatColor.WHITE + "Upgrade";
			event_time = 900;

		} else if (new IntRange(600, 899).containsInteger(getGameTimer())) {
			next_event = ChatColor.DARK_GREEN + "Emerald " + ChatColor.WHITE + "Upgrade";
			event_time = 600;

		} else if (new IntRange(480, 599).containsInteger(getGameTimer())) {
			next_event = ChatColor.DARK_PURPLE + "Sudden Death";
			event_time = 480;

		} else if (new IntRange(0, 479).containsInteger(getGameTimer())) {
			next_event = ChatColor.RED + "Game End";
			event_time = 0;

		}

		if (getGameTimer() % 60 < 10) {
			time_display = "" + ((getGameTimer() - event_time) / 60) + ":0" + ((getGameTimer() - event_time) % 60);
		} else {
			time_display = "" + ((getGameTimer() - event_time) / 60) + ":" + ((getGameTimer() - event_time) % 60);
		}

		String rs = getTeamDisplayStatus(ChatColor.RED).toString();
		String bs = getTeamDisplayStatus(ChatColor.DARK_AQUA).toString();
		String ys = getTeamDisplayStatus(ChatColor.YELLOW).toString();
		String gs = getTeamDisplayStatus(ChatColor.GREEN).toString();
		String ps = getTeamDisplayStatus(ChatColor.DARK_PURPLE).toString();
		String os = getTeamDisplayStatus(ChatColor.GOLD).toString();
		String bs2 = getTeamDisplayStatus(ChatColor.GRAY).toString();
		String ws = getTeamDisplayStatus(ChatColor.WHITE).toString();

		String team_statuses = rs + bs + ys + gs + ps + os + ws + bs2;

		for (Player p : getWorld().getPlayers()) {

			String egg = ChatColor.GREEN + "Alive";
			String tms = " " + String.valueOf(getPlayerTeam(p).getAlivePlayers().size());

			if (game.getPlayerTeam(p).can_respawn == false) {

				egg = ChatColor.RED + "Broken";

			}
			com.crimsoncentral.util.ScoreboardWrapper sw = ArenaManager.scoreboards.get(p);

			p.setScoreboard(sw.getScoreboard());
			sw.changePage(getArenaId());
			sw.setTitle(getArenaId(), ChatColor.YELLOW + "" + ChatColor.BOLD + "EGG WARS" + ChatColor.GRAY + " - "
					+ ChatColor.WHITE + timer);

			sw.setLine(getArenaId(), 14, "" + ChatColor.GRAY + Main.date);
			sw.setLine(getArenaId(), 13, next_event + ":");
			sw.setLine(getArenaId(), 12, ChatColor.GREEN + time_display);
			sw.setLine(getArenaId(), 11, " ");
			sw.setLine(getArenaId(), 10, ChatColor.GOLD + "" + ChatColor.BOLD + "Team Statuses:");
			sw.setLine(getArenaId(), 9, " " + team_statuses);

			sw.setLine(getArenaId(), 8, " ");
			sw.setLine(getArenaId(), 7, ChatColor.AQUA + "" + ChatColor.BOLD + "Your Team:");
			sw.setLine(getArenaId(), 6, "Color: " + getTeamDisplaySquare(p));
			sw.setLine(getArenaId(), 5, "Egg: " + egg);
			sw.setLine(getArenaId(), 4, "Alive Members:" + ChatColor.GREEN + tms);
			sw.setLine(getArenaId(), 3, " ");
			sw.setLine(getArenaId(), 2, ChatColor.WHITE + "Map: ", ChatColor.DARK_GREEN + game.getMapName());
			sw.setLine(getArenaId(), 1, " ");

			sw.setLine(getArenaId(), 0, ChatColor.RED + " crimson-centr", ChatColor.RED + "al.com");
		}

	}

	public String getTeamDisplayStatus(ChatColor c) {

		String display = "";

		for (Team t : getTeams()) {

			if (t.team_color == c) {

				if (t.can_respawn == true) {

					display = c + "✔";
				} else if (t.getAlivePlayers().size() != 0) {
					display = "" + c + t.getAlivePlayers().size();

				} else {
					display = t.team_color + "✘";

				}

				break;
			}
		}
		return display;
	}

	public String getTeamDisplaySquare(Player p) {

		String display = "";

		for (Team t : getTeams()) {

			if (t.getPlayers().contains(p)) {

				if (t.can_respawn == true) {

					display = t.team_color + "" + ChatColor.BOLD + "■";

				}
				break;
			}
		}
		return display;
	}

	public void sendGameStartInfoMessage() {

		ArenaUtil.sendCenteredWorldMessage(this,
				"" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.AQUA + ChatColor.BOLD + "SKY WARS");
		ArenaUtil.sendCenteredWorldMessage(this, "");
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.GREEN + ChatColor.BOLD + "Gear up by collecting");
		ArenaUtil.sendCenteredWorldMessage(this,
				"" + ChatColor.GREEN + ChatColor.BOLD + "items from chests. Knock people off the");
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
						"" + ChatColor.GOLD + "+" + coins + " Unusable Sky Wars Coins");
				ArenaUtil.sendCenteredPlayerMessage(player, "" + ChatColor.AQUA + "+" + xp + " Unusable Network XP");
				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.GREEN + "+" + crates + " Unusable Loot Crates");

				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");

				JSONMessage.create("§lThe Game has ended! ").color(ChatColor.GOLD).then("§lWant to play again? ")
						.color(ChatColor.YELLOW).then("§l[CLICK HERE]").color(ChatColor.GREEN)
						.tooltip("Click to Play Again").runCommand("/play skywars ballistic solo").send(player);
			}
		}

	}

	public void startGenerators() {

		for (Entry<Generator, Team> e : player_generators.entrySet()) {

			e.getKey().start();
		}

		for (Generator g : lapis_generators) {

			g.start();
		}

		for (Generator g : diamond_generators) {

			g.start();
		}

		for (Generator g : emerald_generators) {

			g.start();
		}
	}

	public ArrayList<Generator> getGenerators() {
		ArrayList<Generator> gens = new ArrayList<Generator>();

		for (Entry<Generator, Team> e : player_generators.entrySet()) {

			gens.add(e.getKey());
		}

		for (Generator g : lapis_generators) {

			gens.add(g);
		}

		for (Generator g : diamond_generators) {

			gens.add(g);
		}

		for (Generator g : emerald_generators) {

			gens.add(g);
		}

		return gens;

	}

	public static enum GeneratorType {
		PLAYER, LAPIS, DIAMOND, EMERALD

	}

	public void upgradeGeneratorsOfType(GeneratorType t, int level, int interval) {

		String numeral = "I";
		String gen_name = "null gen";

		if (level == 2) {
			numeral = "II";
		}
		if (level == 3) {
			numeral = "III";
		}
		if (level == 4) {
			numeral = "IV";
		}
		if (level == 5) {
			numeral = "VI";
		}

		if (t == GeneratorType.PLAYER) {

			for (Entry<Generator, Team> e : player_generators.entrySet()) {

				e.getKey().setSpawnInterval(interval);
			}
			gen_name = ChatColor.WHITE + "Team Generators";
		} else if (t == GeneratorType.LAPIS) {
			for (Generator g : lapis_generators) {

				g.setSpawnInterval(interval);
			}
			gen_name = ChatColor.DARK_BLUE + "Lapis Generators";
		} else if (t == GeneratorType.DIAMOND) {
			for (Generator g : diamond_generators) {

				g.setSpawnInterval(interval);
			}
			gen_name = ChatColor.AQUA + "Diamond Generators";
		} else if (t == GeneratorType.EMERALD) {
			for (Generator g : emerald_generators) {

				g.setSpawnInterval(interval);

			}
			gen_name = ChatColor.DARK_GREEN + "Emerald Generators";
		}

		game.sendWorldMessage(ChatColor.YELLOW + "All " + gen_name + ChatColor.YELLOW + " have been upgraded to level "
				+ ChatColor.GREEN + numeral);
		game.playWorldSound(Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
		game.sendWorldTitle("", 0, 1, 0);
		game.sendWorldSubTitle(ChatColor.WHITE + gen_name.replace("Generators", "Gens") + ChatColor.YELLOW
				+ " have been upgraded to level " + ChatColor.GREEN + numeral, 0, 3, 0);

	}

	@Override
	public final Arena clone() throws CloneNotSupportedException {
		return new EggWarsSolo("", getGamemode(), getMaxPlayers(), getMinPlayers());

	}
}
