package com.crimsoncentral.server_player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.entity.Player;



public class PlayerManager {

	private static HashMap<Player, ServerPlayer> players = new HashMap<Player, ServerPlayer>();

	public static void addPlayer(Player p, ServerPlayer sp) {

		players.put(p, sp);
	}

	public static ServerPlayer getServerPlayer(Player p) {

		ServerPlayer sp = null;
		Iterator<Entry<Player, ServerPlayer>> it = players.entrySet().iterator();
		while (it.hasNext()) {

			Entry<Player, ServerPlayer> pair = it.next();

			if (pair.getKey() == p) {

				sp = pair.getValue();
				break;
			}

		}

		if (sp == null) {
			
		}

		return sp;
	}

	public static void removeServerPlayer(Player player) {
		players.remove(player, getServerPlayer(player));

	}

}
