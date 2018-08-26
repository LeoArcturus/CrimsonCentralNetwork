package com.crimsoncentral.arena.util.item_shop;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopPage {

	private String name;
	private ItemStack display_item;
	private String display_name;
	private Inventory page_inventory = null;
	private HashMap<Integer, ShopItem> items = new HashMap<Integer, ShopItem>();

	public ShopPage(String name, ItemStack display_item, String display_name, int number_of_rows) {

		this.name = name;
		this.display_item = display_item;
		this.display_name = display_name;
		page_inventory = (Bukkit.createInventory(null, number_of_rows * 9, display_name));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplay_name() {
		return display_name;
	}

	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}

	public Inventory getPageInventory() {
		return page_inventory;
	}

	public HashMap<Integer, ShopItem> getItems() {
		return items;
	}

	public void addItem(Integer slot, ShopItem item) {
		items.put(slot, item);
		page_inventory.setItem(slot, item.getShopItem());
	}

	public ItemStack getDisplayItem() {
		return display_item;
	}

	public void setDisplayItem(ItemStack display_item) {
		this.display_item = display_item;
	}

}
