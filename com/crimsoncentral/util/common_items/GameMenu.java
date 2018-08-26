package com.crimsoncentral.util.common_items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.crimsoncentral.Main;
import com.crimsoncentral.arena.ArenaRegister;
import com.crimsoncentral.arena.util.ArenaUtil;
import com.crimsoncentral.game.GameProfile;
import com.crimsoncentral.game.ModeProfile;
import com.crimsoncentral.util.item.ActionItemManager;
import com.crimsoncentral.util.item.InventoryItem;
import com.crimsoncentral.util.other.ChangingInventory;

import net.md_5.bungee.api.ChatColor;

public class GameMenu extends InventoryItem {
	GameMenu g_menu = this;
	Menu m = new Menu("game menu");
	Inventory gui = Bukkit.createInventory(null, 54, ChatColor.RED + "Game Menu");

	public static HashMap<Integer, ItemStack> game_items = new HashMap<Integer, ItemStack>();
	public static HashMap<Double, ItemStack> game_items_ids = new HashMap<Double, ItemStack>();

	public GameMenu(String name, ItemStack is) {
		super(name, is);
		ItemStack i = new ItemStack(Material.COMPASS);
		ItemMeta im = i.getItemMeta();

		im.setDisplayName(ChatColor.RED + "Game Menu");

		im.setLore(Arrays.asList(" ", ChatColor.WHITE + "Click this compass", ChatColor.WHITE + "to view all of",
				ChatColor.WHITE + "our games."));

		i.setItemMeta(im);

		setIs(i);
	}

	public void perform(Player player) {

		player.openInventory(gui);

	}

	private class Menu extends ChangingInventory {

		public Menu(String name) {
			super(name);
			Main.changing_inventories.add(this);
		}

		int in;

		@Override
		public void change() {

			in = 0;

			ItemStack i = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 9);
			ItemMeta im = i.getItemMeta();

			im.setDisplayName(ChatColor.RED + " ");
			im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

			i.setItemMeta(im);

			ItemStack i1 = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14);
			ItemMeta im1 = i1.getItemMeta();

			im1.setDisplayName(ChatColor.RED + " ");
			im1.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

			i1.setItemMeta(im1);

			Iterator<Entry<Integer, GameProfile>> it = ArenaRegister.game_profiles.entrySet().iterator();
			while (it.hasNext()) {

				Entry<Integer, GameProfile> pair = it.next();

				for (ModeProfile p : pair.getValue().getModeProfiles()) {
					++in;
					if (p.getTypeId() != 0.0) {
						ItemStack ip = p.getActionIitem().getIs();
						ItemMeta ip_m = ip.getItemMeta();
						ip_m.setLore(Arrays.asList(" ",
								ChatColor.LIGHT_PURPLE + "► " + ArenaUtil.getModePlayers(p.getTypeId()).size()
										+ " players" + ChatColor.GREEN + " Currently Playing!"));

						ip.setItemMeta(ip_m);

						if (game_items.containsKey(in)) {
							game_items.get(in).equals(ip);
							game_items_ids.get(p.getTypeId()).equals(ip);
						} else {
							game_items.put(in, ip);
							game_items_ids.put(p.getTypeId(), ip);

						}
					}
				}

			}

			in = 0;

			for (int I = 3; I <= 5; ++I) {

				gui.setItem(I, i1);

			}

			for (int I = 11; I <= 15; ++I) {

				gui.setItem(I, i1);

			}

			for (int I = 20; I <= 24; ++I) {

				gui.setItem(I, i1);

			}

			for (int I = 30; I <= 32; ++I) {

				gui.setItem(I, i1);

			}

			for (int I = 2; I <= 5; ++I) {
				++in;
				if (game_items.get(in) != null)
					gui.setItem(I, game_items.get(in));

			}

			for (int I = 11; I <= 15; ++I) {
				++in;
				if (game_items.get(in) != null)
					gui.setItem(I, game_items.get(in));

			}

			for (int I = 20; I <= 24; ++I) {
				++in;
				if (game_items.get(in) != null)
					gui.setItem(I, game_items.get(in));

			}

			for (int I = 30; I <= 32; ++I) {
				++in;
				if (game_items.get(in) != null)
					gui.setItem(I, game_items.get(in));

			}

			for (int I = 36; I <= 44; ++I) {

				gui.setItem(I, i);

			}

			gui.setItem(49, ActionItemManager.getActionItemItemStack("return to main lobby bed"));
			setInventory(gui);
			g_menu.setInventory(gui);

		}

	}

}
