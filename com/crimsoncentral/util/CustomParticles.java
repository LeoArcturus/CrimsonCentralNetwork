package com.crimsoncentral.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldServer;

public class CustomParticles {

	public static void createHelix(Location l, int radius, int height, EnumParticle particle) {

		for (double y = 0; y <= height; y += 0.05) {
			double x = radius + Math.cos(y);
			double z = radius + Math.sin(y);
			PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, // particle
					// type.
					true, // true
					(float) (l.getBlockX() + x), // x coordinate
					(float) (l.getBlockY() + y), // y coordinate
					(float) (l.getBlockZ() + z), // z coordinate
					0, // x offset
					(float) 1, // y offset
					0, // z offset
					0, // speed
					100 // number of particles
			);

			for (Player online : Bukkit.getOnlinePlayers()) {

				if (online.getWorld() == l.getWorld()) {
					((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
				}
			}
		}
	}

	public static void createFakeRotatingPlayer(Location l, int radius, int height, Player p) {

		for (double y = p.getLocation().getY(); y <= height; y += 0.05) {
			double x = radius + Math.cos(y);
			double z = radius + Math.sin(y);
			PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
			MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();

			WorldServer nmsWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();

			EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld,
					new GameProfile(connection.getPlayer().getProfile().getId(),
							ChatColor.YELLOW + "" + ChatColor.BOLD + "BANNABLE"),
					new PlayerInteractManager(nmsWorld));
			npc.setCustomNameVisible(true);
			npc.setInvisible(false);
			npc.setPositionRotation(x, y, z, l.getYaw(), l.getPitch());

			connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc));

			connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));

			connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));

			p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "NPC LOCATION: " + ChatColor.GREEN + x + ", " + y
					+ ", " + z);
		}
	}

}
