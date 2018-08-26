package com.crimsoncentral.arena.util.parkour_course;

import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ParkourCourseRun {

	private ParkourCourse course;

	public TreeMap<Integer, Location> passedCheckPoints = new TreeMap<Integer, Location>();

	private Player player;
	private int time;

	private String displayTime;

	private boolean started = false;

	public ParkourCourseRun(ParkourCourse course, Player player) {

		this.course = course;
		this.player = player;

		timer.start();

		course.runs.put(getPlayer(), this);

	}

	public Thread timer = new Thread(new Runnable() {
		@SuppressWarnings("all")
		@Override
		public void run() {

			for (int i = 0; i == i; ++i) {

				try {
					timer.sleep(1);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}

				time = time + 1;
			}
		}

	});

	@SuppressWarnings("deprecation")
	public void stopTimming() {

		timer.stop();

		setDisplayTime();

	}

	public ParkourCourse getCourse() {
		return course;
	}

	public void setCourse(ParkourCourse course) {
		this.course = course;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getDisplayTime() {
		setDisplayTime();
		return displayTime;
	}

	private void setDisplayTime() {
		this.displayTime = "" + (time / 1000) + "." + (time % 1000);
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

}
