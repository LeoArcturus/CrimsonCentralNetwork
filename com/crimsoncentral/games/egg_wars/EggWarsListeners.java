package com.crimsoncentral.games.egg_wars;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.crimsoncentral.Main;
import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.Team;
import com.crimsoncentral.arena.util.Hologram;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.util.item.ItemUtil;

import net.md_5.bungee.api.ChatColor;

public class EggWarsListeners implements Listener {

	private static HashMap<Team, Location> eggs = new HashMap<Team, Location>();
	private static HashMap<Location, Team> egg_keys = new HashMap<Location, Team>();

	private static HashMap<Entity, ItemStack> bridge_eggs = new HashMap<Entity, ItemStack>();
	private static HashMap<Entity, Integer> bridge_eggs_distances = new HashMap<Entity, Integer>();

	private static HashMap<Entity, com.crimsoncentral.arena.Team> silverfish_snowballs = new HashMap<Entity, com.crimsoncentral.arena.Team>();

	private static boolean projectiles_running = false;

	@EventHandler
	public void EggPunchEvent(PlayerInteractEvent e) {

		Player p = e.getPlayer();

		Arena a = PlayerManager.getServerPlayer(p).getArena();
		Team t = a.getPlayerTeam(p);

		if (Integer.valueOf(new DecimalFormat("0").format(a.getGamemode())) == 6) {

			if (e.getItem() != null && e.getItem().getType() == Material.FIREBALL) {

				p.getInventory().setItem(p.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));

				Fireball fb = (Fireball) p.getWorld().spawnEntity(p.getLocation().add(p.getLocation().getDirection()),
						EntityType.FIREBALL);
				fb.setVelocity(fb.getVelocity().multiply(3));
				fb.setCustomNameVisible(true);
				fb.setCustomName(ChatColor.RED + "" + ChatColor.BOLD + "(!)" + ChatColor.RESET + ChatColor.YELLOW
						+ " DANGEROUS FIREBALL " + ChatColor.RED + "" + ChatColor.BOLD + "(!)");

			}

			if (e.getClickedBlock() != null) {
				Location l = eggs.get(egg_keys.get(e.getClickedBlock().getLocation()));

				if (l != null) {

					if (t != egg_keys.get(l)) {
						e.setCancelled(true);
						l.getBlock().breakNaturally();
						a.playWorldSound(Sound.ENTITY_ENDERDRAGON_GROWL, 1, 1);
						a.sendWorldMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "Egg Destruction>> "
								+ ChatColor.RESET + egg_keys.get(l).getPrefix().replace("[", "").replace("]", "")
								+ "'s " + ChatColor.WHITE + "Egg was destroyed by " + t.team_color + p.getName());
						egg_keys.get(l).can_respawn = false;
						for (Player player : egg_keys.get(l).getPlayers()) {

							a.sendTitle(player, ChatColor.DARK_RED + "YOUR EGG HAS BEEN DESTROYED!", 1, 2, 1);
							a.sendSubTitle(player, ChatColor.GRAY + "You will no longer respawn...", 1, 2, 1);

						}

					} else {

						p.sendMessage(ChatColor.RED + "You cant break your own egg!");
						l.getBlock().setType(Material.DRAGON_EGG);
					}
				}
			}
		}

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent e) {

		Player p = e.getPlayer();

		Arena a = PlayerManager.getServerPlayer(p).getArena();

		if (Integer.valueOf(new DecimalFormat("0").format(a.getGamemode())) == 6) {

			if (e.getBlock().getType() == Material.TNT) {
				e.getBlock().setType(Material.AIR);
				e.getBlock().getWorld().spawnEntity(e.getBlock().getLocation(), EntityType.PRIMED_TNT);

			} else if (e.getBlock().getType() == Material.WATER_BUCKET) {

				p.getItemInHand().setType(Material.AIR);

			} else {
				if (Integer.valueOf(new DecimalFormat("0").format(a.getGamemode())) == 6) {

					a.place_blocks.put(e.getBlock(), p);
				}

			}
		}
	}

	@EventHandler
	public void BlockBreakEvent(org.bukkit.event.block.BlockBreakEvent e) {

		Player p = e.getPlayer();

		Arena a = PlayerManager.getServerPlayer(p).getArena();

		if (Integer.valueOf(new DecimalFormat("0").format(a.getGamemode())) == 6) {

			if (!a.place_blocks.containsKey(e.getBlock())) {

				e.setCancelled(true);
				p.sendMessage(ChatColor.RED + "You can only break blocks placed by a player!");
			}

		}
	}

	@EventHandler
	public void ArmorCheckEvent(InventoryClickEvent e) {

		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();

			Arena a = PlayerManager.getServerPlayer(p).getArena();

			if (Integer.valueOf(new DecimalFormat("0").format(a.getGamemode())) == 6) {
				if (e.getSlotType() == SlotType.ARMOR) {

					e.setCancelled(true);
					p.sendMessage(ChatColor.RED + "You can't touch your armor peices!");

				}
			}
		}

	}

	@EventHandler
	public void TnTExplode(EntityExplodeEvent event) {
		Entity e = event.getEntity();

		Player p = null;
		for (Player p2 : e.getWorld().getPlayers()) {

			p = p2;
			break;

		}
		if (p != null) {
			Arena a = PlayerManager.getServerPlayer(p).getArena();

			if (Integer.valueOf(new DecimalFormat("0").format(a.getGamemode())) == 6) {

				if (e.getType() == EntityType.PRIMED_TNT) {
					Iterator<Block> iter = event.blockList().iterator();
					while (iter.hasNext()) {
						Block b = iter.next();
						if (!a.place_blocks.containsKey(b) || b.getType() == Material.STAINED_GLASS
								|| b.getType() == Material.GLASS) {
							iter.remove();
						}
					}
				}

			}
		}

	}

	@EventHandler
	public void BridgeEggSliverFishEvent(ProjectileLaunchEvent event) {
		Projectile e = (Projectile) event.getEntity();

		if (e.getShooter() instanceof Player) {
			Player p = (Player) e.getShooter();

			Arena a = PlayerManager.getServerPlayer(p).getArena();

			if (Integer.valueOf(new DecimalFormat("0").format(a.getGamemode())) == 6) {

				if (e.getType() == EntityType.EGG) {

					if (getTeamWools(a.getPlayerTeam(p).team_color) == null) {
						bridge_eggs.put(e, ItemUtil.newItem(Material.WOOL, 1, (short) 0, "", ""));
						bridge_eggs_distances.put(e, 0);

						e.setInvulnerable(true);

					} else {

						bridge_eggs.put(e, getTeamWools(a.getPlayerTeam(p).team_color));
						bridge_eggs_distances.put(e, 0);

						e.setInvulnerable(true);

					}
					run();
				} else if (e.getType() == EntityType.SNOWBALL) {
					silverfish_snowballs.put(e, a.getPlayerTeam(p));
					run();
				}

			}
		}

	}

	public ItemStack getTeamWools(org.bukkit.ChatColor team_color) {
		HashMap<org.bukkit.ChatColor, ItemStack> team_wools = new HashMap<org.bukkit.ChatColor, ItemStack>();

		team_wools.put(org.bukkit.ChatColor.RED, new ItemStack(Material.WOOL, 1, (short) 14));
		team_wools.put(org.bukkit.ChatColor.DARK_AQUA, new ItemStack(Material.WOOL, 1, (short) 9));
		team_wools.put(org.bukkit.ChatColor.YELLOW, new ItemStack(Material.WOOL, 1, (short) 4));
		team_wools.put(org.bukkit.ChatColor.GREEN, new ItemStack(Material.WOOL, 1, (short) 5));
		team_wools.put(org.bukkit.ChatColor.DARK_PURPLE, new ItemStack(Material.WOOL, 1, (short) 10));
		team_wools.put(org.bukkit.ChatColor.GOLD, new ItemStack(Material.WOOL, 1, (short) 1));
		team_wools.put(org.bukkit.ChatColor.GRAY, new ItemStack(Material.WOOL, 1, (short) 15));
		team_wools.put(org.bukkit.ChatColor.WHITE, new ItemStack(Material.WOOL, 1, (short) 0));

		return team_wools.get(team_color);

	}

	int task;

	public void run() {

		if (projectiles_running != true) {

			task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {

				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					projectiles_running = true;
					for (Entry<Entity, ItemStack> e : bridge_eggs.entrySet()) {

						if (bridge_eggs_distances.get(e.getKey()) <= 50) {

							Projectile pt = (Projectile) e.getKey();
							Player p = (Player) pt.getShooter();

							Arena a = PlayerManager.getServerPlayer(p).getArena();

							bridge_eggs_distances.put(e.getKey(), bridge_eggs_distances.get(e.getKey()) + 1);

							a.replaceBlockRadius(e.getKey().getLocation().subtract(0, 3, 0),
									bridge_eggs.get(e.getKey()).getType(), e.getValue().getData().getData(), 1, 0, 1);

							for (Block b : a.getBlocksInRadius(e.getKey().getLocation().subtract(0, 3, 0), 1, 0, 1)) {
								a.place_blocks.put(b, p);

							}

						} else {
							e.getKey().setInvulnerable(false);
							bridge_eggs.remove(e.getKey(), bridge_eggs.get(e.getKey()));
							bridge_eggs_distances.remove(e.getKey(), bridge_eggs_distances.get(e.getKey()));

						}
					}

					for (Entry<Entity, Team> e : silverfish_snowballs.entrySet()) {

						if (e.getKey().isDead() || e.getKey().isOnGround()) {
							Location l = (Location) e.getKey().getLocation().add(0, e.getKey().getLocation().getY() + 4,
									0);

							Endermite em = (Endermite) e.getKey().getWorld().spawnEntity(l, EntityType.ENDERMITE);
							em.setCustomNameVisible(true);
							em.setCustomName(e.getValue().getPrefix().replace("[", "").replace("]", "") + "'S "
									+ ChatColor.DARK_PURPLE + " Ender Minion");

							silverfish_snowballs.remove(e.getKey(), silverfish_snowballs.get(e.getKey()));
							for (Entity e1 : e.getKey().getWorld().getEntities()) {

								if (e1.getLocation() == e.getKey().getLocation()) {

									break;
								}
							}

						}
					}

					if (silverfish_snowballs.size() == 0 && bridge_eggs.size() == 0) {

						projectiles_running = false;
						Bukkit.getScheduler().cancelTask(task);

					}

				}

			}, 0, (long) 0.1);
		}
	}

	public static void createNewEgg(Location l, Team t) {
		eggs.put(t, l);
		egg_keys.put(l, t);
		l.getBlock().setType(Material.DRAGON_EGG);
		@SuppressWarnings("unused")
		Hologram h = new Hologram(t.getPrefix().replace("[", "").replace("]", "") + "'s " + ChatColor.WHITE + "Egg")
				.spawn(l.clone().subtract(0, 1, 0));

	}

}
