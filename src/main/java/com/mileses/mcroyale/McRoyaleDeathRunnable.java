package com.mileses.mcroyale;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class McRoyaleDeathRunnable extends BukkitRunnable {
	private World world;
	private Location location;

	public McRoyaleDeathRunnable(World world, Location location) {
		this.world = world;
		this.location = location;
	}

	@Override
	public void run() {
		location.setY(100);
		world.playSound(location, Sound.EXPLODE, 220, 1);
		Bukkit.broadcastMessage("A tribute has fallen.");
	}
}
