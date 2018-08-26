package com.crimsoncentral.games.duels.solos;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import com.crimsoncentral.arena.util.kits.Kit;
import com.crimsoncentral.arena.util.kits.Kit.KitSlotType;
import com.crimsoncentral.arena.util.kits.KitInventory;
import com.crimsoncentral.util.item.ItemUtil;

public class DuelsUtil {
	public static KitInventory duels_modes = new KitInventory(
			Bukkit.createInventory(null, 36, ChatColor.RED + "Duels Items"));

	public static HashMap<Double, Kit> kits = new HashMap<Double, Kit>();

	static boolean setup = false;
	
	public static void setUpKits() {

		if (setup != true) {
			duels_modes.opener = ItemUtil.newItem(Material.IRON_SWORD, 1, (short) 0, " ", "");
			setupSoloKits();
			setupDoublesKits();
			duels_modes.registerKitInventory();
		}
		
		setup = true;

	}

	public static void setupSoloKits() {

		normalSolo();
		archerSolo();
		overpoweredSolo();
		noDeBuffSolo();

	}
	
	public static void setupDoublesKits() {

		normalDoubles();
		archerDoubles();
		overpoweredDoubles();
		noDeBuffDoubles();

	}

	private static void normalSolo() {
		Kit normal_solo = new Kit("Normal Solo", Material.FISHING_ROD);

		ItemStack splashha1 = new ItemStack(Material.SPLASH_POTION);
		ItemMeta splashhameta1 = (PotionMeta) splashha1.getItemMeta();
		((PotionMeta) splashhameta1).setBasePotionData(new PotionData(PotionType.REGEN));

		ItemStack splashha2 = new ItemStack(Material.SPLASH_POTION);
		ItemMeta splashhameta2 = (PotionMeta) splashha2.getItemMeta();
		((PotionMeta) splashhameta2).setBasePotionData(new PotionData(PotionType.SPEED));

		splashha1.setItemMeta(splashhameta1);

		splashha2.setItemMeta(splashhameta2);

		normal_solo.addItem(17, null, new ItemStack(Material.ARROW, 16));

		normal_solo.addItem(null, KitSlotType.HELMET, new ItemStack(Material.IRON_HELMET, 1));
		normal_solo.addItem(null, KitSlotType.CHESTPLATE, new ItemStack(Material.IRON_CHESTPLATE, 1));
		normal_solo.addItem(null, KitSlotType.LEGGINGS, new ItemStack(Material.IRON_LEGGINGS, 1));
		normal_solo.addItem(null, KitSlotType.BOOTS, new ItemStack(Material.IRON_BOOTS, 1));

		normal_solo.addItem(0, null, new ItemStack(Material.IRON_SWORD, 1));
		normal_solo.addItem(1, null, new ItemStack(Material.BOW, 1));
		normal_solo.addItem(2, null, new ItemStack(Material.FISHING_ROD, 1));
		normal_solo.addItem(3, null, new ItemStack(Material.FLINT_AND_STEEL, 1));
		normal_solo.addItem(4, null, new ItemStack(Material.WATER_BUCKET, 1));
		normal_solo.addItem(5, null, new ItemStack(Material.LAVA_BUCKET, 1));
		normal_solo.addItem(6, null, new ItemStack(Material.GOLDEN_APPLE, 3));
		normal_solo.addItem(7, null, splashha1);
		normal_solo.addItem(8, null, splashha2);
		kits.put(4.01, normal_solo);

		normal_solo.createKit(duels_modes);

	}

	private static void archerSolo() {
		Kit archer_solo = new Kit("Archer Solo", Material.BOW);
		archer_solo.addItem(null, KitSlotType.HELMET, new ItemStack(Material.LEATHER_HELMET, 1));
		archer_solo.addItem(null, KitSlotType.CHESTPLATE, new ItemStack(Material.LEATHER_CHESTPLATE, 1));
		archer_solo.addItem(null, KitSlotType.LEGGINGS, new ItemStack(Material.LEATHER_LEGGINGS, 1));
		archer_solo.addItem(null, KitSlotType.BOOTS, new ItemStack(Material.LEATHER_BOOTS, 1));

		ItemStack bow = new ItemStack(Material.BOW, 1);

		bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		archer_solo.addItem(0, null, bow);
		archer_solo.addItem(1, null, new ItemStack(Material.GOLDEN_APPLE, 3));
		archer_solo.addItem(2, null, new ItemStack(Material.ENDER_PEARL, 4));

		archer_solo.addItem(17, null, new ItemStack(Material.ARROW));
		kits.put(4.02, archer_solo);
		archer_solo.createKit(duels_modes);
	}

	private static void overpoweredSolo() {
		Kit overpowered_solo = new Kit("Overpowered Solo", Material.DIAMOND_CHESTPLATE);

		ItemStack leatherHelm = new ItemStack(Material.DIAMOND_HELMET, 1);

		ItemMeta a1 = leatherHelm.getItemMeta();

		a1.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);

		ItemStack leatherChest = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);

		ItemMeta a2 = leatherChest.getItemMeta();

		a2.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);

		ItemStack leatherLeg = new ItemStack(Material.DIAMOND_LEGGINGS, 1);

		ItemMeta a3 = leatherLeg.getItemMeta();

		a3.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);

		ItemStack leatherBoots = new ItemStack(Material.DIAMOND_BOOTS, 1);

		ItemMeta a4 = leatherBoots.getItemMeta();

		a4.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);

		leatherHelm.setItemMeta(a1);
		leatherChest.setItemMeta(a2);
		leatherLeg.setItemMeta(a3);
		leatherBoots.setItemMeta(a4);

		ItemStack stonedord = new ItemStack(Material.DIAMOND_SWORD, 1);

		ItemMeta a5 = stonedord.getItemMeta();

		a5.addEnchant(Enchantment.DAMAGE_ALL, 2, true);

		ItemStack splashha1 = new ItemStack(Material.SPLASH_POTION);
		ItemMeta splashhameta1 = (PotionMeta) splashha1.getItemMeta();
		((PotionMeta) splashhameta1).setBasePotionData(new PotionData(PotionType.REGEN));

		ItemStack splashha2 = new ItemStack(Material.SPLASH_POTION);
		ItemMeta splashhameta2 = (PotionMeta) splashha2.getItemMeta();
		((PotionMeta) splashhameta2).setBasePotionData(new PotionData(PotionType.SPEED));

		splashha1.setItemMeta(splashhameta1);

		splashha2.setItemMeta(splashhameta2);

		overpowered_solo.addItem(null, KitSlotType.HELMET, leatherHelm);
		overpowered_solo.addItem(null, KitSlotType.CHESTPLATE, leatherChest);
		overpowered_solo.addItem(null, KitSlotType.LEGGINGS, leatherLeg);
		overpowered_solo.addItem(null, KitSlotType.BOOTS, leatherBoots);
		overpowered_solo.addItem(0, null, new ItemStack(Material.IRON_SWORD, 1));
		overpowered_solo.addItem(1, null, new ItemStack(Material.BOW, 1));
		overpowered_solo.addItem(2, null, new ItemStack(Material.FISHING_ROD, 1));
		overpowered_solo.addItem(3, null, new ItemStack(Material.FLINT_AND_STEEL, 1));
		overpowered_solo.addItem(4, null, new ItemStack(Material.WATER_BUCKET, 1));
		overpowered_solo.addItem(5, null, new ItemStack(Material.LAVA_BUCKET, 1));
		overpowered_solo.addItem(6, null, new ItemStack(Material.GOLDEN_APPLE, 3));
		overpowered_solo.addItem(7, null, splashha1);
		overpowered_solo.addItem(8, null, splashha2);

		kits.put(4.03, overpowered_solo);

		overpowered_solo.createKit(duels_modes);
	}

	private static void noDeBuffSolo() {
		Kit no_de_buff_solo = new Kit("NoDeBuff Solo", Material.DIAMOND_CHESTPLATE);

		ItemStack h = new ItemStack(Material.DIAMOND_HELMET, 1);

		h.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		ItemStack c = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);

		c.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		ItemStack l = new ItemStack(Material.DIAMOND_LEGGINGS, 1);

		l.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		ItemStack b = new ItemStack(Material.DIAMOND_BOOTS, 1);

		b.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		ItemStack s = new ItemStack(Material.DIAMOND_SWORD, 1);

		s.addEnchantment(Enchantment.DAMAGE_ALL, 2);

		ItemStack splashha1 = new ItemStack(Material.SPLASH_POTION);
		ItemMeta splashhameta1 = (PotionMeta) splashha1.getItemMeta();
		((PotionMeta) splashhameta1).setBasePotionData(new PotionData(PotionType.REGEN));

		ItemStack splashha2 = new ItemStack(Material.SPLASH_POTION);
		ItemMeta splashhameta2 = (PotionMeta) splashha2.getItemMeta();
		((PotionMeta) splashhameta2).setBasePotionData(new PotionData(PotionType.SPEED));

		ItemStack splashha3 = new ItemStack(Material.SPLASH_POTION);
		ItemMeta splashhameta3 = (PotionMeta) splashha3.getItemMeta();
		((PotionMeta) splashhameta3).setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));

		splashha1.setItemMeta(splashhameta1);
		splashha2.setItemMeta(splashhameta2);
		splashha3.setItemMeta(splashhameta3);

		no_de_buff_solo.addItem(null, KitSlotType.HELMET, h);
		no_de_buff_solo.addItem(null, KitSlotType.CHESTPLATE, c);
		no_de_buff_solo.addItem(null, KitSlotType.LEGGINGS, l);
		no_de_buff_solo.addItem(null, KitSlotType.BOOTS, b);

		no_de_buff_solo.addItem(0, null, s);
		no_de_buff_solo.addItem(1, null, new ItemStack(Material.GOLDEN_APPLE));
		no_de_buff_solo.addItem(2, null, new ItemStack(Material.FISHING_ROD));
		no_de_buff_solo.addItem(3, null, splashha1);
		no_de_buff_solo.addItem(4, null, splashha2);
		no_de_buff_solo.addItem(5, null, splashha3);
		no_de_buff_solo.addItem(6, null, splashha1);
		no_de_buff_solo.addItem(7, null, splashha2);
		no_de_buff_solo.addItem(8, null, splashha3);

		for (int i = 9; i <= 17; ++i) {

			no_de_buff_solo.addItem(i, null, splashha1);

		}

		for (int i = 18; i <= 26; ++i) {

			no_de_buff_solo.addItem(i, null, splashha2);

		}

		for (int i = 27; i <= 35; ++i) {

			no_de_buff_solo.addItem(i, null, splashha3);

		}

		kits.put(4.04, no_de_buff_solo);

		no_de_buff_solo.createKit(duels_modes);

	}

	private static void normalDoubles() {
		Kit normal_Doubles = new Kit("Normal Doubles", Material.FISHING_ROD);

		ItemStack splashha1 = new ItemStack(Material.SPLASH_POTION);
		ItemMeta splashhameta1 = (PotionMeta) splashha1.getItemMeta();
		((PotionMeta) splashhameta1).setBasePotionData(new PotionData(PotionType.REGEN));

		ItemStack splashha2 = new ItemStack(Material.SPLASH_POTION);
		ItemMeta splashhameta2 = (PotionMeta) splashha2.getItemMeta();
		((PotionMeta) splashhameta2).setBasePotionData(new PotionData(PotionType.SPEED));

		splashha1.setItemMeta(splashhameta1);

		splashha2.setItemMeta(splashhameta2);

		normal_Doubles.addItem(17, null, new ItemStack(Material.ARROW, 16));

		normal_Doubles.addItem(null, KitSlotType.HELMET, new ItemStack(Material.IRON_HELMET, 1));
		normal_Doubles.addItem(null, KitSlotType.CHESTPLATE, new ItemStack(Material.IRON_CHESTPLATE, 1));
		normal_Doubles.addItem(null, KitSlotType.LEGGINGS, new ItemStack(Material.IRON_LEGGINGS, 1));
		normal_Doubles.addItem(null, KitSlotType.BOOTS, new ItemStack(Material.IRON_BOOTS, 1));

		normal_Doubles.addItem(0, null, new ItemStack(Material.IRON_SWORD, 1));
		normal_Doubles.addItem(1, null, new ItemStack(Material.BOW, 1));
		normal_Doubles.addItem(2, null, new ItemStack(Material.FISHING_ROD, 1));
		normal_Doubles.addItem(3, null, new ItemStack(Material.FLINT_AND_STEEL, 1));
		normal_Doubles.addItem(4, null, new ItemStack(Material.WATER_BUCKET, 1));
		normal_Doubles.addItem(5, null, new ItemStack(Material.LAVA_BUCKET, 1));
		normal_Doubles.addItem(6, null, new ItemStack(Material.GOLDEN_APPLE, 3));
		normal_Doubles.addItem(7, null, splashha1);
		normal_Doubles.addItem(8, null, splashha2);
		kits.put(4.05, normal_Doubles);

		normal_Doubles.createKit(duels_modes);

	}

	private static void archerDoubles() {
		Kit archer_Doubles = new Kit("Archer Doubles", Material.BOW);
		archer_Doubles.addItem(null, KitSlotType.HELMET, new ItemStack(Material.LEATHER_HELMET, 1));
		archer_Doubles.addItem(null, KitSlotType.CHESTPLATE, new ItemStack(Material.LEATHER_CHESTPLATE, 1));
		archer_Doubles.addItem(null, KitSlotType.LEGGINGS, new ItemStack(Material.LEATHER_LEGGINGS, 1));
		archer_Doubles.addItem(null, KitSlotType.BOOTS, new ItemStack(Material.LEATHER_BOOTS, 1));

		ItemStack bow = new ItemStack(Material.BOW, 1);

		bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		archer_Doubles.addItem(0, null, bow);
		archer_Doubles.addItem(1, null, new ItemStack(Material.GOLDEN_APPLE, 3));
		archer_Doubles.addItem(2, null, new ItemStack(Material.ENDER_PEARL, 4));

		archer_Doubles.addItem(17, null, new ItemStack(Material.ARROW));
		kits.put(4.06, archer_Doubles);
		archer_Doubles.createKit(duels_modes);
	}

	private static void overpoweredDoubles() {
		Kit overpowered_Doubles = new Kit("Overpowered Doubles", Material.DIAMOND_CHESTPLATE);

		ItemStack leatherHelm = new ItemStack(Material.DIAMOND_HELMET, 1);

		ItemMeta a1 = leatherHelm.getItemMeta();

		a1.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);

		ItemStack leatherChest = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);

		ItemMeta a2 = leatherChest.getItemMeta();

		a2.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);

		ItemStack leatherLeg = new ItemStack(Material.DIAMOND_LEGGINGS, 1);

		ItemMeta a3 = leatherLeg.getItemMeta();

		a3.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);

		ItemStack leatherBoots = new ItemStack(Material.DIAMOND_BOOTS, 1);

		ItemMeta a4 = leatherBoots.getItemMeta();

		a4.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);

		leatherHelm.setItemMeta(a1);
		leatherChest.setItemMeta(a2);
		leatherLeg.setItemMeta(a3);
		leatherBoots.setItemMeta(a4);

		ItemStack stonedord = new ItemStack(Material.DIAMOND_SWORD, 1);

		ItemMeta a5 = stonedord.getItemMeta();

		a5.addEnchant(Enchantment.DAMAGE_ALL, 2, true);

		ItemStack splashha1 = new ItemStack(Material.SPLASH_POTION);
		ItemMeta splashhameta1 = (PotionMeta) splashha1.getItemMeta();
		((PotionMeta) splashhameta1).setBasePotionData(new PotionData(PotionType.REGEN));

		ItemStack splashha2 = new ItemStack(Material.SPLASH_POTION);
		ItemMeta splashhameta2 = (PotionMeta) splashha2.getItemMeta();
		((PotionMeta) splashhameta2).setBasePotionData(new PotionData(PotionType.SPEED));

		splashha1.setItemMeta(splashhameta1);

		splashha2.setItemMeta(splashhameta2);

		overpowered_Doubles.addItem(null, KitSlotType.HELMET, leatherHelm);
		overpowered_Doubles.addItem(null, KitSlotType.CHESTPLATE, leatherChest);
		overpowered_Doubles.addItem(null, KitSlotType.LEGGINGS, leatherLeg);
		overpowered_Doubles.addItem(null, KitSlotType.BOOTS, leatherBoots);
		overpowered_Doubles.addItem(0, null, new ItemStack(Material.IRON_SWORD, 1));
		overpowered_Doubles.addItem(1, null, new ItemStack(Material.BOW, 1));
		overpowered_Doubles.addItem(2, null, new ItemStack(Material.FISHING_ROD, 1));
		overpowered_Doubles.addItem(3, null, new ItemStack(Material.FLINT_AND_STEEL, 1));
		overpowered_Doubles.addItem(4, null, new ItemStack(Material.WATER_BUCKET, 1));
		overpowered_Doubles.addItem(5, null, new ItemStack(Material.LAVA_BUCKET, 1));
		overpowered_Doubles.addItem(6, null, new ItemStack(Material.GOLDEN_APPLE, 3));
		overpowered_Doubles.addItem(7, null, splashha1);
		overpowered_Doubles.addItem(8, null, splashha2);

		kits.put(4.07, overpowered_Doubles);

		overpowered_Doubles.createKit(duels_modes);
	}

	private static void noDeBuffDoubles() {
		Kit no_de_buff_Doubles = new Kit("NoDeBuff Doubles", Material.DIAMOND_CHESTPLATE);

		ItemStack h = new ItemStack(Material.DIAMOND_HELMET, 1);

		h.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		ItemStack c = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);

		c.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		ItemStack l = new ItemStack(Material.DIAMOND_LEGGINGS, 1);

		l.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		ItemStack b = new ItemStack(Material.DIAMOND_BOOTS, 1);

		b.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

		ItemStack s = new ItemStack(Material.DIAMOND_SWORD, 1);

		s.addEnchantment(Enchantment.DAMAGE_ALL, 2);

		ItemStack splashha1 = new ItemStack(Material.SPLASH_POTION);
		ItemMeta splashhameta1 = (PotionMeta) splashha1.getItemMeta();
		((PotionMeta) splashhameta1).setBasePotionData(new PotionData(PotionType.REGEN));

		ItemStack splashha2 = new ItemStack(Material.SPLASH_POTION);
		ItemMeta splashhameta2 = (PotionMeta) splashha2.getItemMeta();
		((PotionMeta) splashhameta2).setBasePotionData(new PotionData(PotionType.SPEED));

		ItemStack splashha3 = new ItemStack(Material.SPLASH_POTION);
		ItemMeta splashhameta3 = (PotionMeta) splashha3.getItemMeta();
		((PotionMeta) splashhameta3).setBasePotionData(new PotionData(PotionType.INSTANT_HEAL));

		splashha1.setItemMeta(splashhameta1);
		splashha2.setItemMeta(splashhameta2);
		splashha3.setItemMeta(splashhameta3);

		no_de_buff_Doubles.addItem(null, KitSlotType.HELMET, h);
		no_de_buff_Doubles.addItem(null, KitSlotType.CHESTPLATE, c);
		no_de_buff_Doubles.addItem(null, KitSlotType.LEGGINGS, l);
		no_de_buff_Doubles.addItem(null, KitSlotType.BOOTS, b);

		no_de_buff_Doubles.addItem(0, null, s);
		no_de_buff_Doubles.addItem(1, null, new ItemStack(Material.GOLDEN_APPLE));
		no_de_buff_Doubles.addItem(2, null, new ItemStack(Material.FISHING_ROD));
		no_de_buff_Doubles.addItem(3, null, splashha1);
		no_de_buff_Doubles.addItem(4, null, splashha2);
		no_de_buff_Doubles.addItem(5, null, splashha3);
		no_de_buff_Doubles.addItem(6, null, splashha1);
		no_de_buff_Doubles.addItem(7, null, splashha2);
		no_de_buff_Doubles.addItem(8, null, splashha3);

		for (int i = 9; i <= 17; ++i) {

			no_de_buff_Doubles.addItem(i, null, splashha1);

		}

		for (int i = 18; i <= 26; ++i) {

			no_de_buff_Doubles.addItem(i, null, splashha2);

		}

		for (int i = 27; i <= 35; ++i) {

			no_de_buff_Doubles.addItem(i, null, splashha3);

		}

		kits.put(4.08, no_de_buff_Doubles);

		no_de_buff_Doubles.createKit(duels_modes);

	}

}
