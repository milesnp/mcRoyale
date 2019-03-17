package com.mileses.mcroyale;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class McRoyaleBirthRunnable extends BukkitRunnable {
private Player p;

public McRoyaleBirthRunnable(Player player) {
p = player;
}



@Override
public void run() {
	p.setGameMode(GameMode.SPECTATOR);
}
}
