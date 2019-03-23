package com.mileses.mcroyale;


import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import de.myzelyam.api.vanish.VanishAPI;

public final class McRoyaleRound {
	
	public static void startRound(Location location, int length, HashMap<String, Boolean> list, Player sender, int peaceTimeArg) {

		String playerString = "";
		for (Player p : Bukkit.getOnlinePlayers()) {
			sender.sendMessage(p.getName() + list.get(p.getName()));
			//reset player to full, healed, clear and visible.
			VanishAPI.showPlayer(p);
			p.setGameMode(GameMode.SURVIVAL);
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(20);;
			clearInv(p);
			playerString = playerString.concat(" " + p.getName());
		}
		McRoyale.getLogr().info("start round with players: " + playerString);
		McRoyale.roundActive = true;
		//calculate min distance between players
		int distance = length / (list.size() + 1);
		if (distance >= length/2) 
			{
			distance = length/2 - 1;
			}

		//run command /spreadplayers x z distance teams player player player player player player player etc.
		McRoyale.getLogr().info("running spreadplayers..");
		String commandString ="spreadplayers " + Integer.toString(location.getBlockX()) + " " + Integer.toString(location.getBlockZ()) + " " + Integer.toString(distance) + " " + Integer.toString((length/2) -1) + " false" + playerString;
		McRoyale.getLogr().info(commandString);
	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandString);
	McRoyale.getLogr().info("players spread.");
	//set bed spawns
	for (Player p : Bukkit.getOnlinePlayers()) {
		// set respawn location TODO make this work. probably just make listener for respawn event and tp to this point using hashmap
		p.setBedSpawnLocation(p.getLocation(),true);
	}
	if (peaceTimeArg > 0){
		McRoyale.peaceTime = true;
	    Bukkit.broadcastMessage("Peace Time is active.");
	     new McRoyalePeaceRunnable().runTaskLater(McRoyale.getInst(), peaceTimeArg*1200);
	}

	}
	public static void clearInv(Player player){
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		}
}
