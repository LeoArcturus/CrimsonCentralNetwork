package com.crimsoncentral.games.hunger_games;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.crimsoncentral.game.GameProfile;
import com.crimsoncentral.game.ModeProfile;
import com.crimsoncentral.util.ArenaConfig;
import com.crimsoncentral.util.item.ActionItem;

import com.crimsoncentral.util.item.ItemUtil;

public class HungerGamesProfile extends GameProfile {

	public HungerGamesProfile(String name, Integer parent_id, ActionItem action_item) {
		super(name, parent_id, action_item);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unused")
	@Override
	public void setup() {

		double nokits_solo = 2.01;
		double nokits_doubles = 2.02;
		double kits_solo = 1.03;
		double kits_doubles = 1.04;

	
		addMode(nokits_solo,
				new ModeProfile("nokits solo", nokits_solo,
						new HungerGamesNoKitsSolo("Hunger-Games-NoKits-Solo-Original-Copy", nokits_solo, 24, 1),
						new ActionItem("hungergames nokits solo", ItemUtil.newItem(Material.CHEST, 1, (short)0,
								ChatColor.YELLOW + "" + ChatColor.BOLD + "Hunger Games NoKits Solo", "")) {

							@Override
							public void preform(Player player) {

								player.performCommand("play hungergames nokits solo");
							}
						}, new ArenaConfig(nokits_solo).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 24)
								.doesHungerGamesChestScan()));
//
//		addMode(normal_doubles,
//				new ModeProfile("normal doubles", normal_doubles,
//						new SkyWarsNormalDoubles("Sky-Wars-Normal-doubles-Original-Copy", normal_doubles, 24, 4),
//						new ActionItem("skywars normal doubles", ItemUtil.newItem(Material.ENDER_PEARL, 2, (short)0,
//								ChatColor.YELLOW + "" + ChatColor.BOLD + "Sky Wars Normal doubles", "")) {
//
//							@Override
//							public void preform(Player player) {
//
//								player.performCommand("play duels normal doubles");
//							}
//						}, new ArenaConfig(normal_doubles).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 12)
//								.doesChestScan()));
//
//		addMode(ballistic_solo,
//				new ModeProfile("ballistic solo", ballistic_solo,
//						new SkyWarsBallisticSolo("Sky-Wars-Ballistic-Solo-Original-Copy", ballistic_solo, 12, 1),
//						new ActionItem("skywars ballistic solo", ItemUtil.newItem(Material.EYE_OF_ENDER, 1, (short) 0,
//								ChatColor.YELLOW + "" + ChatColor.BOLD + "Sky Wars Ballistic Solo", "")) {
//
//							@Override
//							public void preform(Player player) {
//
//								player.performCommand("play skywars ballistic solo");
//							}
//						}, new ArenaConfig(ballistic_solo).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 12)
//								.doesChestScan()));
//
//		addMode(ballistic_doubles,
//				new ModeProfile("ballistic doubles", ballistic_doubles,
//						new SkyWarsBallisticDoubles("Sky-Wars-Ballistic-doubles-Original-Copy", ballistic_doubles, 24,
//								4),
//						new ActionItem("skywars ballistic doubles", ItemUtil.newItem(Material.EYE_OF_ENDER, 2,
//								(short) 0, ChatColor.YELLOW + "" + ChatColor.BOLD + "Sky Wars Ballistic doubles", "")) {
//
//							@Override
//							public void preform(Player player) {
//
//								player.performCommand("play skywars ballistic doubles");
//							}
//						}, new ArenaConfig(ballistic_doubles).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 12)
//								.doesChestScan()));

	}

}
