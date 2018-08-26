package com.crimsoncentral.arena;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.crimsoncentral.arena.Arena.Conditions;
import com.crimsoncentral.util.other.CoolDown;

public class CustomPvPMechanics implements Listener {

	private static HashMap<Material, Double> damage_values = new HashMap<Material, Double>();
	private static HashMap<Material, Double> knockback_values = new HashMap<Material, Double>();

	@EventHandler
	public static void KnockBackCalculator(EntityDamageByEntityEvent event) {

		for (Arena a : ArenaManager.local_arenas.keySet()) {

			if (a.getWorld().getEntities().contains(event.getEntity())
					&& a.getConditions().contains(Conditions.CAN_PVP)) {

				Entity damaged = event.getEntity();
				Entity damager = event.getDamager();

				Vector playerDirection = damaged.getLocation().getDirection();

				Location targetLocation = damaged.getLocation().subtract(playerDirection); // add the direction to the
																							// player's
				targetLocation.setDirection(damaged.getLocation().subtract(damager.getLocation()).toVector());

				double itemKnockBack = 0;
				double blockResitance = 1;
				double criticalKnockBack = 1.3;

				if (damager.isOnGround()) {
					criticalKnockBack = 1;

				}
				if (damager instanceof Player) {
					Player p = (Player) damager;
					@SuppressWarnings("deprecation")
					ItemStack is = p.getItemInHand();
					Double k = knockback_values.get(is.getType());
					if (is != null && k != null) {

						itemKnockBack = k;

					}

				}
				Material m = damaged.getLocation().getBlock().getType();
				if (m != Material.AIR) {
					if (m == Material.WATER) {
						blockResitance = 0.5;

					} else if (m == Material.LAVA) {
						blockResitance = 0.4;
					} else if (m == Material.VINE || m == Material.SIGN) {
						blockResitance = 0.7;
					} else if (m == Material.GRASS || m == Material.YELLOW_FLOWER) {
						blockResitance = 0.8;
					} else if (m == Material.LONG_GRASS) {
						blockResitance = 0.75;
					}

				}

				double knockBackVelocity = (((damager.getVelocity().length() - damaged.getVelocity().length())
						+ itemKnockBack) * criticalKnockBack) * blockResitance + 1;

				targetLocation.multiply(knockBackVelocity);

				if (!CoolDown.coolDownExists("knockback-" + damaged.getName() + "-" + damager.getName())) {
					new CoolDown("knockback-" + damaged.getName() + "-" + damager.getName(), damaged, 2, 5);
					Random ran = new Random();
					double r = 0.1 + (0.5 - 0.2) * ran.nextDouble();
					damaged.teleport(damaged.getLocation().add(0, r, 0));

					damaged.setVelocity(targetLocation.getDirection().multiply(1.2));
				}

				break;
			}
		}
	}

	public static void setupPvp() {

		/**
		 * Sword Info
		 */
		damage_values.put(Material.WOOD_SWORD, 4.0);

		damage_values.put(Material.GOLD_SWORD, 4.0);

		damage_values.put(Material.STONE_SWORD, 5.0);

		damage_values.put(Material.IRON_SWORD, 6.0);

		damage_values.put(Material.DIAMOND_SWORD, 7.0);

		knockback_values.put(Material.WOOD_SWORD, 1.0);

		knockback_values.put(Material.GOLD_SWORD, 0.9);

		knockback_values.put(Material.STONE_SWORD, 0.8);

		knockback_values.put(Material.IRON_SWORD, 0.7);

		knockback_values.put(Material.DIAMOND_SWORD, 0.6);

		/**
		 * 
		 * Axe Info
		 * 
		 */

		damage_values.put(Material.WOOD_AXE, 2.0);

		damage_values.put(Material.GOLD_AXE, 2.0);

		damage_values.put(Material.STONE_AXE, 2.5);

		damage_values.put(Material.IRON_AXE, 3.0);

		damage_values.put(Material.DIAMOND_AXE, 3.5);

		knockback_values.put(Material.WOOD_AXE, 0.55);

		knockback_values.put(Material.GOLD_AXE, 0.5);

		knockback_values.put(Material.STONE_AXE, 0.45);

		knockback_values.put(Material.IRON_AXE, 0.4);

		knockback_values.put(Material.DIAMOND_AXE, 0.35);

		/**
		 * 
		 * Shovel Info
		 * 
		 */

		damage_values.put(Material.WOOD_SPADE, 1.0);

		damage_values.put(Material.GOLD_SPADE, 1.0);

		damage_values.put(Material.STONE_SPADE, 1.25);

		damage_values.put(Material.IRON_SPADE, 1.5);

		damage_values.put(Material.DIAMOND_SPADE, 1.75);

		knockback_values.put(Material.WOOD_SPADE, 0.3);

		knockback_values.put(Material.GOLD_SPADE, 0.25);

		knockback_values.put(Material.STONE_SPADE, 0.2);

		knockback_values.put(Material.IRON_SPADE, 0.15);

		knockback_values.put(Material.DIAMOND_SPADE, 0.1);

		/**
		 * 
		 * Hoe Info
		 * 
		 */

		damage_values.put(Material.WOOD_HOE, 0.85);

		damage_values.put(Material.GOLD_HOE, 0.85);

		damage_values.put(Material.STONE_HOE, 1.0);

		damage_values.put(Material.IRON_HOE, 1.15);

		damage_values.put(Material.DIAMOND_HOE, 1.35);

		knockback_values.put(Material.WOOD_HOE, 0.09);

		knockback_values.put(Material.GOLD_HOE, 0.09);

		knockback_values.put(Material.STONE_HOE, 0.09);

		knockback_values.put(Material.IRON_HOE, 0.09);

		knockback_values.put(Material.DIAMOND_HOE, 0.09);

	}

}
