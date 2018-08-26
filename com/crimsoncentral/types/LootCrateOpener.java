package com.crimsoncentral.cosmetics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.crimsoncentral.Main;
import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.ArenaEvents;
import com.crimsoncentral.arena.ArenaEvents.BlockedMovementType;
import com.crimsoncentral.arena.util.Hologram;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.server_player.ServerPlayer;

import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minecraft.server.v1_12_R1.WorldServer;

public class LootCrateOpener implements Listener {

	public static HashMap<Location, LootCrateOpener> openers = new HashMap<Location, LootCrateOpener>();

	private HashMap<Integer, Location> rotation_slots = new HashMap<Integer, Location>();

	private Location location;
	private int max_openers;
	private int opener_yaw;

	Hologram name = null;
	Hologram rc = null;

	public LootCrateOpener(Location location, int max_openers, int opener_yaw) {
		if (location != null) {
			setLocation(location);
			setMaxOpeners(max_openers);
			setOpenerYaw(opener_yaw);
			getLocation().setYaw(getOpenerYaw());
			openers.put(getLocation(), this);

			name = new Hologram(ChatColor.YELLOW + "" + ChatColor.BOLD + "LOOT CRATE OPENER")
					.spawn(getLocation().subtract(0, 0.75, 0));
			rc = new Hologram(ChatColor.AQUA + "Right Click").spawn(getLocation().subtract(0, 0.5, 0));

			for (int i = 90; i <= 180; ++i) {

				if (i % 45 == 0) {

					Location l = getLocationAroundCircle(1.5, i / 45);
					if (!(Math.abs(l.getY() - getLocation().getY()) < 1.2 && l.getX() != getLocation().getX()
							&& l.getZ() != getLocation().getZ())) {
						int q = rotation_slots.size() + 1;
						rotation_slots.put(q, l);
						Bukkit.broadcastMessage("" + q);
					}
				}
			}

			for (int i = -179; i <= -90; ++i) {

				if (i % 45 == 0) {

					Location l = getLocationAroundCircle(1.5, i / 45);
					if (!(Math.abs(l.getY() - getLocation().getY()) < 1.2 && l.getX() != getLocation().getX()
							&& l.getZ() != getLocation().getZ())) {
						int q = rotation_slots.size() + 1;
						rotation_slots.put(q, l);
						Bukkit.broadcastMessage("" + q);
					}
				}
			}

			for (int i = -89; i <= 0; ++i) {

				if (i % 45 == 0) {

					Location l = getLocationAroundCircle(1.5, i / 45);
					if (!(Math.abs(l.getY() - getLocation().getY()) < 1.2 && l.getX() != getLocation().getX()
							&& l.getZ() != getLocation().getZ())) {
						int q = rotation_slots.size() + 1;
						rotation_slots.put(q, l);
						Bukkit.broadcastMessage("" + q);
					}
				}
			}
			for (int i = 1; i <= 89; ++i) {

				if (i % 45 == 0) {

					Location l = getLocationAroundCircle(1.5, i / 45);
					if (!(Math.abs(l.getY() - getLocation().getY()) < 1.2 && l.getX() != getLocation().getX()
							&& l.getZ() != getLocation().getZ())) {
						int q = rotation_slots.size() + 1;
						rotation_slots.put(q, l);
						Bukkit.broadcastMessage("" + q);
					}
				}
			}

			Integer w = null;
			Location l = null;

			ArrayList<Double> ys = new ArrayList<Double>();

			for (Entry<Integer, Location> e1 : rotation_slots.entrySet()) {
				ys.add(e1.getValue().getY());

			}

			for (Entry<Integer, Location> e1 : rotation_slots.entrySet()) {
				if (e1.getValue().getY() == ys.indexOf(Collections.min(ys))) {

					w = e1.getKey();
					l = e1.getValue();
				}

			}

			rotation_slots.remove(w, l);

		}
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public int getMaxOpeners() {
		return max_openers;
	}

	public void setMaxOpeners(int max_openers) {
		this.max_openers = max_openers;
	}

	public int getOpenerYaw() {
		return opener_yaw;
	}

	public void setOpenerYaw(int opener_yaw) {
		this.opener_yaw = opener_yaw;
	}

	int task;

	public void switchItemPlaces(Player p, ArrayList<ItemStack> items, HashMap<Integer, EntityArmorStand> stands,
			HashMap<Integer, ItemStack> easi) {

		HashMap<Integer, ItemStack> s = new HashMap<Integer, ItemStack>();
		PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
		for (Entry<Integer, EntityArmorStand> e : stands.entrySet()) {
			s.put(e.getKey(), easi.get(e.getKey()));
		}

		for (Entry<Integer, ItemStack> e : s.entrySet()) {

			ItemStack is = (ItemStack) e.getValue();
			if (is != null) {

				EntityArmorStand eas = (EntityArmorStand) stands.get(nextSlot(e.getKey()));

				connection.sendPacket(
						new PacketPlayOutEntityEquipment(eas.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(is)));
				easi.put(nextSlot(e.getKey()), is);
			}
		}
	}

	private Integer nextSlot(int i) {
		Integer r = 0;

		if (i % rotation_slots.size() != 0) {
			r = i + 1;

		} else {

			r = 1;
		}

		return r;

	}

	@SuppressWarnings("deprecation")
	public void openLootCrate(Player player, LootCrate loot_crate) {

		Location prev_loc = player.getLocation();
		HashMap<Integer, EntityArmorStand> stands = new HashMap<Integer, EntityArmorStand>();
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		items.add(new ItemStack(Material.DIAMOND));
		items.add(new ItemStack(Material.EMERALD));
		items.add(new ItemStack(Material.IRON_INGOT));
		items.add(new ItemStack(Material.GOLD_INGOT));
		items.add(new ItemStack(Material.REDSTONE));
		items.add(new ItemStack(Material.COAL));
		items.add(new ItemStack(Material.DIRT));
		items.add(new ItemStack(351, 1, (short) 4));
		HashMap<Integer, ItemStack> is = new HashMap<Integer, ItemStack>();

		for (ItemStack ii : items) {

			is.put(is.size() + 1, ii);
		}

		name.hide(player);
		rc.hide(player);

		Hologram current_cosmetic = new Hologram(ChatColor.GREEN + "Preparing...")
				.spawn(getLocation().clone().subtract(0, 0.5, 0));
		current_cosmetic.addSpeficicVisiblity(player);

		CraftWorld w = (CraftWorld) getLocation().getWorld();
		
		WorldServer nmsWorld = (WorldServer) w.getHandle();

		HashMap<Integer, ItemStack> easi = new HashMap<Integer, ItemStack>();

		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
			int timer;

			int t = task;

			@Override
			public void run() {

				++timer;
				if (timer == 4) {
					Vector dir = getLocation().clone().getDirection(); // get the player's direction
					dir.multiply(4);

					Location l = getLocation().clone().add(dir);
					l.setYaw(getOpenerYaw() + 180);
					l.setY(l.getY() + 1);
					player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "LOOT CREATE OPENER " + ChatColor.GREEN
							+ ChatColor.BOLD + ">> " + ChatColor.AQUA + "Preparing to open your loot crates...");

					player.teleport(l);

					player.setGravity(false);

					ArenaEvents.freezePlayer(player, BlockedMovementType.ALL);

					for (Entry<Integer, Location> e : rotation_slots.entrySet()) {

						EntityArmorStand as = new EntityArmorStand(nmsWorld);
						as.setLocation(e.getValue().getX(), e.getValue().getY(), e.getValue().getY(), getOpenerYaw(),
								0);
						as.setCustomName("" + e.getKey());
						as.setCustomNameVisible(true);
						as.setNoGravity(true);

						((CraftPlayer) player).getHandle().playerConnection
								.sendPacket(new PacketPlayOutSpawnEntityLiving(as));
						((CraftPlayer) player).getHandle().playerConnection
								.sendPacket(new PacketPlayOutEntityEquipment(as.getId(), EnumItemSlot.HEAD,
										CraftItemStack.asNMSCopy(new ItemStack(Material.BARRIER))));

						as.setInvisible(true);
						as.setSmall(true);
						easi.put(e.getKey(), new ItemStack(Material.BARRIER));
						stands.put(e.getKey(), as);
					}

				} else if (timer == 12) {
					player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 2);
				} else if (timer == 18) {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1, (float) 1.8);
				} else if (timer == 20) {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_CHIME, 1, (float) 1.4);

					((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(
							stands.get(1).getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(items.get(1))));
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(
							stands.get(7).getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(items.get(7))));
				} else if (timer == 23) {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_CHIME, 1, (float) 1.6);
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(
							stands.get(2).getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(items.get(2))));
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(
							stands.get(6).getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(items.get(6))));
				} else if (timer == 26) {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_FLUTE, 1, (float) 1.7);
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(
							stands.get(3).getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(items.get(3))));
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(
							stands.get(5).getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(items.get(5))));
				} else if (timer == 27) {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_FLUTE, 1, (float) 1.9);
					((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityEquipment(
							stands.get(4).getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(items.get(4))));

				} else if (timer >= 28 && timer <= 57) {

					if (timer % 2 == 0) {
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1, (float) 1.5);
						switchItemPlaces(player, items, stands, easi);
						current_cosmetic.holo.setCustomName(stands.get(7).getName());
						current_cosmetic.updateName(player);
					} else if (timer % 2 == 1) {
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1, (float) 1.0);
						switchItemPlaces(player, items, stands, easi);
						current_cosmetic.holo.setCustomName(stands.get(7).getName());
						current_cosmetic.updateName(player);
					}

				} else if (timer == 59) {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1, (float) 0.7);
					switchItemPlaces(player, items, stands, easi);
					current_cosmetic.holo.setCustomName(stands.get(7).getName());
					current_cosmetic.updateName(player);
				} else if (timer == 62) {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 1, (float) 0.5);
					switchItemPlaces(player, items, stands, easi);
					current_cosmetic.holo.setCustomName(stands.get(7).getName());
					current_cosmetic.updateName(player);
				} else if (timer == 65) {
					player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_BLAST, 2, (float) 2);
					player.playEffect(getLocation().add(0, 1, 0), Effect.EXPLOSION, 5);

				} else if (timer == 72) {
					current_cosmetic.hide(player);
					name.reShow(player);
					rc.reShow(player);
					player.teleport(prev_loc);
					player.setGravity(true);
					ArenaEvents.frozen_players.remove(player, ArenaEvents.frozen_players.get(player));
					Bukkit.getScheduler().cancelTask(t);
				}

			}

		}, 0, 4);
	}

	private Location getLocationAroundCircle(double radius, double angleInRadian) {
		double x = getLocation().getX();
		double z = getLocation().getZ() + radius * Math.sin(angleInRadian * (180 / Math.PI));
		double y = getLocation().getY() + 1 + radius * Math.cos(angleInRadian * (180 / Math.PI));

		Location loc = new Location(getLocation().getWorld(), x, y, z);
		Vector difference = getLocation().toVector().clone().subtract(loc.toVector()); // this sets the returned
																						// location's direction toward
																						// the center of the circle
		loc.setDirection(difference);
		loc.setDirection(difference);

		return loc;
	}

	@EventHandler
	public void OpenEvent(PlayerInteractEvent e) {

		Player p = e.getPlayer();
		ServerPlayer sp = PlayerManager.getServerPlayer(p);
		Arena a = sp.getArena();

		if (a != null) {
			Location location = null;
			Block b = e.getClickedBlock();
			if (e.getClickedBlock() != null) {

				location = b.getLocation();

			}

			if (location != null) {

				for (Entry<Location, LootCrateOpener> ef : openers.entrySet()) {

					if (ef.getKey().distance(location) <= 1.5) {

						LootCrateOpener lco = ef.getValue();

						if (lco != null) {

							lco.openLootCrate(p, null);
							e.setCancelled(true);
						}

						break;

					}

				}

			}

		}

	}

}
