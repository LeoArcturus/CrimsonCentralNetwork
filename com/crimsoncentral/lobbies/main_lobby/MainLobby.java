package com.crimsoncentral.lobbies.main_lobby;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;

import com.crimsoncentral.Main;
import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.ArenaManager;
import com.crimsoncentral.arena.util.ArenaUtil;
import com.crimsoncentral.arena.util.NPC;
import com.crimsoncentral.arena.util.PlayerVisiblity;
import com.crimsoncentral.arena.util.parkour_course.ParkourCourse;
import com.crimsoncentral.ranks.Rank;
import com.crimsoncentral.ranks.RanksManager;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.server_player.ServerPlayer;
import com.crimsoncentral.server_player.social.ProfileMenu;
import com.crimsoncentral.util.ScoreboardWrapper;
import com.crimsoncentral.util.common_items.GameMenu;
import com.crimsoncentral.util.item.ActionItemManager;
import com.crimsoncentral.util.other.ChatUtil;

public class MainLobby extends Arena {

	HashMap<Integer, Location> npc_locations = new HashMap<Integer, Location>();
	HashMap<Location, NPC> play_npcs = new HashMap<Location, NPC>();

	NPC rewards = null;
	NPC n1 = null;
	NPC n2 = null;
	NPC n3 = null;
	NPC n4 = null;
	NPC n5 = null;
	NPC n6 = null;
	NPC n7 = null;
	NPC n8 = null;

	NPC np1 = null;
	NPC np2 = null;

	NPC nu1 = null;
	NPC nu2 = null;

	Arena main = this;
	public int scoreboard_page = ArenaManager.local_arenas.size();

	public MainLobby(String arena_name, Double game_mode, int max_players, int min_players) {
		super(arena_name, game_mode, max_players, min_players);
		// TODO Auto-generated constructor stub,
	}

	@Override
	public void setup() {

		main = this;

		main.setArenaSpawn(new Location(main.getWorld(), 208.5, 68, 0.5, -90, 0));
		main.allowNewPlayers(true);
		makeArenaWorld();
		setRadius(400);

		npc_locations.put(npc_locations.size() + 1, new Location(getWorld(), 246.5, 66.5, -10.5, 45, 0));
		npc_locations.put(npc_locations.size() + 1, new Location(getWorld(), 246.5, 66.5, 11.5, 135, 0));
		npc_locations.put(npc_locations.size() + 1, new Location(getWorld(), 248.5, 66.5, -8.5, 45, 0));
		npc_locations.put(npc_locations.size() + 1, new Location(getWorld(), 248.5, 66.5, 9.5, 135, 0));

		npc_locations.put(npc_locations.size() + 1, new Location(getWorld(), 250.5, 66.5, -6.5, 90, 0));
		npc_locations.put(npc_locations.size() + 1, new Location(getWorld(), 250.5, 66.5, 7.5, 90, 0));
		npc_locations.put(npc_locations.size() + 1, new Location(getWorld(), 251.5, 66.5, -3.5, 90, 0));
		npc_locations.put(npc_locations.size() + 1, new Location(getWorld(), 251.5, 66.5, 4.5, 90, 0));

		npc_locations.put(npc_locations.size() + 1, new Location(getWorld(), 244.5, 66, -4.5, 35, 0));
		npc_locations.put(npc_locations.size() + 1, new Location(getWorld(), 244.5, 66, 5.5, 135, 0));
		npc_locations.put(npc_locations.size() + 1, new Location(getWorld(), 246.5, 66, -1.5, 90, 0));
		npc_locations.put(npc_locations.size() + 1, new Location(getWorld(), 246.5, 66, 2.5, 90, 0));

		new ParkourCourse("Main-Lobby-Course", this, new Location(getWorld(), 189.5, 51, 130.5),
				new Location(getWorld(), -49.5, 62, 2.5), new Location(getWorld(), 163.5, 34, 131.5),
				new Location(getWorld(), 100.5, 47, 136.5), new Location(getWorld(), 65.5, 55, 143.5),
				new Location(getWorld(), 7.5, 68, 134.5), new Location(getWorld(), -20.5, 76, 104.5),
				new Location(getWorld(), -28.5, 55, 51.5));
		// n1 = new NPC("§ev.3.1 [A]").addLine(ChatColor.GREEN +
		// "[Gamemode-1.0-Player-Count] playing...", 0.0, true)
		// .addLine(ChatColor.AQUA + "" + ChatColor.BOLD + "Sky Wars", 0.35,
		// false).addCommand("play skywars normal solo").spawn(npc_locations.get(1));
		// n2 = new NPC("§ev.2.1 [A]").addLine(ChatColor.GREEN +
		// "[Gamemode-2.0-Player-Count] playing...", 0.0, true)
		// .addLine(ChatColor.AQUA + "" + ChatColor.BOLD + "Hunger Games", 0.35,
		// false).spawn(npc_locations.get(2));
		// n3 = new NPC("§ev.2.1 [A]").addLine(ChatColor.GREEN +
		// "[Gamemode-3.0-Player-Count] playing...", 0.0, true)
		// .addLine(ChatColor.AQUA + "" + ChatColor.BOLD + "Duels", 0.35,
		// false).spawn(npc_locations.get(3));
		// n4 = new NPC("§ev.1.0 [A]").addLine(ChatColor.GREEN +
		// "[Gamemode-4.0-Player-Count] playing...", 0.0, true)
		// .addLine(ChatColor.AQUA + "" + ChatColor.BOLD + "The Walls", 0.35,
		// false).spawn(npc_locations.get(4));
		//
		// n5 = new NPC("§ev.1.0 [A]").addLine(ChatColor.GREEN +
		// "[Gamemode-5.0-Player-Count] playing...", 0.0, true)
		// .addLine(ChatColor.AQUA + "" + ChatColor.BOLD + "The Bridge", 0.35,
		// false).spawn(npc_locations.get(5));
		// n6 = new NPC("§ev.1.0 [A]").addLine(ChatColor.GREEN +
		// "[Gamemode-6.0-Player-Count] playing...", 0.0, true)
		// .addLine(ChatColor.AQUA + "" + ChatColor.BOLD + "Capture The Flag", 0.35,
		// false).spawn(npc_locations.get(6));
		// n7 = new NPC("§ev.1.0 [A]").addLine(ChatColor.GREEN +
		// "[Gamemode-7.0-Player-Count] playing...", 0.0, true)
		// .addLine(ChatColor.AQUA + "" + ChatColor.BOLD + "Egg Wars", 0.35,
		// false).spawn(npc_locations.get(7));
		// n8 = new NPC("§ev.1.0 [A]").addLine(ChatColor.GREEN +
		// "[Gamemode-8.0-Player-Count] playing...", 0.0, true)
		// .addLine(ChatColor.AQUA + "" + ChatColor.BOLD + "Ultrahardcore", 0.35,
		// false).spawn(npc_locations.get(8));
		//
		//
		//
		//
		// nu1 = new NPC("§ev.1.0 [A]").addLine(ChatColor.GREEN +
		// "[Gamemode-9.0-Player-Count] playing...", 0.0, true)
		// .addLine(ChatColor.AQUA + "" + ChatColor.BOLD + "Who Done it?", 0.35,
		// false).spawn(npc_locations.get(9));
		// nu2 = new NPC("§ev.1.0 [A]").addLine(ChatColor.GREEN +
		// "[Gamemode-10.0-Player-Count] playing...", 0.0, true)
		// .addLine(ChatColor.AQUA + "" + ChatColor.BOLD + "Build Battle", 0.35,
		// false).spawn(npc_locations.get(10));
		// np1 = new NPC("§ev.1.0 [A]").addLine(ChatColor.GREEN +
		// "[Gamemode-11.0-Player-Count] playing...", 0.0, true)
		// .addLine(ChatColor.AQUA + "" + ChatColor.BOLD + "Zombiez", 0.35,
		// false).spawn(npc_locations.get(11));
		// np2 = new NPC("§ev.1.0 [A]").addLine(ChatColor.GREEN +
		// "[Gamemode-12.0-Player-Count] playing...", 0.0, true)
		// .addLine(ChatColor.AQUA + "" + ChatColor.BOLD + "Spleef", 0.35,
		// false).spawn(npc_locations.get(12));

	

		nu1 = new NPC("§ev.3.1 [A]") {

			@Override
			public void perform(Player p) {

				Inventory i = Bukkit.createInventory(null, 36, ChatColor.RED + "Play " + ChatColor.AQUA + "Sky Wars?");

				i.setItem(12, GameMenu.game_items_ids.get(1.01));
				i.setItem(13, GameMenu.game_items_ids.get(1.02));
				i.setItem(14, GameMenu.game_items_ids.get(1.03));
				i.setItem(22, GameMenu.game_items_ids.get(1.04));

				p.openInventory(i);

			}
		}.addLine(ChatColor.GREEN + "[Gamemode-1.0-Player-Count] playing...", 0.0, true)

				.addLine(ChatColor.AQUA + "Sky Wars", 0.35, false).spawn(npc_locations.get(9));

		nu2 = new NPC("§ev.2.1 [A]") {

			@Override
			public void perform(Player p) {

				Inventory i = Bukkit.createInventory(null, 36, ChatColor.RED + "Play " + ChatColor.AQUA + "Duels?");

				i.setItem(11, GameMenu.game_items_ids.get(4.01));
				i.setItem(12, GameMenu.game_items_ids.get(4.02));
				i.setItem(13, GameMenu.game_items_ids.get(4.03));
				i.setItem(14, GameMenu.game_items_ids.get(4.04));
				i.setItem(15, GameMenu.game_items_ids.get(4.05));

				i.setItem(21, GameMenu.game_items_ids.get(4.06));
				i.setItem(22, GameMenu.game_items_ids.get(4.07));
				i.setItem(23, GameMenu.game_items_ids.get(4.08));

				p.openInventory(i);

			}
		}.addLine(ChatColor.GREEN + "[Gamemode-4.0-Player-Count] playing...", 0.0, true)

				.addLine(ChatColor.AQUA + "Duels", 0.35, false).spawn(npc_locations.get(10));

		n1 = new NPC("§ev.2.2 [A]") {

			@Override
			public void perform(Player p) {

				Inventory i = Bukkit.createInventory(null, 36,
						ChatColor.RED + "Play " + ChatColor.AQUA + "Hunger Games?");

				i.setItem(12, GameMenu.game_items_ids.get(2.01));
				i.setItem(13, GameMenu.game_items_ids.get(2.02));
				i.setItem(14, GameMenu.game_items_ids.get(2.03));
				i.setItem(22, GameMenu.game_items_ids.get(2.04));

				p.openInventory(i);

			}
		}.addLine(ChatColor.GREEN + "[Gamemode-2.0-Player-Count] playing...", 0.0, true)

				.addLine(ChatColor.AQUA + "Hunger Games", 0.35, false).spawn(npc_locations.get(11));

		n2 = new NPC("§ev.2.1 [A]") {

			@Override
			public void perform(Player p) {

				Inventory i = Bukkit.createInventory(null, 36, ChatColor.RED + "Play " + ChatColor.AQUA + "Egg Wars?");

				i.setItem(12, GameMenu.game_items_ids.get(6.01));
				i.setItem(13, GameMenu.game_items_ids.get(6.02));
				i.setItem(14, GameMenu.game_items_ids.get(6.03));
				i.setItem(22, GameMenu.game_items_ids.get(6.04));

				p.openInventory(i);

			}
		}.addLine(ChatColor.GREEN + "[Gamemode-6.0-Player-Count] playing...", 0.0, true)

				.addLine(ChatColor.AQUA + "Egg Wars", 0.35, false).spawn(npc_locations.get(12));

	}

	int task;

	@Override
	public void run() {

		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {

			@Override
			public void run() {

				String server_name = "";
				if (Main.is_test_server == true) {

					server_name = "[TS]";
				}

				// n1.replaceLine(1, "" + ChatRelated.randomChatColor() +
				// ArenaUtil.getGamePlayers(1).size()
				// + ChatColor.GREEN + " playing...");
				// n2.replaceLine(1, "" + ChatRelated.randomChatColor() +
				// ArenaUtil.getGamePlayers(2).size()
				// + ChatColor.GREEN + " playing...");
				// n3.replaceLine(1, "" + ChatRelated.randomChatColor() +
				// ArenaUtil.getGamePlayers(3).size()
				// + ChatColor.GREEN + " playing...");
				// n4.replaceLine(1, "" + ChatRelated.randomChatColor() +
				// ArenaUtil.getGamePlayers(4).size()
				// + ChatColor.GREEN + " playing...");
				//
				// n5.replaceLine(1, "" + ChatRelated.randomChatColor() +
				// ArenaUtil.getGamePlayers(5).size()
				// + ChatColor.GREEN + " playing...");
				// n6.replaceLine(1, "" + ChatRelated.randomChatColor() +
				// ArenaUtil.getGamePlayers(6).size()
				// + ChatColor.GREEN + " playing...");
				// n7.replaceLine(1, "" + ChatRelated.randomChatColor() +
				// ArenaUtil.getGamePlayers(7).size()
				// + ChatColor.GREEN + " playing...");
				// n8.replaceLine(1, "" + ChatRelated.randomChatColor() +
				// ArenaUtil.getGamePlayers(8).size()
				// + ChatColor.GREEN + " playing...");

				nu1.replaceLine(1, "" + ChatUtil.randomChatColor() + ArenaUtil.getGamePlayers(1).size()
						+ ChatColor.GREEN + " playing...");

				nu2.replaceLine(1, "" + ChatUtil.randomChatColor() + ArenaUtil.getGamePlayers(4).size()
						+ ChatColor.GREEN + " playing...");

				n1.replaceLine(1, "" + ChatUtil.randomChatColor() + ArenaUtil.getGamePlayers(2).size() + ChatColor.GREEN
						+ " playing...");

				n2.replaceLine(1, "" + ChatUtil.randomChatColor() + ArenaUtil.getGamePlayers(6).size() + ChatColor.GREEN
						+ " playing...");

				// nu2.replaceLine(1, "" + ChatRelated.randomChatColor() +
				// ArenaUtil.getModePlayers(1.03).size()
				// + ChatColor.GREEN + " playing...");
				//
				// np1.replaceLine(1, "" + ChatRelated.randomChatColor() +
				// ArenaUtil.getModePlayers(1.02).size()
				// + ChatColor.GREEN + " playing...");
				//
				// np2.replaceLine(1, "" + ChatRelated.randomChatColor() +
				// ArenaUtil.getModePlayers(1.04).size()
				// + ChatColor.GREEN + " playing...");

				for (Player p : getWorld().getPlayers()) {

					ScoreboardWrapper sw = ArenaManager.scoreboards.get(p);
					p.setScoreboard(sw.getScoreboard());
					String rank = ChatColor.GRAY + "Default";
					if (PlayerManager.getServerPlayer(p).getRank() != RanksManager.default_rank) {
						rank = PlayerManager.getServerPlayer(p).getRank().getPrefix().replace("[", "").replace("]", "")
								.replace("§l", "");

					}

					sw.changePage(scoreboard_page);
					sw.setTitle(scoreboard_page, ChatColor.YELLOW + "" + ChatColor.BOLD + "CRIMSON CENTRAL");

					sw.setLine(scoreboard_page, 14, "" + ChatColor.GRAY + Main.date + " " + server_name);

					sw.setLine(scoreboard_page, 13, ChatColor.WHITE + " ");
					sw.setLine(scoreboard_page, 12, ChatColor.WHITE + "Rank: " + rank);
					sw.setLine(scoreboard_page, 11, ChatColor.WHITE + "Level: " + ChatColor.AQUA + "1");
					sw.setLine(scoreboard_page, 10, "Loot Crates: " + ChatColor.YELLOW + "0");
					sw.setLine(scoreboard_page, 9, "Gold Coins: " + ChatColor.GOLD + "0");

					sw.setLine(scoreboard_page, 8, " ");

					sw.setLine(scoreboard_page, 7, ChatColor.WHITE + "Clan: " + ChatColor.GRAY + "no clan");

					sw.setLine(scoreboard_page, 6, ChatColor.WHITE + "Friends online: " + ChatColor.YELLOW + "0/0");
					sw.setLine(scoreboard_page, 5, " ");
					sw.setLine(scoreboard_page, 4,
							"Players online: " + ChatColor.AQUA + Bukkit.getOnlinePlayers().size());
					sw.setLine(scoreboard_page, 3, "Server: " + ChatColor.GREEN + Main.server_name);
					sw.setLine(scoreboard_page, 2, ChatColor.WHITE + "Version: " + ChatColor.YELLOW + "1.12");
					sw.setLine(scoreboard_page, 1, " ");
					sw.setLine(scoreboard_page, 0, ChatColor.RED + " crimson-centr", ChatColor.RED + "al.com");
				}
			}
		}, 0, 20);

	}

	@Override
	public void join(Player player) {
		Location spawn = new Location(main.getArenaSpawn().getWorld(), main.getArenaSpawn().getX(),
				main.getArenaSpawn().getY(), main.getArenaSpawn().getZ(), main.getArenaSpawn().getYaw(),
				main.getArenaSpawn().getPitch());
		ServerPlayer sp = PlayerManager.getServerPlayer(player);
		Rank r = sp.getRank();

		managePlayerScoreboard(player);
		resetPlayer(player);
		sendLoginJASONS(player);
		applyRankStuff(player);
		applyPreferencesPlayer(player);
		sendFireworks(player);
		if (RanksManager.canPlayerPreform(player, RanksManager.mage, false) == true) {
			spawn.setY(spawn.getY() + 3);

			main.sendWorldMessage(r.getPlayerFullName(player) + ChatColor.GOLD + " has joined the lobby!");

		}

		player.getInventory().setItem(0, ActionItemManager.getActionItemItemStack("game menu"));
		player.getInventory().setItem(4, ProfileMenu.giveProfileItem(player));
		player.teleport(spawn);

		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1, 1);

		enableFlight(player);

		// OtherUtil.fakePlayer(player, 15, 3);
	}

	@Override
	public void quit(Player player) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerDeath(Player player, Player killer, DamageCause cause) {
		// TODO Auto-generated method stub

	}

	public void sendFireworks(Player p) {
		if (RanksManager.canPlayerPreform(p, RanksManager.crimson, false)) {
			ArrayList<Firework> fireworks = new ArrayList<Firework>();

			Location f1_l = new Location(p.getWorld(), 230.5, 66, -43.5);
			Location f2_l = new Location(p.getWorld(), 235.5, 66, -49.5);

			Location f3_l = new Location(p.getWorld(), 239.5, 66, -31.5);
			Location f4_l = new Location(p.getWorld(), 246.5, 66, -20.5);

			Location f5_l = new Location(p.getWorld(), 230.5, 66, 43.5);
			Location f6_l = new Location(p.getWorld(), 235.5, 66, 49.5);

			Location f7_l = new Location(p.getWorld(), 239.5, 66, 31.5);
			Location f8_l = new Location(p.getWorld(), 246.5, 66, 20.5);

			Firework f1 = (Firework) f1_l.getWorld().spawnEntity(f1_l, EntityType.FIREWORK);
			Firework f2 = (Firework) f2_l.getWorld().spawnEntity(f2_l, EntityType.FIREWORK);
			Firework f3 = (Firework) f3_l.getWorld().spawnEntity(f3_l, EntityType.FIREWORK);
			Firework f4 = (Firework) f4_l.getWorld().spawnEntity(f4_l, EntityType.FIREWORK);

			Firework f5 = (Firework) f1_l.getWorld().spawnEntity(f5_l, EntityType.FIREWORK);
			Firework f6 = (Firework) f2_l.getWorld().spawnEntity(f6_l, EntityType.FIREWORK);
			Firework f7 = (Firework) f3_l.getWorld().spawnEntity(f7_l, EntityType.FIREWORK);
			Firework f8 = (Firework) f4_l.getWorld().spawnEntity(f8_l, EntityType.FIREWORK);

			fireworks.add(f1);
			fireworks.add(f2);
			fireworks.add(f3);
			fireworks.add(f4);
			fireworks.add(f5);
			fireworks.add(f6);
			fireworks.add(f7);
			fireworks.add(f8);

			for (Firework fw : fireworks) {

				fw.setFireworkMeta(PlayerManager.getServerPlayer(p).getRank().getFireworkMeta());

			}

		}
	}

	public void sendLoginJASONS(Player p) {

		ArenaUtil.sendTitle(p, ChatColor.YELLOW + "" + ChatColor.BOLD + "Welcome to", 1, 3, 1);
		ArenaUtil.sendSubTitle(p, ChatColor.DARK_RED + "" + ChatColor.BOLD + "Crimson " + ChatColor.GOLD + ""
				+ ChatColor.BOLD + "Central ", 1, 3, 1);

		p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
		p.sendMessage(" ");
		p.sendMessage(ArenaUtil.centerText(
				ChatColor.DARK_RED + "Crimson " + ChatColor.GOLD + "Central " + ChatColor.WHITE + "is currently"));
		p.sendMessage(ArenaUtil.centerText(ChatColor.WHITE + "in early alpha testing. At some point"));
		p.sendMessage(ArenaUtil.centerText(ChatColor.WHITE + "everything may break. Please except"));
		p.sendMessage(ArenaUtil.centerText(ChatColor.WHITE + "it to happen. Thank You"));
		p.sendMessage(ArenaUtil.centerText(ChatColor.WHITE + "for your patience."));
		p.sendMessage(" ");
		p.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");

	}

	public void applyRankStuff(Player p) {

	}

	public void applyPreferencesPlayer(Player p) {

		PlayerVisiblity.setPlayerVisiblity(p, PlayerVisiblity.Visiblity.VISIBLE);
		PlayerVisiblity.updateWorldVisiblity(p.getWorld());

	}

	public void getPlayerStats(Player p) {

	}

	public void enableFlight(Player p) {
		if (RanksManager.canPlayerPreform(p, RanksManager.commoner, false)) {
			p.sendMessage(ChatColor.GREEN + "Flying has been enabled in lobbies!");

			p.setAllowFlight(true);
			p.setFlying(true);

		}
	}

	public void disableFlight(Player p) {

		p.setAllowFlight(false);
		p.setFlying(false);
		p.sendMessage(ChatColor.RED + "Flying has been disabled in lobbies!");
	}

}
