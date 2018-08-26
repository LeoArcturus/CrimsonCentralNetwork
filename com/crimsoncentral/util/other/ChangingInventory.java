package com.crimsoncentral.util.other;

import org.bukkit.inventory.Inventory;

public abstract class ChangingInventory {

	private String name;
	private int interval;
	private int time;
	private Inventory inventory;
	public abstract void change();

	public ChangingInventory(String name) {

		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

}
