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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.List;
import java.lang.reflect.Array;
import java.util.ArrayList;
import de.myzelyam.api.vanish.VanishAPI;

public final class McRoyale extends JavaPlugin {
	private static McRoyale instance;
	private static Logger logger;
	public HashSet<String> playerlist;
	
	@Override
	public void onEnable() {
		//TODO initializaiton logic	
		instance = this;
		logger = getLogger();
		McRoyaleDeathListener mrdl = new McRoyaleDeathListener(this);
		getServer().getPluginManager().registerEvents(mrdl, this);
		for (Player x : Bukkit.getOnlinePlayers()) playerlist.add(x.getName());
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
					if (args.length >= 2) {
						if (args[1].equalsIgnoreCase("wall")) {
							sender.sendMessage("/royale wall <wall length> OR /royale wall <length> <x> <z>");
							return true;
						}
						if (args[1].equalsIgnoreCase("round")) {
							sender.sendMessage("/royale round <wall length> <peacetime in ticks or false> [x z]");
							return true;
						}
					}
					else sender.sendMessage("/royale help wall or /royale help round");
					return true;
				}
				
				
				//check for testdeath command TODO remove
				if ((args[0].equalsIgnoreCase("testdeath") && (sender instanceof Player))) {
					new McRoyaleDeathRunnable(((Player) sender).getWorld(),((Player) sender).getLocation()).runTaskLater(McRoyale.getInst(), 300L);
					return true;
				}
				//end testdeath command
				
				// check for wall command
				if (args[0].equalsIgnoreCase("wall") ) {
					//check length if sender is Player
					if ((sender instanceof Player) && (args.length == 2)) {
							//init length at 3 for debug and to cheat around a compile error.
							int iLength = 3;
							//check for Integer
							//automatically calculate length based on number of players. uses 10,000 blocks area per player.
							if (args[1].equalsIgnoreCase("auto")) {
								int playercount = Bukkit.getOnlinePlayers().size();
								iLength = (int) Math.sqrt(10000*playercount);

							}
							else if (!isInt(args[1])){
								sender.sendMessage("Argument should be a whole number.");
								return false;
							}
							else iLength = Integer.parseInt(args[1]);
							
							generateWalls(((Player) sender).getLocation(), iLength);
							return true;
						}
						else {
							//check if coords are given
							if (args.length == 4) {
								// check args for integership
								//default to 3 to show something is wrong
								int iLength = 3;
								int ix;
								int iz;
								if (isInt(args[1])) {
									 iLength = Integer.parseInt(args[1]);
								}
								//automatically calculate length based on number of players. uses 10,000 blocks area per player.
								else if (args[1].equalsIgnoreCase("auto")) {
									int playercount = Bukkit.getOnlinePlayers().size();
									iLength = (int) Math.sqrt(10000*playercount);
								}
								else {
									sender.sendMessage("Length must be a number or \"auto\"");
									return false;
								}
								if (isInt(args[2]) && isInt(args[3])){
									 ix = Integer.parseInt(args[2]);
									 iz = Integer.parseInt(args[3]);
								}
								else
								{
									sender.sendMessage("Co-ords must be whole numbers.");
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
				//end wall command
				
				//check for round command and for args
				if (args[0].equalsIgnoreCase("round") && args.length >= 3) {
						Location location;
						//check that optional x z args are missing
						if (!(args.length >= 5)) {
							//TODO set location here then continue
							location = ((Player) sender).getLocation();
						}
						//check that x and z are integers and set location accordingly
						else if (isInt(args[3]) && isInt(args[4])){
							 int ix = Integer.parseInt(args[3]);
							 int iz = Integer.parseInt(args[4]);
							location = new Location(((Player) sender).getWorld(), ix, 0, iz);
						}
						else {
							sender.sendMessage("x and z should be numbers.");
							return false;
						}
						
						//check that peacetime is either "false" or a number
						int peacetime;
						int length;
						//set peacetime
						if (isInt(args[2])) peacetime = Integer.parseInt(args[2]);
						else if (args[2].equalsIgnoreCase("false")) peacetime = 0;
						else return false;
						//check length is int and set
						if (isInt(args[1])) length = Integer.parseInt(args[1]);
						else return false;
						//list all players
						
						for (Player x : Bukkit.getOnlinePlayers()) playerlist.add(x.getName());
						
						//start round
						generateWalls(location,length);
						McRoyaleRound.startRound(location, length, playerlist, (Player) sender, peacetime);
						return true;
						
				}
				//end round command
				
				
					}
		}
		
		return false;
		
	}
	
	public boolean isInt(String s) {
		return s.matches("-?\\d+");
	}
	public void generateWalls(Location loc, int length) {
		Bukkit.broadcastMessage("Building walls of length " + Integer.toString(length));
		//get corner relative to player position
		//uses getBlockN() instead of getN() for int
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
	public static Logger getLogr() {
		return logger;
	}
}
