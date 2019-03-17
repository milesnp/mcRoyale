package com.mileses.mcroyale;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class McRoyaleBirthRunnable extends BukkitRunnable {
private String p;

public McRoyaleBirthRunnable(String playerName) {
p = playerName;
}



@Override
public void run() {
	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamemode spectator " + p);
	McRoyale.getLogr().info(p + " is now spectating.");
}
}
