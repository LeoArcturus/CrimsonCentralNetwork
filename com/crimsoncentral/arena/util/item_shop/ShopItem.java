package com.crimsoncentral.arena.util.item_shop;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShopItem {

	private String name;
	private ItemStack shop_item;
	private Material m;
	private int ammount;
	private ArrayList<ItemStack> items = new ArrayList<ItemStack>();

	public ShopItem(String name, ItemStack shop_item, Material m, int ammount, ItemStack... items) {
		this.name = name;
		this.shop_item = shop_item;
		this.m = m;
		this.ammount = ammount;
		for (ItemStack i : items) {

			this.items.add(i);
		}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Material getM() {
		return m;
	}

	public void setM(Material m) {
		this.m = m;
	}

	public int getAmmount() {
		return ammount;
	}

	public void setAmmount(int ammount) {
		this.ammount = ammount;
	}

	enum ItemType {

		HELMET, CHESTPLATE, LEGGINGS, BOOTS
	}

	public void makePurchase(Player p) {

		HashMap<Material, ItemType> items = new HashMap<Material, ItemType>();

		items.put(Material.DIAMOND_HELMET, ItemType.HELMET);
		items.put(Material.DIAMOND_CHESTPLATE, ItemType.CHESTPLATE);
		items.put(Material.DIAMOND_LEGGINGS, ItemType.LEGGINGS);
		items.put(Material.DIAMOND_BOOTS, ItemType.BOOTS);

		items.put(Material.GOLD_HELMET, ItemType.HELMET);
		items.put(Material.GOLD_CHESTPLATE, ItemType.CHESTPLATE);
		items.put(Material.GOLD_LEGGINGS, ItemType.LEGGINGS);
		items.put(Material.GOLD_BOOTS, ItemType.BOOTS);

		items.put(Material.IRON_HELMET, ItemType.HELMET);
		items.put(Material.IRON_CHESTPLATE, ItemType.CHESTPLATE);
		items.put(Material.IRON_LEGGINGS, ItemType.LEGGINGS);
		items.put(Material.IRON_BOOTS, ItemType.BOOTS);

		items.put(Material.CHAINMAIL_HELMET, ItemType.HELMET);
		items.put(Material.CHAINMAIL_CHESTPLATE, ItemType.CHESTPLATE);
		items.put(Material.CHAINMAIL_LEGGINGS, ItemType.LEGGINGS);
		items.put(Material.CHAINMAIL_BOOTS, ItemType.BOOTS);

		items.put(Material.LEATHER_HELMET, ItemType.HELMET);
		items.put(Material.LEATHER_CHESTPLATE, ItemType.CHESTPLATE);
		items.put(Material.LEATHER_LEGGINGS, ItemType.LEGGINGS);
		items.put(Material.LEATHER_BOOTS, ItemType.BOOTS);

		items.put(Material.ELYTRA, ItemType.CHESTPLATE);

		boolean imMatch = false;
		for (ItemStack im : p.getInventory().getContents()) {
			if (im != null && im.getType() == m && im.getAmount() >= ammount) {

				for (ItemStack item : this.items) {
					if (items.containsKey(im.getType()) && items.get(im.getType()) != null) {
						if (items.get(im.getType()) == ItemType.HELMET) {
							p.getInventory().setHelmet(item);
						} else if (items.get(im.getType()) == ItemType.CHESTPLATE) {
							p.getInventory().setChestplate(item);
						} else if (items.get(im.getType()) == ItemType.LEGGINGS) {
							p.getInventory().setLeggings(item);
						} else if (items.get(im.getType()) == ItemType.BOOTS) {
							p.getInventory().setBoots(item);
						} else {
							p.getInventory().addItem(item);
						}

					} else {

						p.getInventory().addItem(item);
					}
				}
				p.sendMessage(ChatColor.GREEN + "You have purchased, " + ChatColor.GOLD + name);
				p.getInventory().removeItem(new ItemStack(m, ammount));
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HARP, 2, 2);
				imMatch = true;
				break;
			} else if (im == null) {

			} else if (im.getType() != m) {

			}
		}
		if (imMatch != true) {
			p.sendMessage(ChatColor.RED + "You do not have enough" + Character.toUpperCase(m.name().charAt(0))
					+ m.name().substring(1).toLowerCase() + "to pruchase this item");
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASEDRUM, 1, 1);
			p.closeInventory();
		}

	}

	public ArrayList<ItemStack> getItems() {
		return items;
	}

	public void addItem(ItemStack item) {
		this.items.add(item);
	}

	public ItemStack getShopItem() {
		
		return this.shop_item;
	}
}
