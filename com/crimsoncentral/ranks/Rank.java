package com.crimsoncentral.ranks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scoreboard.Team;

public class Rank {

	public enum RankType {

		NON, DONATOR, FAMOUS, STAFF
	}

	private String name;

	private String prefix;
	private ChatColor rank_color;
	private ChatColor message_color;

	private RankType rank_type;
	private boolean vespen_bypass;
	private Double cosmetic_multiplier;
	private int priority;

	private org.bukkit.scoreboard.Team team;

	private FireworkMeta firework_meta;

	public Rank(String name, String prefix, ChatColor rank_color, ChatColor message_color, RankType rank_type,
			boolean vespen_bypass, Double cosmetic_multiplier, int priority) {

		this.name = name.toLowerCase();
		this.prefix = prefix;
		this.rank_color = rank_color;
		this.message_color = message_color;
		this.rank_type = rank_type;
		this.vespen_bypass = vespen_bypass;
		this.cosmetic_multiplier = cosmetic_multiplier;
		this.priority = priority;
		
		RanksManager.stringranks.put(name.toLowerCase(), this);
		RanksManager.intranks.put(priority, this);
	}

	public String getName() {
		return name.toLowerCase();
	}

	public void setName(String name) {
		this.name = name.toLowerCase();
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public ChatColor getRankColor() {
		return rank_color;
	}

	public void setRankColor(ChatColor rank_color) {
		this.rank_color = rank_color;
	}

	public ChatColor getMessageColor() {
		return message_color;
	}

	public void setMessageColor(ChatColor message_color) {
		this.message_color = message_color;
	}

	public RankType getRankType() {
		return rank_type;
	}

	public void setRankType(RankType rank_type) {
		this.rank_type = rank_type;
	}

	public boolean canBypassVespen() {
		return vespen_bypass;
	}

	public void setCanBypassVespen(boolean vespen_bypass) {
		this.vespen_bypass = vespen_bypass;
	}

	public Double getCosmeticMultiplier() {
		return cosmetic_multiplier;
	}

	public void setCosmeticMultiplier(Double cosmetic_multiplier) {
		this.cosmetic_multiplier = cosmetic_multiplier;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Team getTeam() {
		return team;
	}

	public void applyPlayerPrefix(Player player) {
		RanksManager.applyRankPrefixes(player.getWorld());

	}

	public void applyPlayerRankColor(Player player) {

		player.setDisplayName(getRankColor() + player.getName());
		player.setPlayerListName(getRankColor() + player.getName());
	}

	public String getPlayerFullName(Player player) {

		return getPrefix() + getRankColor() + player.getName();
	}

	public FireworkMeta getFireworkMeta() {
		return firework_meta;
	}

	public void setFireworkMeta(FireworkMeta firework_meta) {
		this.firework_meta = firework_meta;
	}

}
