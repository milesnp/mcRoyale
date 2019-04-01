package com.mileses.mcroyale;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;



public class McRoyaleRespawnListener implements Listener {
	McRoyale pl;

	public McRoyaleRespawnListener(McRoyale pl) {
		this.pl = pl;
		
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		if (McRoyale.roundActive) {
			//todo Adapt for new round system
			//if (!pl.playerList.containsKey(e.getPlayer().getName()) || (pl.playerList.containsKey(e.getPlayer().getName()) && !pl.playerList.get(e.getPlayer().getName()))) {
				new McRoyaleRespawnRunnable(e.getPlayer(), pl).runTaskLater(pl,10);
			//}
		}
		
	}
	


}
