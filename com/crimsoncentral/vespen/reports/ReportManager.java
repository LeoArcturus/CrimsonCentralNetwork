package com.crimsoncentral.vespen.reports;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import com.crimsoncentral.util.item.ItemUtil;
import com.crimsoncentral.vespen.reports.Report.HackType;

public class ReportManager {

	private static HashMap<String, HackType> hackTypes = new HashMap<String, HackType>();
	private boolean setUp = false;

	private static Inventory reportsMenu = Bukkit.createInventory(null, 54,
			ChatColor.GOLD + "" + ChatColor.MAGIC + "|" + ChatColor.RESET + ChatColor.RED + " PLAYER CHEAT REPORTS "
					+ ChatColor.GOLD + "" + ChatColor.MAGIC + "|" + ChatColor.RESET);

	public static void setUp() {

		hackTypes.put("killaura", HackType.KILLAURA);
		hackTypes.put("reach", HackType.REACH);
		hackTypes.put("autoclicker", HackType.AUTOCLICKER);
		hackTypes.put("speed", HackType.SPEED);
		hackTypes.put("jesus", HackType.JESUS);
		hackTypes.put("fly", HackType.FLY);
		hackTypes.put("bhop", HackType.BHOP);
		hackTypes.put("spider", HackType.SPIDER);

		for (int i = 31; i <= 42; ++i) {
			reportsMenu.setItem(i, ItemUtil.newItem(Material.STAINED_GLASS_PANE, 1, (short) 9, " ", " "));

		}
	}

	public boolean isSetUp() {
		return setUp;
	}

	public void setSetUp(boolean setUp) {
		this.setUp = setUp;
	}

}
