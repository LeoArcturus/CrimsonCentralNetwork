package com.crimsoncentral.games.egg_wars;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.crimsoncentral.game.GameProfile;
import com.crimsoncentral.game.ModeProfile;
import com.crimsoncentral.util.ArenaConfig;
import com.crimsoncentral.util.item.ActionItem;

import com.crimsoncentral.util.item.ItemUtil;

public class EggWarsProfile extends GameProfile {

	public EggWarsProfile(String name, Integer parent_id, ActionItem action_item) {
		super(name, parent_id, action_item);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setup() {

		addListener(new EggWarsListeners());

		addMode(6.01,
				new ModeProfile("normal solo", 6.01, new EggWarsSolo("Egg-Wars-Solo-Original-Copy", 6.01, 8, 1),
						new ActionItem("eggwars normal solo", ItemUtil.newItem(Material.DRAGON_EGG, 1, (short) 3,
								ChatColor.YELLOW + "" + ChatColor.BOLD + "Egg Wars Solo", "")) {

							@Override
							public void preform(Player player) {

								player.performCommand("play eggwars normal solo");
							}
						},

						new ArenaConfig(6.01).addLine("Red-Team-Spawn", "0, 140, 0, 0, 0")
								.addLine("Blue-Team-Spawn", "0, 140, 0, 0, 0")
								.addLine("Yellow-Team-Spawn", "0, 140, 0, 0, 0")
								.addLine("Green-Team-Spawn", "0, 140, 0, 0, 0")
								.addLine("Purple-Team-Spawn", "0, 140, 0, 0, 0")
								.addLine("Orange-Team-Spawn", "0, 140, 0, 0, 0")
								.addLine("Black-Team-Spawn", "0, 140, 0, 0, 0")
								.addLine("White-Team-Spawn", "0, 140, 0, 0, 0")
								.addLine("Red-Team-Generator", "0, 140, 0, 0, 0")
								.addLine("Blue-Team-Generator", "0, 140, 0, 0, 0")
								.addLine("Yellow-Team-Generator", "0, 140, 0, 0, 0")
								.addLine("Green-Team-Generator", "0, 140, 0, 0, 0")
								.addLine("Purple-Team-Generator", "0, 140, 0, 0, 0")
								.addLine("Orange-Team-Generator", "0, 140, 0, 0, 0")
								.addLine("Black-Team-Generator", "0, 140, 0, 0, 0")
								.addLine("White-Team-Generator", "0, 140, 0, 0, 0")
								.addLine("Red-Team-Egg", "0, 140, 0, 0, 0").addLine("Blue-Team-Egg", "0, 140, 0, 0, 0")
								.addLine("Yellow-Team-Egg", "0, 140, 0, 0, 0")
								.addLine("Green-Team-Egg", "0, 140, 0, 0, 0")
								.addLine("Purple-Team-Egg", "0, 140, 0, 0, 0")
								.addLine("Orange-Team-Egg", "0, 140, 0, 0, 0")
								.addLine("Black-Team-Egg", "0, 140, 0, 0, 0")
								.addLine("White-Team-Egg", "0, 140, 0, 0, 0")
								.addMultipleSimLines("Lapis-Generator-*", "0, 140, 0, 0, 0", 4)
								.addMultipleSimLines("Diamond-Generator-*", "0, 140, 0, 0, 0", 4)
								.addMultipleSimLines("Emerald-Generator-*", "0, 140, 0, 0, 0", 4)));

	}

}
