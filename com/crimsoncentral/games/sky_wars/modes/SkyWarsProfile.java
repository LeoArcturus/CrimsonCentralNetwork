package com.crimsoncentral.games.sky_wars.modes;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.crimsoncentral.game.GameProfile;
import com.crimsoncentral.game.ModeProfile;
import com.crimsoncentral.games.sky_wars.SkyWarsListeners;
import com.crimsoncentral.games.sky_wars.modes.doubles.SkyWarsBallisticDoubles;
import com.crimsoncentral.games.sky_wars.modes.doubles.SkyWarsNormalDoubles;
import com.crimsoncentral.games.sky_wars.modes.solo.SkyWarsBallisticSolo;
import com.crimsoncentral.games.sky_wars.modes.solo.SkyWarsNormalSolo;
import com.crimsoncentral.util.ArenaConfig;
import com.crimsoncentral.util.item.ActionItem;

import com.crimsoncentral.util.item.ItemUtil;

public class SkyWarsProfile extends GameProfile {

	public SkyWarsProfile(String name, Integer parent_id, ActionItem action_item) {
		super(name, parent_id, action_item);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setup() {

		double normal_solo = 1.01;
		double normal_doubles = 1.02;
		double ballistic_solo = 1.03;
		double ballistic_doubles = 1.04;

		addListener(new SkyWarsListeners());
		addMode(normal_solo,
				new ModeProfile("normal solo", normal_solo,
						new SkyWarsNormalSolo("Sky-Wars-Normal-Solo-Original-Copy", normal_solo, 12, 2),
						new ActionItem("skywars normal solo", ItemUtil.newItem(Material.ENDER_PEARL, 1, (short)0,
								ChatColor.YELLOW + "" + ChatColor.BOLD + "Sky Wars Normal Solo", "")) {

							@Override
							public void preform(Player player) {

								player.performCommand("play skywars normal solo");
							}
						}, new ArenaConfig(normal_solo).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 12)
								.doesChestScan()));

		addMode(normal_doubles,
				new ModeProfile("normal doubles", normal_doubles,
						new SkyWarsNormalDoubles("Sky-Wars-Normal-doubles-Original-Copy", normal_doubles, 24, 4),
						new ActionItem("skywars normal doubles", ItemUtil.newItem(Material.ENDER_PEARL, 2, (short)0,
								ChatColor.YELLOW + "" + ChatColor.BOLD + "Sky Wars Normal doubles", "")) {

							@Override
							public void preform(Player player) {

								player.performCommand("play duels normal doubles");
							}
						}, new ArenaConfig(normal_doubles).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 12)
								.doesChestScan()));

		addMode(ballistic_solo,
				new ModeProfile("ballistic solo", ballistic_solo,
						new SkyWarsBallisticSolo("Sky-Wars-Ballistic-Solo-Original-Copy", ballistic_solo, 12, 1),
						new ActionItem("skywars ballistic solo", ItemUtil.newItem(Material.EYE_OF_ENDER, 1, (short) 0,
								ChatColor.YELLOW + "" + ChatColor.BOLD + "Sky Wars Ballistic Solo", "")) {

							@Override
							public void preform(Player player) {

								player.performCommand("play skywars ballistic solo");
							}
						}, new ArenaConfig(ballistic_solo).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 12)
								.doesChestScan()));

		addMode(ballistic_doubles,
				new ModeProfile("ballistic doubles", ballistic_doubles,
						new SkyWarsBallisticDoubles("Sky-Wars-Ballistic-doubles-Original-Copy", ballistic_doubles, 24,
								4),
						new ActionItem("skywars ballistic doubles", ItemUtil.newItem(Material.EYE_OF_ENDER, 2,
								(short) 0, ChatColor.YELLOW + "" + ChatColor.BOLD + "Sky Wars Ballistic doubles", "")) {

							@Override
							public void preform(Player player) {

								player.performCommand("play skywars ballistic doubles");
							}
						}, new ArenaConfig(ballistic_doubles).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 12)
								.doesChestScan()));

	}

}
