package com.crimsoncentral.util.other;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import com.crimsoncentral.Main;

public class OtherUtil {
	static int i = 0;
	static int task;
	static int current = 0;

	public static enum Rarity {

		COMMON, UNCOMMON, RARE, EPIC, LEGENDARY
	}

	public static void makeCircle(final Entity e, int time, final int radius) {

		final int precision = 12;
		i = 0;
		current = 0;
		task = Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			public void run() {
				++i;
				double p1 = (current * Math.PI) / (precision / 2);
				double p2 = (((current == 0) ? precision : current - 1) * Math.PI) / (precision / 2);

				double x1 = Math.cos(p1) * radius;
				double x2 = Math.cos(p2) * radius;
				double z1 = Math.sin(p1) * radius;
				double z2 = Math.sin(p2) * radius;

				Vector vec = new Vector(x2 - x1, 0, z2 - z1);
				e.setVelocity(vec);
				current++;
				if (current > precision) {
					current = 0;
				}

				if (i / 4 >= time) {
					Bukkit.getScheduler().cancelTask(task);
				}
			}
		}, 1, 5);
	}

	public static Player nearestPlayer(ArrayList<Player> players, Location loc) {

		Player nearest = null;

		ArrayList<Player> removals = new ArrayList<Player>();

		for (Player p : players) {
			for (Player p1 : players) {

				if (p.getLocation().distance(loc) <= p1.getLocation().distance(loc)) {

					removals.add(p1);
				}

			}
		}

		for (Player p : removals) {

			players.remove(p);

		}

		for (Player p : players) {

			nearest = p;
			break;

		}

		return nearest;

	}

	private static HashMap<Player, Integer> cc = new HashMap<Player, Integer>();

	private static HashMap<Player, String> sn = new HashMap<Player, String>();

	public static void flashScoreboardTitle() {

		for (Player p : Bukkit.getOnlinePlayers()) {

			Scoreboard s = p.getScoreboard();
			if (s != null) {
				String name = s.getObjective(DisplaySlot.SIDEBAR).getDisplayName().replace("§0", "").replace("§1", "")
						.replace("§2", "").replace("§3", "").replace("§4", "").replace("§5", "").replace("§6", "")
						.replace("§7", "").replace("§8", "").replace("§9", "").replace("§a", "").replace("§b", "")
						.replace("§c", "").replace("§d", "").replace("§e", "").replace("§f", "").replace("§k", "")
						.replace("§l", "").replace("§m", "").replace("§n", "").replace("§o", "").replace("§r", "");

				if (sn.get(p) == null || cc.get(p) == null || !sn.get(p).equalsIgnoreCase(name)) {

					sn.put(p, name);
					cc.put(p, 0);

				}

				int current = cc.get(p);
				if (current <= name.length() - 1) {
					if (current == 0) {

						name = ChatColor.GOLD + "" + ChatColor.BOLD + name.charAt(current) + ChatColor.YELLOW + ""
								+ ChatColor.BOLD + getBetweenChars(current + 1, name.length(), name);

					} else {
						name = ChatColor.YELLOW + "" + ChatColor.BOLD + getBetweenChars(0, current - 1, name)
								+ ChatColor.GOLD + ChatColor.BOLD + name.charAt(current) + ChatColor.YELLOW + ""
								+ ChatColor.BOLD + getBetweenChars(current + 2, name.length(), name);

					}
					Bukkit.broadcastMessage(name);
				} else {

					if (current <= name.length() + 10 && current <= name.length() + 25) {

						name = ChatColor.GOLD + "" + ChatColor.BOLD + name;
					} else if (current <= name.length() + 26 && current <= name.length() + 30) {
						System.out.println("4");
						name = ChatColor.WHITE + "" + ChatColor.BOLD + name;
					} else {

						name = ChatColor.YELLOW + "" + ChatColor.BOLD + name;
						if (current == name.length() + 130) {
							System.out.println("6");
							cc.put(p, -1);
						}

					}

				}

				cc.put(p, current + 1);
				s.getObjective(DisplaySlot.SIDEBAR).setDisplayName(name);

			}

		}
	}

	public static String getBetweenChars(int first, int second, String string) {

		String s = "";

		int mn = first;
		int mx = second;

		if (second < first) {
			mn = second;
			mx = first;

		}

		if (mn < 0) {

			mn = 0;
		}

		if (mx > string.length()) {
			mx = string.length();
		}
		for (int i = 0; i < string.length(); i++) {

			if (i <= mn && i <= mx) {
				char c = string.charAt(i);
				s = s + c;
			}

		}

		return s;

	}
	// @SuppressWarnings("unused")
	// public static void fakePlayer(Player p, int time, final int radius) {
	//
	// Player r = null;
	//
	//
	// HashMap<Integer, Player> players = new HashMap<Integer, Player>();
	//
	// for (Player p1 : p.getWorld().getPlayers()) {
	//
	// players.put(players.size() + 1, p1);
	//
	// }
	// Random ran = new Random();
	// int randomNum = ran.nextInt((players.size() - 1) + 1) + 1;
	// r = players.get(randomNum);
	//
	// Location loc = p.getLocation();
	// MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
	// CraftWorld w = (CraftWorld) loc.getWorld();
	// WorldServer nmsWorld = (WorldServer) w.getHandle();
	//
	// EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, new
	// GameProfile(r.getUniqueId(), r.getName()),
	// new PlayerInteractManager(nmsWorld));
	//
	// npc.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
	// loc.getPitch());
	//
	// npc.setCustomNameVisible(false);
	// PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
	//
	// connection.sendPacket(new
	// PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc));
	//
	// connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
	//
	// connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte)
	// (npc.yaw * 256 / 360)));
	//
	// npc.spawnIn(nmsWorld);
	// EntityPlayer e = (EntityPlayer) loc.getWorld().spawnEntity(loc,
	// EntityType.PLAYER);
	//
	//
	// i = 0;
	// current = 0;
	// final int precision = 12;
	// task =
	// Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin,
	// new Runnable() {
	// public void run() {
	// ++i;
	// Location l = p.getLocation();
	// double p1 = (current * Math.PI) / (precision / 2);
	// double p2 = (((current == 0) ? precision : current - 1) * Math.PI) /
	// (precision / 2);
	//
	// double x1 = l.getX() + Math.cos(p1) * radius;
	// double x2 = l.getX() + Math.cos(p2) * radius;
	// double z1 = l.getZ() + Math.sin(p1) * radius;
	// double z2 = l.getZ() + Math.sin(p2) * radius;
	//
	// Vector vec = new Vector(x2 - x1, l.getY() + 1, z2 - z1);
	//
	// ((Entity) e).setVelocity(vec);
	// current++;
	// if (current > precision) {
	// current = 0;
	// }
	//
	// if (i / 4 >= time) {
	// Bukkit.getScheduler().cancelTask(task);
	// }
	// }
	// }, 1, 5);
	// }

}
