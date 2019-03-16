package com.mileses.mcroyale;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import de.myzelyam.api.vanish.VanishAPI;

public class McRoyaleDeathListener implements Listener{
	McRoyale pl;
	public McRoyaleDeathListener(McRoyale plugin) {
		pl = plugin;
	}
	
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		//harvest info 
		//report for debug
		Bukkit.getLogger().info("death occurs");
		//get player and location
		 Player player = e.getEntity();
		 final Location location = player.getLocation();
		 final World world = player.getWorld();
		//check if killed by a player
		if ((player.getKiller() instanceof Player)) {
			Player killer = player.getKiller();
			Bukkit.getLogger().info(player.getName()+" died by player " + killer.getName());
			//check for SuperVanish installed, vanish if present
			if (Bukkit.getPluginManager().isPluginEnabled("SuperVanish") || Bukkit.getPluginManager().isPluginEnabled("PremiumVanish")) {
				VanishAPI.hidePlayer(player);
				// set respawn location
				player.sendMessage("You've been eliminated. Entering Spectator mode.");
				}
			else player.sendMessage("You've been eliminated. Please avoid interfering with play. Sorry you're far away now.");
			killer.sendMessage("You killed " + player.getName() + ". Congrats!");
			pl.playerlist.put(player.getName(),false);
			new McRoyaleDeathRunnable(world,location).runTaskLater(McRoyale.getInst(), 200L);
			int activeplayers = 0;
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (pl.playerlist.get(p.getName()))
					activeplayers++;
			}
			if (activeplayers == 1) {
				killer.sendMessage("VICTORY ROYALE!");
			}
		}
		else {
			Bukkit.getLogger().info(player.getName()+" killed not by player");
			player.sendMessage("You were not killed by a player. You are still in play.");
		}
	}
		

	}

