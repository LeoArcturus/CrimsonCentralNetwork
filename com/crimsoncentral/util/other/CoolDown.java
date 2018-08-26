package com.crimsoncentral.util.other;

import java.util.HashMap;

import org.bukkit.entity.Entity;

public class CoolDown {

	public static HashMap<String, CoolDown> cool_downs = new HashMap<String, CoolDown>();

	private String name;
	private Entity Entity;
	private int time;
	private int interval;

	public CoolDown(String name, Entity Entity, int time, int interval) {

		this.setName(name);
		this.Entity = Entity;
		this.time = time;
		this.interval = interval;
		cool_downs.put(getName().toLowerCase(), this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Entity getEntity() {
		return Entity;
	}

	public void setEntity(Entity Entity) {
		this.Entity = Entity;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {

		this.time = time;

		if (time <= 0) {

			cool_downs.remove(getName(), this);

		}
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public static CoolDown getCoolDown(String name) {

		return cool_downs.get(name.toLowerCase());
	}

	public static boolean coolDownExists(String name) {

		boolean b = false;

		if (cool_downs.get(name.toLowerCase()) != null) {

			b = true;

		}

		return b;

	}

}
