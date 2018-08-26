
package com.crimsoncentral.vespen.verspohl_stockpilers;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import com.comphenix.protocol.events.PacketEvent;
import com.crimsoncentral.vespen.Vespen;
import com.crimsoncentral.vespen.VespenPlayer;

public class Events {

	@EventHandler
	public void onPacketReceive(PacketEvent e) {

	}

	@EventHandler
	public void OnPlayerLoginEvent(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		Vespen.addVespenPlayer(p);

	}

	@SuppressWarnings("unused")
	@EventHandler
	public void OnComabtEvent(EntityDamageByEntityEvent event) {

		if (event.getDamager() instanceof Player) {

			Player p = (Player) event.getDamager();
			Entity e = event.getEntity();

			Location d1 = e.getLocation();
			Location d2 = p.getLocation();

			double x = Math.pow(Math.abs(d2.getX() - d1.getX()), 2);
			double z = Math.pow(Math.abs(d2.getZ() - d1.getZ()), 2);
			double q = Math.pow(x + z, 0.5);
			double theta = (Math.PI * (90 - Math.abs(d2.getPitch()))) / 180;
			double radian = Math.sin(theta);

			
			Double reach = q * radian;
			Date dNow = new Date();
			SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddhhmmssSSS");

			int time = Integer.valueOf(ft.format(dNow));

			VespenPlayer vp = Vespen.getVespenPlayer(p);
			
//			vp.getCombatProfile().addNewLog(
//					new CombatLog(p, e, reach, null, Double.valueOf(d2.getYaw()), Double.valueOf(d2.getPitch()), time,
//							TPSLag.TICK_COUNT, ((CraftPlayer) p).getHandle().ping, TPSLag.getTPS()));

		}

	}

}
