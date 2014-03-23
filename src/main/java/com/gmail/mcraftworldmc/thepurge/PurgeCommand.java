package com.gmail.mcraftworldmc.thepurge;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PurgeCommand implements CommandExecutor{
	private Main plugin;
	public PurgeCommand(Main instance){
		plugin = instance;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "You must be an in-game player to use this command!");
			return true;
		}
		Player p = (Player) sender;
		if(!p.hasPermission("purge.setup")){
			p.sendMessage(plugin.prefix + ChatColor.RED + "You must have permission to use this command! Contact an administrator if you think this is an error!");
			return true;
		}
		if(args.length == 0){
			p.sendMessage(ChatColor.YELLOW + "---------Purge Setup Help----------");
			p.sendMessage(ChatColor.GREEN + "/purge setpurgeworld - Sets the world you are currently in one of the game's maps");
			p.sendMessage(ChatColor.GREEN + "/purge sethousespawnpoint - Set the house spawn points where civilians will be spawned");
			p.sendMessage(ChatColor.GREEN + "/purge setsyndicatespawnpoint - Sets where the syndicate will spawn");
			p.sendMessage(ChatColor.GREEN + "/purge settargetspawnpoint - This where targets will be spawned at");
			p.sendMessage(ChatColor.GREEN + "/purge setlobbyworld - This is where the lobby will be");
			p.sendMessage(ChatColor.GREEN + "/purge removepurgeworld = Remove this world if it is flagged as a purgeWorld");
			return true;
		}
		if(args.length > 0 && args.length == 1){
			if(args[0].equalsIgnoreCase("setpurgeworld")){
				p.sendMessage(plugin.prefix + ChatColor.RED + "Correct usage: /purge setpurgeworld <name>");
				return true;
			}
			if(args[0].equalsIgnoreCase("setsyndicatespawnpoint")){
				if(!isPurgeWorld(p.getWorld().getName())){
					p.sendMessage(plugin.prefix + ChatColor.RED + "You must be in a purge world!");
					return true;
				}
				String location = (p.getLocation().getX() + "," + p.getLocation().getY() + "," + p.getLocation().getBlockZ());
				plugin.maps.getMapsInfoConfig().set(p.getWorld().getName() + ".syndicatespawnpoint", location);
				plugin.maps.saveMapsInfoConfig();
				p.sendMessage(plugin.prefix + ChatColor.GREEN + "Successfully set the syndicate spawn point at your location!");
				return true;
			}
			if(args[0].equalsIgnoreCase("sethousespawnpoint")){
				if(!isPurgeWorld(p.getWorld().getName())){
					p.sendMessage(plugin.prefix + ChatColor.RED + "You must be in a Purge world!");
					return true;
				}
				List<String> spawnPoints = new ArrayList<>();
				String location = (p.getLocation().getX() + "," + p.getLocation().getY() + "," + p.getLocation().getZ());
				if(plugin.maps.getMapsInfoConfig().getStringList(p.getWorld().getName() + ".housespawn") == null || plugin.maps.getMapsInfoConfig().getStringList(p.getWorld().getName() + ".housespawn").isEmpty()){
					spawnPoints.add(location);
					plugin.maps.getMapsInfoConfig().set(p.getWorld().getName() + ".housespawn", spawnPoints);
					p.sendMessage(plugin.prefix + ChatColor.GREEN + "Successfully set a house spawn at your location!");
					plugin.maps.saveMapsInfoConfig();
					return true;
				}else{
					spawnPoints = plugin.maps.getMapsInfoConfig().getStringList(p.getWorld().getName()+ ".housespawn");
					spawnPoints.add(location);
					plugin.maps.getMapsInfoConfig().set(p.getWorld().getName() + ".housespawn", spawnPoints);
					p.sendMessage(plugin.prefix + ChatColor.GREEN + "Successfully set another house spawn at your location!");
					plugin.maps.saveMapsInfoConfig();
					return true;
				}
			}
			if(args[0].equalsIgnoreCase("setlobbyworld")){
				plugin.maps.getMapsConfig().set("lobby", p.getWorld().getName() + "," + p.getLocation().getX() + "," + p.getLocation().getY() + "," + p.getLocation().getZ());
				plugin.maps.saveMapsConfig();
				p.sendMessage(plugin.prefix + ChatColor.GREEN + "Set the lobby world spawn at your location!");
				return true;
			}
			if(args[0].equalsIgnoreCase("settargetspawnpoint")){
				if(!isPurgeWorld(p.getWorld().getName())){
					p.sendMessage(plugin.prefix + ChatColor.RED + "You must be in a purge world!");
					return true;
				}
				plugin.maps.getMapsInfoConfig().set(p.getWorld().getName() + ".targetspawn", p.getLocation().getX() + "," + p.getLocation().getY() + "," + p.getLocation().getZ());
				plugin.maps.saveMapsInfoConfig();
				p.sendMessage(plugin.prefix + ChatColor.GREEN + "Successfully set the target spawn point at your location");
				return true;
			}
			if(args[0].equalsIgnoreCase("removepurgeworld")){
				if(isPurgeWorld(p.getWorld().getName())){
					for(String s : plugin.maps.getMapsConfig().getStringList("purgeWorlds")){
						String[] split = s.split(",");
						List<String> listOfWorlds = plugin.maps.getMapsConfig().getStringList("purgeWorlds");
						if(split[0].equalsIgnoreCase(p.getWorld().getName())){
							listOfWorlds.remove(s);
							plugin.maps.getMapsConfig().set("purgeWorlds", listOfWorlds);
							plugin.maps.getMapsInfoConfig().set(p.getWorld().getName(), null);
							plugin.maps.saveMapsConfig();
							plugin.maps.saveMapsInfoConfig();
							p.sendMessage(plugin.prefix + ChatColor.GREEN + "Successfully un flagged this world from purgeWorlds!");
							return true;
						}
					}
				}else{
					p.sendMessage(plugin.prefix + ChatColor.RED + "This world is not a purge world!");
					return true;
				}
			}
			p.sendMessage("Unknown argument! Please refer to /purge for help.");
			return true;
		}
		if(args.length >= 2){
			if(args[0].equalsIgnoreCase("setpurgeworld")){
				for(String s : plugin.maps.getMapsConfig().getStringList("purgeWorlds")){
					if(s.equalsIgnoreCase(p.getWorld().getName())){
						p.sendMessage(plugin.prefix + ChatColor.RED + "The world that you are in, is already flagged as a Purge world!");
						return true;
					}
				}
				List<String> purgeWorlds = plugin.maps.getMapsConfig().getStringList("purgeWorlds");
				StringBuilder sb = new StringBuilder();
				for(int i = 1; i < args.length; i++){
					sb.append(args[i]).append(" ");
				}
				purgeWorlds.add(p.getWorld().getName() + "," + sb.toString().trim() + "," + purgeWorlds.size() + 1);
				plugin.maps.getMapsConfig().set("purgeWorlds", purgeWorlds);
				plugin.maps.saveMapsConfig();
				plugin.maps.getMapsInfoConfig().set(p.getWorld().getName(), null);
				plugin.maps.saveMapsInfoConfig();
				p.sendMessage(plugin.prefix + ChatColor.YELLOW + "Flagged this world: " + ChatColor.GREEN + p.getWorld().getName() + " as a Purge world!");
				return true;
			}
		}
		return false;
	}
	private boolean isPurgeWorld(String worldName){
		for(String s : plugin.maps.getMapsConfig().getStringList("purgeWorlds")){
			String[] split = s.split(",");
			if(split[0].equalsIgnoreCase(worldName)){
				return true;
			}else{
				continue;
			}
		}
		return false;
	}
}
