package com.mileses.mcroyale;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scoreboard.Score;

public class McRoyaleOreListener implements Listener{
	McRoyale pl;
	public McRoyaleOreListener(McRoyale pl) {
		this.pl = pl;
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Material blockType = e.getBlock().getType();
		String playerName = e.getPlayer().getName();
		if (oreCheck(blockType)) {
			Score ostat = McRoyale.oreStat.getScore(playerName);
			ostat.setScore(ostat.getScore() + 1);
		}
	}
	public boolean oreCheck(Material m) {
		if (m == Material.IRON_ORE || m == Material.DIAMOND_ORE || m == Material.GOLD_ORE || m == Material.REDSTONE_ORE || m == Material.GLOWING_REDSTONE_ORE) {
			return true;
		}
		else return false;
			
	}
}
