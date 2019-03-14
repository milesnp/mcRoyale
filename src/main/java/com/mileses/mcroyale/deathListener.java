package com.mileses.mcroyale;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class deathListener implements Listener{
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		//harvest info 
		Bukkit.getLogger().info("death occurs");
		Player player = e.getEntity();
		Location location = player.getLocation();
		location.setY(160);
		//TEST TODO UNDO TEST PROBLEM
		if (!(player.getKiller() instanceof Player)) {
			Bukkit.getLogger().info("death by player");
			Player killer = player.getKiller();
			World world = killer.getWorld();
			world.playSound(location, Sound.EXPLODE, 220, 1);
			Bukkit.broadcastMessage(killer.getName()+" killed "+player.getName());
		}
		else Bukkit.getLogger().info("not by player");
	}
}
