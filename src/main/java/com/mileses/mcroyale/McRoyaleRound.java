package com.mileses.mcroyale;


import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import de.myzelyam.api.vanish.VanishAPI;

public final class McRoyaleRound {
	
	public static void startRound(Location location, int length, HashSet<String> list, Player sender, int peacetime) {

		String playerString = "";
		for (String n: list) {
			sender.sendMessage(n);
			Player p = McRoyale.getInst().getServer().getPlayer(n);
			//reset player to full, healed, clear and visible.
			VanishAPI.showPlayer(p);
			McRoyale.getLogr().info("show player " + p.getName());
			p.setGameMode(GameMode.SURVIVAL);
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(15);;
			p.getInventory().clear();
			playerString = playerString.concat(" " + p.getName());
			McRoyale.getLogr().info(playerString);
		}
		//calculate min distance between players
		int distance = length / (list.size() + 1);
		//int distance = 40;
		//run command /spreadplayers x z distance teams player player player player player player player etc.
		McRoyale.getLogr().info("running spreadplayers..");
		String commandString ="spreadplayers " + Integer.toString(location.getBlockX()) + " " + Integer.toString(location.getBlockZ()) + " " + Integer.toString(distance) + " " + Integer.toString((length/2) -1) + " false" + playerString;
		Bukkit.broadcastMessage(commandString);
	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandString);
	McRoyale.getLogr().info("players spread.");
	//set bed spawns
	for (String n: list) {
		// set respawn location
		Player p = McRoyale.getInst().getServer().getPlayer(n);
		p.setBedSpawnLocation(p.getLocation(),true);
	}
	}

}
