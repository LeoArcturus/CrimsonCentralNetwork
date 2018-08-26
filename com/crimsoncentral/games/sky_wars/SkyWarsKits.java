package com.crimsoncentral.games.sky_wars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.crimsoncentral.arena.util.kits.Kit;
import com.crimsoncentral.arena.util.kits.KitInventory;
import com.crimsoncentral.arena.util.kits.Kit.KitSlotType;

public class SkyWarsKits {

	public static KitInventory solo_kit_selector = new KitInventory(
			Bukkit.createInventory(null, 36, ChatColor.RED + "Sky Wars Solo Kit Selector"));

	static boolean kits_setup = false;

	public static void setupSoloKitSelector() {

		if (kits_setup != true) {

			SkyWarsKits.solo_kit_selector.opener = new ItemStack(Material.CHEST, 1);

			ItemMeta meta = SkyWarsKits.solo_kit_selector.opener.getItemMeta();
			meta.setDisplayName(ChatColor.RED + "Sky Wars Solo Kit Selector");

			SkyWarsKits.solo_kit_selector.opener.setItemMeta(meta);

			Kit default_kit = new Kit("Default", Material.WOOD_PICKAXE);

			default_kit.addItem(0, null, new ItemStack(Material.WOOD_PICKAXE, 1));
			default_kit.addItem(1, null, new ItemStack(Material.WOOD_SPADE, 1));
			default_kit.addItem(2, null, new ItemStack(Material.WOOD_AXE, 1));

			default_kit.createKit(SkyWarsKits.solo_kit_selector);

			Kit warrior = new Kit("Warrior", Material.LEATHER_HELMET);

			warrior.addItem(0, null, new ItemStack(Material.WOOD_SWORD, 1));
			warrior.addItem(1, null, new ItemStack(Material.GLASS, 16));
			warrior.addItem(103, KitSlotType.HELMET, new ItemStack(Material.LEATHER_HELMET, 1));
			warrior.addItem(102, KitSlotType.CHESTPLATE, new ItemStack(Material.LEATHER_CHESTPLATE, 1));
			warrior.addItem(101, KitSlotType.LEGGINGS, new ItemStack(Material.LEATHER_LEGGINGS, 1));
			warrior.addItem(100, KitSlotType.BOOTS, new ItemStack(Material.LEATHER_BOOTS, 1));

			warrior.createKit(SkyWarsKits.solo_kit_selector);

			Kit archer = new Kit("Archer", Material.BOW);

			archer.addItem(0, null, new ItemStack(Material.BOW, 1));
			archer.addItem(1, null, new ItemStack(Material.ARROW, 10));

			archer.createKit(SkyWarsKits.solo_kit_selector);

			Kit armor = new Kit("Armor", Material.GOLD_CHESTPLATE);

			armor.addItem(0, KitSlotType.HELMET, new ItemStack(Material.GOLD_HELMET, 1));
			armor.addItem(1, KitSlotType.CHESTPLATE, new ItemStack(Material.GOLD_CHESTPLATE, 1));
			armor.addItem(1, KitSlotType.LEGGINGS, new ItemStack(Material.GOLD_LEGGINGS, 1));
			armor.addItem(1, KitSlotType.BOOTS, new ItemStack(Material.GOLD_BOOTS, 1));

			armor.createKit(SkyWarsKits.solo_kit_selector);

			ItemStack speed_pots = new ItemStack(Material.SPLASH_POTION, 1);

			PotionMeta p_meta = (PotionMeta) speed_pots.getItemMeta();

			p_meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1600, 3), true);

			Kit bolt = new Kit("Bolt", speed_pots.getType());

			speed_pots.setAmount(3);

			speed_pots.setItemMeta(p_meta);

			bolt.addItem(0, null, speed_pots);

			bolt.createKit(SkyWarsKits.solo_kit_selector);

			SkyWarsKits.solo_kit_selector.registerKitInventory();
			kits_setup = true;
		}
	}

}
