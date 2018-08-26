package com.crimsoncentral.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.crimsoncentral.Main;
import com.crimsoncentral.game.GameProfile;
import com.crimsoncentral.games.duels.DuelsProfile;
import com.crimsoncentral.games.egg_wars.EggWarsProfile;
import com.crimsoncentral.games.hunger_games.HungerGamesProfile;
import com.crimsoncentral.games.sky_wars.modes.SkyWarsProfile;
import com.crimsoncentral.lobbies.main_lobby.MainLobbyProfile;
import com.crimsoncentral.util.item.ActionItem;
import com.crimsoncentral.util.item.ActionItemManager;

import com.crimsoncentral.util.item.ItemUtil;

import net.md_5.bungee.api.ChatColor;

public class ArenaRegister {

	public static HashMap<Integer, GameProfile> game_profiles = new HashMap<Integer, GameProfile>();

	public static void registerAllGames() {

		if (Main.is_the_build_server == false) {
			GameProfile skywars = new SkyWarsProfile("skywars", 1,
					new ActionItem("skywars", ItemUtil.newItem(Material.EYE_OF_ENDER, 1, (short) 0,
							ChatColor.AQUA + "" + ChatColor.BOLD + "Sky Wars", "")) {

						@Override
						public void preform(Player player) {

							player.performCommand("lobby skywars");
						}
					});
			skywars.setup();

			GameProfile hungergames = new HungerGamesProfile("hungergames", 2, new ActionItem("hungergames", ItemUtil
					.newItem(Material.CHEST, 1, (short) 0, ChatColor.RED + "" + ChatColor.BOLD + "Hunger Games", "")) {

				@Override
				public void preform(Player player) {

					player.performCommand("lobby hungergames");
				}
			});
			hungergames.setup();

			// GameProfile thewalls = new TheWallsProfile("thewalls", 3,
			// new CommandItem("thewalls",
			// ItemUtil.newItem(Material.FIREBALL, 1, (short) 0,
			// ChatColor.DARK_RED + "" + ChatColor.BOLD + "The Walls", ""),
			// "lobby thewalls", SenderType.PLAYER));
			// thewalls.setup();
			//
			GameProfile duels = new DuelsProfile("duels", 4,
					new ActionItem("duels", ItemUtil.newItem(Material.IRON_SWORD, 1, (short) 0,
							ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "Duels", "")) {

						@Override
						public void preform(Player player) {

							player.performCommand("lobby duels");
						}
					});
			duels.setup();
			//
			// GameProfile whodoneit = new WhoDoneItProfile("whonedoneit", 5,
			// new CommandItem("whodoneit",
			// ItemUtil.newItem(Material.DIAMOND_SWORD, 1, (short) 0,
			// ChatColor.YELLOW + "" + ChatColor.BOLD + "Who Done It", ""),
			// "lobby whodoneit", SenderType.PLAYER));
			// whodoneit.setup();
			//
			GameProfile eggwars = new EggWarsProfile("eggwars", 6,
					new ActionItem("eggwars", ItemUtil.newItem(Material.DRAGON_EGG, 1, (short) 0,
							ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Egg Wars", "")) {

						@Override
						public void preform(Player player) {

							player.performCommand("lobby eggwars");
						}
					});
			eggwars.setup();

			// Arena hunger_games_nokits_solo = new
			// HungerGamesNoKitsSolo("Hunger-Games-No-Kits-Solo-Original-Copy", 2.02, 24,
			// 5);
			//
			// games.put(hunger_games_nokits_solo.getGamemode(), hunger_games_nokits_solo);
			//
			// Arena hunger_games_kits_teams = new
			// HungerGamesKitsTeams("Hunger-Games-Kits-Teams-Original-Copy", 2.03, 24,
			// 5);
			//
			// games.put(hunger_games_kits_teams.getGamemode(), hunger_games_kits_teams);
			//
			//
			// Arena hunger_games_nokits_teams = new
			// HungerGamesNoKitsteams("Hunger-Games-No-Kits-Teams-Original-Copy", 2.04, 24,
			// 5);
			//
			// games.put(hunger_games_nokits_teams.getGamemode(),
			// hunger_games_nokits_teams);
		}
	}

	public static void registerAllLobbies() {

		GameProfile main = new MainLobbyProfile("Main Lobby", 0,
				ActionItemManager.getActionItem("return to main lobby bed"));
		main.setup();

	}

	public static ArrayList<GameProfile> getGameProfiles() {

		ArrayList<GameProfile> profiles = new ArrayList<GameProfile>();

		Iterator<Entry<Integer, GameProfile>> it = game_profiles.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, GameProfile> pair = it.next();

			profiles.add(pair.getValue());

		}

		return profiles;

	}

	public static GameProfile getGameProfile(int mode) {

		GameProfile gp = null;

		for (GameProfile g : getGameProfiles()) {

			if (g.getParentId() == mode) {
				gp = g;
				break;

			}
		}

		return gp;
	}

}
