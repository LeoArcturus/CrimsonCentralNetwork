package com.crimsoncentral.arena.util.parkour_course;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.NavigableMap;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.util.ArenaUtil;
import com.crimsoncentral.server_player.PlayerManager;
import com.crimsoncentral.server_player.ServerPlayer;
import com.crimsoncentral.util.CrimsonMap;
import com.crimsoncentral.util.mysql.Sql;
import com.crimsoncentral.util.other.CoolDown;

public class ParkourCourseManager implements Listener {
	public static HashMap<Arena, ParkourCourse> courses = new HashMap<Arena, ParkourCourse>();

	@EventHandler
	public void PointListener(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		Location l = p.getLocation();
		ServerPlayer sp = PlayerManager.getServerPlayer(p);
		Arena a = sp.getArena();
		ParkourCourse pc = null;
		ParkourCourseRun run = null;

		for (Entry<Arena, ParkourCourse> e : courses.entrySet()) {

			if (e.getKey() == a) {
				pc = e.getValue();
				run = pc.runs.get(p);

				if (p.getLocation().getBlockY() <= 5) {

					NavigableMap<Integer, Location> points = run.passedCheckPoints;

					Entry<Integer, Location> ent = points.lastEntry();

					if (ent != null && ent.getValue() != null) {
						p.teleport(ent.getValue());
						p.sendMessage(ChatColor.YELLOW + "You fell to far! Your welcome for the free teleport :p");

					} else {

						p.teleport(pc.getStart());

					}
					return;
				}

				if (run != null && p.isFlying()) {
					p.sendMessage(ChatColor.RED + "Parkour failed! You may not fly!");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2, 2);
					run.stopTimming();
					pc.runs.remove(p, run);
					new CoolDown(a.getArenaName() + " parkour checkpoint message cool down", p, 10, 20);
					for (PotionEffect pe : p.getActivePotionEffects()) {

						p.removePotionEffect(pe.getType());
					}
					return;
				}

				if (pc.getStart().distance(l) <= 0.5) {
					if (run == null) {
						p.sendMessage(ChatColor.GREEN + "Go! Your time has started!");
						p.playSound(l, Sound.BLOCK_NOTE_CHIME, 2, 2);

						pc.runs.put(p, new ParkourCourseRun(pc, p));
						run = pc.runs.get(p);
						run.setStarted(true);
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, 2), true);
						p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100000, 1), true);

					}
				} else if (pc.getEnd().distance(l) <= 0.5) {

					if (run == null) {

						if (!CoolDown.coolDownExists(a.getArenaName() + " parkour checkpoint message cool down")) {
							p.sendMessage(ChatColor.RED + "Go the begining of the parkour to start!");

							new CoolDown(a.getArenaName() + " parkour checkpoint message cool down", p, 10, 20);
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2, 2);
						}
					} else if (run.passedCheckPoints.size() == pc.getCheckPoints().size()) {
						p.sendMessage(ChatColor.GREEN + "You finished with a time of " + ChatColor.GOLD
								+ run.getDisplayTime() + ChatColor.GREEN + " seconds!");
						p.playSound(l, Sound.ENTITY_PLAYER_LEVELUP, 2, 2);
						run.stopTimming();
						pc.runs.remove(p, run);
						playOutEnd(run);
						new CoolDown(a.getArenaName() + " parkour checkpoint message cool down", p, 10, 20);

						for (PotionEffect pe : p.getActivePotionEffects()) {

							p.removePotionEffect(pe.getType());
						}

					} else {
						p.sendMessage(ChatColor.RED + "You must pass all points before finishing the course!");
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2, 2);
					}
				} else {

					for (Entry<Integer, Location> e1 : pc.getCheckPoints().entrySet()) {
						if (e1.getValue().distance(l) <= 0.5) {

							if (run == null) {
								if (!CoolDown
										.coolDownExists(a.getArenaName() + " parkour checkpoint message cool down")) {
									p.sendMessage(ChatColor.RED + "Go the begining of the parkour to start!");
									p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2, 2);
									new CoolDown(a.getArenaName() + " parkour checkpoint message cool down", p, 10, 20);
								}
							} else if (run.passedCheckPoints.size() == 0) {

								if (e1.getKey() == 1) {
									pointPass(run, e1.getKey(), e1.getValue());
								} else {
									p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2, 2);
									p.sendMessage(ChatColor.RED
											+ "You must start first before you can pass this checkpoint!");
								}

							} else {

								if (run.passedCheckPoints.get(1) == e1.getValue()) {
									pointPass(run, e1.getKey(), e1.getValue());
								} else if (run.passedCheckPoints.get(e1.getKey() - 1) != null) {

									pointPass(run, e1.getKey(), e1.getValue());
								} else {
									p.sendMessage(ChatColor.RED + "You must pass checkpoint " + (e1.getKey() - 1)
											+ " fist before passing checkpoint " + e1.getKey() + "!");
									p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2, 2);
								}

							}

						}
					}
				}

				break;
			}
		}

	}

	private void playOutEnd(ParkourCourseRun r) {

		Player p = r.getPlayer();

		Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation().add(0, 3, 0), EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();

		fwm.setPower(5);
		fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

		fw.setFireworkMeta(fwm);
		fw.detonate();

		Firework fw1 = (Firework) p.getWorld().spawnEntity(p.getLocation().add(0, 3, 0), EntityType.FIREWORK);
		FireworkMeta fwm1 = fw1.getFireworkMeta();

		fwm1.setPower(05);
		fwm1.addEffect(FireworkEffect.builder().withColor(Color.YELLOW).flicker(true).build());

		fw1.setFireworkMeta(fwm1);
		fw1.detonate();

		Firework fw11 = (Firework) p.getWorld().spawnEntity(p.getLocation().add(0, 3, 0), EntityType.FIREWORK);
		FireworkMeta fwm11 = fw11.getFireworkMeta();

		fwm11.setPower(5);
		fwm11.addEffect(FireworkEffect.builder().withColor(Color.PURPLE).flicker(true).build());

		fw11.setFireworkMeta(fwm11);
		fw11.detonate();

		ArenaUtil.sendTitle(p, ChatColor.GREEN + "You finished with a time of", 1, 5, 1);
		ArenaUtil.sendSubTitle(p, ChatColor.GOLD + r.getDisplayTime() + ChatColor.GRAY + " seconds...", 1, 5, 1);

	}

	public void pointPass(ParkourCourseRun run, Integer point, Location loc) {
		Sql.establishConnection();
		Player p = run.getPlayer();
		if (!run.passedCheckPoints.containsKey(point)) {
			p.sendMessage(ChatColor.GREEN + "You made it to " + ChatColor.GOLD + "Check Point #" + point
					+ ChatColor.GREEN + " with a time of " + ChatColor.GRAY + run.getDisplayTime() + " seconds"
					+ ChatColor.GREEN + ". Keep going to the end!");
			run.passedCheckPoints.put(point, loc);
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_HARP, 2, (float) 1.5);

			if (!Sql.checkTableForRow("parkour_courses_data", "player", "run_id",
					"yellow")) {
				CrimsonMap m = new CrimsonMap();
				Date dNow = new Date();
				SimpleDateFormat ft = new SimpleDateFormat("MM-dd-yyyy");

				m.add("player", p.getUniqueId().toString());
				m.add("run_id", p.getUniqueId().toString() + "-" + run.getCourse().getName() + "-" + point);

				m.add("time", run.getDisplayTime());
				m.add("date", ft.format(dNow).toString());

				Sql.createRow("parkour_courses_data", m);

			} else if (run.getTime() < Integer.valueOf(Sql.getString("parkour_courses_data", "time", "run_id",
					p.getUniqueId().toString() + "-" + run.getCourse().getName() + "-" + point))) {

				p.sendMessage(ChatColor.GREEN + "Congrats! You passed this point with a new record of " + ChatColor.GOLD
						+ run.getDisplayTime());
				Sql.setString("parkour_courses_data", "time", "run_id", run.getDisplayTime(),
						p.getUniqueId().toString() + "-" + run.getCourse().getName() + "-" + point);
			} else {

				p.sendMessage(ChatColor.GRAY + "You did not beat your previous record up to this point of "
						+ ChatColor.WHITE + Integer.valueOf(Sql.getString("parkour_courses_data", "time", "run_id",
								p.getUniqueId().toString() + "-" + run.getCourse().getName() + "-" + point)));
			}
		}
		Sql.closeConnection();
	}
}
