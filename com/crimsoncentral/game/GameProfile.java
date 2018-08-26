package com.crimsoncentral.game;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import com.crimsoncentral.Main;
import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.ArenaManager;
import com.crimsoncentral.arena.ArenaRegister;
import com.crimsoncentral.util.item.ActionItem;

public abstract class GameProfile {

	/**If the game profile was setup and registered to the Arena Register Game Profiles Hash Map*/
	
	private boolean wasSetup = false;
	private String name;
	private Integer parent_id;
	private ActionItem action_item;

	private Arena lobby;
	private String scoreboardTitle;

	
	
	private HashMap<Double, ModeProfile> modes = new HashMap<Double, ModeProfile>();

	private ArrayList<Listener> listeners = new ArrayList<Listener>();

	private boolean eventsRegistered = false;

	public abstract void setup();

	public GameProfile(String name, Integer parent_id, ActionItem action_item) {

		this.name = name.toLowerCase();
		this.parent_id = parent_id;
		this.action_item = action_item;
		ArenaRegister.game_profiles.put(parent_id, this);
	}

	public boolean wasSetup() {
		return wasSetup;
	}

	public void setWasSetup(boolean wasSetup) {
		this.wasSetup = wasSetup;
	}

	public String getName() {
		return name;
	}

	public Integer getParentId() {
		return parent_id;
	}

	public ModeProfile getMode(Double d) {

		return modes.get(d);
	}

	public ArrayList<Double> getModeNumbers() {
		ArrayList<Double> m = new ArrayList<Double>();

		Iterator<Entry<Double, ModeProfile>> it = modes.entrySet().iterator();
		while (it.hasNext()) {

			Entry<Double, ModeProfile> pair = it.next();

			m.add(pair.getKey());

		}

		return m;
	}

	public ArrayList<ModeProfile> getModeProfiles() {
		ArrayList<ModeProfile> m = new ArrayList<ModeProfile>();

		Iterator<Entry<Double, ModeProfile>> it = modes.entrySet().iterator();
		while (it.hasNext()) {

			Entry<Double, ModeProfile> pair = it.next();

			m.add(pair.getValue());

		}

		return m;
	}

	public void addMode(Double d, ModeProfile p) {
		modes.put(d, p);
	}

	public void calculatePlayersPlaying() {
		int players = 0;
		Iterator<Entry<Arena, Double>> it = ArenaManager.local_arenas.entrySet().iterator();
		while (it.hasNext()) {

			Entry<Arena, Double> pair = it.next();

			DecimalFormat f = new DecimalFormat("#");

			if (Integer.valueOf(f.format(pair.getValue())) == parent_id) {

				players = players + pair.getKey().getPlayers().size();

			}

		}

	}

	public ActionItem getActionItem() {
		return action_item;
	}

	public void setActionItem(ActionItem action_item) {
		this.action_item = action_item;
	}

	public Arena getLobby() {
		return lobby;
	}

	public void setLobby(Arena lobby) {
		this.lobby = lobby;
	}

	public String getScoreboardTitle() {
		return scoreboardTitle;
	}

	public void setScoreboardTitle(String scoreboardTitle) {
		this.scoreboardTitle = scoreboardTitle;
	}

	public void addListener(Listener l) {

		listeners.add(l);
	}

	public ArrayList<Listener> getListeners() {
		return listeners;
	}

	public void registerListeners() {

		if (eventsRegistered != true) {
			for (Listener l : listeners) {

				if (l != null) {

					Main.pm.registerEvents(l, Main.plugin);

				}
			}
		}
		eventsRegistered = true;
	}

	public void unregisterListeners() {
		for (Listener l : listeners) {

			HandlerList.unregisterAll(l);

		}

		eventsRegistered = false;
	}

	public boolean isEventsRegistered() {
		return eventsRegistered;
	}

}
