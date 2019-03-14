package com.mileses.mcroyale;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class McRoyale extends JavaPlugin {
	private static McRoyale instance;
	@Override
	public void onEnable() {
		//TODO initializaiton logic	
		instance = this;
		getServer().getPluginManager().registerEvents(new McRoyaleDeathListener(), this);
	}
	
	@Override
	public void onDisable() {
		instance = null;
		//TODO disable logic
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("royale")) {
			//check for arguments (subcommands)
			if (args.length > 0) {
				//check for help
				if(args[0].equalsIgnoreCase("help")) {
					sender.sendMessage("/royale wall <length> OR /royale wall <length> <x> <z>");
				}
				// check for wall command
				if (args[0].equalsIgnoreCase("wall") ) {
					//check length if sender is Player
					if ((sender instanceof Player) && (args.length == 2)) {
							
							//check for Integer
							if (!isInt(args[1])) {
								sender.sendMessage("Argument should be a whole number.");
								return false;
							}
							int iLength = Integer.parseInt(args[1]);
							generateWalls(((Player) sender).getLocation(), iLength);
							return true;
						}
						else {
							//check if coords are given
							if (args.length == 4) {
								// check args for integership
								int iLength;
								int ix;
								int iz;
								if (isInt(args[1]) && isInt(args[2]) && isInt(args[3])){
									 iLength = Integer.parseInt(args[1]);
									 ix = Integer.parseInt(args[2]);
									 iz = Integer.parseInt(args[3]);
								}
								else
								{
									sender.sendMessage("Argument should be a whole number.");
									return false;
								}
								World world = Bukkit.getServer().getWorld("World");
								Location location = new Location(world, ix, 0, iz);
								generateWalls(location, iLength);
								return true;
							}
							else if (sender instanceof Player) {
									sender.sendMessage("Wrong number of arguments. Should be 2 or 4, not: " + Integer.toString(args.length));
									return false;
								}
								else {
									sender.sendMessage("Must use 4 arguments in console.");
									return false;
								}
							
						
							}
						}
					}
		}
		
		return false;
		
	}
	
	public boolean isInt(String s) {
		return s.matches("-?\\d+");
	}
	public void generateWalls(Location loc, int length) {
		getLogger().info("generating walls of length " + Integer.toString(length) + " at " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ());
		//get corner relative to player position
		//uses getBlockN() instead of getN()
		int absLength = Math.abs(length);
		int x1 = loc.getBlockX() - (absLength / 2);
		int y1 = 0;
		int z1 = loc.getBlockZ() - (absLength / 2);
		
		// create opposite corner adding length
		int x2 = x1 + absLength;
		int y2 = 150;
		int z2 = z1 + absLength;
		
		World world = loc.getWorld();
		
		//loop z
		getLogger().info("wall loop started");
		for (int yPoint = y1; yPoint <= y2; yPoint++) {
			//loop x 
	
				for (int xPoint = x1; xPoint <= x2; xPoint++) {
					int zPoint = z1;
					Block currentBlock = world.getBlockAt(xPoint,yPoint,zPoint);
					currentBlock.setType(Material.BEDROCK);
					zPoint = z2;
					currentBlock = world.getBlockAt(xPoint,yPoint,zPoint);
					currentBlock.setType(Material.BEDROCK);
				}
			
			//loop y
			
				for (int zPoint = z1 + 1; zPoint < z2; zPoint++) {
					int xPoint = x1;
					Block currentBlock = world.getBlockAt(xPoint,yPoint,zPoint);
					currentBlock.setType(Material.BEDROCK);
					xPoint = x2;
					currentBlock = world.getBlockAt(xPoint,yPoint,zPoint);
					currentBlock.setType(Material.BEDROCK);
					
				}
				
		}
		getLogger().info("wall loop complete");
	}
	public static McRoyale getInst() {
		return instance;
	}
}
