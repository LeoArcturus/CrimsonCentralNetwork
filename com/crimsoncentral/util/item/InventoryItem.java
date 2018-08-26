package com.crimsoncentral.util.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryItem extends ActionItem {

	private Inventory inventory;
	
	public InventoryItem(String name, ItemStack is) {
		super(name, is);
		// TODO Auto-generated constructor stub
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	@Override
	public void preform(Player player) {
		player.openInventory(getInventory());
		
	}

}
