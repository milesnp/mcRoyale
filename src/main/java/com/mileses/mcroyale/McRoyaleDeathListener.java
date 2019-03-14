package com.mileses.mcroyale;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class McRoyaleDeathListener implements Listener{
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		//harvest info 
		Bukkit.getLogger().info("death occurs");
		Player player = e.getEntity();
		final Location location = player.getLocation();

		final World world = player.getWorld();
		if ((player.getKiller() instanceof Player)) {
			Bukkit.getLogger().info("death by player");
			Player killer = player.getKiller();
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(McRoyale.getInst(), new Runnable() {
				@Override
				public void run() {
					location.setY(100);
					world.playSound(location, Sound.EXPLODE, 220, 1);
				}
			}, 20L);
			Bukkit.broadcastMessage(killer.getName()+" killed "+player.getName());
		}
		else Bukkit.getLogger().info("not by player");

	}
		

	}

