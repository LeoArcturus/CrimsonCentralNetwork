package com.crimsoncentral.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.crimsoncentral.Main;
import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.Arena.ArenaStage;
import com.crimsoncentral.arena.ArenaEvents;
import com.crimsoncentral.arena.ArenaEvents.BlockedMovementType;
import com.crimsoncentral.arena.ArenaManager;
import com.crimsoncentral.arena.ArenaRegister;
import com.crimsoncentral.arena.Team;
import com.crimsoncentral.arena.util.ArenaUtil;
import com.crimsoncentral.game.GameProfile;
import com.crimsoncentral.game.ModeProfile;
import com.crimsoncentral.ranks.Rank;
import com.crimsoncentral.ranks.RanksManager;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.server_player.ServerPlayer;
import com.crimsoncentral.util.other.ChatUtil;

import net.md_5.bungee.api.ChatColor;

public class ArenaCommands implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {

			Player p = (Player) sender;

			if (cmd.getName().equalsIgnoreCase("newarenaconfig")) {

				if (RanksManager.hasRank(p, RanksManager.buildteam, false) == true
						|| RanksManager.canPlayerPreform(p, RanksManager.admin, true) == true) {
					for (GameProfile gp : ArenaRegister.getGameProfiles()) {

						if (gp.getName().equalsIgnoreCase(args[0])) {

							String all_arguements = "";

							for (String s : args) {

								if (!s.equalsIgnoreCase(args[0])) {

									all_arguements = all_arguements + s;

								}

							}

							for (ModeProfile mp : gp.getModeProfiles()) {

								if (mp.getName().replace(" ", "").equalsIgnoreCase(all_arguements)) {

									String s = "";

									for (String s1 : args) {

										s = s + " " + ChatUtil.randomChatColor() + ChatColor.BOLD
												+ Character.toUpperCase(s1.charAt(0)) + s1.substring(1) + ", ";
									}

									mp.getConfig().create(p);
									p.sendMessage(ChatColor.GREEN + "Successfully created a new config for:"
											+ ChatColor.GOLD + ChatColor.BOLD + s);
									break;
								}

							}

						}

					}
				}

			} else if (cmd.getName().equalsIgnoreCase("who")) {

				Arena arena = ArenaUtil.getPlayerArena((Player) sender);

				if (arena != null) {
					sender.sendMessage(
							ChatColor.GREEN + "" + ChatColor.BOLD + "-----------------------------------------");
					sender.sendMessage(ArenaUtil.centerText(ChatColor.WHITE + "" + ChatColor.BOLD + "PLAYERS:"));

					ArrayList<Team> teams = arena.getTeams();

					int i = 0;
					for (Team t : teams) {
						i = i + 1;
						ArrayList<String> names = new ArrayList<String>();

						for (Player p1 : t.getPlayers()) {

							names.add(PlayerManager.getServerPlayer(p1).getRank().getRankColor() + p1.getName());
						}

						sender.sendMessage(ChatColor.GOLD + "Team-" + i + ": " + ChatColor.RESET
								+ String.join(", ", String.join(", ", names.stream().toArray(String[]::new))));

					}
					sender.sendMessage(
							ChatColor.GREEN + "" + ChatColor.BOLD + "-----------------------------------------");
				} else {

					sender.sendMessage(ChatColor.RED
							+ "You don't appear to be in a game or lobby! You Cant use it unless you are!");
				}

			} else if (cmd.getName().equalsIgnoreCase("performance")) {

				sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
				sender.sendMessage(ArenaUtil.centerText(ChatColor.GOLD + "" + ChatColor.BOLD + Main.server_name
						+ ChatColor.GREEN + ChatColor.BOLD + " Information"));
				sender.sendMessage(" ");
				sender.sendMessage(ArenaUtil.centerText(ChatColor.AQUA + "Players Online: " + ChatColor.WHITE
						+ Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers()));
				sender.sendMessage(ArenaUtil.centerText(ChatColor.DARK_GREEN + "# of Plugins: " + ChatColor.WHITE
						+ Bukkit.getPluginManager().getPlugins().length));
				sender.sendMessage(" ");
				sender.sendMessage(ArenaUtil.centerText(ChatColor.YELLOW + "Ram Usage: " + ChatColor.WHITE
						+ ((Runtime.getRuntime().freeMemory() / 1024) / 1024) + "/"
						+ ((Runtime.getRuntime().maxMemory() / 1024) / 1024) + " MB "));
				sender.sendMessage(ArenaUtil.centerText(ChatColor.RED + "CPU Usage: " + ChatColor.WHITE + "error"));
				sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
			} else if (cmd.getName().equalsIgnoreCase("rank")) {
				boolean b = false;

				if (sender == Bukkit.getConsoleSender()) {
					b = true;
				} else if (sender instanceof Player) {

					if (((Player) sender).getUniqueId() == Bukkit
							.getOfflinePlayer(UUID.fromString("e5f7e077-2964-45f4-aefd-d2180368067b")).getUniqueId()
							|| ((Player) sender).getUniqueId() == Bukkit
									.getOfflinePlayer(UUID.fromString("3667d39a-767e-4f32-b681-b4bcb7b1896f"))
									.getUniqueId()
							|| RanksManager.canPlayerPreform(p, RanksManager.admin, true)) {

						b = true;
					}

				}
				if (b == true) {
					if (args.length == 0) {
						sender.sendMessage(ChatColor.RED + "Invaild Arguement! Please use set or reset!");
					} else if (args.length >= 1) {
						if (args[0].equalsIgnoreCase("set")) {
							if (args.length >= 3) {
								Rank r = RanksManager.getRank(args[2]);
								@SuppressWarnings("deprecation")
								Player player = (Player) Bukkit.getOfflinePlayer(args[1]);
								if (r != null && player != null) {

									if (r == RanksManager.owner) {

										if (((Player) sender).getUniqueId() == Bukkit
												.getOfflinePlayer(
														UUID.fromString("e5f7e077-2964-45f4-aefd-d2180368067b"))
												.getUniqueId()
												|| ((Player) sender).getUniqueId() == Bukkit
														.getOfflinePlayer(
																UUID.fromString("3667d39a-767e-4f32-b681-b4bcb7b1896f"))
														.getUniqueId()) {

											if (((Player) player).getUniqueId() == Bukkit
													.getOfflinePlayer(
															UUID.fromString("e5f7e077-2964-45f4-aefd-d2180368067b"))
													.getUniqueId()
													|| ((Player) player).getUniqueId() == Bukkit
															.getOfflinePlayer(UUID
																	.fromString("3667d39a-767e-4f32-b681-b4bcb7b1896f"))
															.getUniqueId()) {

												PlayerManager.getServerPlayer(player).setRank(r);

												PlayerManager.getServerPlayer(player).getRank()
														.applyPlayerPrefix(player);

												sender.sendMessage(
														"" + ChatColor.BOLD + ChatColor.GREEN + "Welcome Back "
																+ PlayerManager.getServerPlayer(player).getRank()
																		.getRankColor()
																+ ChatColor.BOLD + sender.getName() + ChatColor.BOLD
																+ ChatColor.GREEN + "!");

											} else {

												sender.sendMessage(PlayerManager.getServerPlayer(player).getRank()
														.getRankColor() + player.getName() + ChatColor.DARK_RED
														+ " is not allowed to be given "
														+ RanksManager.owner.getPrefix().replace("[", "")
																.replace("]", "").replace("§l", "").replace(" ", "")
														+ ChatColor.DARK_RED + " rank!");
											}

										} else {

											sender.sendMessage(
													ChatColor.DARK_RED + "You do not have permission to set anyone to "
															+ RanksManager.owner.getPrefix().replace("[", "")
																	.replace("]", "").replace("§l", "").replace(" ", "")
															+ ChatColor.DARK_RED + " rank!");
										}

									} else {

										PlayerManager.getServerPlayer(player).setRank(r);

										PlayerManager.getServerPlayer(player).getRank().applyPlayerPrefix(player);
										String rank = ChatColor.GRAY + "Default";
										if (PlayerManager.getServerPlayer(player)
												.getRank() != RanksManager.default_rank) {
											rank = PlayerManager.getServerPlayer(player).getRank().getPrefix()
													.replace("[", "").replace("]", "").replace("§l", "")
													.replace(" ", "");

										}

										sender.sendMessage(ChatColor.GREEN + "You set " + player.getName()
												+ "'s rank to " + rank + ChatColor.GREEN + "!");
									}

								} else if (r == null && player == null) {

									sender.sendMessage(ChatColor.RED
											+ "Thats not a vaild rank and thats also not a valid player!");

								} else if (r == null) {
									sender.sendMessage(ChatColor.RED + "Thats not a vaild rank!");
								} else if (player == null) {
									sender.sendMessage(ChatColor.RED + "Thats not a vaild player!");
								}
							} else {
								sender.sendMessage(ChatColor.RED + "Please supply a player name and a rank!");

							}
						} else if (args[0].equalsIgnoreCase("reset")) {
							if (args.length >= 2) {
								@SuppressWarnings("deprecation")
								Player player = (Player) Bukkit.getOfflinePlayer(args[1]);
								if (player != null) {

									PlayerManager.getServerPlayer(player).setRank(RanksManager.default_rank);

									PlayerManager.getServerPlayer(player).getRank().applyPlayerPrefix(player);
									sender.sendMessage(ChatColor.GREEN + "You reset " + player.getName() + "'s rank to "
											+ ChatColor.GRAY + "Default" + ChatColor.GREEN + "!");
								} else if (player == null) {
									sender.sendMessage(ChatColor.RED + "Thats not a vaild player!");
								}

							} else {
								sender.sendMessage(ChatColor.RED + "Invaild Arguement! Please use set or reset!");

							}

						} else {

							sender.sendMessage(ChatColor.RED + "Please supply a player name!");
						}
					}
				}

			} else if (cmd.getName().equalsIgnoreCase("arena-manager")) {
				if (RanksManager.canPlayerPreform(p, RanksManager.admin, true))
					ArenaManager.openArenaManagerMenu(p);
			} else if (cmd.getName().equalsIgnoreCase("force-start")) {
				if (sender instanceof Player) {

					ServerPlayer sp = PlayerManager.getServerPlayer(p);
					Arena a = sp.getArena();

					if (a != null) {
						if (a.getStage() == ArenaStage.PRE_COUNT_DOWN)
							a.setStage(ArenaStage.COUNT_DOWN);
						a.sendWorldMessage(sp.getRank().getPlayerFullName(p) + ChatColor.GREEN
								+ " has force started the game! Starting in " + ChatColor.GOLD + a.getGameTimer()
								+ " seconds...");

					}
				}

			} else if (cmd.getName().equalsIgnoreCase("set-game-timer")) {
				if (sender instanceof Player) {

					ServerPlayer sp = PlayerManager.getServerPlayer(p);
					Arena a = sp.getArena();

					if (RanksManager.canPlayerPreform(p, RanksManager.admin, true) && a != null) {
						if (a.getStage() == ArenaStage.GAME_TIME) {

							if (isInteger(args[0])) {
								int i = Integer.valueOf(args[0]);

								if (i <= a.getMaxGameTime() && i > 0) {

									a.setGameTimer(i);
								} else {

									p.sendMessage(ChatColor.RED + "Please specify an integer between " + ChatColor.WHITE
											+ "1-" + a.getMaxGameTime());

								}

							} else {
								p.sendMessage(ChatColor.RED + args[0] + " is not a integer!");

							}

						}

					} else {

						p.sendMessage(ChatColor.RED + "You must be in an arena to use this command!");
					}
				}

			} else if (cmd.getName().equalsIgnoreCase("ranks")) {
				if (RanksManager.canPlayerPreform(p, RanksManager.helper, true)) {
					p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
					p.sendMessage(ArenaUtil.centerText(ChatColor.WHITE + "" + ChatColor.BOLD + "ALL RANKS"));
					p.sendMessage(" ");

					HashMap<Integer, String> ss = new HashMap<Integer, String>();
					for (Entry<String, Rank> e : RanksManager.stringranks.entrySet()) {

						String prefix = e.getValue().getPrefix();

						if (e.getValue() == RanksManager.default_rank) {

							prefix = ChatColor.GRAY + "Default";

						}

						ss.put(e.getValue().getPriority(), ArenaUtil.centerText(
								prefix + ChatColor.WHITE + " - " + ChatColor.GOLD + e.getValue().getPriority()));

					}

					for (int i = 0; i <= ss.size(); ++i) {

						p.sendMessage(ss.get(i));

					}

					p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
				}

			} else if (cmd.getName().equalsIgnoreCase("freeze")) {
				if (RanksManager.canPlayerPreform(p, RanksManager.srmod, true)) {
					if (args.length == 2) {

						Player player = Bukkit.getPlayer(args[1]);
						BlockedMovementType bmt = null;
						if (args[0].equalsIgnoreCase("movement")) {

							bmt = BlockedMovementType.MOVEMENT;

						} else if (args[0].equalsIgnoreCase("all")) {
							bmt = BlockedMovementType.ALL;
						} else {
							p.sendMessage(ChatColor.RED + "Please provide the arguement, movement or all");
							return false;
						}

						if (player != null) {

							ArenaEvents.freezePlayer(player, bmt);
							p.sendMessage(ChatColor.GREEN + "You froze "
									+ PlayerManager.getServerPlayer(player).getRank().getPlayerFullName(player));

						} else {

							p.sendMessage(ChatColor.RED + "Please specify an online player!");
						}

					} else {

						p.sendMessage(ChatColor.RED
								+ "Please provide type of freeze (movement or all) and player you would like to freeze");

					}
				}
			} else if (cmd.getName().equalsIgnoreCase("un-freeze")) {
				Player player = Bukkit.getPlayer(args[0]);

				if (args.length == 1) {
					if (player != null) {

						ArenaEvents.frozen_players.remove(player, ArenaEvents.frozen_players.get(player));
						p.sendMessage(ChatColor.GREEN + "You un-froze "
								+ PlayerManager.getServerPlayer(player).getRank().getPlayerFullName(player));

					} else {

						p.sendMessage(ChatColor.RED + "Please specify an online player!");
					}
				} else {

					p.sendMessage(ChatColor.RED + "Please provide the playe you would like to un freeze!");

				}
			} else if (null == null) {

			}
		} else {

		}

		return false;

	}

	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}
}
