package com.crimsoncentral.games.sky_wars.modes.doubles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.StringTokenizer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
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
import com.crimsoncentral.arena.game_events.ChestRefill;
import com.crimsoncentral.arena.util.ArenaUtil;
import com.crimsoncentral.arena.util.DeathMessages;
import com.crimsoncentral.arena.util.PlayerVisiblity;
import com.crimsoncentral.cosmetics.LootCrateUtil;
import com.crimsoncentral.games.GameStats;
import com.crimsoncentral.games.duels.solos.DuelsUtil;
import com.crimsoncentral.games.sky_wars.SkyWarsKits;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.util.Config;
import com.crimsoncentral.util.JSONMessage;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public final class SkyWarsNormalDoubles extends Arena implements Cloneable {

	public SkyWarsNormalDoubles(String arena_name, Double game_mode, int max_players, int min_players) {
		super(arena_name, game_mode, max_players, min_players);

		setGameTimer(20);

	}

	public SkyWarsNormalDoubles game = this;

	public ArrayList<Location> player_island_chests = new ArrayList<Location>();
	public ArrayList<Location> pre_center_island_chests = new ArrayList<Location>();
	public ArrayList<Location> center_island_chests = new ArrayList<Location>();

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

				preGame("SKY WARS", "Doubles", "Ballistic", "Dont forget to select a kit");
				SkyWarsKits.setupSoloKitSelector();
				if (getStage() == ArenaStage.COUNT_DOWN) {

					if (getGameTimer() == 0) {
						addCondition(Arena.Conditions.CAN_MOVE_ITEMS);
						addCondition(Arena.Conditions.CAN_PVP);
						addCondition(Arena.Conditions.SHOW_PLAYER_HEALTH);
						addCondition(Conditions.CAN_TAKE_DAMAGE);
						addCondition(Conditions.CAN_BLOCK_INTERACT);
						addCondition(Conditions.ELIMINATE_ON_LOG_OUT);

						DuelsUtil.setUpKits();
						sendGameStartInfoMessage();

						for (Player p : getAlivePlayers()) {

							p.teleport(getPlayerTeam(p).team_spawn);

						}
						playerRefill(player_island_chests, 10);
						preCenterRefill(pre_center_island_chests, 7);
						centerRefill(center_island_chests, 8);
						deleteCages();
						SkyWarsKits.solo_kit_selector.givePlayerKits(game, getAlivePlayers());
					}

				} else if (getStage() == ArenaStage.GAME_TIME) {

					sendGameScoreboard();

					if (getWinningTeam() != null) {
						setGameTimer(10);
						setStage(ArenaStage.AFTER_GAME);

					} else if (getGameTimer() == 896) {
						addCondition(Arena.Conditions.CAN_TAKE_DAMAGE);

					} else if (getGameTimer() == 600) {

						playerRefill(player_island_chests, 12);
						preCenterRefill(pre_center_island_chests, 9);
						centerRefill(center_island_chests, 10);

						game.sendWorldMessage(ChatColor.GREEN + "All chests have been refilled!");
						game.playWorldSound(Sound.BLOCK_CHEST_OPEN, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.GREEN + "All chests have been refilled!", 0, 2, 0);

					} else if (getGameTimer() == 300) {

						game.sendWorldMessage(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + "5"
								+ ChatColor.RED + " Minutes");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + "5"
								+ ChatColor.RED + " minutes", 0, 2, 0);

					} else if (getGameTimer() == 240) {

						preCenterRefill(player_island_chests, 10);
						centerRefill(pre_center_island_chests, 8);
						centerRefill(center_island_chests, 10);

						game.sendWorldMessage(ChatColor.GREEN + "All chests have been refilled!");
						game.playWorldSound(Sound.BLOCK_CHEST_OPEN, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.GREEN + "All chests have been refilled!", 0, 2, 0);

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

					} else if (getGameTimer() == 150) {

						centerRefill(player_island_chests, 10);
						centerRefill(pre_center_island_chests, 11);
						centerRefill(center_island_chests, 12);

						game.sendWorldMessage(ChatColor.GREEN + "All chests have been refilled!");
						game.playWorldSound(Sound.BLOCK_CHEST_OPEN, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.GREEN + "All chests have been refilled!", 0, 2, 0);

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

		for (int i = 1; i <= 12; ++i) {
			game.createTeam("Team-" + i, 2, "", null, config.getLocation("Team-" + i + "-Spawn", game.getWorld()),
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
					if (name.contains(("tier-1-chest"))) {
						center_island_chests.add(loc);
					} else if (name.contains(("tier-2-chest"))) {
						pre_center_island_chests.add(loc);
					} else if (name.contains(("tier-3-chest"))) {
						player_island_chests.add(loc);
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

	public void sendGameScoreboard() {

		for (Player p : getPlayers()) {
			if (p.getWorld() == getWorld()) {
				com.crimsoncentral.util.ScoreboardWrapper sw = ArenaManager.scoreboards.get(p);

				p.setScoreboard(sw.getScoreboard());
				sw.changePage(getArenaId());
				sw.setTitle(getArenaId(), ChatColor.YELLOW + "" + ChatColor.BOLD + "SKY WARS");

				sw.setLine(getArenaId(), 14, "" + ChatColor.GRAY + Main.date + " Doubles");

				sw.setLine(getArenaId(), 13, ChatColor.WHITE + "Game Time Left", ChatColor.WHITE + ":");
				if (getStage() == ArenaStage.GAME_TIME) {
					if (getGameTimer() % 60 < 10) {
						sw.setLine(getArenaId(), 12, ChatColor.GREEN + "",
								getGameTimer() / 60 + ":0" + getGameTimer() % 60);
					} else {
						sw.setLine(getArenaId(), 12, ChatColor.GREEN + "",
								getGameTimer() / 60 + ":" + getGameTimer() % 60);
					}
				} else if (getStage() == ArenaStage.AFTER_GAME) {
					sw.setLine(getArenaId(), 12, ChatColor.GREEN + "Game Ended!");
				} else {
					sw.setLine(getArenaId(), 12, ChatColor.GREEN + "15:00");
				}
				sw.setLine(getArenaId(), 11, ChatColor.DARK_PURPLE + "");

				if (getStage() == ArenaStage.GAME_TIME) {
					if (getGameTimer() >= 600) {
						int i = getGameTimer() / 60 - 10;
						if (getGameTimer() % 60 < 10) {
							sw.setLine(getArenaId(), 10, ChatColor.WHITE + "Next Refill: ",
									ChatColor.GREEN + "" + i + ":0" + getGameTimer() % 60);
						} else {
							sw.setLine(getArenaId(), 10, ChatColor.WHITE + "Next Refill: ",
									ChatColor.GREEN + "" + i + ":" + getGameTimer() % 60);
						}
					} else if (getGameTimer() >= 240) {
						int i = getGameTimer() / 60 - 4;
						if (getGameTimer() % 60 < 10) {
							sw.setLine(getArenaId(), 10, ChatColor.WHITE + "Next Refill: ",
									ChatColor.GREEN + "" + i + ":0" + getGameTimer() % 60);
						} else {
							sw.setLine(getArenaId(), 10, ChatColor.WHITE + "Next Refill: ",
									ChatColor.GREEN + "" + i + ":" + getGameTimer() % 60);
						}
					} else {
						if (getGameTimer() % 60 < 10) {
							sw.setLine(getArenaId(), 10, ChatColor.WHITE + "Game End: ",
									ChatColor.GREEN + "" + getGameTimer() / 60 + ":0" + getGameTimer() % 60);
						} else {
							sw.setLine(getArenaId(), 10, ChatColor.WHITE + "Game End: ",
									ChatColor.GREEN + "" + getGameTimer() / 60 + ":" + getGameTimer() % 60);
						}
					}
				} else if (getStage() == ArenaStage.AFTER_GAME) {
					sw.setLine(getArenaId(), 10, ChatColor.WHITE + "Game End: ", ChatColor.GREEN + "0:00");
				} else {
					sw.setLine(getArenaId(), 10, ChatColor.WHITE + "Next Refill: ", ChatColor.GREEN + "5:00");
				}

				sw.setLine(getArenaId(), 8, ChatColor.WHITE + "Players Left:",
						ChatColor.GREEN + " " + getAlivePlayers().size());
				sw.setLine(getArenaId(), 7, ChatColor.WHITE + "Your Team: " + ChatColor.GOLD
						+ game.getPlayerTeam(p).getAlivePlayers().size());
				sw.setLine(getArenaId(), 6, ChatColor.GOLD + "");
				sw.setLine(getArenaId(), 5, ChatColor.WHITE + "Kills: ",
						ChatColor.GREEN + "" + game.getStats(p).getFinalKills());
				sw.setLine(getArenaId(), 4, ChatColor.GOLD + "");
				sw.setLine(getArenaId(), 3, ChatColor.DARK_GREEN + "Map: ", ChatColor.GOLD + game.getMapName());
				sw.setLine(getArenaId(), 2, ChatColor.DARK_GREEN + "Mode: ", ChatColor.DARK_GREEN + "Normal");

				sw.setLine(getArenaId(), 1, ChatColor.DARK_PURPLE + "");
				sw.setLine(getArenaId(), 0, ChatColor.RED + " crimson-centr", ChatColor.RED + "al.com");
			}
		}

	}

	public void buildCages() {

		HashMap<Integer, Player> players = new HashMap<Integer, Player>();

		for (Team t : game.getTeams()) {
			Iterator<Entry<Player, Team.PlayerStatus>> it = t.players.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Player, Team.PlayerStatus> pair1 = it.next();

				players.put(players.size(), pair1.getKey());
			}
		}

		Random rand = new Random();
		Integer randInt = rand.nextInt(players.size());
		Iterator<Entry<Integer, Player>> it = players.entrySet().iterator();
		while (it.hasNext()) {

			Entry<Integer, Player> pair = it.next();

			if (pair.getKey() == randInt) {

			}

		}

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
						"" + ChatColor.GRAY + ChatColor.BOLD + "WINNERS: " + winners);
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
						.tooltip("Click to Play Again").runCommand("/play skywars normal teams").send(player);
			}
		}

	}

	public void deleteCages() {

		ArrayList<Team> ts = game.getTeams();

		for (Team t : ts) {

			Location l = new Location(getWorld(), t.team_spawn.getX(), t.team_spawn.getY() + 1, t.team_spawn.getZ());

			for (int x = l.getBlockX() - 2; x <= l.getBlockX() + 2; x++) {
				for (int y = l.getBlockY() - 2; y <= l.getBlockY() + 2; y++) {
					for (int z = l.getBlockZ() - 2; z <= l.getBlockZ() + 2; z++) {

						Location loc = new Location(game.getWorld(), x, y, z);

						loc.getBlock().setType(Material.AIR);

					}
				}
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

	public void playerRefill(ArrayList<Location> chests, int slots_to_fill) {

		ChestRefill refill = new ChestRefill(1, 750, slots_to_fill);

		for (Location l : chests) {

			refill.addChest(l);

		}

		refill.addItem(new ItemStack(Material.LEATHER_BOOTS), 1, 40);
		refill.addItem(new ItemStack(Material.LEATHER_CHESTPLATE), 41, 80);
		refill.addItem(new ItemStack(Material.LEATHER_LEGGINGS), 81, 120);
		refill.addItem(new ItemStack(Material.LEATHER_BOOTS), 121, 160);

		refill.addItem(new ItemStack(Material.CHAINMAIL_BOOTS), 161, 186);
		refill.addItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE), 187, 208);
		refill.addItem(new ItemStack(Material.CHAINMAIL_LEGGINGS), 209, 236);
		refill.addItem(new ItemStack(Material.CHAINMAIL_BOOTS), 237, 258);

		refill.addItem(new ItemStack(Material.IRON_LEGGINGS), 259, 270);
		refill.addItem(new ItemStack(Material.CHAINMAIL_HELMET), 271, 282);

		refill.addItem(new ItemStack(Material.WOOD_SWORD), 283, 320);
		refill.addItem(new ItemStack(Material.STONE_SWORD), 321, 340);
		refill.addItem(new ItemStack(Material.IRON_SWORD), 341, 351);
		refill.addItem(new ItemStack(Material.BOW), 352, 369);
		refill.addItem(new ItemStack(Material.ARROW, 12), 370, 395);
		refill.addItem(new ItemStack(Material.ARROW, 24), 396, 412);
		refill.addItem(new ItemStack(Material.FISHING_ROD, 2), 413, 450);
		refill.addItem(new ItemStack(Material.SNOW_BALL, 32), 451, 475);
		refill.addItem(new ItemStack(Material.EGG, 16), 476, 500);
		refill.addItem(new ItemStack(Material.COOKED_BEEF, 16), 501, 530);

		refill.addItem(new ItemStack(Material.WOOD, 24), 531, 549);
		refill.addItem(new ItemStack(Material.LOG, 16), 550, 570);
		refill.addItem(new ItemStack(Material.WOOD, 64), 571, 582);
		refill.addItem(new ItemStack(Material.LOG, 24), 583, 595);
		refill.addItem(new ItemStack(Material.STONE, 24), 596, 615);

		refill.addItem(new ItemStack(Material.WOOD_AXE, 1), 616, 640);
		refill.addItem(new ItemStack(Material.WOOD_PICKAXE, 1), 641, 665);
		refill.addItem(new ItemStack(Material.WOOD_SPADE, 1), 666, 675);
		refill.addItem(new ItemStack(Material.STONE_AXE, 1), 676, 688);
		refill.addItem(new ItemStack(Material.STONE_PICKAXE, 1), 689, 700);
		refill.addItem(new ItemStack(Material.IRON_PICKAXE, 1), 701, 715);
		refill.addItem(new ItemStack(Material.IRON_PICKAXE, 1), 716, 750);
		refill.refill();
	}

	public void preCenterRefill(ArrayList<Location> chests, int slots_to_fill) {

		ChestRefill refill = new ChestRefill(1, 235, slots_to_fill);

		for (Location l : chests) {

			refill.addChest(l);

		}

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

		refill.addItem(new ItemStack(Material.GOLDEN_APPLE, 3), 1, 30);
		refill.addItem(new ItemStack(Material.FISHING_ROD, 1), 31, 69);

		refill.addItem(regen_potion, 70, 79);
		refill.addItem(speed_potion, 80, 92);
		refill.addItem(fire_potion, 93, 113);

		refill.addItem(new ItemStack(Material.IRON_SWORD, 1), 114, 134);
		refill.addItem(new ItemStack(Material.SNOW_BALL, 48), 135, 170);
		refill.addItem(new ItemStack(Material.ARROW, 24), 171, 204);
		refill.addItem(new ItemStack(Material.ARROW, 32), 205, 215);
		refill.addItem(new ItemStack(Material.STONE, 48), 216, 235);
		refill.refill();
	}

	public void centerRefill(ArrayList<Location> chests, int slots_to_fill) {

		ChestRefill refill = new ChestRefill(1, 808, slots_to_fill);

		for (Location l : chests) {

			refill.addChest(l);

		}

		ItemStack enchanted_iron_sword = new ItemStack(Material.IRON_SWORD, 1);
		enchanted_iron_sword.addEnchantment(Enchantment.FIRE_ASPECT, 1);

		ItemStack enchanted_bow = new ItemStack(Material.BOW, 1);
		enchanted_bow.addEnchantment(Enchantment.ARROW_DAMAGE, 1);

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
		refill.addItem(new ItemStack(Material.IRON_SWORD, 1), 41, 71);
		refill.addItem(enchanted_iron_sword, 72, 89);
		refill.addItem(new ItemStack(Material.DIAMOND_SWORD, 1), 90, 105);
		refill.addItem(new ItemStack(Material.SNOW_BALL, 64), 106, 48);
		refill.addItem(enchanted_bow, 149, 189);
		refill.addItem(new ItemStack(Material.ARROW, 16), 190, 204);
		refill.addItem(new ItemStack(Material.ARROW, 32), 205, 215);

		refill.addItem(new ItemStack(Material.IRON_HELMET, 1), 206, 241);
		refill.addItem(new ItemStack(Material.IRON_CHESTPLATE, 1), 242, 277);
		refill.addItem(new ItemStack(Material.IRON_LEGGINGS, 1), 278, 303);
		refill.addItem(new ItemStack(Material.IRON_BOOTS, 1), 304, 339);
		refill.addItem(new ItemStack(Material.DIAMOND_HELMET, 1), 340, 362);
		refill.addItem(new ItemStack(Material.DIAMOND_BOOTS, 1), 363, 385);

		refill.addItem(new ItemStack(Material.LOG, 24), 486, 435);
		refill.addItem(new ItemStack(Material.STONE, 24), 436, 475);

		refill.addItem(new ItemStack(Material.GOLDEN_APPLE, 5), 476, 520);
		refill.addItem(new ItemStack(Material.COOKED_BEEF, 32), 521, 542);

		refill.addItem(new ItemStack(Material.EGG, 16), 543, 593);
		refill.addItem(new ItemStack(Material.TNT, 10), 594, 629);
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
		return new SkyWarsNormalDoubles("", getGamemode(), getMaxPlayers(), getMinPlayers());

	}
}
