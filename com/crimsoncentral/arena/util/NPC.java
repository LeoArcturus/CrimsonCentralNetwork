package com.crimsoncentral.arena.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldServer;

public abstract class NPC extends Thread implements Listener {

	public static HashMap<NPC, World> npcs = new HashMap<NPC, World>();
	public HashMap<Double, Hologram> holograms = new HashMap<Double, Hologram>();
	public HashMap<Integer, Hologram> hologram_lines = new HashMap<Integer, Hologram>();

	public ArrayList<ArmorStand> stands = new ArrayList<ArmorStand>();

	private String name;
	private boolean is_spawned;

	MinecraftServer nmsServer = null;
	WorldServer nmsWorld = null;

	private static boolean a = false;

	public EntityPlayer npc = null;

	public abstract void perform(Player p);

	public NPC(String name) {

		this.name = name;

	}

	public NPC() {
		// TODO Auto-generated constructor stub
	}

	public NPC addLine(String name, double y_offset, boolean updating) {

		double d = 0.0;

		Iterator<Entry<Double, Hologram>> it = holograms.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Double, Hologram> pair = it.next();

			if (pair.getKey() > d) {
				d = pair.getKey();

			}

		}
		Hologram h = new Hologram(name);
		h.setNeedsUpdating(updating);
		holograms.put(d + y_offset, h);
		hologram_lines.put(hologram_lines.size() + 1, h);
		return this;
	}

	public NPC replaceLine(Integer line_number, String replace_text) {

		hologram_lines.get(line_number).holo.setCustomName(replace_text);
		return this;
	}

	public NPC spawn(Location loc) {

		CraftWorld w = (CraftWorld) loc.getWorld();
		nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
		nmsWorld = (WorldServer) w.getHandle();

		npc = new EntityPlayer(nmsServer, nmsWorld,
				new GameProfile(UUID.fromString("6d859e94-5027-11e8-9c2d-fa7ae01bbebc"), name),
				new PlayerInteractManager(nmsWorld));

		npc.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

		npc.setCustomNameVisible(false);

		

		for (Entry<Double, Hologram> e : holograms.entrySet()) {

			Hologram h = e.getValue();
			h.spawn(new Location(loc.getWorld(), loc.getX(), loc.getY() + e.getKey() + 0.25, loc.getZ()));

		}

		npcs.put(this, loc.getWorld());

		return this;

	}

	int i;

	public void ShowPlayerNPCS(Player p) {

		i = 0;

		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
		Iterator<Entry<NPC, World>> it = npcs.entrySet().iterator();
		while (it.hasNext()) {
			Entry<NPC, World> pair = it.next();

			if (pair.getValue() == p.getWorld()) {
				++i;
				if (i == 3) {

					i = 0;
				}
				pair.getKey().npc.getBukkitEntity().setRemoveWhenFarAway(false);
				connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, pair.getKey().npc));

				connection.sendPacket(new PacketPlayOutNamedEntitySpawn(pair.getKey().npc));

				connection.sendPacket(new PacketPlayOutEntityHeadRotation(pair.getKey().npc,
						(byte) (pair.getKey().npc.yaw * 256 / 360)));
				connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER,
						(((CraftPlayer) pair.getKey().npc.getBukkitEntity()).getHandle())));

				// Main.api.setRandomPlayerSkin(event.getPlayer());

			}

		}

	}

	public static void killAllNPCS() {

		for (Entry<NPC, World> h : npcs.entrySet()) {

			for (ArmorStand s : h.getKey().stands) {
				s.damage(10000);

			}
		}
	}

	public static void killAllNPCS(World w) {

		for (Entry<NPC, World> h : npcs.entrySet()) {
			if (h.getKey().npc.getBukkitEntity().getLocation().getWorld() == w)
				for (ArmorStand s : h.getKey().stands) {
					s.damage(10000);

				}
		}
	}

	public static void spawnNPCS(Player p) {

		if (a != true) {
			for (Entry<NPC, World> h : npcs.entrySet()) {

				ArmorStand a1 = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
				a1.teleport(new Location(h.getKey().npc.getBukkitEntity().getLocation().getWorld(),
						h.getKey().npc.getBukkitEntity().getLocation().getX() + 0.5,
						h.getKey().npc.getBukkitEntity().getLocation().getY(),
						h.getKey().npc.getBukkitEntity().getLocation().getZ() + 0.5));

				ArmorStand a2 = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
				a2.teleport(new Location(h.getKey().npc.getBukkitEntity().getLocation().getWorld(),
						h.getKey().npc.getBukkitEntity().getLocation().getX() - 0.5,
						h.getKey().npc.getBukkitEntity().getLocation().getY(),
						h.getKey().npc.getBukkitEntity().getLocation().getZ() + 0.5));

				ArmorStand a3 = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
				a3.teleport(new Location(h.getKey().npc.getBukkitEntity().getLocation().getWorld(),
						h.getKey().npc.getBukkitEntity().getLocation().getX() + 0.5,
						h.getKey().npc.getBukkitEntity().getLocation().getY(),
						h.getKey().npc.getBukkitEntity().getLocation().getZ() - 0.5));

				ArmorStand a4 = (ArmorStand) p.getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
				a4.teleport(new Location(h.getKey().npc.getBukkitEntity().getLocation().getWorld(),
						h.getKey().npc.getBukkitEntity().getLocation().getX() - 0.5,
						h.getKey().npc.getBukkitEntity().getLocation().getY(),
						h.getKey().npc.getBukkitEntity().getLocation().getZ() - 0.5));

				a1.setVisible(false);
				a2.setVisible(false);
				a3.setVisible(false);
				a4.setVisible(false);

				a1.setInvulnerable(true);
				a2.setInvulnerable(true);
				a3.setInvulnerable(true);
				a4.setInvulnerable(true);

				a1.setGravity(false);
				a2.setGravity(false);
				a3.setGravity(false);
				a4.setGravity(false);

				a1.setCustomName(ArenaUtil.decideArenaName().replace("Arena", "NPC-" + npcs.size()));
				a2.setCustomName(ArenaUtil.decideArenaName().replace("Arena", "NPC-" + npcs.size()));
				a3.setCustomName(ArenaUtil.decideArenaName().replace("Arena", "NPC-" + npcs.size()));
				a4.setCustomName(ArenaUtil.decideArenaName().replace("Arena", "NPC-" + npcs.size()));

				h.getKey().stands.add(a1);
				h.getKey().stands.add(a2);
				h.getKey().stands.add(a3);
				h.getKey().stands.add(a4);

			}
			a = true;
		}

	}

	@EventHandler
	public void PlayerChangedWorldEvent(org.bukkit.event.player.PlayerChangedWorldEvent event) {

		ShowPlayerNPCS(event.getPlayer());

		spawnNPCS(event.getPlayer());
	}

	@EventHandler
	public void Login(PlayerJoinEvent event) {

		ShowPlayerNPCS(event.getPlayer());

		spawnNPCS(event.getPlayer());
	}

	@EventHandler
	public void NpcInteract(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();

		if (e.getRightClicked().getType() != null) {

			boolean c = false;
			for (Entry<NPC, World> h : npcs.entrySet()) {

				for (ArmorStand s : h.getKey().stands) {

					if (e.getRightClicked().getType() == EntityType.ARMOR_STAND
							&& e.getRightClicked().getCustomName().equalsIgnoreCase(s.getCustomName())) {
						e.setCancelled(true);

						h.getKey().perform(p);

						c = true;
						break;
					}

				}

				if (c == true) {

					break;
				}

			}
		}
	}

	@EventHandler
	public void onPunch(EntityDamageByEntityEvent e) {

		if (e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			for (Entry<NPC, World> h : npcs.entrySet()) {

				for (ArmorStand s : h.getKey().stands) {

					if (e.getEntity().getType() == EntityType.ARMOR_STAND
							&& e.getEntity().getCustomName().equalsIgnoreCase(s.getCustomName())) {
						e.setCancelled(true);
						h.getKey().perform(p);
						break;
					}
				}
			}
		}
	}

	public boolean isSpawned() {
		return is_spawned;
	}

}
