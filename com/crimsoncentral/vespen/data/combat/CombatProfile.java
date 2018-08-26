package com.crimsoncentral.vespen.data.combat;

import org.bukkit.entity.Player;

public class CombatProfile {
	private Player player;
	private CpsLog cpsLog;


	public CombatProfile(Player player) {

		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

//	public HashMap<Integer, CombatLog> getCombatLogs() {
//		return combatLogs;
//	}
//
//	public void addNewLog(CombatLog log) {
//
//		combatLogs.put(log.getTime(), log);
//	}
//
//	public void clearLogs() {
//
//		combatLogs.clear();
//
//	}

	public CpsLog getCpsLog() {

		if (cpsLog == null) {
			cpsLog = new CpsLog(player);

		}

		return cpsLog;
	}

	public void setCpsLog(CpsLog cpsLog) {
		this.cpsLog = cpsLog;
	}

}
