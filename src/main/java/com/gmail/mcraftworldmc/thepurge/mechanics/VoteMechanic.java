package com.gmail.mcraftworldmc.thepurge.mechanics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.gmail.mcraftworldmc.thepurge.Main;

public class VoteMechanic {
	private Main plugin;
	public VoteMechanic(Main instance){
		plugin = instance;
	}
	private HashMap<String, Integer> purgeWorldsVote = new HashMap<>();
	private List<String> hasVotedList = new ArrayList<>(); 
	private Random rGen = new Random();
	private World w = null;
	public void addVote(int worldNumber, Player p){
		String worldName = null;
		for(String s : plugin.maps.getMapsConfig().getStringList("purgeWorlds")){
			String[] split = s.split(",");
			if(Integer.parseInt(split[2]) == worldNumber){
				worldName = split[0];
			}
		}
		try{
			purgeWorldsVote.put(worldName, purgeWorldsVote.get(worldName) + 1);
			if(plugin.getConfig().getBoolean("purge.devTool") == true){
				plugin.getLogger().info(purgeWorldsVote.get(worldName) + "");
			}
		}catch(NullPointerException e){
			purgeWorldsVote.put(worldName, 1);
		}
		this.hasVotedList.add(p.getName());
	}
	public int getVotes(String worldName){
		try{
			return this.purgeWorldsVote.get(worldName);
		}catch(NullPointerException e){
			return 0;
		}
	}
	public boolean hasVoted(Player p){
		if(hasVotedList.contains(p.getName())){
			return true;
		}else{
			return false;
		}
	}
	public boolean isPurgeWorld(int number){
		for(String s : plugin.maps.getMapsConfig().getStringList("purgeWorlds")){
			String[] split = s.split(",");
			if(Integer.parseInt(split[2]) == number){
				return true;
			}else{
				continue;
			}
		}
		return false;
	}
	public World getHighestVote(){
		int voteHighest = 0;
		for(Map.Entry<String, Integer> e : purgeWorldsVote.entrySet()){
			if(e.getValue() > voteHighest){
				voteHighest = e.getValue();
				this.w = Bukkit.getWorld(e.getKey());
			}
		}
		if(w == null){
			this.getRandomMap();
		}
		return w;
	}
	public void clear(){
		this.hasVotedList = null;
		this.purgeWorldsVote = null;
	}
	private void getRandomMap(){
		if(plugin.getConfig().getString("purge.devTool").equalsIgnoreCase("true")){
			plugin.getLogger().info("LAUNCHED RANDOM MAP!");
		}
		if(plugin.maps.getMapsConfig().getStringList("purgeWorlds").size() == 1){
			String[] split = plugin.maps.getMapsConfig().getStringList("purgeWorlds").get(0).split(",");
			this.w = Bukkit.getWorld(split[0]);
			return;
		}
		String[] split = plugin.maps.getMapsConfig().getStringList("purgeWorlds").get(rGen.nextInt(plugin.maps.getMapsConfig().getStringList("purgeWorlds").size()) + 1).split(",");
		this.w = Bukkit.getWorld(split[0]);
	}
}
