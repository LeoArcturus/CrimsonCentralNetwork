package com.crimsoncentral.arena.util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;

public class Generator {

	private Location location;
	private ItemStack item_stack;
	private int tier;
	private int spawn_interval;
	private boolean display;
	private String display_text;

	private int time_until_spawn;
	private Hologram h;
	private Hologram h2;

	private EnumParticle ep;

	private PacketPlayOutWorldParticles packet = null;

	private boolean active;

	public Generator(Location location, ItemStack item_stack, int tier, int spawn_interval, boolean display,
			String display_text, EnumParticle e) {

		this.location = location;
		this.item_stack = item_stack;
		this.spawn_interval = spawn_interval;
		this.display = display;
		this.display_text = display_text;
		this.setTimeUntilSpawn(spawn_interval);
		this.ep = e;
		if (display == true) {

			h = new Hologram(display_text);
			h2 = new Hologram(
					ChatColor.WHITE + "Spawns in " + ChatColor.GREEN + time_until_spawn + ChatColor.WHITE + " seconds");
			h.setNeedsUpdating(true);
			h2.setNeedsUpdating(true);

		}

		packet = new PacketPlayOutWorldParticles(ep, true, location.getBlockX(), location.getBlockY(),
				location.getBlockZ(), 0, 0, 0, 1, 2_500, null);
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public ItemStack getItemStack() {
		return item_stack;
	}

	public void setItemStack(ItemStack item_stack) {
		this.item_stack = item_stack;
	}

	public int getSpawnInterval() {
		return spawn_interval;
	}

	public void setSpawnInterval(int spawn_interval) {
		this.spawn_interval = spawn_interval;
	}

	public boolean isDisplay() {
		return display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public String getDisplayText() {
		return display_text;
	}

	public void setDisplayText(String display_text) {
		this.display_text = display_text;
	}

	public int getTimeUntilSpawn() {
		return time_until_spawn;
	}

	public void setTimeUntilSpawn(int time_until_spawn) {
		this.time_until_spawn = time_until_spawn;
	}

	public void check() {

		if (time_until_spawn <= spawn_interval) {
			spawn();
		}
	}

	public void spawn() {
		location.getWorld().dropItemNaturally(location, item_stack);

		if (ep != null) {

			for (Player p : location.getWorld().getPlayers()) {

				((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);

			}
		}

		time_until_spawn = spawn_interval;

	}

	public void updateDisplay() {
		String numeral = "I";
		if (tier == 2) {
			numeral = "II";
		}
		if (tier == 3) {
			numeral = "III";
		}
		if (tier == 4) {
			numeral = "IV";
		}
		if (tier == 5) {
			numeral = "VI";
		}

		h = new Hologram(display_text + "" + ChatColor.DARK_RED + numeral);
		h2 = new Hologram(
				ChatColor.WHITE + "Spawns in " + ChatColor.GREEN + time_until_spawn + ChatColor.WHITE + " seconds");
	}

	public void start() {

		active = true;

	}

	public void stop() {

		active = false;
	}

	public boolean getActivity() {

		return active;
	}

	public int getTier() {
		return tier;
	}

	public void setTier(int tier) {
		this.tier = tier;
	}
}
