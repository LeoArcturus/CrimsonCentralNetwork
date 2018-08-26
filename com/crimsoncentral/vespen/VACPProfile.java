package com.crimsoncentral.vespen;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class VACPProfile {

	private Player player;

	/**
	 * Reach Data
	 */

	HashMap<Double, Double> reach_vs_ping = new HashMap<Double, Double>();
	HashMap<Double, Double> reach_vs_tps = new HashMap<Double, Double>();

	/**
	 * Killaura Data
	 */

	HashMap<Double, Double> reaction_vs_ping = new HashMap<Double, Double>();
	HashMap<Double, Double> reaction_vs_tps = new HashMap<Double, Double>();

	HashMap<Double, Double> circle_vs_ping = new HashMap<Double, Double>();
	HashMap<Double, Double> circle_vs_tps = new HashMap<Double, Double>();

	/**
	 * Speed Data
	 */

	HashMap<Double, Double> speed_vs_ping = new HashMap<Double, Double>();
	HashMap<Double, Double> speed_vs_tps = new HashMap<Double, Double>();

	/**
	 * Click Data
	 */

	HashMap<Double, Double> cps_vs_ping = new HashMap<Double, Double>();
	HashMap<Double, Double> cps_vs_tps = new HashMap<Double, Double>();

	/**
	 * Fly Data
	 */

	public VACPProfile(Player player) {
		this.setPlayer(player);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
