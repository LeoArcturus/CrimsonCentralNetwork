package com.crimsoncentral.server_player;

import java.text.SimpleDateFormat;
import java.util.Date;


import org.bukkit.entity.Player;


import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.util.ArenaUtil;
import com.crimsoncentral.arena.util.PlayerVisiblity.Visiblity;
import com.crimsoncentral.cosmetics.CosmeticProfile;
import com.crimsoncentral.ranks.Rank;
import com.crimsoncentral.ranks.RanksManager;
import com.crimsoncentral.util.CrimsonMap;
import com.crimsoncentral.util.mysql.Sql;

public class ServerPlayer {

	private Player player;
	private Rank rank;
	private String custom_prefix;
	private NickProfile nick;
	private Arena arena;
	private Visiblity visiblity;
	private ChatProfile chat_profile;
	private PartyInfo party_info;
	private ClanInfo clan_info;
	private LevelingInfo leveling_info;
	private CosmeticProfile cosmetic_profile;
	private RewardsProfile rewards_profile;

	public ServerPlayer(Player player, Rank rank, String custom_prefix, NickProfile nick, Arena arena,
			Visiblity visiblity, ChatProfile chat_profile, PartyInfo party_info, ClanInfo clan_info,
			LevelingInfo leveling_info, CosmeticProfile cosmetic_profile) {

		this.player = player;
		this.rank = rank;
		this.custom_prefix = custom_prefix;
		this.nick = nick;
		this.arena = arena;
		this.visiblity = visiblity;
		this.setLevelingInfo(leveling_info);
		this.setCosmeticProfile(cosmetic_profile);
		PlayerManager.addPlayer(player, this);

		if (!Sql.checkTableForRow("player_data", "player", "player_uuid", player.getUniqueId().toString())) {
			Date dNow = new Date();
			SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy");

			CrimsonMap map = new CrimsonMap();
			map.add("player", player.getName());
			map.add("player_uuid", player.getUniqueId().toString());
			map.add("player_rank", RanksManager.getRank("default").getName());
			map.add("prefix", "noprefix");

			map.add("first_joined", ft.format(dNow).toString());
			map.add("logins", "1");

			Sql.createRow("player_data", map);

		}

	}

	public ServerPlayer(Player player) {
		this.player = player;
		this.rank = RanksManager.default_rank;
		this.custom_prefix = "";
		this.nick = null;
		this.arena = ArenaUtil.getPlayerArena(player);

		PlayerManager.addPlayer(player, this);

	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Rank getRank() {
		return rank;
	}

	public void setRank(Rank rank) {

		this.rank = rank;
		Sql.establishConnection();
		Sql.setString("player_data", "player_rank", "player_uuid", rank.getName(), player.getUniqueId().toString());
		Sql.closeConnection();
	}

	public String getCustomPrefix() {
		return custom_prefix;
	}

	public void setCustomPrefix(String custom_prefix) {
		this.custom_prefix = custom_prefix;
		Sql.establishConnection();
		Sql.setString("player_data", "prefix", "player_uuid", player.getUniqueId().toString(), custom_prefix);
		Sql.closeConnection();
	}

	public NickProfile getNick() {
		return nick;
	}

	public void setNick(NickProfile nick) {
		this.nick = nick;
	}

	public Arena getArena() {
		return arena;
	}

	public void setArena(Arena arena) {
		this.arena = arena;
	}

	public ChatProfile getChatProfile() {
		return chat_profile;
	}

	public void setChatProfile(ChatProfile chat_profile) {
		this.chat_profile = chat_profile;
	}

	public PartyInfo getParty_info() {
		return party_info;
	}

	public void setPartyInfo(PartyInfo party_info) {
		this.party_info = party_info;
	}

	public ClanInfo getClanInfo() {
		return clan_info;
	}

	public void setClanInfo(ClanInfo clan_info) {
		this.clan_info = clan_info;
	}

	public LevelingInfo getLevelingInfo() {
		return leveling_info;
	}

	public void setLevelingInfo(LevelingInfo leveling_info) {
		this.leveling_info = leveling_info;
	}

	public CosmeticProfile getCosmeticProfile() {
		return cosmetic_profile;
	}

	public void setCosmeticProfile(CosmeticProfile cosmetic_profile) {
		this.cosmetic_profile = cosmetic_profile;
	}

	public Visiblity getVisiblity() {
		return visiblity;
	}

	public void setVisiblity(Visiblity visiblity) {
		this.visiblity = visiblity;
	}

	public RewardsProfile getRewardsProfile() {
		return rewards_profile;
	}

	public void setRewardsProfile(RewardsProfile rewards_profile) {
		this.rewards_profile = rewards_profile;
	}

}
