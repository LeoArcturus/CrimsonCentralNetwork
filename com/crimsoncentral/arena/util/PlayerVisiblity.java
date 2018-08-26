package com.crimsoncentral.arena.util;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerVisiblity implements Listener {

	public static enum Visiblity {

		VISIBLE, HIDDEN_IN_LOBBIES, SPECTATOR, YT_VANISH, STAFF_VANISH, OWNER_INCOGNITIO
	}

	public static HashMap<Player, Visiblity> visiblities = new HashMap<Player, Visiblity>();

	@SuppressWarnings("deprecation")
	public static void updateWorldVisiblity(World w) {

		for (Player player : w.getPlayers()) {

			for (Player p : w.getPlayers()) {
				if (getPlayerVisiblityLevel(p) > getPlayerVisiblityLevel(player)) {

					player.hidePlayer(p);
					p.showPlayer(player);
				} else if (getPlayerVisiblityLevel(p) < getPlayerVisiblityLevel(player)) {

					p.hidePlayer(player);
					player.showPlayer(p);
				} else {

					p.showPlayer(player);
					player.showPlayer(p);
				}

			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void updateServerVisiblity() {

		for (Player player : Bukkit.getOnlinePlayers()) {

			for (Player p : Bukkit.getOnlinePlayers()) {
				if (getPlayerVisiblityLevel(p) > getPlayerVisiblityLevel(player)) {

					player.hidePlayer(p);
					p.showPlayer(player);
				} else if (getPlayerVisiblityLevel(p) < getPlayerVisiblityLevel(player)) {

					p.hidePlayer(player);
					player.showPlayer(p);
				} else {

					p.showPlayer(player);
					player.showPlayer(p);
				}

			}
		}
	}

	public static int getPlayerVisiblityLevel(Player p) {
		Visiblity vp = visiblities.get(p);
		int vp_level = 0;

		if (vp == Visiblity.VISIBLE) {

			vp_level = 1;
		} else if (vp == Visiblity.HIDDEN_IN_LOBBIES) {

			vp_level = 2;
		} else if (vp == Visiblity.YT_VANISH) {

			vp_level = 3;
		} else if (vp == Visiblity.SPECTATOR) {

			vp_level = 4;
		} else if (vp == Visiblity.STAFF_VANISH) {

			vp_level = 5;
		} else if (vp == Visiblity.OWNER_INCOGNITIO) {

			vp_level = 6;
		}

		return vp_level;

	}

	public static void setPlayerVisiblity(Player p, Visiblity v) {

		if (visiblities.containsKey(p) == true) {

			visiblities.remove(p, visiblities.get(p));
			visiblities.put(p, v);
		} else {

			visiblities.put(p, v);
		}

		updateWorldVisiblity(p.getWorld());
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void ChangeWorld(PlayerChangedWorldEvent e) {

		updateWorldVisiblity(e.getPlayer().getWorld());
		for (Player p : Bukkit.getOnlinePlayers()) {

			if(p.getWorld() != e.getPlayer().getWorld()) {
				
				e.getPlayer().hidePlayer(p);
				p.hidePlayer(e.getPlayer());
				
			}
		}
	}

}
