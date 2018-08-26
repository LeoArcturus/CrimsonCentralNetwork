package com.crimsoncentral.vespen;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import com.crimsoncentral.arena.util.Hologram;
import com.crimsoncentral.server_player.PlayerManager;

import net.md_5.bungee.api.ChatColor;

public class BanEffect extends Thread {

	@SuppressWarnings({ "unused", "deprecation" })
	public static boolean effect(Player player) throws InterruptedException {

		ArrayList<ArmorStand> stands = new ArrayList<ArmorStand>();
		HashMap<ArmorStand, Double> ls = new HashMap<ArmorStand, Double>();

		BanEffect be = new BanEffect();
		Location lpre = player.getLocation();
		Location l = new Location(lpre.getWorld(), lpre.getX(), lpre.getY() + 5.5, lpre.getZ());

		double radius = 10;

		be.start();

		double d = 0;
		for (int i = 1; i <= 16; ++i) {

			d = d + 22.5;
			ArmorStand a = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);

			a.setArms(true);
			a.setRightArmPose(new EulerAngle(258, 180, 0));
			a.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));

			a.setVisible(false);

			a.teleport(getLocationAroundCircle(l, radius, d).setDirection(lpre.subtract(a.getLocation()).toVector()));
			stands.add(a);
			ls.put(a, d);

		}

		for (ArmorStand a : stands) {

			// for (int i2 = 1; i2 <= 36000; ++i2) {

			a.teleport(a.getLocation().setDirection(player.getLocation().subtract(a.getLocation()).toVector()));

			a.setVelocity(a.getLocation().getDirection().multiply(0.75).setY(1));
			// }

		}

		BanEffect.sleep(175);

		player.getWorld().playEffect(player.getLocation().add(0, 4, 0), Effect.ENDER_SIGNAL, 10);
		player.getWorld().playEffect(player.getLocation().add(0, 2, 0), Effect.ENDER_SIGNAL, 10);
		player.getWorld().playEffect(player.getLocation().add(0, 2, 0), Effect.FIREWORK_SHOOT, 3);

		ArmorStand t = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);

		t.setArms(true);
		t.setRightArmPose(new EulerAngle(90, 0, 0));
		t.setItemInHand(new ItemStack(Material.IRON_SWORD));

		t.setVisible(false);
		t.setInvulnerable(true);

		t.teleport(player.getLocation().add(0, -1, 0));

		Hologram h = new Hologram(
				PlayerManager.getServerPlayer(player).getRank().getPlayerFullName(player).replace("§l", "")
						+ ChatColor.RED + " was banned by " + ChatColor.DARK_PURPLE + ChatColor.BOLD + "VESPEN")
				.spawn(new Location(lpre.getWorld(), lpre.getX(), lpre.getY() + 1.5, lpre.getZ()));
		Hologram h2 = new Hologram(ChatColor.RED + "for hacking or abuse!").spawn(new Location(lpre.getWorld(), lpre.getX(), lpre.getY() + 1, lpre.getZ()));

		BanEffect.sleep(1000);
		for (ArmorStand a : stands) {
			a.setHealth(0);
		}

		be.stop();
		return true;
	}

	public static Location getLocationAroundCircle(Location center, double radius, double angleInRadian) {
		double x = center.getX() + radius * Math.cos(angleInRadian);
		double z = center.getZ() + radius * Math.sin(angleInRadian);
		double y = center.getY();

		Location loc = new Location(center.getWorld(), x, y, z);
		Vector difference = center.toVector().clone().subtract(loc.toVector()); // this sets the returned location's
																				// direction toward the center of the
																				// circle
		loc.setDirection(difference);

		return loc;
	}

}
