package com.mileses.mcroyale;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class McRoyale extends JavaPlugin {
	@Override
	public void onEnable() {
		//TODO initializaiton logic	
	}

	@Override
	public void onDisable() {
		//TODO disable logic
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("royale")) {
			//check for arguments (subcommands)
			if (args.length > 0) {
				// check for wall command
				if (args[1].equalsIgnoreCase("wall") ) {
					//check if sender is Player or Console
					if (sender instanceof Player) {
						//check for length argument
						if (args.length == 2) {
							//todo build wall
						}
						else {
						sender.sendMessage("wrong number of arguments. Should be 2 or 4, not: " + Integer.toString(args.length));
						}
					}
					//console means require coordinates and length
					else {
						//check for length, x, z
						if (args.length == 4) {
							//TODO check all args for integership, then execute wall generate
						}
					}
				}
				return true;
			}
			else {
				return false;
			}
		}
		
		return false;
		
	}
}
