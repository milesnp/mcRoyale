package com.mileses.mcroyale;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class deathListener implements Listener{
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Bukkit.getLogger().info("death occurs");
		Player player = e.getEntity();
		if (player.getKiller() instanceof Player) {
			Bukkit.getLogger().info("death by player");
			Player killer = player.getKiller();
			World world = killer.getWorld();
			world.playSound(killer.getLocation(), Sound.EXPLODE, 200, 1);
			Bukkit.broadcastMessage(killer.getName()+" killed "+player.getName());
		}
		else Bukkit.getLogger().info("not by player");
	}
}
