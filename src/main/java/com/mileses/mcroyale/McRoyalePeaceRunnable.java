package com.mileses.mcroyale;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class McRoyalePeaceRunnable extends BukkitRunnable {

	public McRoyalePeaceRunnable() {

	}

	@Override
	public void run() {
		McRoyale.peaceTime = false;
		Bukkit.broadcastMessage("Peacetime has ended.");
	}
}
