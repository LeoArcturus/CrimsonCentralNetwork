package com.crimsoncentral.arena.util.kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Kit {

	public String kit_name;
	public Material display_item;
	public HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
	public HashMap<KitSlotType, ItemStack> item_slots = new HashMap<KitSlotType, ItemStack>();

	public static enum KitSlotType {

		HELMET, CHESTPLATE, LEGGINGS, BOOTS
	}

	public Kit(String kit_name, Material display_item) {
		this.kit_name = kit_name.toLowerCase();
		this.display_item = display_item;
	}

	public void addItem(Integer slot, KitSlotType slot_type, ItemStack item) {

		if (slot_type != null) {
			item_slots.put(slot_type, item);
		} else {
			items.put(slot, item);
		}
	}

	public void createKit(KitInventory inv) {

		ItemStack is = new ItemStack(display_item, 1);
		ArrayList<String> lore = new ArrayList<String>();
		ItemMeta meta = is.getItemMeta();

		meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + kit_name);
		lore.add(ChatColor.WHITE + "" + ChatColor.BOLD + "Items:");
		lore.add(" ");

		Iterator<Entry<KitSlotType, ItemStack>> it = item_slots.entrySet().iterator();
		while (it.hasNext()) {
			Entry<KitSlotType, ItemStack> pair = it.next();

			lore.add(ChatColor.GRAY + "- " + ChatColor.GRAY
					+ pair.getValue().getType().name().replace("_", " ").toLowerCase() + " x"
					+ pair.getValue().getAmount());
		}

		Iterator<Entry<Integer, ItemStack>> it1 = items.entrySet().iterator();
		while (it1.hasNext()) {
			Entry<Integer, ItemStack> pair = it1.next();

			lore.add(ChatColor.GRAY + "- " + ChatColor.GRAY
					+ pair.getValue().getType().name().replace("_", " ").toLowerCase() + " x"
					+ pair.getValue().getAmount());
		}

		lore.add(" ");
		lore.add(ChatColor.GREEN + "Click to Select!");

		meta.setLore(lore);
		is.setItemMeta(meta);
		inv.addKit(this, is);

	}

}
