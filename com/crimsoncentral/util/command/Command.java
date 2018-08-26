package com.crimsoncentral.util.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.entity.Player;

import com.crimsoncentral.ranks.Rank;

public abstract class Command {

	public static enum CommandType {

		NORMAL, OVERRIDE
	}

	public static enum SenderType {

		PLAYER, CONSOLE, NON_PLAYER_ENTITY
	}

	public static enum HasTo {

		EQUAL_RANK, BE_ABOVE_RANK
	}

	private String name;
	private CommandType type;
	private SenderType sender;
	private Rank requiredRank;
	private HasTo hasTo;

	public abstract void perform(Player player);

	private static HashMap<String, CommandArguement> arguements = new HashMap<String, CommandArguement>();

	public Command(String name, CommandType type, SenderType sender, Rank rank, HasTo has_to) {

		this.setName(name);
		this.setType(type);
		this.setSender(sender);
		this.setRequiredRank(rank);
		this.setHasTo(has_to);
		CommandManager.commands.put(getName().toLowerCase(), this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CommandType getType() {
		return type;
	}

	public void setType(CommandType type) {
		this.type = type;
	}

	public SenderType getSender() {
		return sender;
	}

	public void setSender(SenderType sender) {
		this.sender = sender;
	}

	public Rank getRequiredRank() {
		return requiredRank;
	}

	public void setRequiredRank(Rank requiredRank) {
		this.requiredRank = requiredRank;
	}

	public HasTo getHasTo() {
		return hasTo;
	}

	public void setHasTo(HasTo hasTo) {
		this.hasTo = hasTo;
	}

	public CommandArguement getArguement(String arg) {

		return arguements.get(arg);
	}

	public ArrayList<CommandArguement> getArguements() {
		ArrayList<CommandArguement> args = new ArrayList<CommandArguement>();

		for (CommandArguement ca : arguements.values()) {

			args.add(ca);
		}

		return args;

	}

	public void addArguement(CommandArguement ca) {

		arguements.put(ca.getName().toLowerCase(), ca);

	}

	public class CommandArguement {

		private String name;
		private String previousRequiredArguement;
		private CommandType type;
		private SenderType sender;
		private Rank requiredRank;
		private HasTo hasTo;

		public CommandArguement(String command_name, String name, String previousRequiredArguement, CommandType type,
				SenderType sender, Rank rank, HasTo has_to) {
			this.setName(name);
			this.setType(type);
			this.setSender(sender);
			this.setRequiredRank(rank);
			this.setHasTo(has_to);
			if (name != null && name != "") {

				this.setPreviousRequiredArguement(previousRequiredArguement);
			}

			CommandManager.commands.get(command_name.toLowerCase()).addArguement(this);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPreviousRequiredArguement() {
			return previousRequiredArguement;
		}

		public void setPreviousRequiredArguement(String previousRequiredArguement) {
			this.previousRequiredArguement = previousRequiredArguement;
		}

		public CommandType getType() {
			return type;
		}

		public void setType(CommandType type) {
			this.type = type;
		}

		public SenderType getSender() {
			return sender;
		}

		public void setSender(SenderType sender) {
			this.sender = sender;
		}

		public Rank getRequiredRank() {
			return requiredRank;
		}

		public void setRequiredRank(Rank requiredRank) {
			this.requiredRank = requiredRank;
		}

		public HasTo getHasTo() {
			return hasTo;
		}

		public void setHasTo(HasTo hasTo) {
			this.hasTo = hasTo;
		}
	}

	public abstract class FinalCommandArguement extends CommandArguement {

		public abstract void perform();

		public FinalCommandArguement(String command_name, String name, String previousRequiredArguement,
				CommandType type, SenderType sender, Rank rank, HasTo has_to) {
			super(command_name, name, previousRequiredArguement, type, sender, rank, has_to);
			this.setName(name);
			this.setType(type);
			this.setSender(sender);
			this.setRequiredRank(rank);
			this.setHasTo(has_to);
			if (name != null && name != "") {

				this.setPreviousRequiredArguement(previousRequiredArguement);
			}

			CommandManager.commands.get(command_name.toLowerCase()).addArguement(this);
		}

	}

}
