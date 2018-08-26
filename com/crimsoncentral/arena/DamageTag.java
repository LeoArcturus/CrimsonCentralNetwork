package com.crimsoncentral.arena;

import java.util.ArrayList;
import org.bukkit.entity.Player;

public class DamageTag {

	public static ArrayList<DamageTag> damage_tags = new ArrayList<DamageTag>();

	public int expire_time;
	public Player damager;
	public Player damaged;

	public DamageTag(int expire_time, Player damager, Player damaged) {

		this.expire_time = expire_time;
		this.damager = damager;
		this.damaged = damaged;
		damage_tags.add(this);
	}

	public static boolean hasDamageTag(Player player) {
		boolean has = false;

		for (DamageTag tag : damage_tags) {

			if (tag.damaged == player) {

				has = true;

				break;
			}
		}

		return has;
	}

	public static DamageTag getDamageTag(Player player) {

		DamageTag tag = null;

		for (DamageTag t : damage_tags) {

			if (t.damaged == player) {

				tag = t;

				break;
			}
		}

		return tag;
	}

}
