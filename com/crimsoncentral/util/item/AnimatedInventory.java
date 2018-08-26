package com.crimsoncentral.util.item;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class AnimatedInventory {

	private static HashMap<String, AnimatedInventory> inventories = new HashMap<String, AnimatedInventory>();

	private Player player;
	private Inventory inventory;
	private int tickInterval;
	private int expriesIn = 100;

	public abstract void updateInventory();

	public AnimatedInventory(String name, Player player) {

		this.player = player;
		inventories.put(name.toLowerCase(), this);
	}

	public Player getPlayer() {
		return player;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public int getTickInterval() {
		return tickInterval;
	}

	public void setTickInterval(int tickInterval) {
		this.tickInterval = tickInterval;
	}

	public int getExpriesIn() {
		return expriesIn;
	}

	public void setExpriesIn(int expriesIn) {
		this.expriesIn = expriesIn;
	}

	public static void updateAllInventories(int i) {

		for (Entry<String, AnimatedInventory> e : inventories.entrySet()) {
			AnimatedInventory ai = e.getValue();
			if (i % ai.getTickInterval() == 0) {
				if (ai.getPlayer().getOpenInventory() == ai.getInventory()) {

					ai.updateInventory();
				} else {
					ai.setExpriesIn(ai.getExpriesIn() - 1);

					if (ai.getExpriesIn() <= 0) {

						inventories.remove(e.getKey(), ai);
					}
				}
			}
		}
	}

	public static AnimatedInventory getInventory(String name) {

		return inventories.get(name.toLowerCase());

	}

}
