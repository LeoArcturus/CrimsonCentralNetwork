package com.crimsoncentral.server_player.social;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.crimsoncentral.ranks.Rank;
import com.crimsoncentral.ranks.RanksManager;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.server_player.ServerPlayer;
import com.crimsoncentral.util.item.ActionItem;
import com.crimsoncentral.util.item.ActionItemManager;
import com.crimsoncentral.util.item.ItemUtil;

public class ProfileMenu extends ActionItem {

	private static HashMap<Player, ProfileMenu> menus = new HashMap<Player, ProfileMenu>();

	public static ItemStack giveProfileItem(Player player) {

		if (menus.get(player) == null) {

			menus.put(player, new ProfileMenu("", null, player));

		}

		return menus.get(player).getIs();

	}

	private Player player;

	@SuppressWarnings("deprecation")
	public ProfileMenu(String name, ItemStack is, Player player) {

		super(player.getName() + "s profile",
				ItemUtil.newItem(Material.SKULL_ITEM, 1, (short) 3, ChatColor.DARK_AQUA + "Your Profile", " ",
						ChatColor.WHITE + "This item will open your", ChatColor.WHITE + "Profile which is full of",
						ChatColor.WHITE + "settings, info, and more", ChatColor.WHITE + "for you."));
		SkullMeta skullMeta = (SkullMeta) getIs().getItemMeta();
		skullMeta.setOwner(player.getName());
		getIs().setItemMeta((ItemMeta) skullMeta);

		this.setPlayer(player);
	}


	@Override
	public void preform(Player player) {

		Inventory menu = Bukkit.createInventory(getPlayer(), 54, ChatColor.DARK_AQUA + "Your Profile");
	

		menu.setItem(9, getGenInfoItem(player));
		menu.setItem(27, ActionItemManager.getActionItemItemStack("open server store"));
		menu.setItem(45, ActionItemManager.getActionItemItemStack("close inventory"));

		placeBarrierColoum(menu);

		player.openInventory(menu);

	}

	
	@SuppressWarnings("deprecation")
	private static ItemStack getGenInfoItem(Player player) {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

		

		ServerPlayer sp = PlayerManager.getServerPlayer(player);

		Rank r = sp.getRank();

		String rank_name = r.getPrefix().replace("[", "").replace("]", "");

		if (r == RanksManager.default_rank) {

			rank_name = ChatColor.GRAY + "Default";

		}

		String colored_name = r.getRankColor() + player.getName();

		ItemStack info = ItemUtil.newItem(Material.SKULL_ITEM, 1, (short) 3,
				ChatColor.DARK_GRAY + "General Info About You", " ", ChatColor.WHITE + "Name: " + colored_name,
				ChatColor.WHITE + "Rank: " + rank_name, ChatColor.WHITE + "Network Level: " + ChatColor.GREEN + "1",
				ChatColor.WHITE + "First Joined: " + ChatColor.GOLD + ft.format(dNow).replace(".", "/"), " ",
				ChatColor.WHITE + "Friends: " + ChatColor.RED + "NONE :(",
				ChatColor.WHITE + "Clan: " + ChatColor.YELLOW + "Coming Soon",
				ChatColor.WHITE + "Party Status: " + ChatColor.RED + "not in one");

		SkullMeta skullMeta = (SkullMeta) info.getItemMeta();
		skullMeta.setOwner(player.getName());
		info.setItemMeta((ItemMeta) skullMeta);
		
		
		return info;
		
		
	}
	
	
	
	
	private void placeBarrierColoum(Inventory i) {
		ItemStack orange_barrier = ItemUtil.newItem(Material.STAINED_GLASS_PANE, 1, (short) 1, "", "");

		i.setItem(1, orange_barrier);
		i.setItem(10, orange_barrier);
		i.setItem(19, orange_barrier);
		i.setItem(28, orange_barrier);
		i.setItem(37, orange_barrier);
		i.setItem(46, orange_barrier);

	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
