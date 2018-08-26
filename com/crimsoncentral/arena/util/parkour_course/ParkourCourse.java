package com.crimsoncentral.arena.util.parkour_course;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.crimsoncentral.arena.Arena;
import com.crimsoncentral.arena.util.Hologram;

import net.md_5.bungee.api.ChatColor;

public class ParkourCourse {

	public HashMap<Player, ParkourCourseRun> runs = new HashMap<Player, ParkourCourseRun>();

	private String name;

	private Arena arena;

	private Location start;
	private Location end;

	private HashMap<Integer, Location> checkPoints = new HashMap<Integer, Location>();

	@SuppressWarnings("unused")
	public ParkourCourse(String name, Arena arena, Location start, Location end, Location... checkpoints) {

		this.setName(name);
		this.arena = arena;
		this.start = start;
		this.end = end;

		boolean b = true;
		new Hologram(ChatColor.GREEN + "Parkour Start").spawn(start.clone().add(0, 0.5, 0));
		new Hologram(ChatColor.GREEN + "Parkour End").spawn(end.clone().add(0, 0.5, 0));

		for (Location l : checkpoints) {

			if (l.getWorld() == arena.getWorld()) {

				checkPoints.put(checkPoints.size() + 1, l);

				new Hologram(ChatColor.GREEN + "Parkour Checkpoint " + ChatColor.GOLD + "#" + checkPoints.size())
						.spawn(l);

			} else {

				b = false;
			}

		}
		if (b == true) {

			ParkourCourseManager.courses.put(getArena(), this);

		}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<Integer, Location> getCheckPoints() {
		return checkPoints;
	}

	public void setCheckPoints(HashMap<Integer, Location> checkPoints) {
		this.checkPoints = checkPoints;
	}

	public Arena getArena() {
		return arena;
	}

	public void setArena(Arena arena) {
		this.arena = arena;
	}

	public Location getStart() {
		return start;
	}

	public void setStart(Location start) {
		this.start = start;
	}

	public Location getEnd() {
		return end;
	}

	public void setEnd(Location end) {
		this.end = end;
	}

}
