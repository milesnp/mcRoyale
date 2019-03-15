package com.mileses.mcroyale;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitScheduler;
import de.myzelyam.api.vanish.VanishAPI;

public final class McRoyaleDeathListener implements Listener{
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		//harvest info 
		//report for debug
		Bukkit.getLogger().info("death occurs");
		 Player player = e.getEntity();
		 final Location location = player.getLocation();
		 final World world = player.getWorld();
		//check if killed by a player
		if ((player.getKiller() instanceof Player)) {
			Bukkit.getLogger().info("death by player");
			Player killer = player.getKiller();
			if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) {
				VanishAPI.hidePlayer(player);
				}
			new McRoyaleDeathRunnable(world,location).runTaskLater(McRoyale.getInst(), 300L);
			Bukkit.broadcastMessage(killer.getName()+" killed "+player.getName());
		}
		else Bukkit.getLogger().info("not by player");

	}
		

	}

