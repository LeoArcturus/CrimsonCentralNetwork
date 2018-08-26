package com.crimsoncentral.server_player;

import java.util.HashMap;

public class ChatProfile {

	private HashMap<Double, String> messages = new HashMap<Double, String>();

	private ProfanityFilter filterPreference;
	private ChatChannel channel;

	public enum ProfanityFilter {

		HIGH, MEDIUM, LOW

	}

	public enum ChatChannel {

		PUBLIC, PRIVATE, CLAN, PARTY

	}

	public ChatProfile() {

	}

	public HashMap<Double, String> getMessages() {
		return messages;
	}

	public void setMessages(HashMap<Double, String> messages) {
		this.messages = messages;
	}

	public ProfanityFilter getFilterPreference() {
		return filterPreference;
	}

	public void setFilterPreference(ProfanityFilter filterPreference) {
		this.filterPreference = filterPreference;
	}

	public ChatChannel getChannel() {
		return channel;
	}

	public void setChannel(ChatChannel channel) {
		this.channel = channel;
	}

}
