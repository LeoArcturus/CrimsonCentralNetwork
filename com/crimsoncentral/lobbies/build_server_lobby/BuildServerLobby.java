package com.crimsoncentral.lobbies.build_server_lobby;

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
import com.crimsoncentral.Main;
import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.ArenaManager;
import com.crimsoncentral.arena.util.ArenaUtil;
import com.crimsoncentral.arena.util.NPC;
import com.crimsoncentral.arena.util.PlayerVisiblity;
import com.crimsoncentral.ranks.Rank;
import com.crimsoncentral.ranks.RanksManager;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.server_player.ServerPlayer;
import com.crimsoncentral.util.ScoreboardWrapper;
import com.crimsoncentral.util.item.ActionItemManager;
import com.crimsoncentral.util.other.ChatUtil;

public class BuildServerLobby extends Arena {

	HashMap<Integer, Location> npc_locations = new HashMap<Integer, Location>();
	HashMap<Location, NPC> play_npcs = new HashMap<Location, NPC>();
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

	public BuildServerLobby(String arena_name, Double game_mode, int max_players, int min_players) {
		super(arena_name, game_mode, max_players, min_players);
		// TODO Auto-generated constructor stub,
	}

	@Override
	public void setup() {

		main = this;

		main.setArenaSpawn(new Location(main.getWorld(), 208.5, 68, 0.5, -90, 0));
		main.allowNewPlayers(true);
		makeArenaWorld();

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

				nu1.replaceLine(1, "" + ChatUtil.randomChatColor() + ArenaUtil.getModePlayers(1.01).size()
						+ ChatColor.GREEN + " playing...");

				nu2.replaceLine(1, "" + ChatUtil.randomChatColor() + ArenaUtil.getModePlayers(1.03).size()
						+ ChatColor.GREEN + " playing...");

				np1.replaceLine(1, "" + ChatUtil.randomChatColor() + ArenaUtil.getModePlayers(1.02).size()
						+ ChatColor.GREEN + " playing...");

				np2.replaceLine(1, "" + ChatUtil.randomChatColor() + ArenaUtil.getModePlayers(1.04).size()
						+ ChatColor.GREEN + " playing...");

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

					sw.setLine(scoreboard_page, 10, "" + ChatColor.GRAY + Main.date + " " + server_name);

					sw.setLine(scoreboard_page, 9, ChatColor.WHITE + " ");
					sw.setLine(scoreboard_page, 8, ChatColor.WHITE + "Rank: " + rank);
					sw.setLine(scoreboard_page, 7, ChatColor.WHITE + "Visiblity Preference: ");
					sw.setLine(scoreboard_page, 6,
							ChatColor.GREEN + String.valueOf(PlayerVisiblity.visiblities.get(p)));
					sw.setLine(scoreboard_page, 5, ChatColor.WHITE + "Level: " + ChatColor.GREEN + "1");
					sw.setLine(scoreboard_page, 4, " ");

					sw.setLine(scoreboard_page, 3, ChatColor.WHITE + "Lobby: " + ChatColor.GREEN + "#1");

					sw.setLine(scoreboard_page, 2,
							ChatColor.WHITE + "Players: " + ChatColor.GREEN + Bukkit.getOnlinePlayers().size());

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

		ArenaManager.scoreboards.remove(player);
		ScoreboardWrapper sw = new ScoreboardWrapper(player.getUniqueId());
		ArenaManager.scoreboards.put(player, sw);

		sendLoginJASONS(player);
		applyRankStuff(player);
		applyPreferencesPlayer(player);
		sendFireworks(player);
		if (RanksManager.canPlayerPreform(player, RanksManager.mage, false) == true) {
			spawn.setY(spawn.getY() + 3);

			main.sendWorldMessage(r.getPlayerFullName(player) + ChatColor.GOLD + " has joined the lobby!");

		}

		player.getInventory().setItem(0, ActionItemManager.getActionItemItemStack("game menu"));

		player.teleport(spawn);

		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1, 1);

		enableFlight(player);

		// ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new
		// PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, ));

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

		PlayerManager.getServerPlayer(p).getRank().applyPlayerPrefix(p);
	}

	public void applyPreferencesPlayer(Player p) {

		PlayerVisiblity.setPlayerVisiblity(p, PlayerVisiblity.Visiblity.VISIBLE);
		PlayerVisiblity.updateWorldVisiblity(p.getWorld());

	}

	public void getPlayerStats(Player p) {

	}

	public void enableFlight(Player p) {

		p.setAllowFlight(true);
		p.setFlying(true);
		p.sendMessage(ChatColor.GREEN + "Flying has been enabled in lobbies!");
	}

	public void disableFlight(Player p) {

		p.setAllowFlight(false);
		p.setFlying(false);
		p.sendMessage(ChatColor.RED + "Flying has been disabled in lobbies!");
	}

}
