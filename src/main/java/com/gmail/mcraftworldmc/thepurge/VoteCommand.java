package com.gmail.mcraftworldmc.thepurge;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCommand implements CommandExecutor{
	private Main plugin;
	public VoteCommand(Main instance){
		plugin = instance;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Only in game players may use this command!");
		}
		if(plugin.getConfig().getBoolean("purge.setupmode")){
			sender.sendMessage(plugin.prefix + ChatColor.RED + "Set up mode is enabled!");
			return true;
		}
		Player p = (Player) sender;
		if(args.length == 0){
			p.sendMessage(plugin.prefix + ChatColor.WHITE + "Type " + ChatColor.GREEN + "/vote " + ChatColor.WHITE + "to choose your favorite map!");
			for(String s : plugin.maps.getMapsConfig().getStringList("purgeWorlds")){
				String[] split = s.split(",");
				p.sendMessage(ChatColor.YELLOW + "" + Integer.parseInt(split[2]) + ". " + ChatColor.WHITE + split[1] + ChatColor.GRAY + " [" + ChatColor.GREEN + plugin.voteMech.getVotes(split[0]) + " Votes" + ChatColor.GRAY + "]");
			}
			return true;
		}
		if(!plugin.game.getGameState().equals(GameState.LOBBY)){
			p.sendMessage(plugin.prefix + ChatColor.RED + "Cannot vote during game!");
			return true;
		}
		if(args.length > 0 && args.length == 1){
			if(plugin.voteMech.hasVoted(p)){
				p.sendMessage(plugin.prefix + ChatColor.RED + "You already have voted!");
				return true;
			}
			if(plugin.voteMech.isPurgeWorld(Integer.parseInt(args[0]))){
				plugin.voteMech.addVote(Integer.parseInt(args[0]), p);
				p.sendMessage(plugin.prefix + ChatColor.GREEN + "Successful vote!");
				return true;
			}else{
				p.sendMessage(plugin.prefix + ChatColor.RED + "Map does not exist!");
				return true;
			}
		}
		return false;
	}
}
