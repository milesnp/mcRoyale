package com.mileses.mcroyale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.ScoreboardManager;

public class McRoyaleStatRunnable extends BukkitRunnable {
	private ScoreboardManager manager;

	public McRoyaleStatRunnable(ScoreboardManager manager) {
		this.manager = manager;
	}

	@Override
	public void run() {
		if (McRoyale.roundActive) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.setScoreboard(McRoyale.board);
			}
		} else {
			for (Player p : Bukkit.getOnlinePlayers()) {
				// clear scoreboard for all on round end.
				p.setScoreboard(manager.getNewScoreboard());
			}
		}
	}
}
