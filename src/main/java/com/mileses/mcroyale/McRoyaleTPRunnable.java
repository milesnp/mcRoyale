package com.mileses.mcroyale;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class McRoyaleTPRunnable extends BukkitRunnable {
	public McRoyale pl;
	public McRoyaleTPRunnable(McRoyale plugin) {
		pl = plugin;
	}
	
	@Override
	public void run() {
		for (Player p: Bukkit.getOnlinePlayers()) {
			if (pl.playerList.containsKey(p.getName()) && pl.playerList.get(p.getName())){
				Location location = p.getLocation();
				World world = p.getWorld();
				location.setY(world.getHighestBlockYAt(location));
				p.teleport(location);
			}
		}
		
	}

}
