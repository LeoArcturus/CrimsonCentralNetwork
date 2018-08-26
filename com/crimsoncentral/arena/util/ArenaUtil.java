
package com.crimsoncentral.arena.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.Arena.ArenaStage;
import com.crimsoncentral.arena.ArenaManager;
import com.crimsoncentral.arena.ArenaRegister;
import com.crimsoncentral.game.GameProfile;
import com.crimsoncentral.game.ModeProfile;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.server_player.ServerPlayer;
import com.crimsoncentral.util.other.DefaultFontInfo;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;

public class ArenaUtil {
	static int task;
	static int task2;

	public static void joinArena(Player p, Double game_mode, String map) {
		boolean found = false;

		ArrayList<Arena> arenas = new ArrayList<Arena>();

		ServerPlayer sp = PlayerManager.getServerPlayer(p);

		Iterator<Entry<Arena, Double>> it = ArenaManager.local_arenas.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Arena, Double> pair = it.next();
			if (pair.getValue().doubleValue() == game_mode) {
				if (pair.getKey() != null) {
					if (pair.getKey().isAccpetingNewPlayers() != false
							&& pair.getKey().getPlayers().size() < pair.getKey().getMaxPlayers()
							&& sp.getArena() != pair.getKey()) {

						if (map.equalsIgnoreCase("none")) {
							found = true;
							arenas.add(pair.getKey());
						} else if (pair.getKey().getMapName().equalsIgnoreCase(map)) {
							found = true;
							arenas.add(pair.getKey());
						}
					}
				}
			}

		}

		if (found == true) {
			Arena arena_to_join = null;

			if (arenas.size() > 1) {
				for (Arena a1 : arenas) {

					for (Arena a2 : arenas) {

						if (a1.getPlayers().size() < a2.getPlayers().size()) {

							arenas.remove(a1);
						} else if (a1.getPlayers().size() > a2.getPlayers().size()) {

							arenas.remove(a2);
						} else {

							arenas.remove(a2);
						}

					}

				}

				for (Arena arena : arenas) {
					if (arena != null) {

						arena_to_join = arena;
						break;
					}

				}

			} else {

				for (Arena arena : arenas) {

					arena_to_join = arena;

				}
			}

			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_FLUTE, 2, 2);

			DecimalFormat f = new DecimalFormat("0");

			if (Integer.valueOf(f.format(game_mode)) != 0) {
				p.sendMessage(ChatColor.GOLD + "An arena has been found in that mode, commencing teleport...");
			}
			Arena a = getPlayerArena(p);

			if (a != null) {
				a.quit(p);

			}

			arena_to_join.join(p);

		} else if (found == false) {

			// p.sendMessage(
			// ChatColor.YELLOW + "No arena was found in that mode, creating arena. Please
			// wait a momment...");
			//
			// ByteArrayDataOutput out = ByteStreams.newDataOutput();
			// out.writeUTF("Bungeecord");
			// out.writeUTF("play " + game_mode + ", " + map);
			//
			// p.sendPluginMessage(Main.plugin, "Arena", out.toByteArray());
			//
			// } else {
			String arena_name = decideArenaName();

			Arena arena = createArena(game_mode, arena_name, null, map);

			if (arena != null) {

				p.sendMessage(ChatColor.GREEN + "Joining Local Arena - " + ChatColor.GOLD + arena_name + "...");

				Arena a = sp.getArena();

				if (a != null) {
					a.quit(p);

					if (a.getStage() != ArenaStage.GAME_TIME) {
						if (a.getPlayerTeam(p) != null)
							a.getPlayerTeam(p).players.remove(p, a.getPlayerTeam(p).players.get(p));

					}

				}

				arena.join(p);

				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HARP, 2, 0.5f);

			} else if (arena == null) {

				p.playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_HURT, 2, 0.5f);
				p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "ERROR: " + ChatColor.RESET + ChatColor.WHITE
						+ "(" + game_mode + ") " + ChatColor.RED + "was not found in: " + ChatColor.YELLOW
						+ "com.crimsoncentral.ArenaRegister.games.ObjectType.HashMap" + ChatColor.RED
						+ " If this error persits, please contact a staff member.");

			}

		}

	}

	public static Arena createArena(Double game_mode, String arena_name, byte[] arena_files, String map) {
		Arena new_arena = null;

		Iterator<Entry<Integer, GameProfile>> it = ArenaRegister.game_profiles.entrySet().iterator();
		while (it.hasNext()) {

			Entry<Integer, GameProfile> pair = it.next();
			DecimalFormat f = new DecimalFormat("0");

			if (pair.getValue().getParentId() == Integer.valueOf(f.format(game_mode))) {

				for (ModeProfile p : pair.getValue().getModeProfiles()) {

					try {
						if (p.getArena().getGamemode().equals(game_mode)
								&& pair.getValue().getMode(game_mode).getArena().doesAllowForNewCopies() == true) {

							new_arena = pair.getValue().getMode(game_mode).getArena().clone();
							pair.getValue().registerListeners();
							ArenaManager.addToArenaIds(new_arena);

							if (arena_files == null) {
								new_arena.setWorld(generateRandomMap(new_arena, game_mode, arena_name, map));
							}
							break;
						}
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				break;
			}

		}
		if (new_arena != null) {

			ArenaManager.local_arenas.put(new_arena, game_mode);
			new_arena.setup();

			new_arena.run();

		}
		return new_arena;

	}

	public static void deleteArenaFolders() {

		File server_folder = Bukkit.getServer().getWorldContainer();

		Bukkit.broadcastMessage(centerText(
				ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH + "-------------[" + ChatColor.RESET + ChatColor.WHITE
						+ " DELETED WORLDS " + ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH + "]-------------"));

		for (File f : server_folder.listFiles()) {

			if (f.getName().contains("Arena-")) {

				Bukkit.unloadWorld(Bukkit.getWorld(f.getName()), true);

				Bukkit.getWorlds().remove(Bukkit.getWorld(f.getName()));

				Bukkit.broadcastMessage(
						centerText(ChatColor.GRAY + "- " + ChatColor.RED + f.getName() + ChatColor.GRAY + " -"));
				try {
					org.apache.commons.io.FileUtils.cleanDirectory(f);
					f.delete();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		Bukkit.broadcastMessage(
				centerText(ChatColor.DARK_RED + "" + ChatColor.STRIKETHROUGH + "---------------------------"));

	}

	public static String decideArenaName() {

		int int_id = ThreadLocalRandom.current().nextInt(1, 1000);

		char letter1 = 0;
		char letter2 = 0;
		char letter3 = 0;
		char letter4 = 0;

		final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		final int N = alphabet.length();

		Random r = new Random();

		for (int i = 0; i < 26; i++) {
			letter1 = alphabet.charAt(r.nextInt(N));
		}

		for (int i = 0; i < 26; i++) {
			letter2 = alphabet.charAt(r.nextInt(N));
		}

		for (int i = 0; i < 26; i++) {
			letter3 = alphabet.charAt(r.nextInt(N));
		}

		for (int i = 0; i < 26; i++) {
			letter4 = alphabet.charAt(r.nextInt(N));
		}

		String arena_name = "Arena-" + int_id + "-" + letter1 + letter2 + letter3 + letter4;

		return arena_name;

	}

	public static void sendWorldMessage(Arena a, String s)

	{
		if (a.getWorld().getPlayers().isEmpty() == false) {
			for (Player p : a.getWorld().getPlayers())

			{

				p.sendMessage(s);

			}
		}
	}

	public final static int CENTER_PX = 154;

	public static void sendCenteredWorldMessage(Arena arena, String message) {

		if (arena.getWorld().getPlayers().isEmpty() == false) {
			if (message == null || message.equals(""))
				sendWorldMessage(arena, "");
			message = ChatColor.translateAlternateColorCodes('&', message);

			int messagePxSize = 0;
			boolean previousCode = false;
			boolean isBold = false;

			for (char c : message.toCharArray()) {
				if (c == '§') {
					previousCode = true;
					continue;
				} else if (previousCode == true) {
					previousCode = false;
					if (c == 'l' || c == 'L') {
						isBold = true;
						continue;
					} else
						isBold = false;
				} else {
					DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
					messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
					messagePxSize++;
				}
			}

			int halvedMessageSize = messagePxSize / 2;
			int toCompensate = CENTER_PX - halvedMessageSize;
			int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
			int compensated = 0;
			StringBuilder sb = new StringBuilder();
			while (compensated < toCompensate) {
				sb.append(" ");
				compensated += spaceLength;
			}
			sendWorldMessage(arena, sb.toString() + message);
		}
	}

	public static void sendCenteredPlayerMessage(Player player, String message) {
		if (message == null || message.equals(""))
			player.sendMessage("");
		message = ChatColor.translateAlternateColorCodes('&', message);

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for (char c : message.toCharArray()) {
			if (c == '§') {
				previousCode = true;
				continue;
			} else if (previousCode == true) {
				previousCode = false;
				if (c == 'l' || c == 'L') {
					isBold = true;
					continue;
				} else
					isBold = false;
			} else {
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
		}

		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}
		player.sendMessage(sb.toString() + message);
	}

	public static String centerText(String message) {

		if (message == null || message.equals(""))
			message = "";
		message = ChatColor.translateAlternateColorCodes('&', message);

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for (char c : message.toCharArray()) {
			if (c == '§') {
				previousCode = true;
				continue;
			} else if (previousCode == true) {
				previousCode = false;
				if (c == 'l' || c == 'L') {
					isBold = true;
					continue;
				} else
					isBold = false;
			} else {
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}

		}

		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}
		return (sb.toString() + message);
	}

	public static void playWorldSound(Arena arena, Sound sound, int i, int j) {
		if (arena.getWorld().getPlayers().isEmpty() == false) {
			for (Player player : arena.getPlayers()) {

				player.playSound(player.getLocation(), sound, i, j);

			}
		}
	}

	public static void sendWorldTitle(Arena arena, String s, int fade_in_time, int duration, int fade_out_time)

	{
		if (arena.getWorld().getPlayers().isEmpty() == false) {
			IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + s + "\"}");

			PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
			PacketPlayOutTitle length = new PacketPlayOutTitle(fade_in_time, duration * 20, fade_out_time);
			for (Player p : arena.getPlayers())

			{

				((CraftPlayer) p.getPlayer()).getHandle().playerConnection.sendPacket(title);
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
			}
		}

	}

	public static void sendWorldSubTitle(Arena arena, String s, int fade_in_time, int duration, int fade_out_time)

	{
		if (arena.getWorld().getPlayers().isEmpty() == false) {
			IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + s + "\"}");

			PacketPlayOutTitle subTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE,
					chatSubTitle);
			PacketPlayOutTitle length = new PacketPlayOutTitle(fade_in_time, duration * 20, fade_out_time);

			for (Player p : arena.getPlayers())

			{

				((CraftPlayer) p.getPlayer()).getHandle().playerConnection.sendPacket(subTitle);
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
			}
		}
	}

	public static void sendTitle(Player p, String s, int fade_in_time, int duration, int fade_out_time)

	{

		IChatBaseComponent chatTitle = ChatSerializer.a("{\"text\": \"" + s + "\"}");

		PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
		PacketPlayOutTitle length = new PacketPlayOutTitle(fade_in_time, duration * 20, fade_out_time);
		((CraftPlayer) p.getPlayer()).getHandle().playerConnection.sendPacket(title);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
	}

	public static void sendSubTitle(Player p, String s, int fade_in_time, int duration, int fade_out_time)

	{

		IChatBaseComponent chatSubTitle = ChatSerializer.a("{\"text\": \"" + s + "\"}");

		PacketPlayOutTitle subTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatSubTitle);
		PacketPlayOutTitle length = new PacketPlayOutTitle(fade_in_time, duration * 20, fade_out_time);
		((CraftPlayer) p.getPlayer()).getHandle().playerConnection.sendPacket(subTitle);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(length);
	}

	public static Arena getPlayerArena(Player player) {

		Arena arena = null;

		ServerPlayer p = PlayerManager.getServerPlayer(player);

		if (p != null) {

			arena = p.getArena();
		}

		return arena;
	}

	public static void showFiles(File[] files) {
		for (File file : files) {
			if (file.isDirectory()) {
				System.out.println("Directory: " + file.getName());
				showFiles(file.listFiles()); // Calls same method again.
			} else {
				System.out.println("File: " + file.getName());
			}
		}
	}

	public static World generateRandomMap(Arena arena, Double game_type_id, String arena_name, String map) {

		World target = Bukkit.getServer().createWorld(new WorldCreator(arena_name).type(WorldType.FLAT));

		Bukkit.getServer().unloadWorld(target, false);

		HashMap<Integer, File> maps = new HashMap<Integer, File>();

		DecimalFormat df1 = new DecimalFormat("0");

		String main_dir = df1.format(game_type_id);

		File main_dir_world_folder = null;

		for (File f : (File[]) new File("plugins/Crimson-Central/Maps").listFiles()) {

			if (f.getName().replaceAll("\\D", "").equalsIgnoreCase(main_dir)) {

				main_dir_world_folder = f;
			}

		}
		boolean b = false;

		File[] directoryListing = main_dir_world_folder.listFiles();

		for (File f : directoryListing) {

			if (f.getName().toString().contains("" + game_type_id)) {

				File[] directoryListing2 = f.listFiles();
				for (File f2 : directoryListing2) {
					if (f2.getName().equalsIgnoreCase(map)) {

						maps.put(maps.size() + 1, f2);
						b = true;
						break;

					} else {
						maps.put(maps.size() + 1, f2);
					}
				}

			}
		}
		Random ran = new Random();
		int randomNum = ran.nextInt((maps.size() - 1) + 1) + 1;
		if (b == true) {

			randomNum = 1;
		}

		World world = null;
		Iterator<Entry<Integer, File>> it1 = maps.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<Integer, File> pair = it1.next();
			if (pair.getKey() == randomNum) {

				File map_file = pair.getValue();

				arena.setMapName(pair.getValue().getName());

				copyMap(map_file, target.getWorldFolder());

				world = Bukkit.getServer().createWorld(new WorldCreator(arena_name).type(WorldType.FLAT));

			}

		}

		return world;

	}

	public static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
		Object obj = null;
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		try {
			bis = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bis);
			obj = ois.readObject();
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (ois != null) {
				ois.close();
			}
		}
		return obj;
	}

	public static void copyMap(File source, File target) {
		try {

			ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.lock"));

			if (!ignore.contains(source.getName())) {

				if (source.isDirectory()) {

					if (!target.exists())

						target.mkdirs();

					String files[] = source.list();

					for (String file : files) {

						File srcFile = new File(source, file);

						File destFile = new File(target, file);

						copyMap(srcFile, destFile);

					}

				} else {

					InputStream in = new FileInputStream(source);

					OutputStream out = new FileOutputStream(target);

					byte[] buffer = new byte[1024];

					int length;

					while ((length = in.read(buffer)) > 0)

						out.write(buffer, 0, length);

					in.close();

					out.close();

				}

			}

		} catch (IOException e) {

		}

	}

	public static void dropPlayerInventory(Player p) {

		World w = p.getWorld();

		for (ItemStack is : p.getInventory().getContents()) {

			if (is != null) {
				w.dropItemNaturally(p.getLocation(), is);
			}
		}
		p.getInventory().clear();

	}

	public static ArrayList<Player> getModePlayers(Double mode) {

		ArrayList<Player> players = new ArrayList<Player>();

		Iterator<Entry<Arena, Double>> it = ArenaManager.local_arenas.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Arena, Double> pair = it.next();

			if (pair.getValue() == mode) {

				for (Player p : pair.getKey().getPlayers()) {

					if (p.getWorld() == pair.getKey().getWorld()) {

						players.add(p);
					}

				}

			}

		}

		return players;

	}

	public static ArrayList<Player> getGamePlayers(int mode) {

		ArrayList<Player> players = new ArrayList<Player>();

		DecimalFormat f = new DecimalFormat("0");

		Iterator<Entry<Arena, Double>> it = ArenaManager.local_arenas.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Arena, Double> pair = it.next();

			if (Integer.valueOf(f.format(pair.getValue())) == mode) {

				for (Player p : pair.getKey().getPlayers()) {

					if (p.getWorld() == pair.getKey().getWorld()) {

						players.add(p);
					}

				}

			}

		}

		return players;

	}

}
