package com.crimsoncentral.server_player;

import java.io.File;

import com.crimsoncentral.ranks.Rank;

public class NickProfile {

	private String name;
	private File skin;
	private Rank rank;

	public NickProfile(String name, File skin, Rank rank) {

		this.name = name;
		this.skin = skin;
		this.rank = rank;

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public File getSkin() {
		return skin;
	}

	public void setSkin(File skin) {
		this.skin = skin;
	}

	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}

}
