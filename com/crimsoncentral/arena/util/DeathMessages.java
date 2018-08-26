package com.crimsoncentral.arena.util;

import java.text.DecimalFormat;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.DamageTag;
import com.crimsoncentral.server_player.PlayerManager;

import net.md_5.bungee.api.ChatColor;

public class DeathMessages {

	public static void broadcastDeathMessage(Player damaged, DamageCause cause) {

		if (cause == DamageCause.FALL) {

			fallDeathMessage(damaged);

		} else if (cause == DamageCause.FIRE || cause == DamageCause.DRAGON_BREATH || cause == DamageCause.FIRE_TICK
				|| cause == DamageCause.HOT_FLOOR || cause == DamageCause.LAVA || cause == DamageCause.MELTING) {

			fireDeathMessage(damaged);

		} else if (cause == DamageCause.CONTACT || cause == DamageCause.CRAMMING
				|| cause == DamageCause.FLY_INTO_WALL) {

			smashedDeathMessage(damaged);
		} else if (cause == DamageCause.WITHER || cause == DamageCause.POISON || cause == DamageCause.MAGIC) {
			posionDeathMessage(damaged);
		} else if (cause == DamageCause.PROJECTILE) {

			shotDeathMessage(damaged);
		} else if (cause == DamageCause.DROWNING) {

			drownDeathMessage(damaged);
		} else if (cause == DamageCause.SUFFOCATION) {

			sufficateDeathMessage(damaged);
		} else if (cause == DamageCause.VOID) {

			voidDeathMessage(damaged);

		} else if (cause == DamageCause.DROWNING) {

			drownDeathMessage(damaged);
		} else if (cause == DamageCause.STARVATION) {

			starveDeathMessage(damaged);
		} else if (cause == DamageCause.BLOCK_EXPLOSION || cause == DamageCause.ENTITY_EXPLOSION) {
			blownUpDeathMessage(damaged);
		} else {

			normalDeathMessage(damaged);
		}

	}

	public static void fallDeathMessage(Player p) {

		Arena arena = ArenaUtil.getPlayerArena(p);

		if (arena != null) {

			if (DamageTag.hasDamageTag(p) == true) {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " was knocked off a cliff by "
								+ PlayerManager.getServerPlayer(DamageTag.getDamageTag(p).damager).getRank().getRankColor()
								+ DamageTag.getDamageTag(p).damager.getName());

			} else {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " jumped off a cliff");

			}
		}
	}

	public static void fireDeathMessage(Player p) {

		Arena arena = ArenaUtil.getPlayerArena(p);

		if (arena != null) {

			if (DamageTag.hasDamageTag(p) == true) {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " was toasted "
								+ PlayerManager.getServerPlayer(DamageTag.getDamageTag(p).damager).getRank().getRankColor()
								+ DamageTag.getDamageTag(p).damager.getName());

			} else {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " melted");
			}
		}
	}

	public static void smashedDeathMessage(Player p) {

		Arena arena = ArenaUtil.getPlayerArena(p);

		if (arena != null) {

			if (DamageTag.hasDamageTag(p) == true) {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " was squished by "
								+ PlayerManager.getServerPlayer(DamageTag.getDamageTag(p).damager).getRank().getRankColor()
								+ DamageTag.getDamageTag(p).damager.getName());

			} else {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " got squished");
			}
		}
	}

	public static void posionDeathMessage(Player p) {

		Arena arena = ArenaUtil.getPlayerArena(p);

		if (arena != null) {

			if (DamageTag.hasDamageTag(p) == true) {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " was posioned by "
								+ PlayerManager.getServerPlayer(DamageTag.getDamageTag(p).damager).getRank().getRankColor()
								+ DamageTag.getDamageTag(p).damager.getName());

			} else {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " got poisioned");
			}
		}
	}

	public static void shotDeathMessage(Player p) {

		Arena arena = ArenaUtil.getPlayerArena(p);

		if (arena != null) {

			if (DamageTag.hasDamageTag(p) == true) {

				Player d = (Player) DamageTag.getDamageTag(p);

				DecimalFormat f = new DecimalFormat("#.##");

				double x_d = Math.abs(p.getLocation().getX() - d.getLocation().getX())
						* Math.abs(p.getLocation().getX() - d.getLocation().getX());

				double y_d = Math.abs(p.getLocation().getY() - d.getLocation().getY())
						* Math.abs(p.getLocation().getY() - d.getLocation().getY());

				double z_d = Math.abs(p.getLocation().getZ() - d.getLocation().getZ())
						* Math.abs(p.getLocation().getZ() - d.getLocation().getZ());

				double f_d = Math.pow(x_d + y_d + z_d, 0.5);

				if (f_d > 17) {

					ArenaUtil.sendWorldMessage(arena,
							PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " shot by "
									+ PlayerManager.getServerPlayer(DamageTag.getDamageTag(p).damager).getRank().getRankColor()
									+ DamageTag.getDamageTag(p).damager.getName() + ChatColor.RED + " from "
									+ ChatColor.GOLD + f.format(f_d) + " blocks" + ChatColor.RED + "away!");

				} else {

					ArenaUtil.sendWorldMessage(arena,
							PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " shot by "
									+ PlayerManager.getServerPlayer(DamageTag.getDamageTag(p).damager).getRank().getRankColor()
									+ DamageTag.getDamageTag(p).damager.getName());
				}

			} else {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " was some how shot? IDK...");
			}
		}
	}

	public static void drownDeathMessage(Player p) {

		Arena arena = ArenaUtil.getPlayerArena(p);

		if (arena != null) {

			if (DamageTag.hasDamageTag(p) == true) {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " was drownd by "
								+ PlayerManager.getServerPlayer(DamageTag.getDamageTag(p).damager).getRank().getRankColor()
								+ DamageTag.getDamageTag(p).damager.getName());

			} else {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " drownd");
			}
		}
	}

	public static void sufficateDeathMessage(Player p) {

		Arena arena = ArenaUtil.getPlayerArena(p);

		if (arena != null) {

			if (DamageTag.hasDamageTag(p) == true) {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED
								+ " sufficated in a wall because of "
								+ PlayerManager.getServerPlayer(DamageTag.getDamageTag(p).damager).getRank().getRankColor()
								+ DamageTag.getDamageTag(p).damager.getName());

			} else {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " sufficated in a wall");
			}
		}
	}

	public static void starveDeathMessage(Player p) {

		Arena arena = ArenaUtil.getPlayerArena(p);

		if (arena != null) {

			if (DamageTag.hasDamageTag(p) == true) {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " starved mid fighting "
								+ PlayerManager.getServerPlayer(DamageTag.getDamageTag(p).damager).getRank().getRankColor()
								+ DamageTag.getDamageTag(p).damager.getName());

			} else {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " starved to death");
			}
		}
	}

	public static void voidDeathMessage(Player p) {

		Arena arena = ArenaUtil.getPlayerArena(p);

		if (arena != null) {

			if (DamageTag.hasDamageTag(p) == true) {

				ArenaUtil.sendWorldMessage(arena, PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED
						+ " was knocked into the void by " + PlayerManager.getServerPlayer(DamageTag.getDamageTag(p).damager).getRank().getRankColor()
						+ DamageTag.getDamageTag(p).damager.getName());

			} else {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " fell into the void");
			}
		}
	}

	public static void blownUpDeathMessage(Player p) {

		Arena arena = ArenaUtil.getPlayerArena(p);

		if (arena != null) {

			if (DamageTag.hasDamageTag(p) == true) {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " was blown up by "
								+ PlayerManager.getServerPlayer(DamageTag.getDamageTag(p).damager).getRank().getRankColor()
								+ DamageTag.getDamageTag(p).damager.getName());

			} else {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " went out with a bang!");
			}
		}
	}

	public static void normalDeathMessage(Player p) {

		Arena arena = ArenaUtil.getPlayerArena(p);

		if (arena != null) {

			if (DamageTag.hasDamageTag(p) == true) {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " was killed by "
								+ PlayerManager.getServerPlayer(DamageTag.getDamageTag(p).damager).getRank().getRankColor()
								+ DamageTag.getDamageTag(p).damager.getName());

			} else {

				ArenaUtil.sendWorldMessage(arena,
						PlayerManager.getServerPlayer(p).getRank().getRankColor() + p.getName() + ChatColor.RED + " died");
			}
		}
	}

}
