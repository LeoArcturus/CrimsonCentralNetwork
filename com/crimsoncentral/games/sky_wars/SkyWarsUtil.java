package com.crimsoncentral.games.sky_wars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

public class SkyWarsUtil {

	public static BossBar bar = (BossBar) Bukkit.createBossBar(ChatColor.AQUA + "" + ChatColor.BOLD + "SKY WARS" + ChatColor.WHITE + " - " + ChatColor.YELLOW
			+ "CRIMSON-CENTRAL.COM", BarColor.WHITE, BarStyle.SOLID,
			BarFlag.PLAY_BOSS_MUSIC);
	
	public static void setUpBar() {
		
	
		bar.setVisible(true);
		bar.setProgress(1.0);
	}
}
