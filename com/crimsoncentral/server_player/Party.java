package com.crimsoncentral.server_player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class Party {

	public static ArrayList<Party> parties = new ArrayList<Party>();

	public static enum PartyRole {

		LEADER, DEAN, MEMBER

	}

	private Player leader;
	private HashMap<Player, PartyRole> players = new HashMap<Player, PartyRole>();

	public Party(String name, Player leader) {

		this.leader = leader;
		parties.add(this);

	}

	public Player getLeader() {
		return leader;
	}

	public void setLeader(Player leader) {
		this.leader = leader;
		for (Entry<Player, PartyRole> e : getPlayersHash().entrySet()) {

			if (e.getValue() == PartyRole.LEADER) {

				players.put(e.getKey(), players.get(leader));

			}

		}

		players.put(leader, PartyRole.LEADER);

	}

	public ArrayList<Player> getDeans() {
		ArrayList<Player> deans = new ArrayList<Player>();

		for (Entry<Player, PartyRole> e : players.entrySet()) {

			if (e.getValue() == PartyRole.DEAN) {

				deans.add(e.getKey());
			}
		}

		return deans;
	}

	public void addDean(Player dean) {

		players.put(dean, PartyRole.DEAN);

	}
	

	public void removeDean(Player dean) {

		players.put(dean, PartyRole.MEMBER);

	}

	public PartyRole getRole(Player player) {

		return players.get(player);
	}

	public HashMap<Player, PartyRole> getPlayersHash() {
		return players;
	}

	public ArrayList<Player> getPlayers() {
		ArrayList<Player> players = new ArrayList<Player>();

		for (Entry<Player, PartyRole> e : getPlayersHash().entrySet()) {

			players.add(e.getKey());

		}

		return players;

	}

}
