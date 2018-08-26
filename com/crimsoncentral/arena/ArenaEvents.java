package com.crimsoncentral.arena;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import com.crimsoncentral.Main;
import com.crimsoncentral.arena.Arena.ArenaStage;
import com.crimsoncentral.arena.Team.PlayerStatus;
import com.crimsoncentral.arena.util.ArenaUtil;
import com.crimsoncentral.arena.util.Hologram;
import com.crimsoncentral.arena.util.NPC;
import com.crimsoncentral.arena.util.kits.Kit;
import com.crimsoncentral.arena.util.kits.KitInventory;
import com.crimsoncentral.ranks.RanksManager;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.server_player.ServerPlayer;

import com.crimsoncentral.util.GeneralUtilities;
import com.crimsoncentral.util.item.ActionItem;
import com.crimsoncentral.util.item.ActionItemManager;
import com.crimsoncentral.util.mysql.Sql;
import com.crimsoncentral.util.other.ChatUtil;
import com.crimsoncentral.util.other.DefaultFontInfo;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PlayerConnection;

public class ArenaEvents implements Listener, CommandExecutor {

	public static HashMap<Inventory, KitInventory> kit_selectors = new HashMap<Inventory, KitInventory>();
	public static HashMap<ItemStack, KitInventory> kit_selector_items = new HashMap<ItemStack, KitInventory>();
	public static HashMap<Player, Arena> queued_to_join = new HashMap<Player, Arena>();

	public static HashMap<Player, BlockedMovementType> frozen_players = new HashMap<Player, BlockedMovementType>();

	public static enum BlockedMovementType {

		ALL, MOVEMENT

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerJoin(PlayerJoinEvent e) {
		e.setJoinMessage(null);

		Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
			public void run() {

				Player player = e.getPlayer();
				Arena arena = ArenaUtil.getPlayerArena(player);

				Sql.establishConnection();

				ServerPlayer sp = new ServerPlayer(player, RanksManager.getRank("default"), null, null, arena, null,
						null, null, null, null, null);
				sp.setRank(RanksManager
						.getRank(Sql.getString("player_data", "player_rank", "player", player.getName().toString())));

				if (arena != null) {
					if (arena.getConditions().contains(Arena.Conditions.ELIMINATE_ON_LOG_OUT)) {

						if (arena.getStage() == ArenaStage.GAME_TIME) {
							arena.playerDeath(player, null, DamageCause.CUSTOM);
							arena.checkForLastTeam();
						}
					} else {

					}
				} else if (queued_to_join.get(player) != null) {

					queued_to_join.get(player).join(player);
				} else if (Main.is_test_server == true) {

					ArenaUtil.joinArena(player, 0.0, "none");

				}

				Iterator<Entry<Arena, Double>> it = ArenaManager.local_arenas.entrySet().iterator();
				while (it.hasNext()) {
					Entry<Arena, Double> pair = it.next();
					Arena a = pair.getKey();

					if (a.getWorld() == player.getWorld()) {

						PlayerManager.getServerPlayer(player).setArena(pair.getKey());

					}

					if (a.getWorld() != player.getWorld()) {

						if (a.getAlivePlayers().contains(player)
								&& a.getConditions().contains(Arena.Conditions.ELIMINATE_ON_LOG_OUT)) {
							if (arena.getStage() == ArenaStage.GAME_TIME) {
								a.playerDeath(player, null, null);
								arena.checkForLastTeam();
							}
						}

					}

				}
				sp.setArena(ArenaUtil.getPlayerArena(player));
				RanksManager.applyRankPrefixes(player.getWorld());
				Sql.closeConnection();
			}
		}, 2);

	}

	int task1;
	int timer;

	@EventHandler
	public void ServerListPing(ServerListPingEvent event) {

		event.setMaxPlayers(Bukkit.getOnlinePlayers().size() + 1);
		event.setMotd(centerMOTDLine(ChatColor.DARK_RED + "Crimson " + ChatColor.GOLD + "Central " + ChatColor.GREEN
				+ ChatColor.MAGIC + "|" + ChatColor.RESET + ChatColor.AQUA + " Supports 1.8-1.13") + "\n"
				+ centerMOTDLine(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "25+ NEW GAMES" + ChatColor.GRAY
						+ ChatColor.BOLD + " - " + ChatColor.GOLD + ChatColor.BOLD + "200+ NEW FEATURES"));

	}

	public final static int CENTER_PX = 130;

	public String centerMOTDLine(String text) {
		if (text == null || text.equals(""))

			text = ChatColor.translateAlternateColorCodes('&', text);

		int textPxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for (char c : text.toCharArray()) {
			if (c == '§') {
				previousCode = true;
				continue;
			} else if (previousCode == true) {
				previousCode = false;
				if (c == 'l' || c == 'L') {
					isBold = true;
					continue;
				} else
					isBold = false;
			} else {
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				textPxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				textPxSize++;
			}
		}

		int halvedtextSize = textPxSize / 2;
		int toCompensate = CENTER_PX - halvedtextSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}
		return sb.toString() + text;
	}

	@EventHandler
	public void playerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		Arena arena = ArenaUtil.getPlayerArena(player);

		e.setQuitMessage(null);
		if (arena != null) {
			if (arena.getStage() == ArenaStage.GAME_TIME) {
				arena.playerDeath(player, null, DamageCause.CUSTOM);
				arena.checkForLastTeam();
			} else {
				Team t = arena.getPlayerTeam(player);
				if (t != null) {
					t.players.remove(player, t.players.get(player));
				}
			}
			if (arena.getConditions().contains(Arena.Conditions.ELIMINATE_ON_LOG_OUT)) {

				arena.quit(player);
				arena.checkForLastTeam();
			}

		}

		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		for (@SuppressWarnings("unused")
		Entry<NPC, World> e1 : NPC.npcs.entrySet()) {

		}

		for (Entry<Hologram, World> h : Hologram.holograms.entrySet()) {

			connection.sendPacket(new PacketPlayOutEntityDestroy(h.getKey().holo.getId()));

		}

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getCurrentItem() != null) {
			ActionItem action_item = ActionItemManager.getActionItem(e.getCurrentItem());
			Player p = (Player) e.getWhoClicked();
			if (action_item != null) {

				action_item.preform(p);

				e.setCancelled(true);

			} else {

				if (ArenaUtil.getPlayerArena((Player) e.getWhoClicked()) != null) {

					Iterator<Entry<Inventory, KitInventory>> it = kit_selectors.entrySet().iterator();
					while (it.hasNext()) {
						Entry<Inventory, KitInventory> pair = it.next();

						if (e.getInventory() == e.getInventory()) {

							Iterator<Entry<Kit, ItemStack>> it1 = pair.getValue().kits.entrySet().iterator();
							while (it1.hasNext()) {
								Entry<Kit, ItemStack> pair1 = it1.next();

								if (pair1.getValue().getType() == e.getCurrentItem().getType()
										&& pair1.getValue().getItemMeta().getDisplayName() == e.getCurrentItem()
												.getItemMeta().getDisplayName()) {

									pair.getValue().playerSelectKit((Player) e.getWhoClicked(), pair1.getKey());
									e.getWhoClicked().sendMessage(ChatColor.GREEN + "You Selected the "
											+ ChatColor.YELLOW + pair1.getKey().kit_name + ChatColor.GREEN + " kit!");
									e.setCancelled(true);
									e.getWhoClicked().closeInventory();
								}

							}
						}

					}

					if (!ArenaUtil.getPlayerArena((Player) e.getWhoClicked()).getConditions()
							.contains(Arena.Conditions.CAN_MOVE_ITEMS)) {
						e.setCancelled(true);

					}

					if (ArenaUtil.getPlayerArena((Player) e.getWhoClicked()).getDeadPlayers().contains(p)) {
						e.setCancelled(true);

					}

				}
			}
		}

	}

	@EventHandler
	public void kitSelector(PlayerInteractEvent e) {
		if (e.getItem() != null) {

			if (ArenaUtil.getPlayerArena(e.getPlayer()) != null) {

				Iterator<Entry<ItemStack, KitInventory>> it = kit_selector_items.entrySet().iterator();
				while (it.hasNext()) {
					Entry<ItemStack, KitInventory> pair = it.next();

					if (pair.getKey().getType() == e.getItem().getType() && pair.getKey().getItemMeta()
							.getDisplayName() == e.getItem().getItemMeta().getDisplayName()) {

						pair.getValue().openInventory(e.getPlayer());
						break;
					}

				}
			}
		}

	}

	@EventHandler
	public void actionItemItems(PlayerInteractEvent e) {
		if (e.getItem() != null) {

			ActionItem action_item = ActionItemManager.getActionItem(e.getItem());

			if (action_item != null) {

				Player p = (Player) e.getPlayer();

				action_item.preform(p);

				e.setCancelled(true);

			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void PlayerDamageReceive(EntityDamageByEntityEvent e) {

		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {

			Player damaged = (Player) e.getEntity();

			Player damager = (Player) e.getDamager();

			DamageCause cause = e.getCause();
			Arena arena = ArenaUtil.getPlayerArena(damaged);

			if (arena != null && ArenaUtil.getPlayerArena(damager) != null) {

				if (arena.getPlayerTeam(damaged).players.get(damaged) != Team.PlayerStatus.DEAD
						&& arena.getPlayerTeam(damager) != null && arena.getPlayerTeam(damaged) != null
						&& arena.getPlayerTeam(damager) != arena.getPlayerTeam(damaged)
						&& arena.getConditions().contains(Arena.Conditions.CAN_PVP)
						&& arena.getConditions().contains(Arena.Conditions.CAN_TAKE_DAMAGE)
						&& arena.getAlivePlayers().contains(damager)) {

					Team team = arena.getPlayerTeam(damaged);

					DamageTag tag = DamageTag.getDamageTag(damaged);
					for (DamageTag d_tag : DamageTag.damage_tags) {

						if (d_tag.damaged == damaged) {

							tag = d_tag;
							tag.damager = damager;
							tag.expire_time = 10;
							break;
						}

					}

					if (tag == null) {

						tag = new DamageTag(10, damager, damaged);

					}

					if (damaged.getHealth() - e.getDamage() <= 0) {

						e.setCancelled(true);

						if (team.can_respawn == true) {

							arena.getStats(damager).addKill(1);
						} else if (team.can_respawn == false) {

							arena.getStats(damager).addFinalKill(1);

						}

						team.players.put(damaged, Team.PlayerStatus.DEAD);

						arena.playerDeath(damaged, damager, cause);
						arena.checkForLastTeam();

					}

				} else {

					e.setCancelled(true);
				}

			}

		} else if (e.getDamager() instanceof Projectile && e.getEntity() instanceof Player) {
			Projectile pro = (Projectile) e.getDamager();
			if (pro.getShooter() instanceof Player) {
				Player damager = (Player) pro.getShooter();
				Player damaged = (Player) e.getEntity();

				DamageCause cause = e.getCause();
				Arena arena = ArenaUtil.getPlayerArena(damaged);

				Team team = arena.getPlayerTeam(damaged);
				if (arena != null && ArenaUtil.getPlayerArena(damager) != null && damager != damaged) {
					DecimalFormat f = new DecimalFormat("0.#");
					Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
						public void run() {
							damager.sendMessage(PlayerManager.getServerPlayer(damaged).getRank().getRankColor()
									+ damaged.getName() + ChatColor.GREEN + " is on " + ChatColor.GRAY
									+ f.format(damaged.getHealth()) + "§c❤ ");

						}
					}, 3);
					if (arena.getPlayerTeam(damaged).players.get(damaged) != Team.PlayerStatus.DEAD
							&& arena.getPlayerTeam(damager) != null && arena.getPlayerTeam(damaged) != null
							&& arena.getPlayerTeam(damager) != arena.getPlayerTeam(damaged)
							&& arena.getConditions().contains(Arena.Conditions.CAN_PVP)
							&& arena.getConditions().contains(Arena.Conditions.CAN_TAKE_DAMAGE)) {

						DamageTag tag = DamageTag.getDamageTag(damaged);
						for (DamageTag d_tag : DamageTag.damage_tags) {

							if (d_tag.damaged == damaged) {

								tag = d_tag;
								tag.damager = damager;
								tag.expire_time = 10;
								break;
							}

						}

						if (tag == null) {

							tag = new DamageTag(10, damager, damaged);

						}

						if (damaged.getHealth() - e.getDamage() <= 0) {

							e.setCancelled(true);

							if (team.can_respawn == true) {

								arena.getStats(damager).addKill(1);
							} else if (team.can_respawn == false) {

								arena.getStats(damager).addFinalKill(1);

							}

							team.players.put(damaged, Team.PlayerStatus.DEAD);
							arena.playerDeath(damaged, damager, cause);
							arena.checkForLastTeam();

						}

					} else {

						e.setCancelled(true);
					}

				}
			}
		} else if (!(e.getDamager() instanceof Player) && e.getEntity() instanceof Player) {

			Player p = (Player) e.getEntity();
			ServerPlayer sp = PlayerManager.getServerPlayer(p);
			Arena a = sp.getArena();
			if (a != null && a.getDeadPlayers().contains(p)) {

				e.setCancelled(true);

			}

		} else if ((e.getDamager() instanceof Player) && !(e.getEntity() instanceof Player)) {

			Player p = (Player) e.getDamager();
			ServerPlayer sp = PlayerManager.getServerPlayer(p);
			Arena a = sp.getArena();
			if (a != null && a.getDeadPlayers().contains(p)) {

				e.setCancelled(true);

			}
		}

	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {

		if (e.getEntity() instanceof Player) {

			DamageCause cause = e.getCause();
			Player damaged = (Player) e.getEntity();
			Arena arena = ArenaUtil.getPlayerArena(damaged);

			if (ArenaUtil.getPlayerArena(damaged) != null) {
				Team team = arena.getPlayerTeam(damaged);
				if (!ArenaUtil.getPlayerArena(damaged).getConditions().contains(Arena.Conditions.CAN_TAKE_DAMAGE)
						|| arena.getDeadPlayers().contains(damaged)) {

					e.setCancelled(true);
				} else {

					if (arena.getPlayerTeam(damaged).getPlayerStatus(damaged) == PlayerStatus.DEAD) {

						e.setCancelled(true);

					} else {
						if (damaged.getHealth() - e.getDamage() <= 0) {

							e.setCancelled(true);

							if (team.can_respawn == true) {
								team.players.put(damaged, Team.PlayerStatus.RESPAWNING);
								team.respawn(damaged);

							} else {

								team.players.put(damaged, Team.PlayerStatus.DEAD);

							}

							arena.playerDeath(damaged, null, cause);
							arena.checkForLastTeam();

						}

					}

				}
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Arena arena = ArenaUtil.getPlayerArena(player);
		Location l = player.getLocation();
		if (arena != null) {
			Team team = arena.getPlayerTeam(player);
			if (player.getLocation().getBlockY() < 0 && team.players.get(player) == Team.PlayerStatus.ALIVE) {

				team.players.put(player, Team.PlayerStatus.DEAD);
				if (arena.getStage() == ArenaStage.GAME_TIME) {
					arena.playerDeath(player, null, DamageCause.VOID);
				}
				arena.checkForLastTeam();

			}

			int radius = arena.getRadius();
			double dis = player.getLocation().distance(arena.getArenaSpawn());
			if (dis > radius) {

				Location playerLocation = player.getLocation(); // get the player's location

				double y = Math.pow(0.5, Math.pow(2, radius) - Math.pow(2, l.getX()) - Math.pow(2, l.getZ()));
				double safeY = l.getY() - y + 0.5;

				playerLocation.setY(l.getY() - safeY + l.getY());

				Vector playerDirection = player.getLocation().getDirection(); // get the player's direction
				playerDirection.multiply(2); // multiply the direction by 2, now it's 2 blocks long

				Location targetLocation = playerLocation.subtract(playerDirection); // add the direction to the player's
				targetLocation.setDirection(l.subtract(arena.getArenaSpawn()).toVector()); // location, now we have the
				// player's target location
				player.teleport(targetLocation); // teleport the player to the target location

				player.sendMessage(
						ChatColor.RED + "Arena border reached! You can not travel any further in this direction!");

			}

		}

	}

	HashMap<ArmorStand, Player> throwers = new HashMap<ArmorStand, Player>();
	ArrayList<ArmorStand> flying_swords = new ArrayList<ArmorStand>();
	ArrayList<ArmorStand> stuck_swords = new ArrayList<ArmorStand>();

	@EventHandler
	public void BallisticArenaWeaponThrowing(PlayerInteractEvent e) {

		Player player = e.getPlayer();
		Location loc = player.getLocation();
		ItemStack is = e.getItem();
		ServerPlayer sp = PlayerManager.getServerPlayer(player);
		Arena a = sp.getArena();
		if (a != null && !a.getDeadPlayers().contains(player))
			if (e.getItem() != null && a != null) {
				if (a.isBallistic()) {
					if (player.isSneaking()) {
						if (is.getType() == Material.WOOD_SWORD || is.getType() == Material.STONE_SWORD
								|| is.getType() == Material.IRON_SWORD || is.getType() == Material.DIAMOND_SWORD
								|| is.getType() == Material.GOLD_AXE || is.getType() == Material.DIAMOND_AXE
								|| is.getType() == Material.WOOD_AXE || is.getType() == Material.STONE_AXE
								|| is.getType() == Material.IRON_AXE || is.getType() == Material.GOLD_AXE
								|| is.getType() == Material.DIAMOND_AXE || is.getType() == Material.WOOD_PICKAXE
								|| is.getType() == Material.STONE_PICKAXE || is.getType() == Material.IRON_PICKAXE
								|| is.getType() == Material.GOLD_PICKAXE || is.getType() == Material.DIAMOND_PICKAXE) {

							ArmorStand t = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);

							t.setArms(true);
							t.setRightArmPose(new EulerAngle(0, 0, 360));
							t.setItemInHand(is);

							t.setVisible(false);
							t.setBodyPose(
									new EulerAngle(player.getLocation().getYaw(), player.getLocation().getPitch(), 0));
							loc.setY(loc.getY() - 1);
							t.teleport(loc);
							t.setCollidable(false);
							t.setHeadPose(
									new EulerAngle(player.getLocation().getYaw(), player.getLocation().getPitch(), 0));
							t.setVelocity(
									loc.getDirection().multiply(5 + player.getVelocity().lengthSquared()).setY(1));

							player.getInventory().setItem(player.getInventory().getHeldItemSlot(),
									new ItemStack(Material.AIR));
							flying_swords.add(t);
							throwers.put(t, player);

							if (started == false) {

								run();
								started = true;
							}

						}

					}
				}
			}

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void NpcInteract(PlayerInteractEntityEvent event) {
		Player p = event.getPlayer();
		Entity e = event.getRightClicked();

		if (e != null) {

			if (e.getType() == EntityType.ARMOR_STAND && stuck_swords.contains((ArmorStand) e)) {

				if (p.getItemInHand() == null) {

					ArmorStand a = (ArmorStand) e;

					event.setCancelled(true);

					flying_swords.remove(a);
					stuck_swords.remove(a);
					throwers.remove(a, throwers.get(a));
					p.getInventory().setItem(p.getInventory().getHeldItemSlot(), a.getItemInHand());
					a.setItemInHand(null);
					a.damage(a.getHealth() + 1);

				} else {

					p.sendMessage(ChatColor.RED + "You cant pick up weapons unless your hand is empty");
				}

			}

		}
	}

	int ballistic_task;
	boolean started = false;

	public void run() {
		ballistic_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {

				for (ArmorStand a : flying_swords) {

					if (a.getVelocity().length() > 0.079) {

						Location lpre = a.getLocation().subtract(0, 0.05, 0);

						a.teleport(lpre);
						for (Entity e : a.getNearbyEntities(1, 1, 1)) {

							if (e instanceof Player) {

								Player p = (Player) e;
								double dur = a.getItemInHand().getDurability();
								double damage = (dur / 125) / 2 + 2;

								p.damage(damage, throwers.get(a));

								DecimalFormat f = new DecimalFormat(".##");

								PlayerDamageReceive(new EntityDamageByEntityEvent(throwers.get(a), p,
										DamageCause.PROJECTILE, damage));
								throwers.get(a)
										.sendMessage(PlayerManager.getServerPlayer(p).getRank().getRankColor()
												+ p.getName() + ChatColor.GREEN + " is on " + ChatColor.GRAY
												+ f.format(p.getHealth()) + "§c❤ ");
								for (Player p1 : e.getWorld().getPlayers()) {

									p1.playEffect(e.getLocation(), Effect.HEART, 7);
								}

							}

						}

					} else if (!stuck_swords.contains(a) && a.getVelocity().length() <= 0.079) {
						Location loc = a.getLocation();
						a.setRightArmPose(new EulerAngle(-55, 180, 0));
						a.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY() - 1.5, loc.getZ(), loc.getYaw(),
								loc.getPitch()));
						stuck_swords.add(a);

					}

					if (a.getItemInHand() == null) {

						flying_swords.remove(a);
						stuck_swords.remove(a);
						throwers.remove(a, throwers.get(a));
						a.damage(a.getHealth() + 1);
					}

				}

				if (flying_swords.size() == 0 && stuck_swords.size() == 0 && throwers.size() == 0) {
					Bukkit.getScheduler().cancelTask(ballistic_task);
					started = false;

				}

			}

		}, 0, 1);
	}

	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e) {
		Player player = e.getPlayer();
		Arena arena = ArenaUtil.getPlayerArena(player);

		if (arena != null) {
			if (!arena.getConditions().contains(Arena.Conditions.CAN_MOVE_ITEMS)
					&& arena.getDeadPlayers().contains(player)) {
				e.setCancelled(true);
			}

		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {

		Player p = e.getPlayer();

		if (ArenaUtil.getPlayerArena(p) != null) {
			if (!ArenaUtil.getPlayerArena(p).getConditions().contains(Arena.Conditions.CAN_BLOCK_INTERACT)
					&& !ArenaUtil.getPlayerArena(p).getDeadPlayers().contains(p)) {

				e.setCancelled(true);
			}

		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {

		Player p = e.getPlayer();

		if (ArenaUtil.getPlayerArena(p) != null) {
			if (!ArenaUtil.getPlayerArena(p).getConditions().contains(Arena.Conditions.CAN_BLOCK_INTERACT)
					&& !ArenaUtil.getPlayerArena(p).getDeadPlayers().contains(p)) {

				e.setCancelled(true);
			}

		}
	}

	// HashMap<Double, Double> reach_vs_ping = new HashMap<Double, Double>();
	// HashMap<Double, Double> reach_vs_tps = new HashMap<Double, Double>();
	// HashMap<Double, Double> tps_vs_ping = new HashMap<Double, Double>();
	//
	// @EventHandler
	// public void PlayerDamageEvent(EntityDamageByEntityEvent e) {
	// if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
	// Player damaged = (Player) e.getEntity();
	// Player damager = (Player) e.getDamager();
	//
	// Location d1 = damaged.getLocation();
	// Location d2 = damager.getLocation();
	//
	// double x = Math.pow(Math.abs(d2.getX() - d1.getX()), 2);
	// double z = Math.pow(Math.abs(d2.getZ() - d1.getZ()), 2);
	// double q = Math.pow(x + z, 0.5);
	// double theta = (Math.PI * (90 - Math.abs(d2.getPitch()))) / 180;
	// double radian = Math.sin(theta);
	//
	// double reach = q * radian;
	//
	// if (reach >= 4.575) {
	//
	// reach_vs_ping.put(reach, (double) ((CraftPlayer) damager).getHandle().ping);
	// reach_vs_tps.put(reach, TPSLag.getTPS());
	// tps_vs_ping.put((double) ((CraftPlayer) damager).getHandle().ping,
	// TPSLag.getTPS());
	//
	// }
	//
	// if (reach_vs_ping.size() >= 7) {
	//
	// new DataPoint();
	//
	// ArrayList<Double> r1 = new ArrayList<Double>();
	// ArrayList<Double> p1 = new ArrayList<Double>();
	//
	// ArrayList<Double> r2 = new ArrayList<Double>();
	// ArrayList<Double> t1 = new ArrayList<Double>();
	//
	// ArrayList<Double> t2 = new ArrayList<Double>();
	// ArrayList<Double> p2 = new ArrayList<Double>();
	//
	// Iterator<Entry<Double, Double>> it = reach_vs_ping.entrySet().iterator();
	// while (it.hasNext()) {
	// Entry<Double, Double> pair = it.next();
	//
	// r1.add(pair.getKey());
	// p1.add(pair.getValue());
	//
	// }
	//
	// Iterator<Entry<Double, Double>> it2 = reach_vs_tps.entrySet().iterator();
	// while (it.hasNext()) {
	// Entry<Double, Double> pair2 = it2.next();
	//
	// r2.add(pair2.getKey());
	// t1.add(pair2.getValue());
	//
	// }
	//
	// Iterator<Entry<Double, Double>> it3 = reach_vs_tps.entrySet().iterator();
	// while (it.hasNext()) {
	// Entry<Double, Double> pair3 = it3.next();
	//
	// t2.add(pair3.getKey());
	// p2.add(pair3.getValue());
	//
	// }
	//
	// if (DataPoint.findCoorelation(r1, p1) <= 0
	// // || String.valueOf(DataPoint.findCoorelation(r1,
	// p1)).equalsIgnoreCase("nan")
	// && String.valueOf(DataPoint.findCoorelation(r2, t1)).equalsIgnoreCase("nan")
	// // && DataPoint.findCoorelation(t2, p2) < 0) {
	// ) {
	//
	// try {
	// if (BanEffect.effect(damager) == true) {
	// DecimalFormat f = new DecimalFormat(".##");
	//
	// String arena_type = "game";
	//
	// String ban = ChatColor.RED + "\nYou are temporarily banned from this server
	// for "
	// + ChatColor.WHITE + "30d 0h 0m 0s";
	//
	// if (PlayerManager.getServerPlayer(damager).getArena() != null
	// && PlayerManager.getServerPlayer(damager).getArena().getGamemode() < 1) {
	//
	// arena_type = "lobby";
	//
	// }
	//
	// for (Player p : damager.getWorld().getPlayers()) {
	//
	// p.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[VESPEN CHEAT
	// DETECTION] "
	// + ChatColor.RESET + ChatColor.WHITE + damager.getName() + ChatColor.RED
	// + " has been removed from your " + arena_type + " for hacking or abuse. "
	// + ChatColor.AQUA + "Thanks for reporting it!");
	//
	// }
	// ((CraftPlayer) damager).kickPlayer(ChatColor.DARK_PURPLE + "" +
	// ChatColor.BOLD
	// + "[VESPEN CHEAT DETECTION]\n" + ban + "\n\n" + ChatColor.GRAY + "Reason: "
	// + ChatColor.WHITE + "Blacklisted Modifications [R]" + "\n" + ChatColor.GRAY
	// + "Probablity of false ban: " + ChatColor.WHITE
	// + f.format((DataPoint.findCoorelation(r1, p1) + 0.1)).replace(".", "") + "%"
	// + "\n"
	// + ChatColor.GRAY + "Find out more: " + ChatColor.AQUA
	// + "http://crimson-central.com/vespen" + "\n" + ChatColor.GRAY + "Hits
	// Measured: "
	// + ChatColor.WHITE + reach_vs_ping.size() + ChatColor.GRAY + "\n\nBan-ID: "
	// + ChatColor.WHITE + ArenaUtil.decideArenaName().replace("Arena", "#8012"));
	// reach_vs_ping.clear();
	// reach_vs_tps.clear();
	// tps_vs_ping.clear();
	// }
	// } catch (InterruptedException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	//
	// }
	// }
	// }
	// }

	// HashMap<Double, Double> speed_vs_ping = new HashMap<Double, Double>();
	// HashMap<Double, Double> speed_vs_tps = new HashMap<Double, Double>();
	// HashMap<Double, Double> s_tps_vs_ping = new HashMap<Double, Double>();
	//
	// @EventHandler
	// public void PlayerMovEvent(PlayerMoveEvent e) {
	//
	// Player p = p;
	// double v = p.getVelocity().length();
	//
	// if (v > 0.2 && p.isOnGround() &&
	// !e.getTo().getBlock().getRelative(BlockFace.DOWN).getType().isOccluding()) {
	//
	// speed_vs_ping.put(v, (double) ((CraftPlayer) p).getHandle().ping);
	// speed_vs_tps.put(v, TPSLag.getTPS());
	// s_tps_vs_ping.put((double) ((CraftPlayer) p).getHandle().ping,
	// TPSLag.getTPS());
	//
	// }
	//
	// if (speed_vs_ping.size() >= 7) {
	//
	// DataPoint dp = new DataPoint();
	//
	// ArrayList<Double> r1 = new ArrayList<Double>();
	// ArrayList<Double> p1 = new ArrayList<Double>();
	//
	// ArrayList<Double> r2 = new ArrayList<Double>();
	// ArrayList<Double> t1 = new ArrayList<Double>();
	//
	// ArrayList<Double> t2 = new ArrayList<Double>();
	// ArrayList<Double> p2 = new ArrayList<Double>();
	//
	// Iterator<Entry<Double, Double>> it = speed_vs_ping.entrySet().iterator();
	// while (it.hasNext()) {
	// Entry<Double, Double> pair = it.next();
	//
	// r1.add(pair.getKey());
	// p1.add(pair.getValue());
	//
	// }
	//
	// Iterator<Entry<Double, Double>> it2 = speed_vs_tps.entrySet().iterator();
	// while (it.hasNext()) {
	// Entry<Double, Double> pair2 = it2.next();
	//
	// r2.add(pair2.getKey());
	// t1.add(pair2.getValue());
	//
	// }
	//
	// Iterator<Entry<Double, Double>> it3 = speed_vs_tps.entrySet().iterator();
	// while (it.hasNext()) {
	// Entry<Double, Double> pair3 = it3.next();
	//
	// t2.add(pair3.getKey());
	// p2.add(pair3.getValue());
	//
	// }
	//
	// if (DataPoint.findCoorelation(r1, p1) >= 0
	// // || String.valueOf(DataPoint.findCoorelation(r1,
	// p1)).equalsIgnoreCase("nan")
	// // && String.valueOf(DataPoint.findCoorelation(r2,
	// t1)).equalsIgnoreCase("nan")
	// // && DataPoint.findCoorelation(t2, p2) < 0) {
	// ) {
	//
	// DecimalFormat f = new DecimalFormat(".##");
	//
	// String arena_type = "game";
	//
	// String ban = ChatColor.RED + "\nYou are temporarily banned from this server
	// for " + ChatColor.WHITE
	// + "30d 0h 0m 0s";
	//
	// if (PlayerManager.getServerPlayer(p).getArena() != null
	// && PlayerManager.getServerPlayer(p).getArena().getGamemode() < 1) {
	//
	// arena_type = "lobby";
	//
	// }
	//
	// for (Player p11 : p.getWorld().getPlayers()) {
	//
	// p11.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "[VESPEN CHEAT
	// DETECTION] "
	// + ChatColor.RESET + ChatColor.WHITE + p.getName() + ChatColor.RED
	// + " has been removed from your " + arena_type + " for hacking or abuse. " +
	// ChatColor.AQUA
	// + "Thanks for reporting it!");
	//
	// }
	// ((CraftPlayer) p).kickPlayer(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD +
	// "[VESPEN CHEAT DETECTION]\n"
	// + ban + "\n\n" + ChatColor.GRAY + "Reason: " + ChatColor.WHITE + "Blacklisted
	// Modifications [S]"
	// + "\n" + ChatColor.GRAY + "Probablity of false ban: " + ChatColor.WHITE
	// + f.format((DataPoint.findCoorelation(r1, p1) + 0.1)).replace(".", "") + "%"
	// + "\n"
	// + ChatColor.GRAY + "Find out more: " + ChatColor.AQUA +
	// "http://crimson-central.com/vespen"
	// + "\n" + ChatColor.GRAY + "V's Measured: " + ChatColor.WHITE +
	// speed_vs_ping.size()
	// + ChatColor.GRAY + "\n\nBan-ID: " + ChatColor.WHITE
	// + ArenaUtil.decideArenaName().replace("Arena", "#8012"));
	// speed_vs_ping.clear();
	// speed_vs_tps.clear();
	// s_tps_vs_ping.clear();
	// }
	//
	// }
	//
	// }

	@EventHandler
	public void PlayerChangedWorldEvent(org.bukkit.event.player.PlayerChangedWorldEvent event) {
		Player p = event.getPlayer();
		ServerPlayer sp = PlayerManager.getServerPlayer(p);
		Arena prev_arena = sp.getArena();
		Iterator<Entry<Arena, Double>> it = ArenaManager.local_arenas.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Arena, Double> pair = it.next();
			Arena a = pair.getKey();

			if (a.getWorld() == p.getWorld()) {

				sp.setArena(pair.getKey());
				break;
			}

			if (prev_arena == sp.getArena()) {

				sp.setArena(null);

			}

			if (a.getWorld() != p.getWorld()) {

				if (a.getAlivePlayers().contains(p)
						&& a.getConditions().contains(Arena.Conditions.ELIMINATE_ON_LOG_OUT)) {
					if (sp.getArena() != null)
						a.playerDeath(p, null, null);
					a.checkForLastTeam();

				}

			}

		}

		RanksManager.applyRankPrefixes(p.getWorld());
	}

	int i = 30;
	int task;
	static boolean is_updating = false;

	// @EventHandler(priority = EventPriority.HIGHEST)
	// public void reprocessCommand(PlayerCommandPreprocessEvent e) {
	//
	// if (e.getMessage().equalsIgnoreCase("/reload")) {
	// e.setCancelled(true);
	// } else if (e.getMessage().equalsIgnoreCase("/tp")) {
	//
	// e.setCancelled(true);
	//
	// } else if (e.getMessage().equalsIgnoreCase("/gamemode")) {
	//
	// e.setCancelled(true);
	// }
	// }

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {

			Player p = (Player) sender;

			if (cmd.getName().equalsIgnoreCase("gamemode")) {
				if (RanksManager.hasRank(p, RanksManager.buildteam, false)
						|| RanksManager.canPlayerPreform(p, RanksManager.admin, true)) {

					if (args[0].equalsIgnoreCase("0")) {

						p.setGameMode(GameMode.SURVIVAL);
						p.sendMessage(ChatColor.GREEN + "Your gamemode was set to " + ChatColor.GOLD
								+ ChatUtil.toTitleCase(p.getGameMode().toString().toLowerCase()));
					} else if (args[0].equalsIgnoreCase("1")) {

						p.setGameMode(GameMode.CREATIVE);
						p.sendMessage(ChatColor.GREEN + "Your gamemode was set to " + ChatColor.GOLD
								+ ChatUtil.toTitleCase(p.getGameMode().toString().toLowerCase()));
					} else if (args[0].equalsIgnoreCase("2")) {

						p.setGameMode(GameMode.ADVENTURE);
						p.sendMessage(ChatColor.GREEN + "Your gamemode was set to " + ChatColor.GOLD
								+ ChatUtil.toTitleCase(p.getGameMode().toString().toLowerCase()));
					} else if (args[0].equalsIgnoreCase("3")) {

						p.setGameMode(GameMode.SPECTATOR);
						p.sendMessage(ChatColor.GREEN + "Your gamemode was set to " + ChatColor.GOLD
								+ ChatUtil.toTitleCase(p.getGameMode().toString().toLowerCase()));
					} else {

						p.sendMessage(
								ChatColor.RED + args[0] + " is not a valid gamemode! Please do one of the following:");
						p.sendMessage(ChatColor.WHITE + "- 0 for Survival Mode");
						p.sendMessage(ChatColor.WHITE + "- 1 for Creative Mode");
						p.sendMessage(ChatColor.WHITE + "- 2 for Adventure Mode");
						p.sendMessage(ChatColor.WHITE + "- 3 for Spectator Mode");

					}

				}
			} else if (cmd.getName().equalsIgnoreCase("gamemode")) {
				if (RanksManager.canPlayerPreform(p, RanksManager.srmod, true)) {

					if (args.length >= 3) {

						if (args.length == 2) {
							Player player_a = Bukkit.getPlayer(args[2]);

							if (player_a != null) {

								p.teleport(player_a);

								p.sendMessage(ChatColor.GREEN + "You teleported to "
										+ PlayerManager.getServerPlayer(player_a).getRank().getRankColor()
										+ player_a.getName());

							} else {

								if (player_a == null) {

									p.sendMessage(ChatColor.WHITE + args[2] + ChatColor.RED
											+ " is either not online or you spelled their name wrong wrong!");

								}

							}

						} else if (args.length == 3) {
							Player player_a = Bukkit.getPlayer(args[2]);
							Player player_b = Bukkit.getPlayer(args[3]);

							if (player_a != null && player_b != null) {

								player_a.teleport(player_b);

								p.sendMessage(ChatColor.GREEN + "You teleported "
										+ PlayerManager.getServerPlayer(player_a).getRank().getRankColor()
										+ player_a.getName() + ChatColor.GREEN + " to "
										+ PlayerManager.getServerPlayer(player_b).getRank().getRankColor()
										+ player_b.getName());

							} else {

								if (player_a == null) {

									p.sendMessage(ChatColor.RED + "Your first player, " + ChatColor.WHITE + args[2]
											+ ChatColor.RED + " is either not online or you spelled their name wrong!");

								}

								if (player_b == null) {

									p.sendMessage(ChatColor.RED + "Your second player, " + ChatColor.WHITE + args[3]
											+ ChatColor.RED + " is either not online or you spelled their name wrong!");

								}

							}

						}

					} else {

						p.sendMessage(ChatColor.RED + "Incorrect Usage! Please use /tp like this: " + ChatColor.WHITE
								+ "/tp {player-a} {player-b} " + ChatColor.RED + "or " + ChatColor.WHITE
								+ "/tp {player}");
					}
				}
			} else if (cmd.getName().equalsIgnoreCase("reload")) {

				if (RanksManager.canPlayerPreform(p, RanksManager.admin, true)) {

					if (Main.is_test_server == true) {

						for (Player p1 : Bukkit.getOnlinePlayers()) {

							p1.kickPlayer(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH
									+ "-+-|-+-[" + ChatColor.RESET + ChatColor.YELLOW + ChatColor.BOLD
									+ " SERVER PROTECTEION ENGAGED " + ChatColor.DARK_PURPLE + "" + ChatColor.BOLD
									+ ChatColor.STRIKETHROUGH + "]-+-|-+-\n\n"
									+ PlayerManager.getServerPlayer(p).getRank().getPlayerFullName(p) + ChatColor.GREEN
									+ " has reloaded the server!" + ChatColor.WHITE
									+ "\nAs a result, all players have been"
									+ "\nkicked to prevent things from breaking." + ChatColor.DARK_PURPLE + ""
									+ ChatColor.BOLD

									+ ChatColor.STRIKETHROUGH + "\n\n-+-|-+-[" + ChatColor.RESET + ChatColor.RED
									+ " You may rejoin in about 20 seconds " + ChatColor.DARK_PURPLE + ""
									+ ChatColor.BOLD + ChatColor.STRIKETHROUGH + "]-+-|-+-\n");
						}

						Bukkit.reload();
					} else {

						if (is_updating == false) {
							is_updating = true;
							i = 30;
							task1 = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {

								@Override
								public void run() {

									switch (i) {

									case 30:

										Bukkit.broadcastMessage(ChatColor.YELLOW + "" + ChatColor.BOLD
												+ "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
										Bukkit.broadcastMessage(" ");
										Bukkit.broadcastMessage(ArenaUtil
												.centerText(ChatColor.WHITE + "" + ChatColor.BOLD + "UPDATER"));
										Bukkit.broadcastMessage(" ");
										Bukkit.broadcastMessage(ArenaUtil
												.centerText(ChatColor.WHITE + "This server will be performing"));
										Bukkit.broadcastMessage(ArenaUtil.centerText(
												ChatColor.WHITE + "an update in " + ChatColor.GOLD + "30 seconds."));
										Bukkit.broadcastMessage(ArenaUtil
												.centerText(ChatColor.RED + "All online players will be kicked!"));
										Bukkit.broadcastMessage(" ");
										Bukkit.broadcastMessage(ChatColor.YELLOW + "" + ChatColor.BOLD
												+ "▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");

										GeneralUtilities.playServerSound(Sound.BLOCK_ANVIL_HIT, 1, 1.5);

										break;

									case 20:

										Bukkit.broadcastMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "[UPDATER]: "
												+ ChatColor.RESET + ChatColor.WHITE + "Performing update in "
												+ ChatColor.GOLD + i + " seconds. " + ChatColor.RED
												+ "All online players will be kicked!");

										GeneralUtilities.playServerSound(Sound.BLOCK_COMPARATOR_CLICK, 1, 1.5);
										break;

									case 10:

										Bukkit.broadcastMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "[UPDATER]: "
												+ ChatColor.RESET + ChatColor.WHITE + "Performing update in "
												+ ChatColor.GOLD + i + " seconds. " + ChatColor.RED
												+ "All online players will be kicked!");
										GeneralUtilities.playServerSound(Sound.BLOCK_COMPARATOR_CLICK, 1, 1.5);
										break;

									case 0:

										Bukkit.broadcastMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "[UPDATER]: "
												+ ChatColor.RESET + ChatColor.WHITE + "Performing update now. "
												+ ChatColor.RED + "Kicking all online players..");
										GeneralUtilities.playServerSound(Sound.ENTITY_GUARDIAN_DEATH_LAND, 1, 1.5);
										break;

									case -2:

										for (Player p : Bukkit.getOnlinePlayers()) {

											p.kickPlayer(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD
													+ ChatColor.STRIKETHROUGH + "-+-|-+-[" + ChatColor.RESET
													+ ChatColor.YELLOW + ChatColor.BOLD + " SERVER PROTECTEION ENGAGED "
													+ ChatColor.DARK_PURPLE + "" + ChatColor.BOLD
													+ ChatColor.STRIKETHROUGH + "]-+-|-+-\n\n"
													+ PlayerManager.getServerPlayer(p).getRank().getPlayerFullName(p)
													+ ChatColor.GREEN + " has reloaded the server!" + ChatColor.WHITE
													+ "\nAs a result, all players have been"
													+ "\nkicked to prevent things from breaking."
													+ ChatColor.DARK_PURPLE + "" + ChatColor.BOLD

													+ ChatColor.STRIKETHROUGH + "\n\n-+-|-+-[" + ChatColor.RESET
													+ ChatColor.RED + " You may rejoin in about 20 seconds "
													+ ChatColor.DARK_PURPLE + "" + ChatColor.BOLD
													+ ChatColor.STRIKETHROUGH + "]-+-|-+-\n");
										}

										Bukkit.getScheduler().cancelTask(task1);

										Bukkit.reload();
										i = 30;
										break;

									}

									--i;

								}
							}, 0, 20);
						} else {

							p.sendMessage(ChatColor.RED
									+ "You cant start another update because there is already on in progress!");

						}
					}
				}
			}

		}

		return true;
	}

	@EventHandler
	public void FrozenPlayerCheck(PlayerMoveEvent e) {

		Player p = e.getPlayer();

		if (frozen_players.get(p) == BlockedMovementType.ALL) {

			e.setCancelled(true);
		} else if (frozen_players.get(p) == BlockedMovementType.MOVEMENT) {

			if (e.getFrom().getPitch() == e.getTo().getPitch() && e.getFrom().getBlockX() != e.getTo().getBlockX()
					|| e.getFrom().getBlockY() != e.getTo().getBlockY()
					|| e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
				for (int i = 0; i < 4; ++i) {
					p.teleport(e.getFrom());
					e.setCancelled(true);
				}

			}
		}

	}

	public static void freezePlayer(Player player, BlockedMovementType type) {

		frozen_players.put(player, type);

	}

	public static void registerSpigotOverrideCommands() {

		Main.plugin.getCommand("gamemode").setExecutor(new ArenaEvents());
		Main.plugin.getCommand("plugins").setExecutor(new ArenaEvents());

		Main.plugin.getCommand("tp").setExecutor(new ArenaEvents());
		Main.plugin.getCommand("reload").setExecutor(new ArenaEvents());
	}

}
