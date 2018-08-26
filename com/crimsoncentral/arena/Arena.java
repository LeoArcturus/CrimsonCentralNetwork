package com.crimsoncentral.arena;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.util.Vector;

import com.crimsoncentral.Main;
import com.crimsoncentral.arena.Team.PlayerStatus;
import com.crimsoncentral.arena.Team.TeamStatus;
import com.crimsoncentral.arena.util.PlayerVisiblity;
import com.crimsoncentral.arena.util.PlayerVisiblity.Visiblity;
import com.crimsoncentral.arena.util.kits.KitInventory;
import com.crimsoncentral.games.GameStats;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.util.JSONMessage;
import com.crimsoncentral.util.ScoreboardWrapper;
import com.crimsoncentral.util.item.ActionItem;
import com.crimsoncentral.util.item.ActionItemManager;
import com.crimsoncentral.util.item.ItemUtil;
import com.crimsoncentral.util.other.ChatUtil;
import com.crimsoncentral.util.other.OtherUtil;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;

public abstract class Arena implements Cloneable {

	private ArrayList<Conditions> conditions = new ArrayList<Conditions>();

	private HashMap<Player, GameStats> stats = new HashMap<Player, GameStats>();

	private HashMap<Integer, Team> teams = new HashMap<Integer, Team>();

	public HashMap<Block, Player> place_blocks = new HashMap<Block, Player>();

	private Double game_mode;

	private ArenaStage stage;

	private String arena_name;

	private String map_name;

	private World world;

	private Location arena_spawn;

	private Team winning_team;

	private boolean accepting_new_players = true;

	private boolean allowForNewCopies = true;

	private boolean weather;
	private int world_time;

	private int max_players;
	private int min_players;

	private int gameTimer;

	private int maxGameTime;

	private int finalKillCoins;
	private int killCoins;
	private int teamBeaconDestroyCoins;
	private int assistCoins;

	private int finalKillXp;
	private int killXp;
	private int teamBeaconDestroyXp;
	private int assistXp;

	private int winCoins;
	private int WinXp;

	private Integer arenaId;

	private String mode;
	private String playerAmmountType;

	private boolean unStable = false;

	private int radius = 200;

	private boolean ballistic;

	public abstract void setup();

	public abstract void run();

	public abstract void join(Player player);

	public abstract void quit(Player player);

	public abstract void playerDeath(Player player, Player killer, DamageCause cause);

	private KitInventory kits;

	private JSONMessage jsonMessage;

	public static enum Conditions {
		CAN_BLOCK_INTERACT, CAN_TAKE_DAMAGE, CAN_PVP, CAN_MOVE_ITEMS, SHOW_PLAYER_HEALTH, ELIMINATE_ON_LOG_OUT
	}

	public static enum ArenaStage {
		PRE_COUNT_DOWN, COUNT_DOWN, GAME_TIME, AFTER_GAME,
	}

	public static enum ArenaType {

		LOBBY, MINI_GAME, REALM, OTHER
	}

	public Arena(String arena_name, Double game_mode, int max_players, int min_players) {

		this.arena_name = arena_name;

		this.game_mode = game_mode;

		this.max_players = max_players;
		this.min_players = min_players;

	}

	public void informBungee() {

	}

	public void rerouteAllPlayers() {

		for (Player p : world.getPlayers()) {
			resetPlayer(p);
			p.performCommand("lobby");

		}

	}

	public void makeArenaWorld() {

		world.setDifficulty(Difficulty.NORMAL);
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setGameRuleValue("doWeatherCycle", "false");
		world.setGameRuleValue("announceAdvancements", "false");
		world.setGameRuleValue("doMopSpawning", "false");
		world.setGameRuleValue("maxEntityCramming", "-1");
		world.setGameRuleValue("showDeathMessages", "false");
		world.setGameRuleValue("randomTickSpeed", "3");
		world.setGameRuleValue("randomTickSpeed", "3");
		world.setTime(6000);
		world.setThundering(false);

	}

	public void setArenaName(String name) {

		this.arena_name = name;
	}

	public String getArenaName() {

		return this.arena_name;
	}

	public void setMapName(String name) {

		this.map_name = name;
	}

	public String getMapName() {

		return this.map_name;
	}

	public void setWorld(World world) {

		this.world = world;
	}

	public net.minecraft.server.v1_12_R1.World getMcWorld() {

		return (net.minecraft.server.v1_12_R1.World) this.world;
	}

	public World getWorld() {

		return this.world;
	}

	public void setGamemode(Double d) {

		this.game_mode = d;
	}

	public Double getGamemode() {

		return this.game_mode;
	}

	public void setArenaSpawn(Location loc) {
		this.arena_spawn = loc;

	}

	public Location getArenaSpawn() {

		return this.arena_spawn;
	}

	public void allowNewPlayers(boolean allow_new_players_to_join) {

		this.accepting_new_players = allow_new_players_to_join;
	}

	public boolean isAccpetingNewPlayers() {

		return this.accepting_new_players;
	}

	public void allowWeather(boolean allowing_weather) {

		this.weather = allowing_weather;
	}

	public boolean canWeather() {

		return this.weather;
	}

	public void setMinPlayers(int min) {

		this.min_players = min;
	}

	public int getMinPlayers() {

		return this.min_players;
	}

	public void setMaxPlayers(int max) {

		this.max_players = max;
	}

	public int getMaxPlayers() {

		return this.max_players;
	}

	public void setWinningTeam(Team t) {

		this.winning_team = t;
	}

	public Team getWinningTeam() {

		return this.winning_team;
	}

	public ArrayList<Player> getPlayers() {

		ArrayList<Player> players = new ArrayList<Player>();

		for (Team t : getTeams()) {

			for (Player p : t.getPlayers()) {

				players.add(p);
			}

		}
		return players;

	}

	public ArrayList<Player> getAlivePlayers() {

		ArrayList<Player> players = new ArrayList<Player>();

		Iterator<Entry<Integer, Team>> it = this.teams.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Team> pair = it.next();

			Iterator<Entry<Player, PlayerStatus>> it1 = pair.getValue().players.entrySet().iterator();
			while (it1.hasNext()) {
				Entry<Player, PlayerStatus> pair1 = it1.next();

				if (pair1.getValue() == Team.PlayerStatus.ALIVE) {
					players.add(pair1.getKey());
				}
			}

		}

		return players;

	}

	public ArrayList<Player> getDeadPlayers() {

		ArrayList<Player> players = new ArrayList<Player>();

		Iterator<Entry<Integer, Team>> it = this.teams.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Team> pair = it.next();

			Iterator<Entry<Player, PlayerStatus>> it1 = pair.getValue().players.entrySet().iterator();
			while (it1.hasNext()) {
				Entry<Player, PlayerStatus> pair1 = it1.next();

				if (pair1.getValue() == Team.PlayerStatus.DEAD) {
					players.add(pair1.getKey());
				}
			}

		}

		return players;

	}

	public ArrayList<Team> getTeams() {

		ArrayList<Team> this_teams = new ArrayList<Team>();

		Iterator<Entry<Integer, Team>> it = this.teams.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Team> pair = it.next();

			this_teams.add(pair.getValue());
		}

		return this_teams;

	}

	public void createTeam(String name, int max_players, String prefix, ChatColor team_color, Location team_spawn,
			boolean can_respawn) {

		Team team = new Team(this, name, max_players, prefix, team_color, team_spawn, can_respawn, false);

		this.teams.put(getTeams().size() + 1, team);
	}

	public void checkForLastTeam() {

		ArrayList<Team> ts = new ArrayList<Team>();

		Iterator<Entry<Integer, Team>> it1 = this.teams.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<Integer, Team> pair = it1.next();

			pair.getValue().refreshTeamStatus();

			if (pair.getValue().team_status == TeamStatus.ALIVE) {

				ts.add(pair.getValue());

			}

		}

		if (ts.size() == 1) {

			for (Team t : ts) {

				winning_team = t;

			}
		}

	}

	public Team getPlayerTeam(Player player) {

		Team team = null;
		boolean b = false;

		Iterator<Entry<Integer, Team>> it1 = this.teams.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<Integer, Team> pair = it1.next();

			Iterator<Entry<Player, Team.PlayerStatus>> it = pair.getValue().players.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Player, Team.PlayerStatus> pair1 = it.next();
				if (pair1.getKey() == player) {

					team = pair.getValue();
					b = true;
					break;

				}

			}

			if (b == true) {

				break;
			}

		}

		return team;
	}

	public GameStats getStats(Player player) {
		GameStats gs = null;
		Iterator<Entry<Player, GameStats>> it1 = stats.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<Player, GameStats> pair = it1.next();

			if (pair.getKey() == player) {

				gs = pair.getValue();
				break;
			}

		}
		return gs;

	}

	public void createBlankStats(Player player, String game_name) {

		GameStats gs = new GameStats(player, game_name, 0, 0, 0);
		stats.put(player, gs);
	}

	public void setTeams(HashMap<Integer, Team> teams) {
		this.teams = teams;
	}

	public void addCondition(Conditions c) {
		this.conditions.add(c);

	}

	public ArrayList<Conditions> getConditions() {

		return this.conditions;
	}

	public void sendWorldMessage(String s)

	{
		if (this.getWorld().getPlayers().isEmpty() == false) {
			for (Player p : getWorld().getPlayers())

			{
				if (p.getWorld() == world) {
					p.sendMessage(s);
				}
			}
		}
	}

	public void playWorldSound(Sound sound, int i, int j) {
		if (getWorld().getPlayers().isEmpty() == false) {
			for (Player player : getPlayers()) {
				if (player.getWorld() == world) {
					player.playSound(player.getLocation(), sound, i, j);
				}
			}
		}
	}

	public void sendWorldTitle(String s, int fade_in_time, int duration, int fade_out_time)

	{
		if (getWorld().getPlayers().isEmpty() == false) {
			IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + s + "\"}");

			PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
			PacketPlayOutTitle length = new PacketPlayOutTitle(fade_in_time, duration * 20, fade_out_time);
			for (Player p : getPlayers())

			{
				if (p.getWorld() == world) {
					((CraftPlayer) p.getPlayer()).getHandle().playerConnection.sendPacket(title);
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
				}
			}
		}

	}

	public void sendWorldSubTitle(String s, int fade_in_time, int duration, int fade_out_time)

	{
		if (getWorld().getPlayers().isEmpty() == false) {
			IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + s + "\"}");

			PacketPlayOutTitle subTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
					chatSubTitle);
			PacketPlayOutTitle length = new PacketPlayOutTitle(fade_in_time, duration * 20, fade_out_time);

			for (Player p : getPlayers())

			{
				if (p.getWorld() == world) {
					((CraftPlayer) p.getPlayer()).getHandle().playerConnection.sendPacket(subTitle);
					((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
				}
			}
		}
	}

	public void sendTitle(Player p, String s, int fade_in_time, int duration, int fade_out_time)

	{
		if (p.getWorld() == world) {
			IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + s + "\"}");

			PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
			PacketPlayOutTitle length = new PacketPlayOutTitle(fade_in_time, duration * 20, fade_out_time);
			((CraftPlayer) p.getPlayer()).getHandle().playerConnection.sendPacket(title);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
		}
	}

	public void sendSubTitle(Player p, String s, int fade_in_time, int duration, int fade_out_time)

	{
		if (p.getWorld() == world) {
			IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + s + "\"}");

			PacketPlayOutTitle subTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
					chatSubTitle);
			PacketPlayOutTitle length = new PacketPlayOutTitle(fade_in_time, duration * 20, fade_out_time);
			((CraftPlayer) p.getPlayer()).getHandle().playerConnection.sendPacket(subTitle);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
		}
	}

	public int getWorld_time() {
		return world_time;
	}

	public void setWorld_time(int world_time) {
		this.world_time = world_time;
	}

	public Double getGame_mode() {
		return game_mode;
	}

	public void setGame_mode(Double game_mode) {
		this.game_mode = game_mode;
	}

	public int getFinalKillCoins() {
		return finalKillCoins;
	}

	public void setFinalKillCoins(int finalKillCoins) {
		this.finalKillCoins = finalKillCoins;
	}

	public int getKillCoins() {
		return killCoins;
	}

	public void setKillCoins(int killCoins) {
		this.killCoins = killCoins;
	}

	public int getTeamBeaconDestroyCoins() {
		return teamBeaconDestroyCoins;
	}

	public void setTeamBeaconDestroyCoins(int teamBeaconDestroyCoins) {
		this.teamBeaconDestroyCoins = teamBeaconDestroyCoins;
	}

	public int getAssistCoins() {
		return assistCoins;
	}

	public void setAssistCoins(int assistCoins) {
		this.assistCoins = assistCoins;
	}

	public int getFinalKillXp() {
		return finalKillXp;
	}

	public void setFinalKillXp(int finalKillXp) {
		this.finalKillXp = finalKillXp;
	}

	public int getKillXp() {
		return killXp;
	}

	public void setKillXp(int killXp) {
		this.killXp = killXp;
	}

	public int getTeamBeaconDestroyXp() {
		return teamBeaconDestroyXp;
	}

	public void setTeamBeaconDestroyXp(int teamBeaconDestroyXp) {
		this.teamBeaconDestroyXp = teamBeaconDestroyXp;
	}

	public int getAssistXp() {
		return assistXp;
	}

	public void setAssistXp(int assistXp) {
		this.assistXp = assistXp;
	}

	public int getWinCoins() {
		return winCoins;
	}

	public void setWinCoins(int winCoins) {
		this.winCoins = winCoins;
	}

	public void setWinXp(int winXp) {
		WinXp = winXp;
	}

	public int getWinXp() {
		return WinXp;
	}

	public Integer getArenaId() {
		return arenaId;
	}

	public void setArenaId(Integer arenaId) {
		this.arenaId = arenaId;
	}

	public void delete() {
		rerouteAllPlayers();
		ArenaManager.local_arenas.remove(this, getGamemode());
		ArenaManager.removeArena(this);
		for (File f : world.getWorldFolder().listFiles()) {

			Bukkit.unloadWorld(world, true);

			Bukkit.getWorlds().remove(world);

			try {
				org.apache.commons.io.FileUtils.cleanDirectory(f);
				f.delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		}

	}

	public void deleteBlockArea(Location pos1, Location pos2) {

		for (int x = pos1.getBlockX(); x <= pos2.getBlockX(); x++) {
			for (int y = pos1.getBlockX(); y <= pos2.getBlockX(); y++) {
				for (int z = pos1.getBlockX(); z <= pos2.getBlockX(); z++) {

					Location l = new Location(getWorld(), x, y, z);

					l.getBlock().setType(Material.AIR);

				}
			}
		}

	}

	public void deleteBlockRadius(Location center, int radius) {

		for (int x = center.getBlockX() - radius; x <= center.getBlockX() + radius; x++) {
			for (int y = center.getBlockY() - radius; y <= center.getBlockY() + radius; y++) {
				for (int z = center.getBlockZ() - radius; z <= center.getBlockZ() + radius; z++) {

					Location l = new Location(getWorld(), x, y, z);

					l.getBlock().setType(Material.AIR);

				}
			}
		}

	}

	public void deleteBlockRadius(Location center, int xr, int ry, int zy) {

		for (int x = center.getBlockX() - xr; x <= center.getBlockX() + xr; x++) {
			for (int y = center.getBlockY() - ry; y <= center.getBlockY() + ry; y++) {
				for (int z = center.getBlockZ() - zy; z <= center.getBlockZ() + zy; z++) {

					Location l = new Location(getWorld(), x, y, z);

					l.getBlock().setType(Material.AIR);

				}
			}
		}

	}

	public void replaceBlockArea(Material material, Location pos1, Location pos2) {

		for (int x = pos1.getBlockX(); x <= pos2.getBlockX(); x++) {
			for (int y = pos1.getBlockX(); y <= pos2.getBlockX(); y++) {
				for (int z = pos1.getBlockX(); z <= pos2.getBlockX(); z++) {

					Location l = new Location(getWorld(), x, y, z);

					if (l.getBlock().getType() != material) {
						l.getBlock().setType(material);
					}

				}
			}
		}

	}

	public void replaceBlockRadius(Location center, Material material, int radius) {

		for (int x = center.getBlockX(); x <= radius; x++) {
			for (int y = center.getBlockX(); y <= radius; y++) {
				for (int z = center.getBlockX(); z <= radius; z++) {

					Location l = new Location(getWorld(), x, y, z);

					if (l.getBlock().getType() != material) {
						l.getBlock().setType(material);
					}

				}
			}
		}

	}

	@SuppressWarnings("deprecation")
	public void replaceBlockRadius(Location center, Material material, byte data, int xr, int ry, int zy) {

		for (int x = center.getBlockX(); x <= xr + center.getBlockX(); x++) {
			for (int y = center.getBlockY(); y <= ry + center.getBlockY(); y++) {
				for (int z = center.getBlockZ(); z <= zy + center.getBlockZ(); z++) {

					Location l = new Location(getWorld(), x, y, z);
					if (l.getBlock().getType() != material) {
						l.getBlock().setType(material);
					}

					l.getBlock().setData(data);

				}
			}
		}

	}

	public ArrayList<Block> getBlocksInRadius(Location center, int xr, int ry, int zy) {

		ArrayList<Block> blocks = new ArrayList<Block>();

		for (int x = center.getBlockX(); x <= xr + center.getBlockX(); x++) {
			for (int y = center.getBlockY(); y <= ry + center.getBlockY(); y++) {
				for (int z = center.getBlockZ(); z <= zy + center.getBlockZ(); z++) {

					Location l = new Location(getWorld(), x, y, z);
					blocks.add(l.getBlock());

				}
			}
		}
		return blocks;
	}

	public void deletePreGameLobby() {

		Location l = getArenaSpawn();

		for (int x = l.getBlockX() - 15; x <= l.getBlockX() + 15; x++) {
			for (int y = l.getBlockY() - 5; y <= l.getBlockY() + 5; y++) {
				for (int z = l.getBlockZ() - 15; z <= l.getBlockZ() + 15; z++) {

					Location loc = new Location(getWorld(), x, y, z);
					if (!loc.getBlock().getType().equals(Material.AIR)) {
						loc.getBlock().setType(Material.AIR);
					}
				}
			}

		}

	}

	public ArenaStage getStage() {
		return stage;
	}

	public void setStage(ArenaStage stage) {
		this.stage = stage;
	}

	public boolean doesAllowForNewCopies() {
		return allowForNewCopies;
	}

	public void setAllowForNewCopies(boolean allowForNewCopies) {
		this.allowForNewCopies = allowForNewCopies;
	}

	int i = 0;
	int task;

	public void preGame(String name, String player_type, String mode_type, String game_tip) {
		if (getStage() != ArenaStage.GAME_TIME && getStage() != ArenaStage.AFTER_GAME) {

			String map_name = getMapName();
			int max_players = getMaxPlayers();
			int min_players = getMinPlayers();
			int current_players = getPlayers().size();

			ChatColor color = ChatColor.AQUA;
			if (mode_type.equalsIgnoreCase("ballistic")) {

				color = ChatColor.RED;

			}

			for (Player p : getWorld().getPlayers()) {

				ScoreboardWrapper sw = ArenaManager.scoreboards.get(p);

				p.setScoreboard(sw.getScoreboard());
				sw.changePage(arenaId);
				sw.setTitle(arenaId, ChatColor.YELLOW + "" + ChatColor.BOLD + name.toUpperCase());

				if (getStage() == ArenaStage.COUNT_DOWN || getStage() == ArenaStage.PRE_COUNT_DOWN) {
					sw.setLine(arenaId, 12,
							"" + ChatColor.GRAY + Main.date + ChatColor.RESET + ChatColor.GRAY + " " + player_type);

					sw.setLine(arenaId, 11, " ");
					sw.setLine(arenaId, 10, ChatColor.WHITE + "Server: " + ChatColor.GOLD + Main.server_name);
					sw.setLine(arenaId, 9, ChatColor.WHITE + "Map: " + ChatColor.GREEN + map_name);
					sw.setLine(arenaId, 8, ChatColor.WHITE + "Mode: " + color + ChatUtil.toTitleCase(mode_type));
					sw.setLine(arenaId, 7, "");
					sw.setLine(arenaId, 6,
							ChatColor.WHITE + "Players: " + ChatColor.YELLOW + current_players + "/" + max_players);
					sw.setLine(arenaId, 5, ChatColor.GRAY + "(" + min_players + " required to start)");
					sw.setLine(arenaId, 4, "");
					sw.setLine(arenaId, 3, ChatColor.WHITE + "Time Until Start: ");

					if (getStage() == ArenaStage.PRE_COUNT_DOWN) {
						sw.setLine(arenaId, 2, ChatColor.DARK_RED + "waiting for players...");
					} else if (getStage() == ArenaStage.COUNT_DOWN) {

						sw.setLine(arenaId, 2, ChatColor.GOLD + "" + getGameTimer() + " seconds");
					}
					sw.setLine(arenaId, 1, " ");

				}
				sw.setLine(arenaId, 0, ChatColor.RED + "crimson-central.com");
			}

			String game_name = ArenaRegister
					.getGameProfile(Integer.valueOf(new DecimalFormat("0").format(getGamemode())))
					.getMode(getGamemode()).getActionIitem().getName();

			int spaceIndex = game_name.indexOf(" ");
			if (spaceIndex != -1) {
				game_name = game_name.substring(0, spaceIndex);
			}

			if (getStage() == ArenaStage.PRE_COUNT_DOWN
					|| getStage() != ArenaStage.COUNT_DOWN && getStage() != ArenaStage.GAME_TIME) {

				if (getGameTimer() == 0 && getPlayers().size() >= getMinPlayers()) {
					setGameTimer(20);
					setStage(ArenaStage.COUNT_DOWN);
					sendWorldMessage(ChatColor.YELLOW + "Game Starting in " + ChatColor.GOLD + "20 " + ChatColor.YELLOW
							+ "Seconds");
					playWorldSound(Sound.BLOCK_LEVER_CLICK, 1, 1);
					sendWorldTitle(ChatColor.YELLOW + "Game Starting in", 7, 3, 7);
					sendWorldSubTitle("" + ChatColor.GOLD + getGameTimer() + " seconds...", 7, 3, 7);
				} else if (getGameTimer() == 0 && getPlayers().size() <= getMinPlayers()) {
					setGameTimer(15);

				} else if (getPlayers().size() == getMaxPlayers()) {
					setGameTimer(10);
					setStage(ArenaStage.COUNT_DOWN);
					sendWorldMessage(ChatColor.YELLOW + "Game Starting in " + ChatColor.DARK_PURPLE + getGameTimer()
							+ ChatColor.YELLOW + " Seconds");
					playWorldSound(Sound.BLOCK_LEVER_CLICK, 1, 1);
					sendWorldTitle(ChatColor.YELLOW + "Game Starting in", 0, 3, 0);
					sendWorldSubTitle("" + ChatColor.DARK_PURPLE + getGameTimer() + " seconds...", 7, 3, 7);
				}
			} else if (getStage() == ArenaStage.COUNT_DOWN) {

				if (getPlayers().size() < getMinPlayers()) {
					sendWorldMessage(ChatColor.RED + "Start Cancelled! Not enough players to start!");
					playWorldSound(Sound.BLOCK_PISTON_EXTEND, 1, 1);

					sendWorldTitle(ChatColor.RED + "Start Cancelled!", 0, 3, 0);
					sendWorldSubTitle(ChatColor.WHITE + "Not enough players to start...", 7, 3, 7);
					setStage(ArenaStage.PRE_COUNT_DOWN);
				} else if (getGameTimer() == 10) {

					for (Team t : getTeams()) {

						for (Player p : t.getPlayers()) {

							if (p.getWorld() == getWorld()) {
								p.teleport(t.team_spawn);
							}
						}

					}

					allowNewPlayers(false);
					deletePreGameLobby();
					sendWorldMessage(ChatColor.YELLOW + "" + "Game Starting in " + ChatColor.DARK_GREEN + "10 "
							+ ChatColor.YELLOW + "Seconds");
					playWorldSound(Sound.BLOCK_LEVER_CLICK, 1, 1);
					sendWorldTitle(ChatColor.YELLOW + "Game Tip:", 0, 3, 0);
					sendWorldSubTitle("" + ChatColor.DARK_GREEN + game_tip, 7, 3, 7);
				} else if (getGameTimer() == 4 || getGameTimer() == 5) {

					sendWorldMessage(ChatColor.YELLOW + "Game Starting in " + ChatColor.GREEN + getGameTimer()
							+ ChatColor.YELLOW + " Seconds");
					playWorldSound(Sound.BLOCK_LEVER_CLICK, 1, 1);
					sendWorldTitle(ChatColor.YELLOW + "Game Starting in", 0, 1, 0);
					sendWorldSubTitle("" + ChatColor.GREEN + getGameTimer() + " seconds", 0, 1, 0);
				} else if (getGameTimer() == 3 || getGameTimer() == 2) {

					sendWorldMessage(ChatColor.YELLOW + "Game Starting in " + ChatColor.RED + getGameTimer()
							+ ChatColor.YELLOW + " Seconds");
					playWorldSound(Sound.BLOCK_LEVER_CLICK, 1, 1);
					sendWorldTitle(ChatColor.YELLOW + "Game Starting in", 0, 1, 0);
					sendWorldSubTitle("" + ChatColor.RED + getGameTimer() + " seconds", 0, 1, 0);
				} else if (getGameTimer() == 1) {
					sendWorldMessage(ChatColor.YELLOW + "Game Starting in " + ChatColor.GOLD + getGameTimer()
							+ ChatColor.YELLOW + " Second");

					sendWorldTitle(ChatColor.YELLOW + "Game Starting in", 0, 1, 0);
					sendWorldSubTitle("" + ChatColor.GOLD + getGameTimer() + " second", 0, 1, 0);
					playWorldSound(Sound.BLOCK_NOTE_HARP, 1, 1);

				} else if (getGameTimer() == 0) {

					for (Player p : getWorld().getPlayers()) {
						p.setPlayerTime(17000, false);
					}

					int radius = 35;
					int precision = 16;
					i = 0;

					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {

						public void run() {

							task = Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin,
									new Runnable() {
										public void run() {
											if (i < precision) {
												++i;
												double p1 = (i * Math.PI) / (precision / 2);
												double p2 = (((i == 0) ? precision : i - 1) * Math.PI)
														/ (precision / 2);

												double x1 = Math.cos(p1) * radius;
												double x2 = Math.cos(p2) * radius;
												double z1 = Math.sin(p1) * radius;
												double z2 = Math.sin(p2) * radius;

												Vector vec = new Vector(x2 - x1, 0, z2 - z1);

												getWorld().strikeLightning(
														new Location(getWorld(), vec.getX(), 0, vec.getY()));

											} else {
												Bukkit.getScheduler().cancelTask(task);
											}
										}
									}, 0, 5);

							playWorldSound(Sound.ENTITY_LIGHTNING_IMPACT, 2, 2);

							Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
								public void run() {
									for (Player p : getWorld().getPlayers()) {
										p.setPlayerTime(getWorld().getTime(), true);
									}
								}

							}, 90L);

						}

					}, 40L);

					OtherUtil.makeCircle(getWorld().spawnEntity(getArenaSpawn(), EntityType.PIG), 15, 2);
					sendWorldTitle(ChatColor.YELLOW + "" + ChatColor.BOLD + ChatUtil.toTitleCase(game_name), 0, 3, 1);
					sendWorldSubTitle("" + ChatColor.GREEN + ChatColor.BOLD + "on " + ChatColor.DARK_RED
							+ ChatColor.BOLD + "Crimson " + ChatColor.GOLD + ChatColor.BOLD + "Central", 0, 3, 1);

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

						}
					}

					for (Player p : getPlayers()) {

						createBlankStats(p,
								ArenaRegister
										.getGameProfile(Integer.valueOf(new DecimalFormat("0").format(getGamemode())))
										.getMode(getGamemode()).getName().replace(" Original-Copy", ""));

					}

					if (kits != null) {
						kits.givePlayerKits(this, getAlivePlayers());

					}

				} else if (getGameTimer() < 0) {
					setGameTimer(getMaxGameTime());
					setStage(ArenaStage.GAME_TIME);

				}

			}

		}

	}

	boolean spectator_setup = false;

	ItemStack tracker_compass;
	ItemStack stats_book;
	ItemStack settings_comparator;
	ItemStack play_again_paper;
	ItemStack lobby_bed;

	public void setSpectating(Player player, boolean compass, boolean stats, boolean settings, boolean play_again,
			boolean lobby) {

		if (spectator_setup == false) {

			String game_name = ArenaRegister
					.getGameProfile(Integer.valueOf(new DecimalFormat("0").format(getGamemode())))
					.getMode(getGamemode()).getName().replace(" Original-Copy", "");

			int spaceIndex = game_name.indexOf("-");
			if (spaceIndex != -1) {
				game_name = game_name.substring(0, spaceIndex);
			}

			ActionItem ai = new ActionItem(game_name + arena_name.replace("Arena-", "-") + arenaId + "play again paper",
					ItemUtil.newItem(Material.PAPER, 1, (short) 0, ChatColor.AQUA + "Play Again?", "")) {

				@Override
				public void preform(Player player) {

					ArenaRegister.getGameProfile(Integer.valueOf(new DecimalFormat("0").format(getGamemode())))
							.getMode(getGamemode()).getActionIitem().preform(player);

				}
			};

			tracker_compass = ItemUtil.newItem(Material.COMPASS, 1, (short) 0, ChatColor.GREEN + "Player Tracker", "");
			stats_book = ItemUtil.newItem(Material.BOOK, 1, (short) 0, ChatColor.YELLOW + "Game Stats", "");

			settings_comparator = ItemUtil.newItem(Material.REDSTONE_COMPARATOR, 1, (short) 0,
					ChatColor.BLUE + "Settings", "");

			play_again_paper = ai.getIs();
			lobby_bed = ActionItemManager.getActionItemItemStack("return to main lobby bed");
			spectator_setup = true;
		}

		if (compass == true && tracker_compass != null) {

			player.getInventory().setItem(0, tracker_compass);
		}

		if (stats == true && stats_book != null) {

			player.getInventory().setItem(1, stats_book);
		}

		if (settings == true && settings_comparator != null) {

			player.getInventory().setItem(4, settings_comparator);
		}

		if (play_again == true && play_again_paper != null) {

			player.getInventory().setItem(7, play_again_paper);
		}

		if (lobby == true && lobby_bed != null) {

			player.getInventory().setItem(8, lobby_bed);
		}

//		getJsonMessage().send(player);
		player.teleport(getPlayerTeam(player).team_spawn);

		for (Team t : getTeams()) {

			if (t.players.containsKey(player)) {

				t.players.put(player, Team.PlayerStatus.DEAD);
			}

		}

		player.setAllowFlight(true);
		player.setFlying(true);
		PlayerVisiblity.setPlayerVisiblity(player, Visiblity.SPECTATOR);
		PlayerVisiblity.updateWorldVisiblity(player.getWorld());

	}

	public void resetPlayer(Player player) {
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().clear();
		player.setHealth(20);
		player.setSaturation(20);
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		player.setFlying(false);
		player.setAllowFlight(false);
		player.setGravity(true
				);

		PlayerVisiblity.setPlayerVisiblity(player, Visiblity.VISIBLE);
		PlayerVisiblity.updateWorldVisiblity(player.getWorld());

	}

	public void joinMessage(Player player) {

		ChatColor color = ChatColor.RED;

		double percent_full = getPlayers().size() / getMaxPlayers();

		if (percent_full >= 0.5 && percent_full <= 0.7) {

			color = ChatColor.YELLOW;
		} else if (percent_full > 0.7 && percent_full != 1) {
			color = ChatColor.GREEN;

		} else {

			color = ChatColor.GOLD;
		}

		sendWorldMessage(ChatColor.WHITE + "[" + ChatColor.GREEN + "+" + ChatColor.WHITE + "] "
				+ PlayerManager.getServerPlayer(player).getRank().getRankColor() + player.getName() + ChatColor.GREEN
				+ " has joined the game! (" + color + getPlayers().size() + color + "/" + color + getMaxPlayers()
				+ ChatColor.GREEN + ")");

	}

	public void managePlayerScoreboard(Player player) {

		player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		ScoreboardWrapper sw = new ScoreboardWrapper(player.getUniqueId());

		ArenaManager.scoreboards.put(player, sw);
	}

	int attempts = 0;
	int failed_joins = 0;

	public void joinTeam(Player player) {

		double avg = 0;
		for (Team t : getTeams()) {

			avg = avg + t.players.size();

		}

		avg = avg / getTeams().size();

		for (Team t : getTeams()) {

			if (t.players.size() <= avg || avg == 0) {
				if (t.players.size() < t.getMaxPlayers()) {
					t.players.put(player, Team.PlayerStatus.NA);
				} else {

					if (attempts < 3) {
						joinTeam(player);
						attempts = attempts + 1;
					} else {

						String game_name = ArenaRegister
								.getGameProfile(Integer.valueOf(new DecimalFormat("0").format(getGamemode())))
								.getMode(getGamemode()).getName().replace(" Original-Copy", "");

						int spaceIndex = game_name.indexOf("-");
						if (spaceIndex != -1) {
							game_name = game_name.substring(0, spaceIndex);
						}
						player.performCommand("lobby");
						player.sendMessage(ChatColor.RED + "There was an error placing you on a team when you joined "
								+ ChatColor.YELLOW + getArenaName() + ChatColor.WHITE + " (" + game_name + ")! "
								+ ChatColor.RED + "If this error presits, please contact a staff member!");
						attempts = 0;

						failed_joins = failed_joins + 1;

						if (failed_joins >= Integer.valueOf(new DecimalFormat("0").format(getMaxPlayers() * 0.25))) {

							if (getPlayers().size() >= getMinPlayers()) {

								setStage(ArenaStage.COUNT_DOWN);
								player.sendMessage(ChatColor.RED
										+ "There was an error with placing any new players in this arena! But there are enough players to start. Game Starting in "
										+ ChatColor.YELLOW + getGameTimer() + " seconds. " + ChatColor.RED
										+ "If this error presits, please contact a staff member!");

								setUnStable();
								allowNewPlayers(false);
							} else {
								sendWorldMessage(ChatColor.RED + "There was an error with placing any new players in "
										+ ChatColor.YELLOW + getArenaName() + "(your previous arena) " + ChatColor.RED
										+ " and there was not enough players to start! AS a result" + ChatColor.YELLOW
										+ getArenaName() + ChatColor.RED
										+ "If this error presits, please contact a staff member!");
								delete();

							}

						}

					}
				}
				break;
			}

		}
	}

	public KitInventory getKits() {
		return kits;
	}

	public void setKits(KitInventory kits) {
		this.kits = kits;
	}

	/**
	 * @return the gameTimer
	 */
	public int getGameTimer() {
		return gameTimer;
	}

	/**
	 * @param gameTimer
	 *            the gameTimer to set
	 */
	public void setGameTimer(int gameTimer) {
		this.gameTimer = gameTimer;
	}

	public int getMaxGameTime() {
		return maxGameTime;
	}

	public void setMaxGameTime(int maxGameTime) {
		this.maxGameTime = maxGameTime;
	}

	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * @return the playerAmmountType
	 */
	public String getPlayerAmmountType() {
		return playerAmmountType;
	}

	/**
	 * @param playerAmmountType
	 *            the playerAmmountType to set
	 */
	public void setPlayerAmmountType(String playerAmmountType) {
		this.playerAmmountType = playerAmmountType;
	}

	/**
	 * @return the unStable
	 */
	public boolean isUnStable() {
		return unStable;
	}

	/**
	 * @param unStable
	 *            the unStable to set
	 */
	public void setUnStable() {
		this.unStable = true;
	}

	public void setNotUnStable() {
		this.unStable = false;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public boolean isBallistic() {
		return ballistic;
	}

	public void setBallistic(boolean ballistic) {
		this.ballistic = ballistic;
	}

	/**
	 * @return the jsonMessage
	 */
	public JSONMessage getJsonMessage() {
		return jsonMessage;
	}

	/**
	 * @param jsonMessage
	 *            the jsonMessage to set
	 */
	public void setJsonMessage(JSONMessage jsonMessage) {
		this.jsonMessage = jsonMessage;
	}

	@Override
	public Arena clone() throws CloneNotSupportedException {

		Arena arena = (Arena) super.clone();

		return arena;
	}

}
