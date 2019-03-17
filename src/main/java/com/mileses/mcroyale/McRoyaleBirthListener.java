package com.mileses.mcroyale;



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
				McRoyale.getLogr().info(e.getPlayer().getName() + " respawned.");
				new McRoyaleBirthRunnable(e.getPlayer().getName()).runTaskLater(McRoyale.getInst(), 100L);
			}
		}
	}
}

