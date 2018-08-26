package com.crimsoncentral.vespen;

import java.util.HashMap;

import org.bukkit.entity.Player;


import com.crimsoncentral.vespen.VespenPlayer.ServerType;
import com.crimsoncentral.vespen.data.combat.CpsLog;

public class Vespen {

	private static HashMap<Player, VespenPlayer> players = new HashMap<Player, VespenPlayer>();

	public static void addVespenPlayer(Player player) {

		if (getVespenPlayer(player) == null) {

			players.put(player, new VespenPlayer(player, ServerType.SPIGOT));

		}

	}

	public static VespenPlayer getVespenPlayer(Player player) {

		return players.get(player);

	}

	@SuppressWarnings("all")
	public static Thread CPSManager = new Thread(new Runnable() {

		@Override
		public void run() {
			for (int i = 0; i == i; ++i) {

				for (VespenPlayer vp : players.values()) {

					CpsLog cl = vp.getCombatProfile().getCpsLog();
					if (cl != null) {
						cl.setMillTime(cl.getMillTime() - 1000);
						if (cl.getMillTime() <= 0) {
							vp.getCombatProfile().setCpsLog(null);
						}

					}
				}

				try {
					CPSManager.sleep(1000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}

			}

		}

	});

	public static void setup() {

	}

//	public static void banCount() {
//
//		for (int i = 1; i <= 7; ++i) {
//			if (!Sql.checkTableForRow("vespen_7_day_statistics", "value", "statistic_name", "sb" + i)) {
//
//				HashMap<String, String> map = new HashMap<String, String>();
//
//				map.put("sb" + i, "0");
//
//				Sql.createRow("vespen_7_day_statistics", map);
//			}
//		}
//		for (int i = 1; i <= 7; ++i) {
//			if (!Sql.checkTableForRow("vespen_7_day_statistics", "value", "statistic_name", "vb" + i)) {
//
//				HashMap<String, String> map = new HashMap<String, String>();
//
//				map.put("vb" + i, "0");
//
//				Sql.createRow("vespen_7_day_statistics", map);
//			}
//		}
//		
//		if (!Sql.checkTableForRow("vespen_7_day_statistics", "value", "statistic_name", "")) {
//
//			HashMap<String, String> map = new HashMap<String, String>();
//
//			map.put("last_updated", "0");
//
//			Sql.createRow("vespen_7_day_statistics", map);
//		}
//
//		int sb1 = Sql.getInt("vespen_7_day_statistics", "value", "statistic_name", "sb1");
//		int sb2 = Sql.getInt("vespen_7_day_statistics", "value", "statistic_name", "sb2");
//		int sb3 = Sql.getInt("vespen_7_day_statistics", "value", "statistic_name", "sb3");
//		int sb4 = Sql.getInt("vespen_7_day_statistics", "value", "statistic_name", "sb4");
//		int sb5 = Sql.getInt("vespen_7_day_statistics", "value", "statistic_name", "sb5");
//		int sb6 = Sql.getInt("vespen_7_day_statistics", "value", "statistic_name", "sb6");
//		int sb7 = Sql.getInt("vespen_7_day_statistics", "value", "statistic_name", "sb7");
//
//		int vb1 = Sql.getInt("vespen_7_day_statistics", "value", "statistic_name", "vb1");
//		int vb2 = Sql.getInt("vespen_7_day_statistics", "value", "statistic_name", "vb2");
//		int vb3 = Sql.getInt("vespen_7_day_statistics", "value", "statistic_name", "vb3");
//		int vb4 = Sql.getInt("vespen_7_day_statistics", "value", "statistic_name", "vb4");
//		int vb5 = Sql.getInt("vespen_7_day_statistics", "value", "statistic_name", "vb5");
//		int vb6 = Sql.getInt("vespen_7_day_statistics", "value", "statistic_name", "vb6");
//		int vb7 = Sql.getInt("vespen_7_day_statistics", "value", "statistic_name", "vb7");
//
//		
//		
//		

}
