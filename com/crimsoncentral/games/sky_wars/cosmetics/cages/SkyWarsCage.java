package com.crimsoncentral.games.sky_wars.cosmetics.cages;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Location;


public class SkyWarsCage {

	public HashMap<Integer, CageLayer> layers = new HashMap<Integer, CageLayer>();

	public SkyWarsCage() {

	}

	int i;

	public void placeLayers(Location loc) {

		Location center = loc;
		Iterator<Entry<Integer, CageLayer>> it = layers.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, CageLayer> pair = it.next();
			++i;
			center.setY(loc.getY() + i);

			pair.getValue().buildLayer(center);
			if (i == 5) {

				break;
			}

		}

	}

}
