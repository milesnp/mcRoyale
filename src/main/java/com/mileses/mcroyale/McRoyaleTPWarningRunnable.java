package com.mileses.mcroyale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


public class McRoyaleTPWarningRunnable extends BukkitRunnable {
	McRoyaleRoundObject currentRound;
	McRoyale pl;
	int tpTimer;
	BukkitTask tpRunnable;
	public McRoyaleTPWarningRunnable(McRoyaleRoundObject currentRound, McRoyale pl, int tpTimer) {
		this.pl = pl;
		this.tpTimer = tpTimer;
		this.currentRound = currentRound;
	}

	@Override
	public void run() {
		if (!McRoyale.roundActive) this.cancel();
		Bukkit.broadcastMessage(ChatColor.RED + "Thirty seconds to surface TP!");
		tpRunnable = new McRoyaleTPRunnable(pl, tpTimer).runTaskLater(pl, 1200);
		
	}

}
