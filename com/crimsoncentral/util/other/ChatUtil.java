package com.crimsoncentral.util.other;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;

public class ChatUtil {

	public static ArrayList<String> getWords(String text) {
		ArrayList<String> words = new ArrayList<String>();
		BreakIterator breakIterator = BreakIterator.getWordInstance();
		breakIterator.setText(text);
		int lastIndex = breakIterator.first();
		while (BreakIterator.DONE != lastIndex) {
			int firstIndex = lastIndex;
			lastIndex = breakIterator.next();
			if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(text.charAt(firstIndex))) {
				words.add(text.substring(firstIndex, lastIndex));
			}
		}

		return words;
	}

	public static ChatColor randomChatColor() {

		HashMap<Integer, ChatColor> colors = new HashMap<Integer, ChatColor>();

		colors.put(1, ChatColor.AQUA);
		colors.put(2, ChatColor.BLUE);
		colors.put(3, ChatColor.DARK_AQUA);
		colors.put(4, ChatColor.DARK_BLUE);
		colors.put(5, ChatColor.DARK_GRAY);
		colors.put(6, ChatColor.DARK_GREEN);
		colors.put(7, ChatColor.DARK_PURPLE);
		colors.put(8, ChatColor.DARK_RED);
		colors.put(9, ChatColor.GOLD);
		colors.put(10, ChatColor.GRAY);
		colors.put(11, ChatColor.GREEN);
		colors.put(12, ChatColor.RED);
		colors.put(13, ChatColor.WHITE);
		colors.put(14, ChatColor.YELLOW);

		Random ran = new Random();
		int randomNum = ran.nextInt((colors.size() - 1) + 1) + 1;

		return colors.get(randomNum);

	}

	public static String toTitleCase(String givenString) {
		String[] arr = givenString.split(" ");
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < arr.length; i++) {
			sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1)).append(" ");
		}
		return sb.toString().trim();
	}
}
