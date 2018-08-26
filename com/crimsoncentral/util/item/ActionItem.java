package com.crimsoncentral.util.item;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ActionItem {

	private String name;
	private Type type;
	private ItemStack is;

	public abstract void preform(Player player);

	public static enum Type {

		OPEN_INVENTORY, COMMAND_RUN, SERVER_CONNECT, OTHER
	}

	public ActionItem(String name, ItemStack is) {

		this.name = name;

		this.is = is;
		ActionItemManager.addItem(name, this);

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public ItemStack getIs() {
		return is;
	}

	public void setIs(ItemStack is) {
		this.is = is;
	}

	

}
