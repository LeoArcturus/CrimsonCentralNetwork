package com.crimsoncentral.games.who_done_it;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.crimsoncentral.game.GameProfile;
import com.crimsoncentral.game.ModeProfile;
import com.crimsoncentral.util.ArenaConfig;
import com.crimsoncentral.util.item.ActionItem;

import com.crimsoncentral.util.item.ItemUtil;

public class WhoDoneItProfile extends GameProfile {

	public WhoDoneItProfile(String name, Integer parent_id, ActionItem action_item) {
		super(name, parent_id, action_item);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setup() {

		addListener(new WhoDoneItListeners());
		addMode(5.01,
				new ModeProfile("normal mystery", 5.01,
						new WhoDoneItMysteryNormal("Who-Done-It-Mystery-Normal-Original-Copy", 5.01, 16, 2),
						new ActionItem("whodoneit normal mystery",
								ItemUtil.newItem(Material.IRON_SWORD, 1, (short) 3,
										ChatColor.YELLOW + "" + ChatColor.BOLD + "Who Done It Mystery Normal", "")) {

							@Override
							public void preform(Player player) {

								player.performCommand("play whodoneit normal mystery");
							}
						},
						new ArenaConfig(1.01).addMultipleSimLines("Team-*-Spawn", "0, 140, 0, 0, 0", 16)));

		
		
		
	}

}
