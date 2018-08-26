package com.crimsoncentral.games.sky_wars;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class ThrowableItem {

	private ItemStack is;
	private double speed;
	private double distance;
	ArmorStand a = null;

	public ThrowableItem(ItemStack is, double speed, double distance) {
		this.is = is;
		this.speed = speed;
		this.distance = distance;
		
		
	}

	public ItemStack getIs() {
		return is;
	}

	public void setIs(ItemStack is) {
		this.is = is;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	
	public void start(Player player) {
		
		Location l = player.getLocation();
		
		a = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);

		a.setArms(true);
		a.setRightArmPose(new EulerAngle(90, 0, 0));
		a.setItemInHand(is);

		a.setVisible(false);
	

		a.teleport(player.getLocation().add(0, -1, 0));
		
		
		a.setVelocity(l.getDirection().multiply(speed + player.getVelocity().length()).setY(0.65));
	}
	
}
