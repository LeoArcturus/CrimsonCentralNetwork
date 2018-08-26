package com.crimsoncentral.lobbies.main_lobby;

import java.util.ArrayList;


import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.server_player.ServerPlayer;

public class MainLobbyListeners implements Listener {

	private static ArrayList<Player> doubleJumpers = new ArrayList<Player>();

	@EventHandler
	public void SlimeBlockLuancher(PlayerMoveEvent e) {

		ServerPlayer sp = PlayerManager.getServerPlayer(e.getPlayer());
		if (sp != null) {

			Arena a = PlayerManager.getServerPlayer(e.getPlayer()).getArena();
			if (a != null) {

				if (a.getGamemode() == 0.0) {
					if (e.getTo().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SLIME_BLOCK) {
						e.getTo().getBlock().getWorld().playSound(e.getPlayer().getLocation(),
								Sound.ENTITY_FIREWORK_SHOOT, 0.35F, 1.0F);
						e.getTo().getBlock().getWorld().playEffect(e.getPlayer().getLocation(),
								Effect.MOBSPAWNER_FLAMES, 3);

						e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().multiply(1.47).setY(0.65));
					}
				}

				if (doubleJumpers.contains(e.getPlayer()) && e.getPlayer().isOnGround()) {

					doubleJumpers.remove(e.getPlayer());
					e.getPlayer().setFlying(false);
				}
			}

		}
	}

	@EventHandler
	public void DoubbleJump(PlayerToggleFlightEvent event) {

		Player p = event.getPlayer();
		ServerPlayer sp = PlayerManager.getServerPlayer(p);
		if (sp != null) {

			Arena a = PlayerManager.getServerPlayer(p).getArena();
			if (a != null) {

				if (a.getGamemode() == 0.0) {

					if (!doubleJumpers.contains(p) && !p.isFlying()
							&& event.getPlayer().getGameMode() != GameMode.CREATIVE) {
						p.setFlying(false);

						
						p.playSound(p.getLocation(), Sound.ENTITY_IRONGOLEM_ATTACK, 10, -10);
						event.setCancelled(true);
						Vector v = p.getLocation().getDirection().multiply(2).setY(1);
						p.setVelocity(v);
						doubleJumpers.add(p);
					}
				}
			}
		}
	}

}
