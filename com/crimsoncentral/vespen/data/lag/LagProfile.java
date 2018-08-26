package com.crimsoncentral.vespen.data.lag;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;

public class LagProfile {

	private HashMap<PacketType, ArrayList<PacketRecivedObj>> packets = new HashMap<PacketType, ArrayList<PacketRecivedObj>>();

	private Player player;

	public LagProfile(Player player) {

		setPlayer(player);

	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public HashMap<PacketType, ArrayList<PacketRecivedObj>> getPackets() {
		return packets;
	}

	public void addPacket(PacketType t, PacketRecivedObj o) {
		if (packets.get(t) == null) {

			packets.put(t, new ArrayList<PacketRecivedObj>());

		}

		packets.get(t).add(o);

	}

}
