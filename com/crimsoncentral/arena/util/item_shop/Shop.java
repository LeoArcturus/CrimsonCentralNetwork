package com.crimsoncentral.arena.util.item_shop;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class Shop {

	private String name;
	private String display_name;

	private Inventory main_shop_page = null;

	private HashMap<Integer, ShopPage> pages = new HashMap<Integer, ShopPage>();

	public Shop(String name, String display_name, int main_page_rows) {

		this.setName(name);
		this.setDisplay_name(display_name);
		this.main_shop_page = (Bukkit.createInventory(null, main_page_rows * 9, display_name));

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

	public Inventory getMainShopPage() {
		return main_shop_page;
	}

	public HashMap<Integer, ShopPage> getPages() {
		return pages;
	}

	public void addPage(Integer page_number, ShopPage page) {
		pages.put(page_number, page);
		main_shop_page.setItem(page_number, page.getDisplayItem());
	}

}
