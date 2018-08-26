
package com.crimsoncentral.arena;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.crimsoncentral.util.ScoreboardWrapper;
import com.crimsoncentral.util.item.ItemUtil;

import net.md_5.bungee.api.ChatColor;

public class ArenaManager {

	public static HashMap<Arena, Double> local_arenas = new HashMap<Arena, Double>();

	public static HashMap<Player, ScoreboardWrapper> scoreboards = new HashMap<Player, ScoreboardWrapper>();

	private static HashMap<Integer, Arena> arena_number_ids = new HashMap<Integer, Arena>();

	private static Inventory arena_manager_menu;

	private static Integer getNextId(Arena a) {
		Integer id = null;
		int i = 0;
		while (null == null) {
			i = i + 1;
			if (!arena_number_ids.containsKey(i)) {
				id = i;
				break;
			}
		}

		a.setArenaId(id);
		return id;

	}

	public static void addToArenaIds(Arena arena) {

		arena_number_ids.put(getNextId(arena), arena);

	}

	@SuppressWarnings("unlikely-arg-type")
	public static void removeArena(Arena arena) {

		arena_number_ids.remove(arena, arena_number_ids.get(arena));

	}

	public static void openArenaManagerMenu(Player p) {

		p.openInventory(arena_manager_menu);

	}

	static int in = 0;

	public static void updateArenaManagerMenu() {
		if (arena_manager_menu == null) {
			arena_manager_menu = Bukkit.createInventory(null, 54,
					ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Arena Manager");
		}
		arena_manager_menu.clear();

		HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();

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

		in = 0;

		for (Entry<Arena, Double> e : local_arenas.entrySet()) {

			ItemStack item = ArenaRegister.getGameProfile(Integer.valueOf(new DecimalFormat("0").format(e.getValue())))
					.getMode(e.getValue()).getActionIitem().getIs().clone();

			Arena a = e.getKey();

			ItemMeta im11 = item.getItemMeta();
			im11.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + a.getWorld().getName() + ChatColor.RESET
					+ ChatColor.WHITE + " ("
					+ ArenaRegister.getGameProfile(Integer.valueOf(new DecimalFormat("0").format(e.getValue())))
							.getMode(e.getValue()).getArena().getArenaName().replace("_", " ").replace("-", " ")
							.replace(" Original Copy", "")
					+ ")");

			im11.setLore(Arrays.asList(" ", ChatColor.GOLD + "Arena Name: " + ChatColor.GRAY + a.getWorld().getName(),
					ChatColor.RED + "Arena Gamemode #: " + ChatColor.GRAY + a.getGamemode(),
					ChatColor.DARK_GREEN + "Map Name: " + ChatColor.GRAY + a.getMapName(),
					ChatColor.WHITE + "Arena ID: " + ChatColor.GRAY + a.getArenaId(), " ",
					ChatColor.GREEN + "Max Players: " + ChatColor.GRAY + a.getMaxPlayers(),
					ChatColor.GREEN + "Min Players: " + ChatColor.GRAY + a.getMinPlayers(), " ",
					ChatColor.GREEN + "Joined Players: " + ChatColor.GRAY + a.getPlayers().size(),
					ChatColor.GREEN + "Alive Players: " + ChatColor.GRAY + a.getAlivePlayers().size(),
					ChatColor.GREEN + "Dead Players: " + ChatColor.GRAY + a.getDeadPlayers().size(),
					ChatColor.GREEN + "Spectating Players: " + ChatColor.GRAY
							+ (a.getWorld().getPlayers().size() - a.getAlivePlayers().size()),
					" "));

			item.setItemMeta(im11);

			im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

			items.put(items.size() + 1, item);

		}

		for (int I = 10; I <= 16; ++I) {

			arena_manager_menu.setItem(I, i1);

		}

		for (int I = 20; I <= 24; ++I) {

			arena_manager_menu.setItem(I, i1);

		}

		for (int I = 30; I <= 32; ++I) {

			arena_manager_menu.setItem(I, i1);

		}

		for (int I = 10; I <= 16; ++I) {
			++in;
			if (items.get(in) != null)
				arena_manager_menu.setItem(I, items.get(in));

		}

		for (int I = 20; I <= 24; ++I) {
			++in;
			if (items.get(in) != null)
				arena_manager_menu.setItem(I, items.get(in));

		}

		for (int I = 30; I <= 32; ++I) {
			++in;
			if (items.get(in) != null)
				arena_manager_menu.setItem(I, items.get(in));

		}

		for (int I = 36; I <= 44; ++I) {

			arena_manager_menu.setItem(I, i);

		}

		arena_manager_menu.setItem(49,
				ItemUtil.newItem(Material.BOOK, 1, (short) 0, ChatColor.GREEN + "General Arena Info", " ",
						ChatColor.GREEN + "# of Arena's: " + ChatColor.GRAY + local_arenas.size()));

	}

}
