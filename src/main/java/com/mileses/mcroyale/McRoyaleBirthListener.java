package com.mileses.mcroyale;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import id.cimbraien.compassradar.CompassRadar;

public class McRoyaleBirthListener implements Listener{
	
	McRoyale pl;

	public McRoyaleBirthListener(McRoyale plugin) {
		// TODO Auto-generated constructor stub
	}


	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		String playername = e.getPlayer().getName();
		if (!pl.playerlist.get(playername)) {
			e.getPlayer().getInventory().addItem(CompassRadar.getTracker());
		}
	}
}

