package com.crimsoncentral.games.hunger_games;

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
import org.bukkit.WorldBorder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.util.Config;
import com.crimsoncentral.util.JSONMessage;
import com.crimsoncentral.util.item.ActionItemManager;
import com.crimsoncentral.util.other.OtherUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

public final class HungerGamesNoKitsSolo extends Arena implements Cloneable {

	public HungerGamesNoKitsSolo(String arena_name, Double game_mode, int max_players, int min_players) {
		super(arena_name, game_mode, max_players, min_players);

		setGameTimer(20);

	}

	public HungerGamesNoKitsSolo game = this;

	public ArrayList<Location> low_tier_chests = new ArrayList<Location>();
	public ArrayList<Location> mid_tier_chests = new ArrayList<Location>();
	public ArrayList<Location> high_tier_chests = new ArrayList<Location>();

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

	public Wither hellburn = null;
	public Player previous_hellburn_target;
	public boolean hellburn_released = false;
	public boolean hellburn_minions_released = false;
	public ArrayList<WitherSkeleton> minions = new ArrayList<WitherSkeleton>();
	int y;

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

				preGame("HUNGER GAMES", "SOLO", "No kits", "Be the last one standing");

				if (getStage() == ArenaStage.COUNT_DOWN) {

					if (getGameTimer() == 0) {
						addCondition(Arena.Conditions.CAN_MOVE_ITEMS);

						addCondition(Arena.Conditions.SHOW_PLAYER_HEALTH);

						addCondition(Conditions.ELIMINATE_ON_LOG_OUT);

						sendGameStartInfoMessage();

						lowTierRefill(low_tier_chests, 5);
						midTierRefill(mid_tier_chests, 7);
						highTierRefill(high_tier_chests, 9);

						for (Player p : getAlivePlayers()) {

							resetPlayer(p);

						}

						for (Team t : getTeams()) {
							Location lt = t.team_spawn;
							Location l = new Location(lt.getWorld(), lt.getX(), lt.getY(), lt.getZ(), lt.getYaw(),
									lt.getPitch());

							l.add(0, 1, 0);

							game.deleteBlockRadius(l, 1, 1, 1);

						}

						game.deleteBlockRadius(getArenaSpawn(), 16);
					}

				} else if (getStage() == ArenaStage.GAME_TIME) {

					sendGameScoreboard();

					if (hellburn_released == true || hellburn_minions_released == true) {

						newHellburnTarget();
						newHellburnMinionsTargets();
					}

					if (getWinningTeam() != null) {
						setGameTimer(10);
						setStage(ArenaStage.AFTER_GAME);

					} else if (getGameTimer() == getMaxGameTime() - 15) {
						addCondition(Arena.Conditions.CAN_TAKE_DAMAGE);
						addCondition(Arena.Conditions.CAN_PVP);

						sendWorldMessage(ChatColor.YELLOW
								+ "Your invincibility has woren off and so has everyone else's! You can kill others as well as be damaged."
								+ ChatColor.RED + " Watch out!");
						game.playWorldSound(Sound.BLOCK_COMPARATOR_CLICK, 2, 2);
					} else if (getGameTimer() == 780) {

						lowTierRefill(low_tier_chests, 7);
						midTierRefill(mid_tier_chests, 9);
						highTierRefill(high_tier_chests, 11);

						game.sendWorldMessage(ChatColor.GREEN + "All chests have been refilled!");
						game.playWorldSound(Sound.BLOCK_CHEST_OPEN, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.GREEN + "All chests have been refilled!", 0, 2, 0);

					} else if (getGameTimer() == 480) {

						midTierRefill(low_tier_chests, 8);
						highTierRefill(mid_tier_chests, 10);
						highTierRefill(high_tier_chests, 12);

						game.sendWorldMessage(ChatColor.GREEN + "All chests have been refilled!");
						game.playWorldSound(Sound.BLOCK_CHEST_OPEN, 1, 1);
						game.sendWorldTitle("", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.GREEN + "All chests have been refilled!", 0, 2, 0);

					} else if (getGameTimer() == 300) {

						WorldBorder border = game.getWorld().getWorldBorder();

						border.setWarningDistance(10);
						border.setDamageAmount(8);
						border.setCenter(getArenaSpawn().getX(), getArenaSpawn().getZ());
						border.setSize(60, 0);

						game.sendWorldMessage(ChatColor.YELLOW + "Death Match has begun! " + ChatColor.RED
								+ "Game will be Ending in " + ChatColor.YELLOW + "5" + ChatColor.RED + " Minutes");
						game.playWorldSound(Sound.BLOCK_NOTE_SNARE, 1, 1);
						game.sendWorldTitle(ChatColor.YELLOW + "Death Match Has Begun", 0, 2, 0);
						game.sendWorldSubTitle(ChatColor.RED + "Game will be Ending in " + ChatColor.YELLOW + "5"
								+ ChatColor.RED + " minutes", 0, 2, 0);

						for (Player p : getAlivePlayers()) {

							p.teleport(getPlayerTeam(p).team_spawn);

						}

					} else if (getGameTimer() == 180) {

						int i = 0;
						for (Team t : getTeams()) {

							i = i + 1;

							WitherSkeleton skell = (WitherSkeleton) getWorld().spawnEntity(t.team_spawn,
									EntityType.WITHER_SKELETON);

							skell.setSilent(true);

							skell.setCustomName(ChatColor.RED + "Hellburn Minion " + ChatColor.DARK_RED + i);
							skell.setCustomNameVisible(true);

							skell.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, 3), true);

							minions.add(skell);

						}

						game.sendWorldMessage(ChatColor.RED + "The Hellburn Minions have arrived!" + ChatColor.DARK_RED
								+ " + 24" + ChatColor.RED + " Minions");
						game.playWorldSound(Sound.ENTITY_WITHER_SHOOT, 1, 1);
						game.sendWorldTitle(ChatColor.RED + "The Hellburn Minions have arrived", 0, 2, 0);

						newHellburnMinionsTargets();
						hellburn_minions_released = true;
					} else if (getGameTimer() == 120) {

						for (Player p : getWorld().getPlayers()) {
							p.setPlayerTime(17000, false);
						}

						Location l = getArenaSpawn().clone();

						for (Team t : getTeams()) {
							l.setY(t.team_spawn.getY() + 5);
							break;
						}
						hellburn = (Wither) getWorld().spawnEntity(l, EntityType.WITHER);

						hellburn.setCustomName(ChatColor.RED + "Hellburn " + ChatColor.DARK_RED + "The Demon");

						hellburn.setCustomNameVisible(true);

						hellburn.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, 3), true);
						hellburn.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100000, 2), true);

						newHellburnTarget();
						hellburn_released = true;
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

		makeArenaWorld();
		setStage(ArenaStage.PRE_COUNT_DOWN);
		setMaxGameTime(1200);

		File c = new File(getWorld().getWorldFolder().getName() + "/arena-config.yml");

		Config config = new Config(c);

		game.setArenaSpawn(config.getLocation("Arena-Spawn", game.getWorld()));

		for (int i = 1; i <= 24; ++i) {
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
					if (name.contains(("low-tier-chest"))) {
						low_tier_chests.add(loc);
					} else if (name.contains(("mid-tier-chest"))) {
						mid_tier_chests.add(loc);
					} else if (name.contains(("high-tier-chest"))) {
						high_tier_chests.add(loc);
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

		player.getInventory().setItem(8, ActionItemManager.getActionItemItemStack("return to main lobby bed"));

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
		ChatColor color = ChatColor.RED;
		if (getGameTimer() % 2 == 1) {

			color = ChatColor.RED;
		} else {

			color = ChatColor.YELLOW;
		}

		int alive_players = getAlivePlayers().size();
		if (getWorld().getPlayers().size() > 0) {

			for (Player p : getWorld().getPlayers()) {

				if (p.getWorld() == getWorld()) {

					com.crimsoncentral.util.ScoreboardWrapper sw = ArenaManager.scoreboards.get(p);

					p.setScoreboard(sw.getScoreboard());
					sw.changePage(getArenaId());
					sw.setTitle(getArenaId(), ChatColor.YELLOW + "" + ChatColor.BOLD + "HUNGER GAMES");

					sw.setLine(getArenaId(), 14, "" + ChatColor.GRAY + Main.date + " SOLO");
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
						if (getGameTimer() >= 780) {
							int i = getGameTimer() / 60 - 13;
							if (getGameTimer() % 60 < 10) {
								sw.setLine(getArenaId(), 9, ChatColor.WHITE + "Next Refill: ",
										ChatColor.GREEN + "" + i + ":0" + getGameTimer() % 60);
							} else {
								sw.setLine(getArenaId(), 9, ChatColor.WHITE + "Next Refill: ",
										ChatColor.GREEN + "" + i + ":" + getGameTimer() % 60);
							}
						} else if (getGameTimer() >= 480) {
							int i = getGameTimer() / 60 - 8;
							if (getGameTimer() % 60 < 10) {
								sw.setLine(getArenaId(), 9, ChatColor.WHITE + "Next Refill: ",
										ChatColor.GREEN + "" + i + ":0" + getGameTimer() % 60);
							} else {
								sw.setLine(getArenaId(), 9, ChatColor.WHITE + "Next Refill: ",
										ChatColor.GREEN + "" + i + ":" + getGameTimer() % 60);
							}

						} else if (getGameTimer() >= 300) {
							int i = getGameTimer() / 60 - 5;
							if (getGameTimer() % 60 < 10) {
								sw.setLine(getArenaId(), 9, ChatColor.WHITE + "Death Match: ",
										ChatColor.GREEN + "" + i + ":0" + getGameTimer() % 60);
							} else {
								sw.setLine(getArenaId(), 9, ChatColor.WHITE + "Death Match: ",
										ChatColor.GREEN + "" + i + ":" + getGameTimer() % 60);
							}

						} else if (getGameTimer() >= 180) {
							int i = getGameTimer() / 60 - 3;
							if (getGameTimer() % 60 < 10) {
								sw.setLine(getArenaId(), 9, ChatColor.RED + "Minions: ",
										color + "" + i + ":0" + getGameTimer() % 60);
							} else {
								sw.setLine(getArenaId(), 9, ChatColor.RED + "Minions: ",
										color + "" + i + ":" + getGameTimer() % 60);
							}

						} else if (getGameTimer() >= 120) {
							int i = getGameTimer() / 60 - 2;
							if (getGameTimer() % 60 < 10) {
								sw.setLine(getArenaId(), 9, ChatColor.DARK_RED + "Hellburn Spawn: ",
										color + " " + i + ":0" + getGameTimer() % 60);
							} else {
								sw.setLine(getArenaId(), 9, ChatColor.DARK_RED + "Hellburn Spawn: ",
										color + " " + i + ":" + getGameTimer() % 60);
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
					sw.setLine(getArenaId(), 2, ChatColor.DARK_GREEN + "Mode: ", ChatColor.GREEN + "No Kits");

					sw.setLine(getArenaId(), 1, ChatColor.DARK_PURPLE + "");
					sw.setLine(getArenaId(), 0, ChatColor.RED + " crimson-centr", ChatColor.RED + "al.com");
				}
			}
		}
	}

	public void sendGameStartInfoMessage() {

		ArenaUtil.sendCenteredWorldMessage(this,
				"" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.RED + ChatColor.BOLD + "HUNGER GAMES");
		ArenaUtil.sendCenteredWorldMessage(this, "");
		ArenaUtil.sendCenteredWorldMessage(this,
				"" + ChatColor.GREEN + ChatColor.BOLD + "24 players have been placed in an");
		ArenaUtil.sendCenteredWorldMessage(this,
				"" + ChatColor.GREEN + ChatColor.BOLD + "arena to fight to the death.");
		ArenaUtil.sendCenteredWorldMessage(this,
				"" + ChatColor.GREEN + ChatColor.BOLD + "Battle 23 of these players to be");
		ArenaUtil.sendCenteredWorldMessage(this, "" + ChatColor.GREEN + ChatColor.BOLD + "the victor of the games!");

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
						"" + ChatColor.GOLD + "+" + coins + " Unusable Hunger Games Coins");
				ArenaUtil.sendCenteredPlayerMessage(player, "" + ChatColor.AQUA + "+" + xp + " Unusable Network XP");
				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.GREEN + "+" + crates + " Unusable Loot Crates");

				ArenaUtil.sendCenteredPlayerMessage(player,
						"" + ChatColor.WHITE + ChatColor.STRIKETHROUGH + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");

			}
		}

		getJsonMessage().send((Player[]) getWorld().getPlayers().toArray());

	}

	public void newHellburnTarget() {
		if (hellburn != null) {
			Player p = OtherUtil.nearestPlayer(getAlivePlayers(), hellburn.getLocation());

			if (p != previous_hellburn_target) {
				previous_hellburn_target = p;
				p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Hell Burn is after you! Good luck my friend...");
				p.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2, 2);

				ArenaUtil.sendTitle(p, ChatColor.RED + "" + ChatColor.BOLD + "Hell Burn is after you!", 0, 2, 0);
				ArenaUtil.sendSubTitle(p, ChatColor.YELLOW + "Good luck my friend...", 0, 2, 0);

				for (Player p1 : getWorld().getPlayers()) {

					if (p1 != p) {

						p1.sendMessage(ChatColor.RED + "Hellburn The Demon" + ChatColor.YELLOW + " has targeted "
								+ PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName()
								+ ChatColor.YELLOW + "!");
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_XYLOPHONE, 2, 2);
					}

				}

			}

			hellburn.setTarget(p);

		}
	}

	public HashMap<Player, Integer> prevs_minion_targets = new HashMap<Player, Integer>();

	public void newHellburnMinionsTargets() {
		HashMap<Player, Integer> minion_targets = new HashMap<Player, Integer>();
		for (WitherSkeleton s : minions) {
			Player p = OtherUtil.nearestPlayer(getAlivePlayers(), s.getLocation());
			s.setTarget(p);

			if (minion_targets.get(p) != null) {
				int i = minion_targets.get(p);
				minion_targets.put(p, i + 1);
			} else {
				minion_targets.put(p, 1);

			}

		}

		for (Player p : getAlivePlayers()) {
			if (getGameTimer() % 60 == 15) {
				if (minion_targets.get(p) == null) {

					minion_targets.put(p, 0);
				}
			}
			if (hellburn != null && hellburn.getTarget() != null) {
				JSONMessage.actionbar(ChatColor.DARK_RED + "Hellburn's" + ChatColor.YELLOW + " current target - "
						+ PlayerManager.getServerPlayer((Player) hellburn.getTarget()).getRank().getRankColor()
						+ hellburn.getTarget().getName() + ChatColor.AQUA + " | " + ChatColor.YELLOW
						+ "Minions after you - " + "" + minion_targets.get(p), p);
			} else {

				JSONMessage.actionbar(ChatColor.YELLOW + "You have " + ChatColor.RED + minion_targets.get(p)
						+ ChatColor.YELLOW + " minions after you", p);

			}

			if (getGameTimer() % 60 == 15) {

				if (minion_targets.get(p) != prevs_minion_targets.get(p)) {
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASEDRUM, 2, 2);
					p.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.RED + minion_targets.get(p)
							+ ChatColor.YELLOW + " minions after you");
				}

			}

			prevs_minion_targets.put(p, minion_targets.get(p));

		}

	}

	public void lowTierRefill(ArrayList<Location> chests, int slots_to_fill) {

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
		refill.addItem(new ItemStack(Material.ARROW, 6), 370, 395);
		refill.addItem(new ItemStack(Material.GOLD_INGOT, 1), 396, 412);
		refill.addItem(new ItemStack(Material.FISHING_ROD, 1), 413, 450);
		refill.addItem(new ItemStack(Material.SNOW_BALL, 16), 451, 475);
		refill.addItem(new ItemStack(Material.EGG, 7), 476, 500);
		refill.addItem(new ItemStack(Material.COOKED_BEEF, 4), 501, 530);

		refill.addItem(new ItemStack(Material.APPLE, 2), 531, 549);
		refill.addItem(new ItemStack(Material.BREAD, 4), 550, 570);
		refill.addItem(new ItemStack(Material.COOKED_BEEF, 3), 571, 582);
		refill.addItem(new ItemStack(Material.COOKED_CHICKEN, 1), 583, 595);
		refill.addItem(new ItemStack(Material.BAKED_POTATO, 3), 596, 615);

		refill.addItem(new ItemStack(Material.WOOD_AXE, 1), 616, 640);
		refill.addItem(new ItemStack(Material.WOOD_PICKAXE, 1), 641, 665);
		refill.addItem(new ItemStack(Material.WOOD_SPADE, 1), 666, 675);
		refill.addItem(new ItemStack(Material.STONE_AXE, 1), 676, 688);
		refill.addItem(new ItemStack(Material.STONE_PICKAXE, 1), 689, 700);
		refill.addItem(new ItemStack(Material.IRON_PICKAXE, 1), 701, 715);
		refill.addItem(new ItemStack(Material.IRON_PICKAXE, 1), 716, 750);
		refill.refill();
	}

	public void midTierRefill(ArrayList<Location> chests, int slots_to_fill) {

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

		refill.addItem(new ItemStack(Material.GOLDEN_APPLE), 1, 30);
		refill.addItem(new ItemStack(Material.FISHING_ROD, 1), 31, 69);

		refill.addItem(regen_potion, 70, 79);
		refill.addItem(speed_potion, 80, 92);
		refill.addItem(fire_potion, 93, 113);

		refill.addItem(new ItemStack(Material.IRON_SWORD, 1), 114, 134);
		refill.addItem(new ItemStack(Material.SNOW_BALL, 48), 135, 170);
		refill.addItem(new ItemStack(Material.ARROW, 8), 171, 204);
		refill.addItem(new ItemStack(Material.IRON_INGOT, 1), 205, 215);
		refill.addItem(new ItemStack(Material.IRON_INGOT, 2), 216, 235);
		refill.refill();
	}

	public void highTierRefill(ArrayList<Location> chests, int slots_to_fill) {

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

		refill.addItem(new ItemStack(Material.EXP_BOTTLE, 12), 386, 419);
		refill.addItem(new ItemStack(Material.DIAMOND, 1), 420, 475);

		refill.addItem(new ItemStack(Material.GOLDEN_APPLE, 3), 476, 520);
		refill.addItem(new ItemStack(Material.COOKED_BEEF, 16), 521, 542);

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
		return new HungerGamesNoKitsSolo("", getGamemode(), getMaxPlayers(), getMinPlayers());

	}
}
