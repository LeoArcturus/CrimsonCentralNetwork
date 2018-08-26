package com.crimsoncentral.ranks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scoreboard.Scoreboard;

import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.ArenaManager;
import com.crimsoncentral.arena.Arena.ArenaStage;
import com.crimsoncentral.arena.util.ArenaUtil;
import com.crimsoncentral.ranks.Rank.RankType;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.server_player.ServerPlayer;
import com.crimsoncentral.util.GeneralUtilities;

public class RanksManager {
	public static HashMap<UUID, PermissionAttachment> ranks = new HashMap<UUID, PermissionAttachment>();
	public static HashMap<Integer, Rank> intranks = new HashMap<Integer, Rank>();
	public static HashMap<String, Rank> stringranks = new HashMap<String, Rank>();

	public static Rank owner = null;
	public static Rank admin = null;

	public static Rank srmod = null;
	public static Rank mod = null;
	public static Rank helper = null;
	public static Rank buildteam = null;

	public static Rank famous = null;

	public static Rank crimson = null;
	public static Rank mage = null;
	public static Rank knight = null;;
	public static Rank rogue = null;
	public static Rank commoner = null;
	public static Rank default_rank = null;

	public static void registerRanks() {

		owner = new Rank("owner", "§6§l[OWNER] ", ChatColor.GOLD, ChatColor.WHITE, RankType.STAFF, true, 10.0, 0);
		owner.setFireworkMeta(GeneralUtilities.generateFireworkMeta(Type.BALL, Color.ORANGE, Color.RED, true, true, 1));

		admin = new Rank("admin", "§4§l[ADMIN] ", ChatColor.DARK_RED, ChatColor.WHITE, RankType.STAFF, true, 10.0, 1);

		admin.setFireworkMeta(GeneralUtilities.generateFireworkMeta(Type.BALL, Color.MAROON, Color.RED, true, true, 1));

		srmod = new Rank("srmod", "§2§l[SRMOD]§e ", ChatColor.YELLOW, ChatColor.WHITE, RankType.STAFF, true, 10.0, 2);

		srmod.setFireworkMeta(
				GeneralUtilities.generateFireworkMeta(Type.BALL, Color.OLIVE, Color.YELLOW, true, true, 1));

		mod = new Rank("mod", "§2§l[MOD] ", ChatColor.DARK_GREEN, ChatColor.WHITE, RankType.STAFF, true, 10.0, 3);

		srmod.setFireworkMeta(
				GeneralUtilities.generateFireworkMeta(Type.BALL, Color.OLIVE, Color.GREEN, true, true, 1));

		helper = new Rank("helper", "§9§l[HELPER] ", ChatColor.BLUE, ChatColor.WHITE, RankType.STAFF, true, 10.0, 4);

		srmod.setFireworkMeta(
				GeneralUtilities.generateFireworkMeta(Type.BALL, Color.MAROON, Color.BLUE, true, true, 1));

		buildteam = new Rank("buildteam", "§3§l[BUILDTEAM] ", ChatColor.DARK_AQUA, ChatColor.WHITE, RankType.STAFF,
				true, 10.0, 5);

		buildteam.setFireworkMeta(
				GeneralUtilities.generateFireworkMeta(Type.BALL, Color.TEAL, Color.SILVER, true, true, 1));

		famous = new Rank("famous", "§c[§fFamous§c] ", ChatColor.GOLD, ChatColor.WHITE, RankType.FAMOUS, true, 10.0, 6);

		famous.setFireworkMeta(GeneralUtilities.generateFireworkMeta(Type.BALL, Color.RED, Color.WHITE, true, true, 1));

		crimson = new Rank("crimson", "§c[CRIMSON] ", ChatColor.RED, ChatColor.WHITE, RankType.DONATOR, false, 10.0, 7);

		crimson.setFireworkMeta(
				GeneralUtilities.generateFireworkMeta(Type.BALL, Color.RED, Color.YELLOW, true, true, 1));

		mage = new Rank("mage", "§e[MAGE] ", ChatColor.YELLOW, ChatColor.WHITE, RankType.DONATOR, false, 8.0, 8);
		knight = new Rank("knight", "§a[KNIGHT] ", ChatColor.GREEN, ChatColor.WHITE, RankType.DONATOR, false, 6.0, 9);
		rogue = new Rank("rogue", "§d[ROGUE] ", ChatColor.LIGHT_PURPLE, ChatColor.WHITE, RankType.DONATOR, false, 4.0,
				10);
		commoner = new Rank("commoner", "§f[COMMONER] ", ChatColor.WHITE, ChatColor.GRAY, RankType.DONATOR, false, 2.0,
				11);
		default_rank = new Rank("default", "§7", ChatColor.GRAY, ChatColor.GRAY, RankType.NON, false, 1.0, 12);

	}

	public static Rank getRank(String name) {

		Rank rank = null;

		Iterator<Entry<String, Rank>> it = stringranks.entrySet().iterator();
		while (it.hasNext()) {

			Entry<String, Rank> pair = it.next();

			if (pair.getKey().toLowerCase().equals(name.toLowerCase())) {
				rank = pair.getValue();
				break;

			}

		}

		if (rank == null) {
			try {
				throw new Exception("ERROR: " + name + " is not a registered rank name");
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		return rank;
	}

	public static Rank getRank(Integer level) {

		Rank rank = null;

		Iterator<Entry<Integer, Rank>> it = intranks.entrySet().iterator();
		while (it.hasNext()) {

			Entry<Integer, Rank> pair = it.next();

			if (pair.getKey() == level) {
				rank = pair.getValue();
				break;

			}

		}

		if (rank == null) {
			try {
				throw new Exception("ERROR: " + level + " is not a registered rank level");
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		return rank;
	}

	@SuppressWarnings("deprecation")
	public static void applyRankPrefixes(World world) {

		for (Player p : world.getPlayers()) {
			Scoreboard s = ArenaManager.scoreboards.get(p).getScoreboard();
		
			for (Player p1 :  world.getPlayers()) {
				ServerPlayer sp = PlayerManager.getServerPlayer(p1);
				Iterator<Entry<String, Rank>> it = stringranks.entrySet().iterator();
				while (it.hasNext()) {

					Entry<String, Rank> pair = it.next();

					org.bukkit.scoreboard.Team t1 = null;
					org.bukkit.scoreboard.Team t2 = null;

					String s1 = pair.getValue().getName();
					String name = p1.getName();
					String uuid = p1.getUniqueId().toString();
					String part = uuid.substring(0, Math.min(uuid.length(), 4))
							+ name.toString().substring(0, Math.min(name.length(), 4))
							+ s1.toString().substring(0, Math.min(s1.length(), 7));
					String full = part + "F";
					String color = part + "C";

					if (s.getTeam(full) != null) {
						t1 = s.getTeam(full);

					} else {

						t1 = s.registerNewTeam(full);
					}

					if (s.getTeam(color) != null) {
						t2 = s.getTeam(color);

					} else {

						t2 = s.registerNewTeam(color);
					}

					t1.setPrefix(pair.getValue().getPrefix());
					t2.setPrefix(pair.getValue().getRankColor() + "");

					if (sp.getClanInfo() == null) {

						t1.setSuffix(
								ChatColor.DARK_AQUA + " [" + ArenaUtil.decideArenaName().replace("Arena-", "") + "]");

					}

					if (pair.getValue() == sp.getRank()) {
						Arena a = sp.getArena();
						if (a != null) {

							if (a.getStage() == ArenaStage.COUNT_DOWN || a.getStage() == ArenaStage.PRE_COUNT_DOWN) {
								t2.addPlayer(p1);
							} else {
								if (a.getStage() != ArenaStage.GAME_TIME)
									t1.addPlayer(p1);

							}

						} else {
							t1.addPlayer(p1);
						}
					

				}}
			}
		}
	}

	public static boolean canPlayerPreform(Player player, Rank required_rank, boolean spit_error_message) {

		boolean can_preform = false;

		if (PlayerManager.getServerPlayer(player).getRank().getPriority() <= required_rank.getPriority()) {

			can_preform = true;

		} else if (can_preform == false) {

			if (spit_error_message == true) {
				player.sendMessage(ChatColor.RED + "You are not allowed to do this! You must have "
						+ required_rank.getPrefix().replaceAll("§l", "") + ChatColor.RESET + ChatColor.RED
						+ " or above to do this!");
			}
		}
		return can_preform;

	}

	public static boolean hasRank(Player player, Rank rank, boolean spit_error_message) {

		boolean has = false;

		if (PlayerManager.getServerPlayer(player).getRank() == rank) {
			has = true;

		} else if (has == false) {
			if (spit_error_message == true) {
				player.sendMessage(ChatColor.RED + "You are not allowed to do this! You must have "
						+ rank.getPrefix().replaceAll("§l", "") + ChatColor.RESET + ChatColor.RED + "to do this!");
			}
		}

		return has;

	}

}
