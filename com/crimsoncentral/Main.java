package com.crimsoncentral;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;

import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.crimsoncentral.arena.ArenaEvents;
import com.crimsoncentral.arena.ArenaManager;
import com.crimsoncentral.arena.ArenaRegister;
import com.crimsoncentral.arena.CustomPvPMechanics;
import com.crimsoncentral.arena.DamageTag;
import com.crimsoncentral.arena.util.ArenaUtil;
import com.crimsoncentral.arena.util.Hologram;
import com.crimsoncentral.arena.util.NPC;
import com.crimsoncentral.arena.util.PlayerVisiblity;
import com.crimsoncentral.arena.util.parkour_course.ParkourCourseManager;
import com.crimsoncentral.commands.ArenaCommands;
import com.crimsoncentral.commands.ArenaJoinCommands;
import com.crimsoncentral.cosmetics.LootCrateOpener;
import com.crimsoncentral.ranks.RanksManager;
import com.crimsoncentral.util.ChatManager;
import com.crimsoncentral.util.Config;
import com.crimsoncentral.util.GeneralUtilities;
import com.crimsoncentral.util.ScoreboardWrapper;
import com.crimsoncentral.util.item.ActionItemManager;
import com.crimsoncentral.util.item.AnimatedInventory;
import com.crimsoncentral.util.other.ChangingInventory;
import com.crimsoncentral.util.other.CoolDown;

import com.crimsoncentral.vespen.TPSLag;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_12_R1.PlayerConnection;

public class Main extends JavaPlugin implements Listener {

	public static Main plugin;

	public static boolean allow_for_new_arena_creation = true;
	public static String server_name = "mini-s-01a";
	public static int max_online_players = Bukkit.getMaxPlayers();
	public static boolean is_test_server = true;
	public static ScoreboardManager sm = null;
	public static Scoreboard scoreboard = null;
	public static boolean is_the_build_server = false;

	public static PluginManager pm = Bukkit.getServer().getPluginManager();
	public static ArrayList<ChangingInventory> changing_inventories = new ArrayList<ChangingInventory>();

	public static HashMap<Integer, String> versions = new HashMap<Integer, String>();

	DecimalFormat format = new DecimalFormat("0.00");

	public Main() {
		plugin = this;
	}

	public PluginDescriptionFile pdfFile = getDescription();
	Logger logger = getLogger();
	public static String date = "2018";

	public void onEnable() {
		countReload();
		sm = Bukkit.getScoreboardManager();
		scoreboard = sm.getNewScoreboard();

		for (Player p : Bukkit.getOnlinePlayers()) {

			p.getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
			p.getScoreboard().clearSlot(DisplaySlot.PLAYER_LIST);
			p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);

			ScoreboardWrapper sw = new ScoreboardWrapper(p.getUniqueId());

			ArenaManager.scoreboards.put(p, sw);
			PlayerVisiblity.setPlayerVisiblity(p, PlayerVisiblity.Visiblity.VISIBLE);
		}

		PlayerVisiblity.updateServerVisiblity();

		ArenaUtil.deleteArenaFolders();
		/**
		 * 
		 * All Commands for this Spigot Server Must be registered below!
		 * 
		 */

		pm.registerEvents(this, this);

		pm.registerEvents(new ArenaEvents(), this);
		pm.registerEvents(new ChatManager(), this);
		pm.registerEvents(new PlayerVisiblity(), this);
		pm.registerEvents(new LootCrateOpener(null, 0, 0), this);
		pm.registerEvents(new CustomPvPMechanics(), this);
		pm.registerEvents(new ParkourCourseManager(), this);
		pm.registerEvents(new NPC() {

			@Override
			public void perform(Player p) {

			}

		}, this);
		pm.registerEvents(new Hologram(), this);

		PluginCommand play = getCommand("play");
		play.setTabCompleter(new ArenaJoinCommands());
		play.setExecutor(new ArenaJoinCommands());
		getCommand("lobby").setExecutor(new ArenaJoinCommands());
		getCommand("force-start").setExecutor(new ArenaCommands());
		getCommand("set-game-timer").setExecutor(new ArenaCommands());
		getCommand("newarenaconfig").setExecutor(new ArenaCommands());
		getCommand("who").setExecutor(new ArenaCommands());
		getCommand("performance").setExecutor(new ArenaCommands());
		getCommand("rank").setExecutor(new ArenaCommands());
		getCommand("ranks").setExecutor(new ArenaCommands());

		getCommand("un-freeze").setExecutor(new ArenaCommands());

		getCommand("freeze").setExecutor(new ArenaCommands());
		getCommand("arena-manager").setExecutor(new ArenaCommands());

		// getServer().getMessenger().registerOutgoingPluginChannel(this, "Arena");
		// getServer().getMessenger().registerIncomingPluginChannel(this, "Arena",
		// this);
		// getServer().getMessenger().registerOutgoingPluginChannel(this, "Bungeecord");
		// getServer().getMessenger().registerIncomingPluginChannel(this, "Bungeecord",
		// this);
		// getServer().getMessenger().registerOutgoingPluginChannel(this, "Sql");
		// getServer().getMessenger().registerIncomingPluginChannel(this, "Sql", this);

		ArenaRegister.registerAllGames();
		ArenaRegister.registerAllLobbies();
		RanksManager.registerRanks();

		ChatManager.setUpChatFilters();

		ActionItemManager.registerItems();

		CustomPvPMechanics.setupPvp();
		core(true);
		configCheck();
		ArenaUtil.createArena(0.0, ArenaUtil.decideArenaName(), null, "none");

		ArenaEvents.registerSpigotOverrideCommands();

		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TPSLag(), 100L, 1L);

		setUpVersions();

	}

	public void countReload() {

		File file = new File(
				Bukkit.getWorldContainer().getAbsolutePath() + "/plugins/Crimson-Central/server-version.yml");
		Config c = new Config(file);
		Double v = Double.parseDouble(c.getLine("server-version"));

		String ver = format.format((v + 0.01));

		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

		c.addLine("version-" + ver, "" + ft.format(dNow));

		c.addLine("server-version", "" + ver);

	}

	static int task;
	static int time;
	int i;
	int messages;

	public void core(boolean toggle) {

		if (toggle == false) {

			Bukkit.getScheduler().cancelTask(task);
			time = 0;
		} else {

			task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				@Override
				public void run() {
					++time;
					++messages;
					DateFormat df = new SimpleDateFormat("MM/dd/yy");

					new Date();
					Calendar calobj = Calendar.getInstance();
					date = df.format(calobj.getTime());

					for (ChangingInventory ci : changing_inventories) {
						ci.change();

					}

					if (time == 7) {
						startUpMessage();
						fastTasks();
						slowTasks();

					}

					if (Integer.remainderUnsigned(time, 10) == 0) {
						ArenaManager.updateArenaManagerMenu();

					}
					Iterator<DamageTag> it = DamageTag.damage_tags.iterator();
					int size = DamageTag.damage_tags.size();
					i = 0;

					for (Player p : Bukkit.getOnlinePlayers()) {
						GeneralUtilities.sendHeaderFooter(p,
								ChatColor.AQUA + "You are beta testing on " + ChatColor.YELLOW + ChatColor.BOLD
										+ "CRIMSON-CENTRAL.COM",
								ChatColor.GREEN + "Support the server on " + ChatColor.RED + "" + ChatColor.BOLD
										+ "CRIMSON-CENTRAL.COM/SHOP");
					}

					if (messages == 900) {

						Bukkit.broadcastMessage(
								ChatColor.AQUA + "" + ChatColor.BOLD + "â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€");
						Bukkit.broadcastMessage(" ");
						Bukkit.broadcastMessage(ArenaUtil.centerText(ChatColor.DARK_RED + "Crimson " + ChatColor.GOLD
								+ "Central " + ChatColor.WHITE + "is currently"));
						Bukkit.broadcastMessage(
								ArenaUtil.centerText(ChatColor.WHITE + "in early alpha testing. At some point"));
						Bukkit.broadcastMessage(
								ArenaUtil.centerText(ChatColor.WHITE + "everything may break. Please except"));
						Bukkit.broadcastMessage(ArenaUtil.centerText(ChatColor.WHITE + "it to happen. Thank You"));
						Bukkit.broadcastMessage(ArenaUtil.centerText(ChatColor.WHITE + "for your patience."));
						Bukkit.broadcastMessage(" ");
						Bukkit.broadcastMessage(
								ChatColor.AQUA + "" + ChatColor.BOLD + "â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€");

					} else if (messages == 1800) {

						Bukkit.broadcastMessage(
								ChatColor.GREEN + "" + ChatColor.BOLD + "â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€");
						Bukkit.broadcastMessage("  ");
						Bukkit.broadcastMessage(ArenaUtil.centerText(ChatColor.WHITE + "Thanks for beta testing"));
						Bukkit.broadcastMessage(
								ArenaUtil.centerText(ChatColor.WHITE + "our server! We want to thank you"));
						Bukkit.broadcastMessage(ArenaUtil.centerText(ChatColor.WHITE + "by giving you the "
								+ ChatColor.BLUE + "[BETA] " + ChatColor.WHITE + "rank!"));
						Bukkit.broadcastMessage(ArenaUtil.centerText(ChatColor.WHITE + "To claim your rank, go to"));
						Bukkit.broadcastMessage(ArenaUtil.centerText(ChatColor.YELLOW + "crimson-central.com/shop"));
						Bukkit.broadcastMessage(" ");
						Bukkit.broadcastMessage(
								ChatColor.GREEN + "" + ChatColor.BOLD + "â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€");

					} else if (messages == 2700) {

						Bukkit.broadcastMessage(
								ChatColor.YELLOW + "" + ChatColor.BOLD + "â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€");
						Bukkit.broadcastMessage(" ");
						Bukkit.broadcastMessage(ArenaUtil.centerText(ChatColor.WHITE + "If you find any bugs"));
						Bukkit.broadcastMessage(ArenaUtil.centerText(ChatColor.WHITE + "while testing, please report"));
						Bukkit.broadcastMessage(ArenaUtil.centerText(ChatColor.WHITE + "them on our website: "));
						Bukkit.broadcastMessage(
								ArenaUtil.centerText(ChatColor.GREEN + "crimson-central.com/bug-report"));
						Bukkit.broadcastMessage(" ");
						Bukkit.broadcastMessage(
								ChatColor.YELLOW + "" + ChatColor.BOLD + "â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€");

					} else if (messages == 3600) {

						Bukkit.broadcastMessage(
								ChatColor.GOLD + "" + ChatColor.BOLD + "â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€");
						Bukkit.broadcastMessage(" ");
						Bukkit.broadcastMessage(
								ArenaUtil.centerText(ChatColor.GREEN + "Have a suggestion for us? Contact us!"));
						Bukkit.broadcastMessage(" ");
						Bukkit.broadcastMessage(ArenaUtil
								.centerText(ChatColor.AQUA + "Twitter: " + ChatColor.WHITE + "@CrimsonCentral"));
						Bukkit.broadcastMessage(ArenaUtil.centerText(
								ChatColor.BLUE + "Facebook: " + ChatColor.WHITE + "[insert facebook here]"));
						Bukkit.broadcastMessage(ArenaUtil
								.centerText(ChatColor.RED + "Website: " + ChatColor.WHITE + "crimson-central.com"));

						Bukkit.broadcastMessage(" ");
						Bukkit.broadcastMessage(
								ChatColor.GOLD + "" + ChatColor.BOLD + "â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€");
						messages = 0;
					}

					while (it.hasNext()) {
						DamageTag tag = it.next();
						++i;
						tag.expire_time = tag.expire_time - 1;

						if (tag.expire_time <= 0) {

							DamageTag.damage_tags.remove(tag);
							tag = null;

						}

						if (i == size) {

							it.remove();
						}

					}

				}

			}, 0, 20);
		}

	}

	public void slowTasks() {

		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {

				for (Player p : Bukkit.getOnlinePlayers()) {
					PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
					for (Entry<NPC, World> e : NPC.npcs.entrySet()) {
						for (@SuppressWarnings("unused")
						ArmorStand a : e.getKey().stands) {

						}
					}

					for (Entry<Hologram, World> h : Hologram.holograms.entrySet()) {
						if (h.getKey().needsUpdating()) {
							if (h.getKey().specific_visiblity.isEmpty() && !h.getKey().excluded_players.contains(p)) {
								connection.sendPacket(new PacketPlayOutEntityDestroy(h.getKey().holo.getId()));
								connection.sendPacket(new PacketPlayOutSpawnEntityLiving(h.getKey().holo));
							} else if (h.getKey().specific_visiblity.contains(p)
									&& !h.getKey().excluded_players.contains(p)) {
								connection.sendPacket(new PacketPlayOutEntityDestroy(h.getKey().holo.getId()));
								connection.sendPacket(new PacketPlayOutSpawnEntityLiving(h.getKey().holo));
							}
						}

					}
				}
			}
		}, 0, 140);

	}

	int fast_time;

	@SuppressWarnings("unused")
	private static HashMap<Player, Integer> char_count = new HashMap<Player, Integer>();

	@SuppressWarnings("unused")
	private static HashMap<Player, String> scoreboard_names = new HashMap<Player, String>();

	public void fastTasks() {

		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				++fast_time;
				AnimatedInventory.updateAllInventories(fast_time);
				
				for (Player p : Bukkit.getOnlinePlayers()) {

					p.stopSound(Sound.ENTITY_ENDERDRAGON_GROWL);
					p.stopSound(Sound.BLOCK_PORTAL_AMBIENT);
					p.stopSound(Sound.BLOCK_PORTAL_TRAVEL);
					p.stopSound(Sound.BLOCK_PORTAL_TRIGGER);

				}
				if (fast_time % 20 == 0) {
					// OtherUtil.flashScoreboardTitle();
				}
				for (Entry<String, CoolDown> e : CoolDown.cool_downs.entrySet()) {
					CoolDown cd = e.getValue();
					if (fast_time % cd.getInterval() == 0) {
						cd.setTime(cd.getTime() - 1);
					}

				}
			}

		}, 0, 1);

	}

	public void startUpMessage() {

		File file = new File(
				Bukkit.getWorldContainer().getAbsolutePath() + "/plugins/Crimson-Central/server-version.yml");
		Config c = new Config(file);

		Double version = Double.valueOf(c.getLine("server-version"));

		c.addLine("server-version", "" + format.format((Double.valueOf(c.getLine("server-version")) + 0.01)));

		Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€");
		Bukkit.broadcastMessage(
				ArenaUtil.centerText(ChatColor.DARK_RED + "" + ChatColor.BOLD + "CRIMSON " + ChatColor.GOLD
						+ ChatColor.BOLD + "CENTRAL " + ChatColor.DARK_AQUA + ChatColor.BOLD + "PLUGIN ENABLE EVENT"));
		Bukkit.broadcastMessage(ArenaUtil.centerText(ChatColor.GREEN + "The " + ChatColor.DARK_RED + "Crimson "
				+ ChatColor.GOLD + "Central " + ChatColor.YELLOW + "Spigot Plugin"));
		Bukkit.broadcastMessage(ArenaUtil.centerText(ChatColor.GREEN + "has been enabled for this server!"));
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(
				ArenaUtil.centerText(ChatColor.AQUA + "Plugin Version: " + ChatColor.WHITE + "v." + version));
		Bukkit.broadcastMessage(ArenaUtil.centerText(ChatColor.WHITE + "crimson-central.com/propertyrights"));
		Bukkit.broadcastMessage(
				ArenaUtil.centerText(ChatColor.GOLD + "Copyright Â© 2018" + ChatColor.RED + " CRIMSON CENTRAL INC"));
		Bukkit.broadcastMessage(
				ArenaUtil.centerText(ChatColor.GREEN + "" + ChatColor.BOLD + "â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€â–€"));

	}

	public void configCheck() {

		File f = new File("/plugins/Crimson-Central/server-config.yml");

		boolean did = true;

		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			did = false;

		}

		Config c = new Config(f);

		if (did == false) {

			c.addLine("Server Name", "mini-s-01a");
			c.addLine("Server-Type", "Mini");
			c.addLine("Is Test Server", "false");
			c.addLine("Is Build Server", "false");
			c.addLine("Allow for New Arenas", "true");
			c.addLine("Max # of Arenas", "true");
		}
		c.saveArenaConfig();

	}

	public static void setUpVersions() {

	}

	public void onDisable() {

		for (Player p : Bukkit.getOnlinePlayers()) {

			p.getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
			p.getScoreboard().clearSlot(DisplaySlot.PLAYER_LIST);
			p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);

		}

		ArenaUtil.deleteArenaFolders();
	}

}
