package com.mileses.mcroyale;


import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import id.cimbraien.compassradar.CompassRadar;

public class McRoyaleBirthListener implements Listener{
	
	McRoyale pl;

	public McRoyaleBirthListener(McRoyale plugin) {
		pl = plugin;
	}


	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		String playername = e.getPlayer().getName();
		if (McRoyale.roundActive) {
			if (!pl.playerList.containsKey(playername) || !pl.playerList.get(playername)) {
				e.getPlayer().getInventory().addItem(CompassRadar.getTracker());
				new McRoyaleBirthRunnable(e.getPlayer()).runTaskLater(McRoyale.getInst(), 2L);
			}
		}
	}
}

