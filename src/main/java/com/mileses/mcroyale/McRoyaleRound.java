package com.mileses.mcroyale;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.bukkit.ChatColor;

public final class McRoyaleRound {

	public static void startRound(Location location, int length, HashMap<String, Boolean> list, Player sender,
			int peaceTimeArg) {

		String playerString = "";
		for (Player p : Bukkit.getOnlinePlayers()) {
			sender.sendMessage(p.getName() + list.get(p.getName()));
			// reset player to full, healed, clear and survival. add scoreboard.
			p.setGameMode(GameMode.SURVIVAL);
			p.setHealth(p.getMaxHealth());
			p.setFoodLevel(20);
			if (length == 0) {
				McRoyale.setPlayerDown(p);
			}
			clearInv(p);
			playerString = playerString.concat(" " + p.getName());
		}
		McRoyale.getLogr().info("start round with players: " + playerString);
		McRoyale.roundActive = true;
		McRoyale.getLogr().info(Boolean.toString(McRoyale.roundActive) + "is the setting of round active.");
		// calculate min distance between players
		int distance = length / (list.size() + 1);
		if (distance >= length / 2) {
			distance = length / 2 + 1;
		}
		if (length >= 5)
			McRoyale.generateWalls(location, length);
		// run command /spreadplayers x z distance teams player player player player
		// player player player etc.
		McRoyale.getLogr().info("running spreadplayers..");
		String commandString = "spreadplayers " + Integer.toString(location.getBlockX()) + " "
				+ Integer.toString(location.getBlockZ()) + " " + Integer.toString(distance) + " "
				+ Integer.toString((length / 2) - 1) + " false" + playerString;
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandString);
		McRoyale.getLogr().info("players spread.");
		// set bed spawns
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setBedSpawnLocation(p.getLocation(), true);
		}
		if (peaceTimeArg > 0) {
			McRoyale.peaceTime = true;
			Bukkit.broadcastMessage(ChatColor.RED + "Peace Time is active for " + ChatColor.DARK_GREEN
					+ Integer.toString(peaceTimeArg) + ChatColor.RED + "minutes.");
			new McRoyalePeaceRunnable().runTaskLater(McRoyale.getInst(), peaceTimeArg * 1200);
		} else
			Bukkit.broadcastMessage(
					ChatColor.DARK_GREEN + "This round is without any Peace Time!! Players can be damaged.");

	}

	public static void clearInv(Player player) {
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
	}
}
