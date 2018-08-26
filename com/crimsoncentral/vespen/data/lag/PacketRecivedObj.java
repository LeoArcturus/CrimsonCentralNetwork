package com.crimsoncentral.vespen.data.lag;

import net.minecraft.server.v1_12_R1.Packet;

public class PacketRecivedObj {

	private Packet<?> packet;
	private Integer time;
	private Integer tick;;
	private Double ping;
	private Double tps;

	public PacketRecivedObj(Packet<?> packet, Integer time, Integer tick, Double ping, Double tps) {

		setPacket(packet);
		setTime(time);
		setTick(tick);
		setPing(ping);
		setPing(tps);

	}

	public Packet<?> getPacket() {
		return packet;
	}

	public void setPacket(Packet<?> packet) {
		this.packet = packet;
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
