package com.mileses.mcroyale;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;



public class McRoyaleDeathListener implements Listener {
	McRoyale pl;

	public McRoyaleDeathListener(McRoyale plugin) {
		pl = plugin;
		
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		// harvest info
		
		if (McRoyale.roundActive) {
			
			int activeplayers;
			// get player and location
			Player player = e.getEntity();
			
			final Location location = player.getLocation();
			final World world = player.getWorld();
			// check if killed by a player
			if ((player.getKiller() instanceof Player)) {
				
				Player killer = player.getKiller();
				// congratulate player, set player to inactive
				killer.sendMessage("You killed " + player.getName() + ". Congrats!");
				pl.currentRound.activePlayers.put(player.getUniqueId(), false);
				McRoyale.changeDeaths(player, 1);
				McRoyale.changeKills(killer, 1);

				new McRoyaleDeathRunnable(world, location).runTaskLater(McRoyale.getInst(), 200L);
				activeplayers = 0;
				UUID lastActive = killer.getUniqueId();
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (pl.currentRound.activePlayers.containsKey(p.getUniqueId()) && pl.currentRound.activePlayers.get(p.getUniqueId())) {
						activeplayers++;
						lastActive = player.getUniqueId();
					}
				}
					if (activeplayers == 1) {
						pl.currentRound.endRound(lastActive);
						player.sendMessage("You've been eliminated and the round is over!");
					} else {
						player.sendMessage("You've been eliminated. Entering Spectator mode.");
						//player.setGameMode(GameMode.SPECTATOR);
					}
				}
			 else
				
				player.sendMessage("You were not killed by a player. You are still in play.");

		}
	}

	public void endRound(Player winner) {
		winner.sendMessage("VICTORY ROYALE!");
		Bukkit.broadcastMessage(ChatColor.GOLD + winner.getName() + ChatColor.WHITE + " is the winner!");
		McRoyale.changeWins(winner, 1);
		McRoyale.roundActive = false;
		//Location newLocation;
		for (Player p : Bukkit.getOnlinePlayers()) {
			//if (p.getGameMode() != GameMode.SURVIVAL) {
			//	newLocation = McRoyale.setPlayerDown(p);
			//	p.setGameMode(GameMode.SURVIVAL);
			//} else
			//	newLocation = p.getLocation();
			//p.setBedSpawnLocation(newLocation, false);
			p.setScoreboard(McRoyale.getScoreManager().getNewScoreboard());
		}
	}

}
