package com.mileses.mcroyale;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.myzelyam.api.vanish.VanishAPI;

public final class McRoyaleRound {
	
	public void startRound(Location location, int length, List<Player> list, Player sender) {
		
		String playerString = "";
		for (Player p: list) {
			//reset player to full, healed, clear and visible.
			VanishAPI.showPlayer(p);
			p.setGameMode(GameMode.SURVIVAL);
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(15);;
			p.getInventory().clear();
			playerString = playerString.concat(p.getName() + " ");
		}
		int distance = length / (list.size() + 1);	
	Bukkit.dispatchCommand(sender, "spreadplayers " + location.getX()+ " " + location.getZ() + " " + distance + " false " + playerString);	
	}

}
