package com.crimsoncentral.vespen.data.combat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CombatLog {
	private Player player;
	private Entity damaged;

	private Double reach;
	private CpsLog cpsLog;
	private Double yaw;
	private Double pitch;

	private Integer time;
	private Integer tick;;
	private int ping;
	private Double tps;

	public CombatLog(Player player, Entity damaged, Double reach, CpsLog cpsLog, Double yaw, Double pitch, Integer time,
			Integer tick, int ping, Double tps) {

		
		setPlayer(player);
		setDamaged(damaged);
		setReach(reach);
		setCpsLog(cpsLog);
		setYaw(yaw);
		setPitch(pitch);
		setTime(time);
		setTick(tick);
		setPing(ping);
		setTps(tps);
		
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Entity getDamaged() {
		return damaged;
	}

	public void setDamaged(Entity damaged) {
		this.damaged = damaged;
	}

	public Double getReach() {
		return reach;
	}

	public void setReach(Double reach) {
		this.reach = reach;
	}

	public CpsLog getCpsLog() {
		return cpsLog;
	}

	public void setCpsLog(CpsLog cpsLog) {
		this.cpsLog = cpsLog;
	}

	public Double getYaw() {
		return yaw;
	}

	public void setYaw(Double yaw) {
		this.yaw = yaw;
	}

	public Double getPitch() {
		return pitch;
	}

	public void setPitch(Double pitch) {
		this.pitch = pitch;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public Integer getTick() {
		return tick;
	}

	public void setTick(Integer tick) {
		this.tick = tick;
	}

	public int getPing() {
		return ping;
	}

	public void setPing(int ping) {
		this.ping = ping;
	}

	public Double getTps() {
		return tps;
	}

	public void setTps(Double tps) {
		this.tps = tps;
	}

}
