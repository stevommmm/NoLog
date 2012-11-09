package com.c45y.NoLog;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class NoLogObject {

	public NoLogObject(Player p, Long t, Location l) {
		attacker = p;
		timestamp = t;
		player_loc = l;
	}

	public String getAttackerName() {
		return attacker.getDisplayName();
	}

	public Player getAttacker() {
		return attacker;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public Location getLocation() {
		return player_loc;
	}

	public int getDistance(Player player) {
		return (int) player.getLocation().distance(attacker.getLocation());
	}

	private Player attacker;
	private Long timestamp;
	private Location player_loc;
}
