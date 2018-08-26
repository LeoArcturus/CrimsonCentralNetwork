package com.crimsoncentral.games.the_walls;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.crimsoncentral.game.GameProfile;
import com.crimsoncentral.game.ModeProfile;
import com.crimsoncentral.util.ArenaConfig;
import com.crimsoncentral.util.item.ActionItem;

import com.crimsoncentral.util.item.ItemUtil;


public class TheWallsProfile extends GameProfile {

	public TheWallsProfile(String name, Integer parent_id, ActionItem action_item) {
		super(name, parent_id, action_item);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setup() {
		addMode(3.01,
				new ModeProfile("normal quads", 3.01, new TheWallsQuads("The-Walls-Quads-Original-Copy", 3.01, 12, 1),
						new ActionItem("thewalls quads", ItemUtil.newItem(Material.FIREBALL, 1, (short) 0,
								ChatColor.YELLOW + "" + ChatColor.BOLD + "The Walls Quads", "")) {

							@Override
							public void preform(Player player) {

								player.performCommand("play thewalls normal quads");
							}
						},
						new ArenaConfig(3.01).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 4)
								.addMultipleSimLines("Wall-*-Pos1", "0, 0, 0", 8)
								.addMultipleSimLines("Wall-*-Pos2", "0, 0, 0", 8).doesChestScan()));

	}

}
