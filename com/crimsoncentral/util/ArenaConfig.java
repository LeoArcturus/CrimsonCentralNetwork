package com.crimsoncentral.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ArenaConfig {
	File arenaData;
	public FileConfiguration arenaConfiguration;
	boolean chest_scan = false;
	boolean hunger_games_chest_scan = false;

	private HashMap<String, Object> lines = new HashMap<String, Object>();

	public ArenaConfig(Double mode) {

		try {
			arenaData = File.createTempFile("temp-arena-config-" + mode, ".yml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		arenaData.deleteOnExit();

	}

	public FileConfiguration getPlayerConfig() {
		return arenaConfiguration;
	}

	public ArenaConfig addMultipleSimLines(String args, Object thing, int ammount) {

		for (int i = 1; i <= ammount; i++) {

			String s = args;
			if (s.contains("*")) {

				lines.put(s.replace("*", String.valueOf(i)), thing);

			} else {
				// lines.put(args + "-" + i, thing);
			}

		}
		return this;

	}

	public ArenaConfig addLine(String args, Object thing) {

		lines.put(args, thing);

		return this;
	}

	public ArenaConfig doesChestScan() {
		chest_scan = true;
		hunger_games_chest_scan = false;
		return this;
	}

	public ArenaConfig doesHungerGamesChestScan() {
		hunger_games_chest_scan = true;
		chest_scan = false;
		return this;
	}

	int i;

	private ArrayList<Location> getChests(World world) {

		i = 0;
		ArrayList<Location> chests = new ArrayList<Location>();

		for (Chunk chunk : world.getLoadedChunks()) {
			int bx = chunk.getX() << 4;
			int bz = chunk.getZ() << 4;

			for (int xx = bx; xx < bx + 16; xx++) {
				for (int zz = bz; zz < bz + 16; zz++) {
					for (int yy = 0; yy < 256; yy++) {

						i = i + 1;

						Block b = world.getBlockAt(xx, yy, zz);
						if (b.getType() == Material.CHEST) {
							chests.add(b.getLocation());

						}
					}
				}
			}

		}
		i = 0;
		return chests;

	}

	public String getLine(String string) {
		return arenaConfiguration.getString(string);

	}

	public Location getLocation(String string, World w) {
		String s = arenaConfiguration.getString(string);

		Location loc = new Location(w, 0, 0, 0, 0, 0);
		StringTokenizer st = new StringTokenizer(s);
		if (st.hasMoreTokens() == true) {
			String xs = st.nextToken(", ");
			Double x = Double.parseDouble(xs);
			loc.setX(x);
			if (st.hasMoreTokens() == true) {
				String ys = st.nextToken(", ");
				Double y = Double.parseDouble(ys);
				loc.setY(y);
				if (st.hasMoreTokens() == true) {
					String zs = st.nextToken(", ");
					Double z = Double.parseDouble(zs);
					loc.setZ(z);
					if (st.hasMoreTokens() == true) {
						String yaws = st.nextToken(", ");
						Float yaw = Float.parseFloat(yaws);
						loc.setYaw(yaw);
						if (st.hasMoreTokens() == true) {
							String pits = st.nextToken(", ");
							Float pit = Float.parseFloat(pits);
							loc.setPitch(pit);
						}
					}
				}
			}
		}
		return loc;
	}

	public void save() {

	}

	int q;
	int z;
	int ii = 0;

	public void create(Player p) {

		arenaData = new File(p.getWorld().getWorldFolder() + "/arena-config.yml");

		arenaConfiguration = YamlConfiguration.loadConfiguration(arenaData);

		arenaConfiguration.set("Map-Name", p.getWorld().getName());
		arenaConfiguration.set("World-Time", 0);
		arenaConfiguration.set("World-Weather", false);
		arenaConfiguration.set("Arena-Spawn", "0, 160, 0, 0, 0");
		arenaConfiguration.set("Allowed-Travel-Radius", "200");
		arenaConfiguration.set("Max-Y-Height", "200");

		HashMap<Integer, String> a = new HashMap<Integer, String>();

		HashMap<Integer, String> b = new HashMap<Integer, String>();
		HashMap<Integer, String> c = new HashMap<Integer, String>();

		HashMap<String, Boolean> was = new HashMap<String, Boolean>();

		for (Map.Entry<String, Object> e : lines.entrySet()) {
			if (e.getKey().contains("Team") && e.getKey().contains("Spawn")) {

				if (!e.getKey().replaceAll("[^\\d.]", "").equalsIgnoreCase("")) {

					ii = Integer.valueOf(e.getKey().replaceAll("[^\\d.]", ""));
				} else {

					ii = ii + 1;
				}
				a.put(ii, e.getKey());
			}
		}

		for (Map.Entry<String, Object> e : lines.entrySet()) {

			was.put(e.getKey(), false);
		}

		for (Map.Entry<String, Object> e : lines.entrySet()) {
			if (e.getKey().contains("Pos1")) {
				b.put(Integer.valueOf(e.getKey().replace("Pos1", "").replaceAll("[^\\d.]", "")), e.getKey());
				was.put(e.getKey(), true);
			}
		}

		for (Map.Entry<String, Object> e : lines.entrySet()) {
			if (e.getKey().contains("Pos2")) {
				c.put(Integer.valueOf(e.getKey().replace("Pos2", "").replaceAll("[^\\d.]", "")), e.getKey());
				was.put(e.getKey(), true);
			}
		}

		for (int i = 1; i <= a.size(); ++i) {

			arenaConfiguration.set(a.get(i), lines.get(a.get(i)).toString());

		}

		for (int i = 1; i <= b.size(); ++i) {

			arenaConfiguration.set(b.get(i), lines.get(b.get(i)).toString());

		}

		for (int i = 1; i <= c.size(); ++i) {

			arenaConfiguration.set(c.get(i), lines.get(c.get(i)).toString());

		}

		for (Map.Entry<String, Object> e : lines.entrySet()) {

			if (!e.getKey().contains("Team") && !e.getKey().contains("Spawn") && !e.getKey().contains("Wall")
					&& e.getKey().contains("Pos")) {

				arenaConfiguration.set(e.getKey(), e.getValue().toString());
			}

		}

		for (Map.Entry<String, Boolean> e : was.entrySet()) {

			if (e.getValue() != true) {

				arenaConfiguration.set(e.getKey(), lines.get(e.getKey()));
				was.put(e.getKey(), true);
			}
		}

		if (chest_scan == true) {
			for (Location loc : getChests(p.getWorld())) {
				q++;
				arenaConfiguration.set(q + "-Tier-1-Chest",
						String.valueOf(loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()));

			}
		}

		if (hunger_games_chest_scan == true) {
			for (Location loc : getChests(p.getWorld())) {
				q++;
				if (loc.getBlockY() < 80) {

					arenaConfiguration.set(q + "-Low-Tier-Chest",
							String.valueOf(loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()));
				} else if (loc.getY() < 100) {

					arenaConfiguration.set(q + "-Mid-Tier-Chest",
							String.valueOf(loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()));

				} else {

					arenaConfiguration.set(q + "-High-Tier-Chest",
							String.valueOf(loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ()));

				}

			}
		}

		try {
			getPlayerConfig().save(arenaData);

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			arenaData.createNewFile();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		i = 0;
		q = 0;
	}

}
