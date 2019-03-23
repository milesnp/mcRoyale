package com.mileses.mcroyale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.ScoreboardManager;

public class McRoyaleStatRunnable extends BukkitRunnable {
	private ScoreboardManager manager;
	private int counter;
	public McRoyaleStatRunnable(ScoreboardManager manager) {
		this.manager = manager;
		counter = 0;
	}

	@Override
	public void run() {
		McRoyale.getLogr().info("Changing Scoreboard.");
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (McRoyale.roundActive) {
					if (counter == 0) {
						p.setScoreboard(McRoyale.killBoard);
						counter ++;
					}
					else if (counter == 1) {
						p.setScoreboard(McRoyale.deathBoard);
						counter ++;
				}
					else if (counter == 2) {
						p.setScoreboard(McRoyale.winBoard);
						counter = 0;
					}
					
			}
				else {
				p.setScoreboard(manager.getNewScoreboard());
			}
		}
	}
}
