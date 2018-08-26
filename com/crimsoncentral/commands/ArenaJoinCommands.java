package com.crimsoncentral.commands;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;


import com.crimsoncentral.arena.ArenaRegister;
import com.crimsoncentral.arena.util.ArenaUtil;
import com.crimsoncentral.game.GameProfile;
import com.crimsoncentral.game.ModeProfile;
import com.crimsoncentral.util.JSONMessage;
import com.crimsoncentral.util.item.ActionItemManager;
import com.crimsoncentral.util.other.ChatUtil;

public class ArenaJoinCommands implements CommandExecutor, TabCompleter {

	ArenaUtil au = new ArenaUtil();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {
			Player p = (Player) sender;
			Double arena_to_join = 0.0;
			String map_name = "none";
			@SuppressWarnings("unused")
			boolean found = false;

			if (cmd.getName().equalsIgnoreCase("play")) {

				if (args.length < 1) {

					ActionItemManager.getActionItem("game menu").preform(p);
				} else {

					for (GameProfile gp : ArenaRegister.getGameProfiles()) {

						if (gp.getName().equalsIgnoreCase(args[0])) {

							String all_arguements = "";

							for (String s : args) {

								if (!s.equalsIgnoreCase(args[0]) && !s.equalsIgnoreCase(args[3])) {

									all_arguements = all_arguements + s;

								}

							}

							for (ModeProfile mp : gp.getModeProfiles()) {

								if (mp.getName().replace(" ", "").equalsIgnoreCase(all_arguements)) {

									arena_to_join = mp.getTypeId();

									found = true;
									break;
								}

							}

							break;

						}

					}
				}
				if (args.length == 4) {
					map_name = args[3];
				}

				if (arena_to_join == 0.0) {
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
					p.sendMessage(ArenaUtil.centerText(ChatColor.WHITE + "Thats not a valid game mode."));
					p.sendMessage(ArenaUtil.centerText(ChatColor.WHITE + "Try one of the following games below!"));
					p.sendMessage(ArenaUtil.centerText(ChatColor.GRAY + "(hover above the names to see all modes)"));
					p.sendMessage(" ");
					ArrayList<GameProfile> games = ArenaRegister.getGameProfiles();
					HashMap<Integer, String> names = new HashMap<Integer, String>();
					HashMap<String, String> mode_names = new HashMap<String, String>();
					int q = 0;
					for (GameProfile gp : games) {
						if (gp.getParentId() != 0) {
							++q;
							String modes = ChatColor.GREEN + "" + ChatColor.BOLD + "MODES: ";

							names.put(q, gp.getName().toLowerCase());
							for (ModeProfile mp : gp.getModeProfiles()) {

								modes = modes + ChatUtil.randomChatColor() + mp.getName() + ", ";
							}

							mode_names.put(gp.getName(), modes);

						}
					}
					DecimalFormat f = new DecimalFormat("0");
					int max = 1;

					if (games.size() % 3 > 0) {

						max = Integer.valueOf(f.format(((games.size() / 3) + 1)));
					} else {

						max = Integer.valueOf(f.format((games.size() / 3)));
					}

					int x = 1;
					int y = 2;
					int z = 3;
					for (int i = 1; i <= max; ++i) {

						ChatColor c1 = ChatUtil.randomChatColor();
						ChatColor c2 = ChatUtil.randomChatColor();
						ChatColor c3 = ChatUtil.randomChatColor();

						JSONMessage.create(String.valueOf(names.get(x) + ", ").replace("null, ", "")).color(c1)
								.tooltip(mode_names.get(names.get(x)) + "")
								.then(String.valueOf(names.get(y) + ", ").replace("null, ", "")).color(c2)
								.tooltip(mode_names.get(names.get(y)) + "")
								.then(String.valueOf(names.get(z) + ", ").replace("null, ", "")).color(c3)
								.tooltip(mode_names.get(names.get(z)) + "").center().send(p);

						x = x + 3;
						y = y + 3;
						z = z + 3;
					}
					p.sendMessage(" ");
					p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
				} else {

					ArenaUtil.joinArena(p, arena_to_join, map_name);
				}

			} else if (cmd.getName().equalsIgnoreCase("lobby")) {

				ArenaUtil.joinArena(p, 0.0, map_name);

			}

		} else {

			sender.sendMessage(ChatColor.RED + "You must be a player to execute this command!");

		}
		return false;

	}

	public void playCommandError(Player p, String game_name, ArrayList<String> modes) {

		ArenaUtil.sendCenteredPlayerMessage(p, game_name);

		for (String s : modes) {

			p.sendMessage(s);

		}

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		ArrayList<String> commands = new ArrayList<String>();

		if (cmd.getLabel().equalsIgnoreCase("play")) {

			if (args.length == 1) {

				for (GameProfile gp : ArenaRegister.getGameProfiles()) {
					if (gp.getParentId() != 0) {
						commands.add(gp.getName());
					}
				}

			} else if (args.length == 2) {
				for (GameProfile gp : ArenaRegister.getGameProfiles()) {
					if (gp.getName().equalsIgnoreCase(args[0])) {

						for (ModeProfile mp : gp.getModeProfiles()) {
							StringTokenizer st = new StringTokenizer(mp.getName());
							commands.add(st.nextToken(" "));

						}
						break;
					}
				}

			} else if (args.length == 3) {
				for (GameProfile gp : ArenaRegister.getGameProfiles()) {
					if (gp.getName().equalsIgnoreCase(args[0])) {

						for (ModeProfile mp : gp.getModeProfiles()) {
							StringTokenizer st = new StringTokenizer(mp.getName());

							if (args[1].equalsIgnoreCase(st.nextToken(" "))) {
								commands.add(st.nextToken(" "));
							}
						}
						break;
					}

				}

			} else if (args.length == 4) {
				for (GameProfile gp : ArenaRegister.getGameProfiles()) {
					if (gp.getName().equalsIgnoreCase(args[0])) {

						for (ModeProfile mp : gp.getModeProfiles()) {
							StringTokenizer st = new StringTokenizer(mp.getName());

							if (args[1].equalsIgnoreCase(st.nextToken(" "))) {
								DecimalFormat df1 = new DecimalFormat("0");
								String main_dir = df1.format(mp.getTypeId());

								File main_dir_world_folder = null;
								for (File f : (File[]) new File("plugins/Crimson-Central/Maps").listFiles()) {

									if (f.getName().replaceAll("\\D", "").equalsIgnoreCase(main_dir)) {

										main_dir_world_folder = f;
									}

								}

								File[] directoryListing = main_dir_world_folder.listFiles();

								for (File f : directoryListing) {

									if (f.getName().toString().contains("" + mp.getTypeId())) {

										File[] directoryListing2 = f.listFiles();
										for (File f2 : directoryListing2) {

											commands.add(f2.getName());
										}

									}
								}

								break;

							}
						}
						break;
					}

				}
			}

		}

		final List<String> completions = new ArrayList<String>(commands);
//		StringUtil.copyPartialMatches(args[0], commands, completions);
		Collections.sort(completions);
		return completions;
	}

}

// Double game_id = null;
//
// Player p = (Player) sender;
//
// if (args.length < 1) {
// sender.sendMessage(ChatColor.RED + "Thats not a vaild game!");
// } else {
//
// if (args[0].equalsIgnoreCase("skywars")) {
//
// if (args.length < 2) {
//
// sender.sendMessage(ChatColor.RED + "Thats not a vaild gamemode!");
//
// } else {
//
// if (args[1].equalsIgnoreCase("normal")) {
//
// if (args.length < 3) {
//
// sender.sendMessage(ChatColor.RED + "Thats not a vaild gamemode!");
// } else {
//
// if (args[2].equalsIgnoreCase("solo")) {
//
// game_id = 1.01;
// } else if (args[2].equalsIgnoreCase("teams")) {
//
// game_id = 1.02;
// }
// }
// } else if (args[1].equalsIgnoreCase("ballistic")) {
// if (args.length < 3) {
//
// sender.sendMessage(ChatColor.RED + "Thats not a vaild gamemode!");
// } else {
//
// if (args[2].equalsIgnoreCase("solo")) {
//
// game_id = 1.03;
// } else if (args[2].equalsIgnoreCase("teams")) {
//
// game_id = 1.04;
// }
//
// }
// }
// }
//
// }
//
// if (game_id != null) {
// ArenaUtil.joinArena(p, game_id, "");
// }