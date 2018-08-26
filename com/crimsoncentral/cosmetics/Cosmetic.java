package com.crimsoncentral.cosmetics;

import org.bukkit.entity.Player;

public abstract class Cosmetic {

	public static enum CosmeticType {

		PET, PARTICLE, HAT, BANNER, SUIT, GADGET, MORPH, CLOAK, EMOTE, ARROW_TRAIL, CAGE, VICTORY_DANCE, KILL_EFFECT, DEATH_CRY, GRAPHITE_SPRAY, STRUCTURE, GLYPH, ANIMATED_HAT, KILL_NOTE, LAST_WORDS
	}

	public static enum ArenaType {

		LOBBY, GAME, OTHER
	}

	
	public abstract void perform(Player p);
	
	public Cosmetic(String name, String id) {

	}

}
