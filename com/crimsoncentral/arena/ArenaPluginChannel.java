package com.crimsoncentral.arena;

import java.util.StringTokenizer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.crimsoncentral.arena.util.ArenaUtil;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class ArenaPluginChannel implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			Bukkit.broadcastMessage(channel);
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		if (subchannel.equals("Arena")) {

			String string = message.toString();

			StringTokenizer st = new StringTokenizer(string);

			if (string.contains("NewArena")) {

				Double mode = Double.valueOf(st.nextToken(", "));
				Arena arena = ArenaUtil.createArena(mode, ArenaUtil.decideArenaName(), message, "none");
				ArenaEvents.queued_to_join.put(player, arena);

			}

		}
	}

}
