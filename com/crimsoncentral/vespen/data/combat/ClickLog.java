package com.crimsoncentral.vespen.data.combat;

public class ClickLog {

	public static enum ClickType {

		LEFT, RIGHT
	}

	private ClickType clickType;

	private Integer time;
	private Integer tick;;
	private Double ping;
	private Double tps;

	public ClickLog(ClickType clickType, Integer time, Integer tick, Double ping, Double tps) {

		setClickType(clickType);
		setTime(time);
		setTick(tick);
		setPing(ping);
		setTps(tps);
	}

	public ClickType getClickType() {
		return clickType;
	}

	public void setClickType(ClickType clickType) {
		this.clickType = clickType;
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

	public Double getPing() {
		return ping;
	}

	public void setPing(Double ping) {
		this.ping = ping;
	}

	public Double getTps() {
		return tps;
	}

	public void setTps(Double tps) {
		this.tps = tps;
	}

}
