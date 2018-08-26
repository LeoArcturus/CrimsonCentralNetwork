package com.crimsoncentral.arena.game_events;

import org.bukkit.Location;

import org.bukkit.entity.LivingEntity;

public class SuddenDeath {

	LivingEntity entity_to_spawn;
	String entity_name;
	Location spawn_location;

	public SuddenDeath(LivingEntity entity_to_spawn, String entity_name, Location spawn_location) {

		this.entity_to_spawn = entity_to_spawn;
		this.entity_name = entity_name;
		this.spawn_location = spawn_location;
entity_to_spawn.setCustomName(entity_name);
entity_to_spawn.setCustomNameVisible(true);
	}

	public void runSuddenDeath() {
	
		spawn_location.getWorld().spawnEntity(spawn_location, entity_to_spawn.getType());

	}

}
