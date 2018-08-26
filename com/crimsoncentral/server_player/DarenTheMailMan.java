package com.crimsoncentral.server_player;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.crimsoncentral.arena.util.NPC;
import com.crimsoncentral.server_player.RewardsProfile.RewardType;
import com.crimsoncentral.util.item.ActionItem;
import com.crimsoncentral.util.item.AnimatedInventory;
import com.crimsoncentral.util.item.ItemUtil;
import com.crimsoncentral.util.other.OtherUtil.Rarity;

public class DarenTheMailMan extends NPC {

	private static HashMap<RewardType, HashMap<Integer, Reward>> rewardsMasterRecord = new HashMap<RewardType, HashMap<Integer, Reward>>();

	private static HashMap<Integer, Reward> dailyRewards = new HashMap<Integer, Reward>();
	private static HashMap<Integer, Reward> bonusRewards = new HashMap<Integer, Reward>();
	private static HashMap<Integer, Reward> voteRewards = new HashMap<Integer, Reward>();
	private static HashMap<Integer, Reward> monthly1Rewards = new HashMap<Integer, Reward>();
	private static HashMap<Integer, Reward> monthly2Rewards = new HashMap<Integer, Reward>();
	private static HashMap<Integer, Reward> monthly3Rewards = new HashMap<Integer, Reward>();
	private static HashMap<Integer, Reward> monthly4Rewards = new HashMap<Integer, Reward>();
	private static HashMap<Integer, Reward> monthly5Rewards = new HashMap<Integer, Reward>();

	@Override
	public void perform(Player p) {

		AnimatedInventory ai = AnimatedInventory.getInventory("Rewards Menu " + p.getUniqueId().toString());

		if (ai != null) {

			p.openInventory(ai.getInventory());
		} else {

			ai = new AnimatedInventory("Rewards Menu " + p.getUniqueId().toString(), p)

			{

				@Override
				public void updateInventory() {

					Inventory i = getInventory();
					com.crimsoncentral.server_player.RewardsProfile rp = PlayerManager.getServerPlayer(p)
							.getRewardsProfile();
					ActionItem d = new ActionItem(p.getUniqueId().toString() + "-daily-reward-claim-item",
							ItemUtil.newItem(Material.GOLD_BLOCK, 1, (short) 0,
									ChatColor.YELLOW + "" + ChatColor.MAGIC + "|" + ChatColor.RESET + ChatColor.GREEN
											+ " Rewards " + ChatColor.YELLOW + "" + ChatColor.MAGIC + "|",
									"", "You can claim this", ChatColor.WHITE + "reward every 24 hours",
									ChatColor.WHITE + "for awesome rewards!", "",
									rp.getDisplayTimeLeft(RewardType.DAILY))) {

						@Override
						public void preform(Player player) {

							if (rp.canClaim(RewardType.DAILY)) {

								p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, (float) 2.0, (float) 1.5);
							}

						}

					};

					i.setItem(22, d.getIs());
				}

			};

			ai.setInventory(
					Bukkit.createInventory(null, 36, ChatColor.GOLD + "" + ChatColor.MAGIC + "|" + ChatColor.RESET
							+ ChatColor.DARK_GREEN + " Rewards " + ChatColor.GOLD + "" + ChatColor.MAGIC + "|"));
			ai.setTickInterval(20);
		}

	}

	@SuppressWarnings("unused")
	private static void setUpRewards() {

		rewardsMasterRecord.put(RewardType.DAILY, dailyRewards);
		rewardsMasterRecord.put(RewardType.BONUS, bonusRewards);
		rewardsMasterRecord.put(RewardType.VOTE, voteRewards);
		rewardsMasterRecord.put(RewardType.MONTHLY_1, monthly1Rewards);
		rewardsMasterRecord.put(RewardType.MONTHLY_2, monthly2Rewards);
		rewardsMasterRecord.put(RewardType.MONTHLY_3, monthly3Rewards);
		rewardsMasterRecord.put(RewardType.MONTHLY_4, monthly4Rewards);
		rewardsMasterRecord.put(RewardType.MONTHLY_5, monthly5Rewards);

		setUpDailyRewards();
		setUpBonusRewards();
		setUpVoteRewards();
		setUpMontly1Rewards();
		setUpMontly2Rewards();
		setUpMontly3Rewards();
		setUpMontly4Rewards();
		setUpMontly5Rewards();
	}

	private static void setUpDailyRewards() {

		RewardType r = RewardType.DAILY;
		
		new Reward("[Loot Crate Combo 1]", r, Rarity.COMMON) {

			@Override
			public void reward(Player player) {
			
				
			}
			
			
		};

	}

	private static void setUpBonusRewards() {

	}

	private static void setUpVoteRewards() {

	}

	private static void setUpMontly1Rewards() {

	}

	private static void setUpMontly2Rewards() {

	}

	private static void setUpMontly3Rewards() {

	}

	private static void setUpMontly4Rewards() {

	}

	private static void setUpMontly5Rewards() {

	}

	public static abstract class Reward {

		private String reward_name;
		private RewardType type;
		private Rarity rarity;

		public abstract void reward(Player player);

		public Reward(String reward_name, RewardType type, Rarity rarity) {

			this.setRewardName(reward_name);
			this.setType(type);
			HashMap<Integer, Reward> h = rewardsMasterRecord.get(type);
			h.put(h.size() + 1, this);

		}

		public String getRewardName() {
			return reward_name;
		}

		public void setRewardName(String reward_name) {
			this.reward_name = reward_name;
		}

		public RewardType getType() {
			return type;
		}

		public void setType(RewardType type) {
			this.type = type;
		}

		public Rarity getRarity() {
			return rarity;
		}

		public void setRarity(Rarity rarity) {
			this.rarity = rarity;
		}

	}

}
