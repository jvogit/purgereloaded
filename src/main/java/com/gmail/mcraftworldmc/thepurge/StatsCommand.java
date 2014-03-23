package com.gmail.mcraftworldmc.thepurge;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor{
	private Main plugin;
	public StatsCommand(Main instance){
		this.plugin = instance;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "Consoles can't have stats! Only in game players may use the command");
		}
		Player p = (Player) sender;
		p.sendMessage(plugin.prefix + "Coming soon!");
		return true;
	}
}
