package com.crimsoncentral.server_player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;


import org.bukkit.entity.Player;

import com.crimsoncentral.util.mysql.Sql;

import net.md_5.bungee.api.ChatColor;

public class RewardsProfile {

	private Player player;

	private String lastClaimedDaily;
	private String lastClaimedBonus;
	private String lastClaimedVote;
	private String lastClaimedMonthly1;
	private String lastClaimedMonthly2;
	private String lastClaimedMonthly3;
	private String lastClaimedMonthly4;
	private String lastClaimedMonthly5;

	private HashMap<RewardType, String> rewards = new HashMap<RewardType, String>();
	private HashMap<RewardType, Boolean> can_claim = new HashMap<RewardType, Boolean>();

	public static enum RewardType {

		DAILY, BONUS, VOTE, MONTHLY_1, MONTHLY_2, MONTHLY_3, MONTHLY_4, MONTHLY_5
	}

	public RewardsProfile(Player player) {

		rewards.put(RewardType.DAILY,
				Sql.getString("player_rewards", "daily", "player", player.getUniqueId().toString()));
		rewards.put(RewardType.BONUS,
				Sql.getString("player_rewards", "bonus", "player", player.getUniqueId().toString()));
		rewards.put(RewardType.VOTE,
				Sql.getString("player_rewards", "vote", "player", player.getUniqueId().toString()));
		rewards.put(RewardType.MONTHLY_1,
				Sql.getString("player_rewards", "monthly_1", "player", player.getUniqueId().toString()));
		rewards.put(RewardType.MONTHLY_2,
				Sql.getString("player_rewards", "monthly_2", "player", player.getUniqueId().toString()));
		rewards.put(RewardType.MONTHLY_3,
				Sql.getString("player_rewards", "monthly_3", "player", player.getUniqueId().toString()));
		rewards.put(RewardType.MONTHLY_4,
				Sql.getString("player_rewards", "monthly_4", "player", player.getUniqueId().toString()));
		rewards.put(RewardType.MONTHLY_5,
				Sql.getString("player_rewards", "monthly_5", "player", player.getUniqueId().toString()));
		getDisplayTimeLeft(RewardType.DAILY);
		getDisplayTimeLeft(RewardType.BONUS);
		getDisplayTimeLeft(RewardType.VOTE);
		getDisplayTimeLeft(RewardType.MONTHLY_1);
		getDisplayTimeLeft(RewardType.MONTHLY_2);
		getDisplayTimeLeft(RewardType.MONTHLY_3);
		getDisplayTimeLeft(RewardType.MONTHLY_4);
		getDisplayTimeLeft(RewardType.MONTHLY_5);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public String getDisplayTimeLeft(RewardType reward) {

		int i = 1;

		HashMap<Integer, String> pluarls = new HashMap<Integer, String>();
		HashMap<Integer, String> singles = new HashMap<Integer, String>();

		pluarls.put(1, " years ");
		singles.put(1, " year ");

		pluarls.put(2, " months ");
		singles.put(2, " month ");

		pluarls.put(3, " days ");
		singles.put(3, " day ");

		pluarls.put(4, " hours ");
		singles.put(4, " hour ");

		pluarls.put(5, " minutes ");
		singles.put(5, " minute ");

		pluarls.put(6, " seconds");
		singles.put(6, " second");

		String last = rewards.get(reward);
		String now = (String) new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()).toString();
		String time_left = "Click to Claim";

		StringTokenizer lst = new StringTokenizer(last);
		StringTokenizer nst = new StringTokenizer(now);

		while (lst.hasMoreTokens() == true && nst.hasMoreTokens() == true) {
			int l = Integer.valueOf(lst.nextToken("-"));
			int n = Integer.valueOf(nst.nextToken("-"));
			int t = l - n;
			if (t > 0) {

				if (t == 1) {
					time_left = time_left + t + singles.get(i);
				} else {

					time_left = time_left + t + pluarls.get(i);
				}
			}
			i = i + 1;
		}
		if (time_left.equalsIgnoreCase("Click To Claim")) {

			time_left = ChatColor.GREEN + time_left;
			can_claim.put(reward, true);
		} else {

			time_left = ChatColor.RED + "You can claim this reward in: " + ChatColor.YELLOW + time_left;
			can_claim.put(reward, false);
		}

		return time_left;
	}

	public boolean canClaim(RewardType reward) {

		return can_claim.get(reward);
	}

	public String getLastClaimedMonthly2() {
		return lastClaimedMonthly2;
	}

	public void setLastClaimedMonthly2(String lastClaimedMonthly2) {
		this.lastClaimedMonthly2 = lastClaimedMonthly2;
	}

	public String getLastClaimedMonthly4() {
		return lastClaimedMonthly4;
	}

	public void setLastClaimedMonthly4(String lastClaimedMonthly4) {
		this.lastClaimedMonthly4 = lastClaimedMonthly4;
	}

	public String getLastClaimedMonthly3() {
		return lastClaimedMonthly3;
	}

	public void setLastClaimedMonthly3(String lastClaimedMonthly3) {
		this.lastClaimedMonthly3 = lastClaimedMonthly3;
	}

	public String getLastClaimedMonthly5() {
		return lastClaimedMonthly5;
	}

	public void setLastClaimedMonthly5(String lastClaimedMonthly5) {
		this.lastClaimedMonthly5 = lastClaimedMonthly5;
	}

	public String getLastClaimedDaily() {
		return lastClaimedDaily;
	}

	public void setLastClaimedDaily(String lastClaimedDaily) {
		this.lastClaimedDaily = lastClaimedDaily;
	}

	public String getLastClaimedBonus() {
		return lastClaimedBonus;
	}

	public void setLastClaimedBonus(String lastClaimedBonus) {
		this.lastClaimedBonus = lastClaimedBonus;
	}

	public String getLastClaimedVote() {
		return lastClaimedVote;
	}

	public void setLastClaimedVote(String lastClaimedVote) {
		this.lastClaimedVote = lastClaimedVote;
	}

	public String getLastClaimedMonthly1() {
		return lastClaimedMonthly1;
	}

	public void setLastClaimedMonthly1(String lastClaimedMonthly1) {
		this.lastClaimedMonthly1 = lastClaimedMonthly1;
	}

}
