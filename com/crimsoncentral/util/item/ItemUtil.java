package com.crimsoncentral.util.item;

import java.util.ArrayList;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class ItemUtil {

	public static ItemStack newItem(ItemStack item, String name) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack newItem(Material m, int ammount, short s, String name, String... lores) {
		ItemStack item = new ItemStack(m, ammount, (short) s);
		 
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		ArrayList<String> lore = new ArrayList<String>();
		for (String str : lores) {
			lore.add(str);
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	@SuppressWarnings("deprecation")
	public static ItemStack newItemStack(Material type, int amt, String name, ItemFlag itemFlag1, ItemFlag itemFlag2,
			ItemFlag itemFlag3, Enchantment enc1, int lev1, Enchantment enc2, int lev2, Enchantment enc3, int lev3,
			PotionType potion, DyeColor dy, String... lores) {
		ItemStack stack = new ItemStack(type, amt);
		ItemMeta im = stack.getItemMeta();
		im.setDisplayName(name);
		if (itemFlag1 != null) {
			im.addItemFlags(itemFlag1);
		}
		if (itemFlag2 != null) {
			im.addItemFlags(itemFlag2);
		}
		if (itemFlag3 != null) {
			im.addItemFlags(itemFlag3);
		}
		if (enc1 != null) {
			im.addEnchant(enc1, lev1, true);
		}
		if (enc2 != null) {
			im.addEnchant(enc2, lev2, true);
		}
		if (enc3 != null) {
			im.addEnchant(enc3, lev3, true);
		}
		if (potion != null) {
			((PotionMeta) im).setBasePotionData(new PotionData(potion));
		}
		if (dy != null) {
			((BannerMeta) im).setBaseColor(dy);
		}
		ArrayList<String> lore = new ArrayList<String>();
		for (String str : lores) {
			lore.add(str);
		}
		im.setLore(lore);
		stack.setItemMeta(im);
		return stack;
	}
}
