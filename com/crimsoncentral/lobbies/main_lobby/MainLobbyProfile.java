package com.crimsoncentral.lobbies.main_lobby;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.crimsoncentral.Main;
import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.game.GameProfile;
import com.crimsoncentral.game.ModeProfile;
import com.crimsoncentral.lobbies.build_server_lobby.BuildServerLobby;
import com.crimsoncentral.lobbies.build_server_lobby.BuildServerLobbyListeners;
import com.crimsoncentral.util.item.ActionItem;
import com.crimsoncentral.util.item.ItemUtil;

public class MainLobbyProfile extends GameProfile {

	public MainLobbyProfile(String name, Integer parent_id, ActionItem action_item) {
		super(name, parent_id, action_item);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setup() {

		addListener(new MainLobbyListeners());
		addMode(0.0, new ModeProfile("main", 0.0, new MainLobby("main_original_copy", 0.0, 50, 1), new ActionItem(
				"lobby main",
				ItemUtil.newItem(Material.BED, 1, (short) 14, ChatColor.RED + "" + ChatColor.BOLD + "Main Lobby", "")) {

			@Override
			public void preform(Player player) {

				player.performCommand("lobby");
			}

		}, null));

		addListener(new BuildServerLobbyListeners());

		Arena bsl = new BuildServerLobby("Build Server Lobby Original Copy", 0.1, 50, 1);
		addMode(0.1, new ModeProfile("main", 0.1, bsl, new ActionItem("lobby build_server",
				ItemUtil.newItem(Material.BED, 1, (short) 14, ChatColor.RED + "" + ChatColor.BOLD + "Main Lobby", "")) {

			@Override
			public void preform(Player player) {

				player.performCommand("lobby");
			}
		}, null));

		if (Main.is_the_build_server == false) {
			bsl.setAllowForNewCopies(false);
		}

	}

}
