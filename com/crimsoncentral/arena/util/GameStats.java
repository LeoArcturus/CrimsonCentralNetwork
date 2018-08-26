package com.crimsoncentral.arena.util;

import org.bukkit.entity.Player;

public class GameStats {

	Player player;
	String game ="";
	int kills = 0;
	int final_kills = 0;
	int eggs_broken = 0;

	public GameStats(Player player, String game, int kills, int final_kills, int eggs_broken) {

		
		this.player = player;
		this.game = game;
		this.kills = kills;
		this.final_kills = final_kills;
		this.eggs_broken = eggs_broken;
		
	}

	public Player getPlayer() {

		return this.player;

	}

	public void setGame(String game) {

		this.game = game;
	}

	public String getGame() {

		return this.game;
	}

	public void addKill(int kills) {

		this.kills = this.kills + kills;
	}

	public int getKills() {

		return this.kills;

	}

	public void addFinalKill(int final_kills) {

		this.final_kills = this.final_kills + final_kills;
	}

	public int getFinalKills() {

		return this.final_kills;

	}

	public void addBrokenEgg(int eggs_broken) {

		this.eggs_broken = this.eggs_broken + eggs_broken;
	}

	public int getEggsBroken() {

		return this.eggs_broken;

	}

}
