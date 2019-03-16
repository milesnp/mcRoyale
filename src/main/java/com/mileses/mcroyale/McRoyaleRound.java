package com.mileses.mcroyale;


import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import de.myzelyam.api.vanish.VanishAPI;

public final class McRoyaleRound {
	
	public static void startRound(Location location, int length, HashMap<String, Boolean> list, Player sender, int peacetime) {

		String playerString = "";
		for (Player p : Bukkit.getOnlinePlayers()) {
			sender.sendMessage(p.getName() + list.get(p.getName()));
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
		if (distance < 49) distance = 49;
		//int distance = 40;
		//run command /spreadplayers x z distance teams player player player player player player player etc.
		McRoyale.getLogr().info("running spreadplayers..");
		String commandString ="spreadplayers " + Integer.toString(location.getBlockX()) + " " + Integer.toString(location.getBlockZ()) + " " + Integer.toString(distance) + " " + Integer.toString((length/2) -1) + " false" + playerString;
		Bukkit.broadcastMessage(commandString);
	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandString);
	McRoyale.getLogr().info("players spread.");
	//set bed spawns
	for (Player p : Bukkit.getOnlinePlayers()) {
		// set respawn location TODO make this work. probably just make listener for respawn event and tp to this point using hashmap
		p.setBedSpawnLocation(p.getLocation(),true);
	}
	}

}
