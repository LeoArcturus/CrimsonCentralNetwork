package com.crimsoncentral.util.command;

import java.util.HashMap;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.crimsoncentral.util.command.Command.SenderType;

public class CommandManager implements CommandExecutor {

	public static HashMap<String, Command> commands = new HashMap<String, Command>();

	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {

		Command c = commands.get(label.toLowerCase());
		SenderType sender_type = null;
		
		if(sender instanceof Player) {
			
			sender_type = SenderType.PLAYER;
		} else if(sender instanceof Entity && !(sender instanceof Player)) {
			
			sender_type = SenderType.NON_PLAYER_ENTITY;
		} else if(sender instanceof ConsoleCommandSender) {
			
			sender_type = SenderType.CONSOLE;
		}

		if (c != null && sender_type == c.getSender()) {

		}

		return true;
	}

}
