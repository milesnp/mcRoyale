package com.mileses.mcroyale;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;

public class McRoyaleStatRunnable extends BukkitRunnable {

	private int counter;

	public Scoreboard board;

	public McRoyaleStatRunnable() {
		counter = 1;
	}

	@Override
	public void run() {

		if (counter == 4) {
			McRoyale.oreStat.setDisplaySlot(DisplaySlot.SIDEBAR);
		} else if (counter == 3) {

			McRoyale.killStat.setDisplaySlot(DisplaySlot.SIDEBAR);
		} else if (counter == 2) {

			McRoyale.deathStat.setDisplaySlot(DisplaySlot.SIDEBAR);
		} else {

			McRoyale.winStat.setDisplaySlot(DisplaySlot.SIDEBAR);
		}
		counter++;

		if (!McRoyale.roundActive)
			this.cancel();

		if (counter == 5) {
			counter = 1;

		}
	}
}
