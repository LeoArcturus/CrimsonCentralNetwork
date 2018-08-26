package com.crimsoncentral.util;

import java.io.File;
import java.io.IOException;

import java.util.StringTokenizer;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Config {

	public File arenaData;
	public FileConfiguration arenaConfiguration;

	public Config(Player p) {
		arenaData = new File(p.getWorld().getWorldFolder() + "/arena-config.yml");
		arenaConfiguration = YamlConfiguration.loadConfiguration(arenaData);
	}

	public Config(File file) {
		arenaData = file;
		arenaConfiguration = YamlConfiguration.loadConfiguration(arenaData);
	}

	public void createArenaConfig() {
		try {
			arenaData.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FileConfiguration getPlayerConfig() {
		return arenaConfiguration;
	}

	public void saveArenaConfig() {
		try {
			getPlayerConfig().save(arenaData);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createDefaults(String tag) {
		arenaConfiguration.set("Map-Name", tag);
		arenaConfiguration.set("World-Time", 0);
		arenaConfiguration.set("World-Weather", false);
		arenaConfiguration.set("Arena-Spawn", "0, 0, 0, 0, 0");
	
		saveArenaConfig();
	}

	public void arenaConfig() {
		try {
			arenaData.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getTag() {
		return arenaConfiguration.getString("World-Tag");
	}

	public int getTime() {
		return arenaConfiguration.getInt("World-Time");
	}

	public boolean getWeather() {
		return arenaConfiguration.getBoolean("World-Weather");
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

	public void addLine(String string, String token) {
		arenaConfiguration.set(string, token);

		saveArenaConfig();
	}

}
