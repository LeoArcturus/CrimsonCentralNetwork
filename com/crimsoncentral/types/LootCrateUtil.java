package com.crimsoncentral.cosmetics;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.entity.Player;

import com.crimsoncentral.ranks.RanksManager;
import com.crimsoncentral.server_player.PlayerManager;

public class LootCrateUtil {
	public static int randomizeForLootCrate(Player p) {

		int max = 10;
		int crates = 0;

		if (PlayerManager.getServerPlayer(p).getRank() == RanksManager.commoner) {
			max = 9;
		} else if (PlayerManager.getServerPlayer(p).getRank() == RanksManager.rogue) {
			max = 8;

		} else if (PlayerManager.getServerPlayer(p).getRank() == RanksManager.knight) {
			max = 7;
		} else if (PlayerManager.getServerPlayer(p).getRank() == RanksManager.mage) {
			max = 6;
		} else if (PlayerManager.getServerPlayer(p).getRank() == RanksManager.crimson) {
			max = 5;
		} else if (PlayerManager.getServerPlayer(p).getRank() == RanksManager.helper
				|| PlayerManager.getServerPlayer(p).getRank() == RanksManager.buildteam
				|| PlayerManager.getServerPlayer(p).getRank() == RanksManager.mod) {
			max = 4;
		} else if (PlayerManager.getServerPlayer(p).getRank() == RanksManager.srmod
				|| PlayerManager.getServerPlayer(p).getRank() == RanksManager.admin
				|| PlayerManager.getServerPlayer(p).getRank() == RanksManager.owner) {
			max = 3;
		}

		int randomNum = ThreadLocalRandom.current().nextInt(1, max);
		if (randomNum == 1) {

			crates = 1;
		}

		return crates;
	}
}
