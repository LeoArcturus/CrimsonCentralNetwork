package com.crimsoncentral.arena.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minecraft.server.v1_12_R1.WorldServer;

public class Hologram extends Thread implements Listener {

	public static HashMap<Hologram, World> holograms = new HashMap<Hologram, World>();

	private String name;

	private boolean needsUpdating = true;

	public ArrayList<Player> specific_visiblity = new ArrayList<Player>();
	public ArrayList<Player> excluded_players = new ArrayList<Player>();

	private String replace_text = "";
	@SuppressWarnings("unused")
	private MinecraftServer nmsServer = null;
	private WorldServer nmsWorld = null;

	public EntityArmorStand holo = null;

	public Hologram(String name) {

		this.name = name;

	}

	public Hologram() {
		// TODO Auto-generated constructor stub
	}

	public Hologram spawn(Location loc) {

		double x = loc.getX();
		double z = loc.getZ();
		double x_center = 0;
		double z_center = 0;

		if (x % 1 == 0) {
			x_center = -0.5;
			if (x < 0) {
				x_center = 0.5;

			}

		}

		if (z % 1 == 0) {
			z_center = -0.5;
			if (z < 0) {
				z_center = 0.5;

			}

		}

		loc.setX(x + x_center);
		loc.setZ(z + z_center);

		CraftWorld w = (CraftWorld) loc.getWorld();
		nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
		nmsWorld = (WorldServer) w.getHandle();

		holo = new EntityArmorStand(nmsWorld);

		holo.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

		holo.setInvisible(true);

		holo.setCustomName(name);
		holo.setCustomNameVisible(true);

		holograms.put(this, loc.getWorld());

		return this;

	}

	public void addSpeficicVisiblity(Player p) {

		specific_visiblity.add(p);

	}


	
	public void ShowPlayerHolograms(Player p) {
		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
		Iterator<Entry<Hologram, World>> it = holograms.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Hologram, World> pair = it.next();

			if (pair.getValue() == p.getWorld()) {
				if (pair.getKey().specific_visiblity.isEmpty() && !pair.getKey().excluded_players.contains(p)) {
					connection.sendPacket(new PacketPlayOutEntityDestroy(pair.getKey().holo.getId()));
					connection.sendPacket(new PacketPlayOutSpawnEntityLiving(pair.getKey().holo));
				} else if (pair.getKey().specific_visiblity.contains(p)
						&& !pair.getKey().excluded_players.contains(p)) {
					connection.sendPacket(new PacketPlayOutEntityDestroy(pair.getKey().holo.getId()));
					connection.sendPacket(new PacketPlayOutSpawnEntityLiving(pair.getKey().holo));
				}
			}

		}

	}

	public void hide(Player p) {

		excluded_players.add(p);

		PacketPlayOutEntityDestroy ppp = new PacketPlayOutEntityDestroy(holo.getId());
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppp);

	}

	public void reShow(Player p) {

		excluded_players.remove(p);
	}

	public void updateName(Player player) {
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutEntityDestroy(holo.getId()));
		connection.sendPacket(new PacketPlayOutSpawnEntityLiving(holo));
	}

	@EventHandler
	public void PlayerChangedWorldEvent(org.bukkit.event.player.PlayerChangedWorldEvent event) {

		ShowPlayerHolograms(event.getPlayer());

	}

	@EventHandler
	public void Login(PlayerJoinEvent event) {

		ShowPlayerHolograms(event.getPlayer());

	}

	public void applyReplacements() {

		String r = replace_text.toString();
		if (r.contains("[") && r.contains("]")) {
			String s = name;

			s = s.substring(s.indexOf("[") + 1);
			s = s.substring(0, s.indexOf("]"));

			if (s.toLowerCase().contains("-gamemode")) {
				replace_text = s;
			}
		}

	}

	public void replaceText() {

	}

	public boolean needsUpdating() {
		return needsUpdating;
	}

	public void setNeedsUpdating(boolean needsUpdating) {
		this.needsUpdating = needsUpdating;
	}

}
