package com.crimsoncentral.util.item;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.crimsoncentral.util.JSONMessage;
import com.crimsoncentral.util.common_items.GameMenu;

public class ActionItemManager {

	public static HashMap<String, ActionItem> items = new HashMap<String, ActionItem>();

	public static void registerItems() {

		@SuppressWarnings("unused")
		ActionItem return_to_lobby_bed = new ActionItem("return to main lobby bed",
				ItemUtil.newItem(Material.BED, 1, (short) 14,
						ChatColor.RED + "" + ChatColor.BOLD + "Return To Main Lobby",
						ChatColor.WHITE + "Use this bed" + ChatColor.WHITE,
						"to return to the" + ChatColor.WHITE + "Main Lobby!")) {

			@Override
			public void preform(Player player) {

				player.performCommand("lobby");
			}

		};

		ActionItem close_inventory = new ActionItem("close inventory",
				ItemUtil.newItem(Material.BARRIER, 1, (short) 0, ChatColor.RED + "Close", " ",
						ChatColor.WHITE + "Use this button", ChatColor.WHITE + "to close this menu!")) {

			@Override
			public void preform(Player player) {

				player.closeInventory();
			}

		};

		@SuppressWarnings("unused")
		ActionItem open_server_store = new ActionItem("open server store",
				ItemUtil.newItem(Material.EMERALD, 1, (short) 0, ChatColor.GREEN + "Server Store Link", " ",
						ChatColor.WHITE + "Click this to go to", ChatColor.WHITE + "our server store to buy",
						ChatColor.WHITE + "ranks, cosmetics, boosters, and more!", ChatColor.YELLOW
								+ "Make sure to click " + ChatColor.GREEN + "''Yes''" + ChatColor.YELLOW + " to open",
						" ", ChatColor.AQUA + "http://crimson-central.com/shop")) {

			@Override
			public void preform(Player player) {

				JSONMessage.create("§lSERVER STORE").color(ChatColor.YELLOW).then(" §l>>").color(ChatColor.GREEN)
						.then(" http://crimson-central.com").openURL("http://crimson-central.com").color(ChatColor.AQUA)
						.send(player);
				player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 2, (float) 1.5);

				close_inventory.preform(player);
				;
			}

		};

		@SuppressWarnings("unused")
		GameMenu game_menu = new GameMenu("game menu", null);

	}

	public static void addItem(String name, ActionItem item) {

		items.put(name.toLowerCase(), item);

	}

	public static ActionItem getActionItem(String name) {

		return items.get(name.toLowerCase());

	}

	public static ActionItem getActionItem(ItemStack is) {
		ActionItem item = null;

		Iterator<Entry<String, ActionItem>> it = items.entrySet().iterator();
		while (it.hasNext()) {

			Entry<String, ActionItem> pair = it.next();

			ItemStack i = pair.getValue().getIs();

			if (i.getType() == is.getType() && i.getItemMeta().getDisplayName() == is.getItemMeta().getDisplayName()) {

				item = pair.getValue();
				break;
			}

		}

		return item;
	}

	public static ItemStack getActionItemItemStack(String name) {

		return items.get(name.toLowerCase()).getIs();

	}

	public static ItemStack getActionItemItemStack(ActionItem item) {

		ItemStack is = null;

		Iterator<Entry<String, ActionItem>> it = items.entrySet().iterator();
		while (it.hasNext()) {

			Entry<String, ActionItem> pair = it.next();

			if (pair.getValue() == item) {

				is = pair.getValue().getIs();
				break;
			}

		}

		return is;

	}

}
