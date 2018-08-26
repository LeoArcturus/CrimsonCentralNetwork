package com.crimsoncentral.games.sky_wars.cosmetics.cages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;

public class CageLayer {

	public HashMap<Integer, Material> positions = new HashMap<Integer, Material>();

	public CageLayer() {

	}

	public void buildLayer(Location loc) {

		loc.add(2, 0, -2).getBlock().setType(getPosition(1));
		loc.add(2, 0, -1).getBlock().setType(getPosition(2));
		loc.add(2, 0, 0).getBlock().setType(getPosition(3));
		loc.add(2, 0, 1).getBlock().setType(getPosition(4));
		loc.add(2, 0, -2).getBlock().setType(getPosition(5));

		loc.add(1, 0, -2).getBlock().setType(getPosition(6));
		loc.add(1, 0, -1).getBlock().setType(getPosition(7));
		loc.add(1, 0, 0).getBlock().setType(getPosition(8));
		loc.add(1, 0, 1).getBlock().setType(getPosition(9));
		loc.add(1, 0, -2).getBlock().setType(getPosition(10));

		loc.add(0, 0, -2).getBlock().setType(getPosition(11));
		loc.add(0, 0, -1).getBlock().setType(getPosition(12));
		loc.add(0, 0, 0).getBlock().setType(getPosition(13));
		loc.add(0, 0, 1).getBlock().setType(getPosition(14));
		loc.add(0, 0, -2).getBlock().setType(getPosition(15));

		loc.add(-1, 0, -2).getBlock().setType(getPosition(16));
		loc.add(-1, 0, -1).getBlock().setType(getPosition(17));
		loc.add(-1, 0, 0).getBlock().setType(getPosition(18));
		loc.add(-1, 0, 1).getBlock().setType(getPosition(19));
		loc.add(-1, 0, -2).getBlock().setType(getPosition(20));

		loc.add(-2, 0, -2).getBlock().setType(getPosition(21));
		loc.add(-2, 0, -1).getBlock().setType(getPosition(22));
		loc.add(-2, 0, 0).getBlock().setType(getPosition(23));
		loc.add(-2, 0, 1).getBlock().setType(getPosition(24));
		loc.add(-2, 0, -2).getBlock().setType(getPosition(25));

	}

	public Material getPosition(Integer number) {

		Material block = null;
		Iterator<Entry<Integer, Material>> it = positions.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Material> pair = it.next();

			if (pair.getKey() == number) {
				block = pair.getValue();
			}

		}
		return block;
	}

}
