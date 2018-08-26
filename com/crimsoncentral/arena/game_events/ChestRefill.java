package com.crimsoncentral.arena.game_events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftChest;
import org.bukkit.inventory.ItemStack;

public class ChestRefill {

	public HashMap<ItemStack, HashMap<Integer, Integer>> items = new HashMap<ItemStack, HashMap<Integer, Integer>>();

	public ArrayList<Location> chest_locations = new ArrayList<Location>();;
	public int item_prob_min;
	public int item_prob_max;

	public int aprox_max_slots_to_fill;

	public ChestRefill(int item_prob_min, int item_prob_max, int aprox_max_slots_to_fill) {

		this.item_prob_min = item_prob_min;
		this.item_prob_max = item_prob_max;

		this.aprox_max_slots_to_fill = aprox_max_slots_to_fill;

	}

	public void addItem(ItemStack item_stack, Integer min_chance, Integer max_chance) {

		HashMap<Integer, Integer> numbers = new HashMap<Integer, Integer>();
		numbers.put(min_chance, max_chance);

		items.put(item_stack, numbers);

	}

	public void addChest(Location l) {

		chest_locations.add(l);
	}

	public void refill() {
		if (chest_locations.size() >= 1) {
			for (Location l : chest_locations) {
				if (l.getBlock().getType() == Material.CHEST) {

					Chest APIChest = (Chest) l.getBlock().getState();

					CraftChest BukkitChest = (CraftChest) APIChest;
					int i = aprox_max_slots_to_fill;
					int can_fill = 1;

					for (int slot = 0; slot < 27; slot++) {

						if (i > 0) {
							int should_fill = ThreadLocalRandom.current().nextInt(can_fill, (27 / i));

							int chance_of_subtraction = ThreadLocalRandom.current().nextInt(1, 3);

							int subtract_ammount = 1;
							if (chance_of_subtraction == 1) {

								subtract_ammount = 0;

							}

							if (should_fill == 1) {

								i = i - subtract_ammount;

								if (i <= 0) {

									i = 0;
									can_fill = 0;
								}

								int item_number = ThreadLocalRandom.current().nextInt(item_prob_min, item_prob_max);

								Iterator<Entry<ItemStack, HashMap<Integer, Integer>>> it = items.entrySet().iterator();
								while (it.hasNext()) {
									Entry<ItemStack, HashMap<Integer, Integer>> pair = it.next();

									Iterator<Entry<Integer, Integer>> it1 = pair.getValue().entrySet().iterator();

									while (it1.hasNext()) {
										Entry<Integer, Integer> pair2 = it1.next();

										if (pair2.getKey().intValue() <= item_number
												&& pair2.getValue().intValue() >= item_number) {

											BukkitChest.getInventory().setItem(slot, pair.getKey());
										}

									}

								}
							}
						}
					}
				}

			}

		}

	}

}
