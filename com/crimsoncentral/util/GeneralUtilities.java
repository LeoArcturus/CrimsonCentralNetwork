package com.crimsoncentral.util;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import com.crimsoncentral.Main;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldServer;

public class GeneralUtilities {

	public static Main plugin = Main.plugin;

	public static void sendHeaderFooter(Player player, String header, String footer) {
		IChatBaseComponent tabHeader = ChatSerializer.a("{\"text\": \"" + header + "\"}");
		IChatBaseComponent tabFooter = ChatSerializer.a("{\"text\": \"" + footer + "\"}");
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
		try {
			Field headerField = packet.getClass().getDeclaredField("a");
			headerField.setAccessible(true);
			headerField.set(packet, tabHeader);
			headerField.setAccessible(false);
			Field footerField = packet.getClass().getDeclaredField("b");
			footerField.setAccessible(true);
			footerField.set(packet, tabFooter);
			footerField.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	public static ItemStack nameItem(ItemStack item, String name) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack nameItem(ItemStack item, String name, String... lores) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		ArrayList<String> lore = new ArrayList<String>();
		for (String str : lores) {
			lore.add(str);
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack createItemStack(Material type, int amt, String name, ItemFlag itemFlag1, ItemFlag itemFlag2,
			ItemFlag itemFlag3, Enchantment enc1, int lev1, Enchantment enc2, int lev2, Enchantment enc3, int lev3,
			PotionType potion, DyeColor dy, String... lores) {
		ItemStack stack = new ItemStack(type, amt);
		ItemMeta im = stack.getItemMeta();
		im.setDisplayName(name);
		if (itemFlag1 != null) {
			im.addItemFlags(itemFlag1);
		}
		if (itemFlag2 != null) {
			im.addItemFlags(itemFlag2);
		}
		if (itemFlag3 != null) {
			im.addItemFlags(itemFlag3);
		}
		if (enc1 != null) {
			im.addEnchant(enc1, lev1, true);
		}
		if (enc2 != null) {
			im.addEnchant(enc2, lev2, true);
		}
		if (enc3 != null) {
			im.addEnchant(enc3, lev3, true);
		}
		if (potion != null) {
			((PotionMeta) im).setBasePotionData(new PotionData(potion));
		}
		if (dy != null) {
			((BannerMeta) im).setBaseColor(dy);
		}
		ArrayList<String> lore = new ArrayList<String>();
		for (String str : lores) {
			lore.add(str);
		}
		im.setLore(lore);
		stack.setItemMeta(im);
		return stack;
	}

	public static void createNpc(Location l, String name, Player p) {

		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;

		MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer nmsWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();

		EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld,
				new GameProfile(connection.getPlayer().getProfile().getId(), name),
				new PlayerInteractManager(nmsWorld));

		npc.setPositionRotation(l.getBlockX(), l.getBlockX(), l.getBlockZ(), l.getYaw(), l.getPitch());
		npc.setCustomNameVisible(false);

		connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, npc));

		connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));

		connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) (npc.yaw * 256 / 360)));

	}

	public static void createHologram(Location l, Player p, String text) {

		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;

		WorldServer nmsWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();

		EntityArmorStand hologram = new EntityArmorStand(nmsWorld);

		hologram.setLocation(l.getX(), l.getY() + 0.5, l.getZ(), 0, 0);
		hologram.setInvisible(true);
		hologram.setCustomName(text);
		hologram.setCustomNameVisible(true);

		connection.sendPacket(new PacketPlayOutSpawnEntityLiving(hologram));

	}

	public static FireworkMeta generateFireworkMeta(FireworkEffect.Type type, Color primary_color, Color fade_color,
			boolean flicker, boolean trail, int power) {

		Firework fw = (Firework) Bukkit.getWorld("World")
				.spawnEntity(new Location(Bukkit.getWorld("World"), 0, 1000, 0), EntityType.FIREWORK);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.addEffect(FireworkEffect.builder().flicker(flicker).trail(trail).with(type).withColor(primary_color)
				.withFade(fade_color).build());
		fm.setPower(power);

		return fm;
	}

	public static void playServerSound(Sound sound, double v, double p) {

		for (Player p1 : Bukkit.getOnlinePlayers()) {

			p1.playSound(p1.getLocation(), sound, Float.valueOf(String.valueOf(v)), Float.valueOf(String.valueOf(p)));
		}
	}

}
