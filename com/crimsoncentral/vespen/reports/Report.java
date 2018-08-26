package com.crimsoncentral.vespen.reports;

import java.util.Date;

import org.bukkit.entity.Player;

import com.crimsoncentral.util.CrimsonMap;
import com.crimsoncentral.util.mysql.Sql;

public class Report {

	
	
	public static enum HackType {
		
		KILLAURA, REACH, AUTOCLICKER, SPEED, JESUS, FLY, BHOP, SPIDER
	}

	private Player accused;
	private Player accuser;
	private HackType hack;
	private Date date;
	
	public Report(Player accused, Player accuser, HackType hack) {

		this.setAccused(accused);
		this.setAccuser(accuser);
		this.setHack(hack);
		this.setDate(new Date());
		
		Sql.establishConnection();
		CrimsonMap map = new CrimsonMap();
		map.add("accused", accused.getUniqueId().toString());
		map.add("accuser", accuser.getUniqueId().toString());
		
		Sql.createRow("player_cheat_reports", map);
	}

	public Player getAccused() {
		return accused;
	}

	public void setAccused(Player accused) {
		this.accused = accused;
	}

	public Player getAccuser() {
		return accuser;
	}

	public void setAccuser(Player accuser) {
		this.accuser = accuser;
	}

	public HackType getHack() {
		return hack;
	}

	public void setHack(HackType hack) {
		this.hack = hack;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
