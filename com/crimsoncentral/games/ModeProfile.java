package com.crimsoncentral.game;

import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.util.ArenaConfig;
import com.crimsoncentral.util.item.ActionItem;

public class ModeProfile {

	private String name;
	private Double type_id;
	private Arena arena;
	private ActionItem action_item;
	private ArenaConfig config;

	public ModeProfile(String name, Double type_id, Arena arena, ActionItem action_item, ArenaConfig config) {
		this.name = name;
		this.type_id = type_id;
		this.arena = arena;

		this.action_item = action_item;

		this.setConfig(config);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getTypeId() {
		return type_id;
	}

	public void setTypeId(Double type_id) {
		this.type_id = type_id;
	}

	public Arena getArena() {
		return arena;
	}

	public void setArena(Arena arena) {
		this.arena = arena;
	}

	public ActionItem getActionIitem() {
		return action_item;
	}

	public void setActionItem(ActionItem action_item) {
		this.action_item = action_item;
	}

	public ArenaConfig getConfig() {
		return config;
	}

	public void setConfig(ArenaConfig config) {
		this.config = config;
	}

}
