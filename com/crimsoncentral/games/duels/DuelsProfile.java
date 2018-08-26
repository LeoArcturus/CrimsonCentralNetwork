package com.crimsoncentral.games.duels;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.crimsoncentral.game.GameProfile;
import com.crimsoncentral.game.ModeProfile;
import com.crimsoncentral.games.duels.doubles.DuelsArcherDoubles;
import com.crimsoncentral.games.duels.doubles.DuelsNoDeBuffDoubles;
import com.crimsoncentral.games.duels.doubles.DuelsNormalDoubles;
import com.crimsoncentral.games.duels.doubles.DuelsOverpoweredDoubles;
import com.crimsoncentral.games.duels.solos.DuelsArcherSolo;
import com.crimsoncentral.games.duels.solos.DuelsNoDeBuffSolo;
import com.crimsoncentral.games.duels.solos.DuelsNormalSolo;
import com.crimsoncentral.games.duels.solos.DuelsOverpoweredSolo;
import com.crimsoncentral.util.ArenaConfig;
import com.crimsoncentral.util.item.ActionItem;
import com.crimsoncentral.util.item.ItemUtil;

public class DuelsProfile extends GameProfile {

	public DuelsProfile(String name, Integer parent_id, ActionItem action_item) {
		super(name, parent_id, action_item);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setup() {
		addMode(4.01,
				new ModeProfile("normal solo", 4.01, new DuelsNormalSolo("Duels-Normal-Solo-Original-Copy", 4.01, 2, 2),
						new ActionItem("duels normal solo", ItemUtil.newItem(Material.FISHING_ROD, 1, (short) 0,
								ChatColor.YELLOW + "" + ChatColor.BOLD + "Duels 1v1 Normal", "")) {

							@Override
							public void preform(Player player) {

								player.performCommand("play duels normal solo");
							}
						}, new ArenaConfig(4.01).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 2)));

		addMode(4.02,
				new ModeProfile("archer solo", 4.02, new DuelsArcherSolo("Duels-Archer-Solo-Original-Copy", 4.02, 2, 2),
						new ActionItem("duels archer solo", ItemUtil.newItem(Material.BOW, 1, (short) 0,
								ChatColor.YELLOW + "" + ChatColor.BOLD + "Duels 1v1 Archer", "")) {

							@Override
							public void preform(Player player) {

								player.performCommand("play duels archer solo");
							}
						}, new ArenaConfig(4.02).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 2)));

		addMode(4.03,
				new ModeProfile("overpowered solo", 4.03,
						new DuelsOverpoweredSolo("Duels-Archer-Solo-Original-Copy", 4.03, 2, 2),
						new ActionItem("duels overpowered solo", ItemUtil.newItem(Material.DIAMOND_CHESTPLATE, 1,
								(short) 0, ChatColor.YELLOW + "" + ChatColor.BOLD + "Duels 1v1 Overpowered", "")) {

							@Override
							public void preform(Player player) {

								player.performCommand("play duels overpowered solo");
							}
						}, new ArenaConfig(4.03).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 2)));

		addMode(4.04,
				new ModeProfile("nodebuff solo", 4.04,
						new DuelsNoDeBuffSolo("Duels-No-Debuff-Solo-Original-Copy", 4.04, 2, 2),
						new ActionItem("duels nodebuff solo", ItemUtil.newItem(Material.SPLASH_POTION, 1, (short) 0,
								ChatColor.YELLOW + "" + ChatColor.BOLD + "Duels 1v1 No Debuff", "")) {

							@Override
							public void preform(Player player) {

								player.performCommand("play duels nodebuff solo");
							}
						}, new ArenaConfig(4.04).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 2)));

		addMode(4.05,
				new ModeProfile("normal doubles", 4.05,
						new DuelsNormalDoubles("Duels-Normal-Doubles-Original-Copy", 4.05, 4, 4),
						new ActionItem("duels normal doubles", ItemUtil.newItem(Material.FISHING_ROD, 2, (short) 0,
								ChatColor.YELLOW + "" + ChatColor.BOLD + "Duels 2v2 Normal", "")) {

							@Override
							public void preform(Player player) {

								player.performCommand("play duels normal doubles");
							}
						}, new ArenaConfig(4.05).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 2)));

		addMode(4.06,
				new ModeProfile("archer doubles", 4.06,
						new DuelsArcherDoubles("Duels-Archer-Doubles-Original-Copy", 4.06, 4, 4),
						new ActionItem("duels archer doubles", ItemUtil.newItem(Material.BOW, 2, (short) 0,
								ChatColor.YELLOW + "" + ChatColor.BOLD + "Duels 2v2 Archer", "")) {

							@Override
							public void preform(Player player) {

								player.performCommand("play duels archer doubles");
							}
						}, new ArenaConfig(4.06).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 2)));

		addMode(4.07,
				new ModeProfile("overpowered doubles", 4.07,
						new DuelsOverpoweredDoubles("Duels-Archer-Doubles-Original-Copy", 4.07, 4, 4),
						new ActionItem("duels overpowered doubles", ItemUtil.newItem(Material.DIAMOND_CHESTPLATE, 2,
								(short) 0, ChatColor.YELLOW + "" + ChatColor.BOLD + "Duels 2v2 Overpowered", "")) {

							@Override
							public void preform(Player player) {

								player.performCommand("play duels overpowered doubles");
							}
						}, new ArenaConfig(4.07).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 2)));

		addMode(4.08,
				new ModeProfile("nodebuff doubles", 4.08,
						new DuelsNoDeBuffDoubles("Duels-No-Debuff-Doubles-Original-Copy", 4.08, 4, 4),
						new ActionItem("duels nodebuff doubles", ItemUtil.newItem(Material.SPLASH_POTION, 2, (short) 0,
								ChatColor.YELLOW + "" + ChatColor.BOLD + "Duels 2v2 No Debuff", "")) {

							@Override
							public void preform(Player player) {

								player.performCommand("play duels nodebuff doubles");
							}
						}, new ArenaConfig(4.08).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 2)));

	}

}
