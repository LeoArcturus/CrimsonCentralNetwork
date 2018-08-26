package com.crimsoncentral.vespen;

import org.bukkit.entity.Player;

import com.crimsoncentral.vespen.data.combat.CombatProfile;
import com.crimsoncentral.vespen.data.lag.LagProfile;
import com.crimsoncentral.vespen.data.movement.MovementProfile;

public class VespenPlayer {

	public static enum ServerType {

		SPIGOT, BUNGEECORD

	}

	public static enum Suspicion {
		
		NONE, LOW, MEDIUM, HIGH, CONFIRMED
		
	}
	
	
	private Player player;
	private ServerType serverType;
	private CombatProfile combatProfile;
	private LagProfile lagProfile;
	private MovementProfile movementProfile;

	
	
	public VespenPlayer(Player player, ServerType serverType) {

		setPlayer(player);
		setServerType(serverType);
		
		combatProfile = new CombatProfile(player);
	
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public ServerType getServerType() {
		return serverType;
	}

	public void setServerType(ServerType serverType) {
		this.serverType = serverType;
	}

	public CombatProfile getCombatProfile() {
		return combatProfile;
	}

	public void setCombatProfile(CombatProfile combatProfile) {
		this.combatProfile = combatProfile;
	}

	public LagProfile getLagProfile() {
		return lagProfile;
	}

	public void setLagProfile(LagProfile lagProfile) {
		this.lagProfile = lagProfile;
	}

	public MovementProfile getMovementProfile() {
		return movementProfile;
	}

	public void setMovementProfile(MovementProfile movementProfile) {
		this.movementProfile = movementProfile;
	}

}
