package com.crimsoncentral.vespen.data.combat;

import java.util.HashMap;
import org.bukkit.entity.Player;

public class CpsLog {

	private HashMap<Integer, ClickLog> rightClicks = new HashMap<Integer, ClickLog>();
	private HashMap<Integer, ClickLog> leftClicks = new HashMap<Integer, ClickLog>();

	private Player player;

	private Double rightCps = 0.0;
	private Double leftCps = 0.0;
	private Double butterflyCps = 0.0;

	private Integer millTime = 1000;

	public CpsLog(Player player) {
		setPlayer(player);
	}

	public HashMap<Integer, ClickLog> getRightClicks() {
		return rightClicks;
	}

	public void addRightClick(ClickLog log) {
		rightClicks.put(log.getTime(), log);

		rightCps = (double) (rightClicks.size() / 1);
		butterflyCps = (double) ((leftClicks.size() + rightClicks.size()) / 2);
	}

	public HashMap<Integer, ClickLog> getLeftClicks() {
		return leftClicks;
	}

	public void addLeftClick(ClickLog log) {
		leftClicks.put(log.getTime(), log);

		leftCps = (double) (leftClicks.size() / 1);

		butterflyCps = (double) ((leftClicks.size() + rightClicks.size()) / 2);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Double getRightCps() {
		return rightCps;
	}

	public void setRightCps(Double rightCps) {
		this.rightCps = rightCps;
	}

	public Double getLeftCps() {
		return leftCps;
	}

	public void setLeftCps(Double leftCps) {
		this.leftCps = leftCps;
	}

	public Double getButterflyCps() {
		return butterflyCps;
	}

	public void setButterflyCps(Double butterflyCps) {
		this.butterflyCps = butterflyCps;
	}

	public Integer getMillTime() {
		return millTime;
	}

	public void setMillTime(Integer millTime) {
		this.millTime = millTime;
	}

}
