package com.crimsoncentral.arena.util.kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.ArenaEvents;
import com.crimsoncentral.arena.util.kits.Kit.KitSlotType;

public class KitInventory {

	public Inventory inventory;
	public ItemStack opener;
	public HashMap<Kit, ItemStack> kits = new HashMap<Kit, ItemStack>();
	public HashMap<Player, Kit> player_kits = new HashMap<Player, Kit>();

	public KitInventory(Inventory inventory) {

		this.inventory = inventory;

	}

	public Kit getPlayersKit(Player player) {
		Kit kit = null;
		Iterator<Entry<Player, Kit>> it1 = player_kits.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<Player, Kit> pair = it1.next();

			if (pair.getKey() == player) {

				kit = pair.getValue();
			}

		}

		return kit;

	}

	public Kit getKitFromString(String name) {
		Kit kit = null;
		Iterator<Entry<Kit, ItemStack>> it1 = kits.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<Kit, ItemStack> pair = it1.next();

			if (pair.getKey().kit_name.equalsIgnoreCase(name)) {

				kit = pair.getKey();
			}

		}

		return kit;

	}

	public void openInventory(Player player) {

		player.openInventory(inventory);

	}

	public void registerKitInventory() {

		ArenaEvents.kit_selector_items.put(opener, this);
		ArenaEvents.kit_selectors.put(inventory, this);
	}

	public void givePlayerKits(Arena a, ArrayList<Player> players) {

		for (Player p : players) {
			p.getInventory().clear();
			if (player_kits.get(p) != null && a.getWorld() == p.getWorld()) {

				Iterator<Entry<Integer, ItemStack>> it = player_kits.get(p).items.entrySet().iterator();
				while (it.hasNext()) {
					Entry<Integer, ItemStack> pair1 = it.next();
					if (pair1.getValue() != null)
						p.getInventory().setItem(pair1.getKey(), pair1.getValue());

				}

				Iterator<Entry<KitSlotType, ItemStack>> it2 = player_kits.get(p).item_slots.entrySet().iterator();
				while (it2.hasNext()) {
					Entry<KitSlotType, ItemStack> pair1 = it2.next();
					if (pair1.getValue() != null)
						if (pair1.getKey() == KitSlotType.BOOTS) {
							p.getInventory().setBoots(pair1.getValue());
						} else if (pair1.getKey() == KitSlotType.LEGGINGS) {
							p.getInventory().setLeggings(pair1.getValue());
						} else if (pair1.getKey() == KitSlotType.CHESTPLATE) {
							p.getInventory().setChestplate(pair1.getValue());
						} else if (pair1.getKey() == KitSlotType.HELMET) {
							p.getInventory().setHelmet(pair1.getValue());
						}
				}

			} else if (a.getWorld() == p.getWorld()) {

				playerSelectKit(p, getKitFromString("Default"));

				if (player_kits.get(p) != null && a.getWorld() == p.getWorld()) {

					Iterator<Entry<Integer, ItemStack>> it = player_kits.get(p).items.entrySet().iterator();
					while (it.hasNext()) {
						Entry<Integer, ItemStack> pair1 = it.next();
						if (pair1.getValue() != null)
							p.getInventory().setItem(pair1.getKey(), pair1.getValue());

					}

					Iterator<Entry<KitSlotType, ItemStack>> it2 = player_kits.get(p).item_slots.entrySet().iterator();
					while (it2.hasNext()) {
						Entry<KitSlotType, ItemStack> pair1 = it2.next();
						if (pair1.getValue() != null)
							if (pair1.getKey() == KitSlotType.BOOTS) {
								p.getInventory().setBoots(pair1.getValue());
							} else if (pair1.getKey() == KitSlotType.LEGGINGS) {
								p.getInventory().setLeggings(pair1.getValue());
							} else if (pair1.getKey() == KitSlotType.CHESTPLATE) {
								p.getInventory().setChestplate(pair1.getValue());
							} else if (pair1.getKey() == KitSlotType.HELMET) {
								p.getInventory().setHelmet(pair1.getValue());
							}
					}

					p.sendMessage(ChatColor.GREEN + "Your kit was auto selected to " + ChatColor.GRAY + "Kit Default"
							+ ChatColor.GREEN + " because you didn't choose a kit!");
				} else {

					p.sendMessage(ChatColor.RED
							+ "We ran into an error! We meant to give you the default kit but something went wrong...");
				}

			}

		}
	}

	public Kit getKit(ItemStack clicked_item) {

		Kit kit = null;

		Iterator<Entry<Kit, ItemStack>> it1 = kits.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<Kit, ItemStack> pair = it1.next();

			if (pair.getValue() == clicked_item) {

				kit = pair.getKey();

			}
		}

		return kit;
	}

	public void addKit(Kit kit, ItemStack display_item) {

		inventory.addItem(display_item);
		kits.put(kit, display_item);
	}

	public void playerSelectKit(Player player, Kit kit) {

		player_kits.put(player, kit);

	}

}
