package com.crimsoncentral.games.egg_wars.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.crimsoncentral.util.item.ActionItem;
import com.crimsoncentral.util.item.ItemUtil;

import net.md_5.bungee.api.ChatColor;

public class EggWarsShop {

	private Inventory menu = Bukkit.createInventory(null, 54, ChatColor.AQUA + "Shop");

	private Double mode;

	public EggWarsShop(Double mode) {
		this.setMode(mode);
		createRowInfo();

	}

	private void createRowInfo() {

		menu.setItem(0, ItemUtil.newItem(Material.CLAY, 1, (short) 0, ChatColor.GREEN + "Blocks", " ",
				ChatColor.WHITE + "This row contains all", ChatColor.WHITE + "purchaseable blocks."));
		menu.setItem(9, ItemUtil.newItem(Material.CLAY, 1, (short) 0, ChatColor.GREEN + "Weapons and Tools", " ",
				ChatColor.WHITE + "This row contains all", ChatColor.WHITE + "purchaseable weapons."));
		menu.setItem(18, ItemUtil.newItem(Material.TNT, 1, (short) 0, ChatColor.GREEN + "Utility", " ",
				ChatColor.WHITE + "This row contains all", ChatColor.WHITE + "purchaseable utility items."));
		menu.setItem(27, ItemUtil.newItem(Material.CLAY, 1, (short) 0, ChatColor.GREEN + "Blocks", " ",
				ChatColor.WHITE + "This row contains all", ChatColor.WHITE + "purchaseable blocks."));
		menu.setItem(36, ItemUtil.newItem(Material.CLAY, 1, (short) 0, ChatColor.GREEN + "Blocks", " ",
				ChatColor.WHITE + "This row contains all", ChatColor.WHITE + "purchaseable blocks."));
		menu.setItem(45, ItemUtil.newItem(Material.CLAY, 1, (short) 0, ChatColor.GREEN + "Blocks", " ",
				ChatColor.WHITE + "This row contains all", ChatColor.WHITE + "purchaseable blocks."));

		for (int i = 0; i <= 54; ++i) {
			if (i % 9 == 0) {
				menu.setItem(i + 1,
						ItemUtil.newItem(Material.STAINED_GLASS_PANE, 1, (short) 8, ChatColor.GREEN + "  ", " "));
			}
		}

	}

	@SuppressWarnings("unused")
	private void createBlockRow() {

		ActionItem wool = new ActionItem("eggwars wool shop wool",
				ItemUtil.newItem(Material.ENDER_PEARL, 2, (short) 0, ChatColor.WHITE + "Wool", "")) {

			@Override
			public void preform(Player player) {
			
				player.performCommand("play duels normal doubles");
			}
		};

	}

	public Double getMode() {
		return mode;
	}

	public void setMode(Double mode) {
		this.mode = mode;
	}

}
