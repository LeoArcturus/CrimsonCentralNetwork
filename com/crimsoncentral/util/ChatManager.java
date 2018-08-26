package com.crimsoncentral.util;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.Team;
import com.crimsoncentral.ranks.Rank;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.server_player.ServerPlayer;
import com.crimsoncentral.server_player.ChatProfile.ProfanityFilter;

public class ChatManager implements Listener {

	public static ArrayList<String> high_filter_words = new ArrayList<String>();
	public static ArrayList<String> medium_filter_words = new ArrayList<String>();
	public static ArrayList<String> low_filter_words = new ArrayList<String>();
	public static ArrayList<String> fun_filter_words = new ArrayList<String>();

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {

		Player p = e.getPlayer();
		ServerPlayer sp = PlayerManager.getServerPlayer(p);
		Arena a = sp.getArena();
		Team t = a.getPlayerTeam(p);
		Rank r = sp.getRank();
		sp.getChatProfile();
		String m = e.getMessage();
		String team_prefix = "";

		if (t != null) {

			team_prefix = t.getPrefix();
		}

		int chars_capped = 0;

		for (int i = 0; i < m.length(); i++) {
			char c = m.charAt(i);

			if (Character.isUpperCase(c)) {
				chars_capped = chars_capped + 1;

			}

		}

		if (chars_capped / m.chars().count() >= 0.5) {

			m = m.toLowerCase();

		}

		boolean f = false;

		String high = m.toString();
		String med = m.toString();
		String low = m.toString();

		for (String s : fun_filter_words) {

			if (m.contains(s)) {
				f = true;
				break;

			}

		}

		if (f == true) {

		} else {

			for (String s : high_filter_words) {

				if (m.replaceAll("[^a-zA-Z]", "").toLowerCase().contains(s)) {
					String replacement = "";

					for (int i = 0; i < s.length(); i++) {

						replacement = replacement + "*";

					}

					high = high.replaceAll("[^a-zA-Z]", "").replaceAll(s, replacement);

				}

			}

			for (String s : medium_filter_words) {

				if (m.replaceAll("[^a-zA-Z]", "").toLowerCase().contains(s)) {
					String replacement = "";

					for (int i = 0; i < s.length(); i++) {

						replacement = replacement + "*";

					}

					med = med.replaceAll(s, replacement);

				}

			}

			for (String s : low_filter_words) {
				if (m.replaceAll("[^a-zA-Z]", "").toLowerCase().contains(s)) {
					String replacement = "";

					for (int i = 0; i < s.length(); i++) {

						replacement = replacement + "*";

					}

					low = low.replaceAll(s, replacement);

				}

			}

		}

		ArrayList<Player> players = (ArrayList<Player>) p.getWorld().getPlayers();

		for (Player player : players) {
			ServerPlayer sp1 = PlayerManager.getServerPlayer(player);

			String message = "";

			if (sp1.getChatProfile() != null) {
				if (sp1.getChatProfile().getFilterPreference().equals(ProfanityFilter.HIGH)) {

					message = high;

				} else if (sp1.getChatProfile().getFilterPreference().equals(ProfanityFilter.MEDIUM)) {

					message = med;

				} else if (sp1.getChatProfile().getFilterPreference().equals(ProfanityFilter.LOW)) {

					message = low;

				}
			} else {

				message = high;
			}
			if (sp.getNick() != null) {

				Rank fr = sp.getNick().getRank();

				player.sendMessage(team_prefix  + fr.getPrefix() + fr.getRankColor() + sp.getNick().getName()
						+ ": " + fr.getMessageColor() + message);
				e.setCancelled(true);

			} else
				player.sendMessage(team_prefix  + r.getPrefix() + r.getRankColor() + p.getName() + ": "
						+ r.getMessageColor() + message);
			e.setCancelled(true);
		}

	}

	public static void setUpChatFilters() {
		setUpHighFilter();
		setUpMedFilter();
		setUpLowFilter();
	}

	private static void setUpHighFilter() {

		high_filter_words.add("arse");
		high_filter_words.add("ass");
		high_filter_words.add("asshole");
		high_filter_words.add("bastard");
		high_filter_words.add("bitch");
		high_filter_words.add("bollocks");
		high_filter_words.add("cunt");
		high_filter_words.add("damn");
		high_filter_words.add("dammit");
		high_filter_words.add("fuck");

		high_filter_words.add("shit");
		high_filter_words.add("whore");
		high_filter_words.add("twat");

		high_filter_words.add("nigger");
		high_filter_words.add("niga");
		high_filter_words.add("cracker");
		high_filter_words.add("hooligan");
		high_filter_words.add("gyp");

		high_filter_words.add("anillingus");
		high_filter_words.add("banjee");
		high_filter_words.add("bareback");
		high_filter_words.add("balls");
		high_filter_words.add("bugger");
		high_filter_words.add("camel toe");
		high_filter_words.add("cock");
		high_filter_words.add("death grip");
		high_filter_words.add("dick");
		high_filter_words.add("dirty sanchez");
		high_filter_words.add("dogging");
		high_filter_words.add("donkey punch");
		high_filter_words.add("douche");
		high_filter_words.add("fleching");
		high_filter_words.add("frig");
		high_filter_words.add("girlfriend experience");
		high_filter_words.add("himbo");
		high_filter_words.add("hoggin");
		high_filter_words.add("hokkien");
		high_filter_words.add("hot karl");
		high_filter_words.add("mama san");
		high_filter_words.add("intercourse");
		high_filter_words.add("mile high club");
		high_filter_words.add("netflix and chill");
		high_filter_words.add("nookie");
		high_filter_words.add("prick");
		high_filter_words.add("pussy");
		high_filter_words.add("penis");
		high_filter_words.add("quickie");
		high_filter_words.add("red wings");
		high_filter_words.add("rusy trombone");
		high_filter_words.add("serosorting");
		high_filter_words.add("sheep shagger");
		high_filter_words.add("shemale");
		high_filter_words.add("slut");
		high_filter_words.add("sex");
		high_filter_words.add("soggy biscuit");
		high_filter_words.add("swaffelen");
		high_filter_words.add("teabagging");
		high_filter_words.add("trans");
		high_filter_words.add("transgender");
		high_filter_words.add("tranny");
		high_filter_words.add("turkey slap");
		high_filter_words.add("wanker");
		high_filter_words.add("whale tail");
	}

	private static void setUpMedFilter() {

	}

	private static void setUpLowFilter() {

	}

}
