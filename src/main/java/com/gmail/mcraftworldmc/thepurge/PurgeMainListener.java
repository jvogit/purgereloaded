package com.gmail.mcraftworldmc.thepurge;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class PurgeMainListener implements Listener{
	private Main plugin;
	public PurgeMainListener(Main instance){
		plugin = instance;
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		final Player p = event.getPlayer();
		p.setHealth(20D);
		p.setFoodLevel(20);
		if(plugin.maps.getMapsConfig().getString("lobby").isEmpty()){
			p.sendMessage(plugin.prefix + "The default Purge lobby has not been set! It has been set for this world: " + ChatColor.GREEN + event.getPlayer().getWorld().getName());
			p.sendMessage(ChatColor.YELLOW + "At this location: " + p.getLocation().getX() + " " + p.getLocation().getY() + " " + p.getLocation().getZ());
			p.sendMessage(ChatColor.YELLOW + "To set a different lobby location, please refer to the /purge command, Thank you!");
			plugin.maps.getMapsConfig().set("lobby", p.getWorld().getName() + "," + p.getLocation().getX() + "," + p.getLocation().getY() + "," + p.getLocation().getZ());
			plugin.maps.saveMapsConfig();
			return;
		}
		if(!plugin.game.getGameState().equals(GameState.LOBBY)){
			return;
		}
		final String[] location = plugin.maps.getMapsConfig().getString("lobby").split(",");
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable(){
			public void run(){
				p.teleport(new Location(Bukkit.getWorld(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3])));
				p.sendMessage(plugin.prefix + "Welcome to the annual purge! All emergency services will be suspended for a 12-hour period!");
			}
		}, 2l);
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
		if(!p.isOp() && !p.getGameMode().equals(GameMode.ADVENTURE)){
			p.setAllowFlight(false);
		}
		if(Bukkit.getOnlinePlayers().length == 1 && plugin.getConfig().getString("purge.setupmode").equalsIgnoreCase("false") && plugin.game.getGameState().equals(GameState.LOBBY)){
			plugin.preTimer.start();
		}
		for(Player pl : Bukkit.getOnlinePlayers()){
			pl.showPlayer(p);
		}
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if(Bukkit.getOnlinePlayers().length - 1 <= 0){
			try{
				plugin.preTimer.stop();
			    plugin.timer.stop();
			}catch(IllegalStateException ex){
				if(plugin.getConfig().getString("purge.devTool").equalsIgnoreCase("true")){
					plugin.getLogger().info("Supressed IllegalStateException: " + ex.getMessage());
				}
			}
		}
	}
	@EventHandler
	public void onPing(ServerListPingEvent e){
		if(plugin.getConfig().getBoolean("purge.setupmode") == true){
			return;
		}
		if(plugin.game.getGameState().equals(GameState.LOBBY)){
			e.setMotd("Lobby");
		}
		if(plugin.game.getGameState().equals(GameState.GAME)){
			e.setMotd("In Progress!");
			e.setMaxPlayers(1);
		}
		if(plugin.game.getGameState().equals(GameState.AFTER_GAME) || plugin.game.getGameState().equals(GameState.RESTARTING)){
			e.setMotd("RESTARTING");
			e.setMaxPlayers(1);
		}
	}
	@EventHandler
	public void onPlace(BlockPlaceEvent e){
		if(!e.getPlayer().isOp())
			e.setCancelled(true);
	}
	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent e){
		if(plugin.game.getGameState().equals(GameState.LOBBY)){
			e.setCancelled(true);
			e.setFoodLevel(10);
		}
		if(plugin.game.getSpectatorList().contains(e.getEntity().getName())){
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		if(!plugin.game.getGameState().equals(GameState.LOBBY) && !plugin.game.getGameState().equals(GameState.AFTER_GAME))
			return;
		e.setCancelled(true);
		if(e.getPlayer().hasPermission("purge.admin")){
			Bukkit.broadcastMessage(ChatColor.DARK_RED + e.getPlayer().getName() + ChatColor.GRAY + ": " + ChatColor.RED + e.getMessage());
			return;
		}else if(e.getPlayer().hasPermission("purge.mod")){
			Bukkit.broadcastMessage(ChatColor.RED + e.getPlayer().getName() + ChatColor.GRAY + ": " + ChatColor.RED + e.getMessage());
			return;
		}else if(e.getPlayer().hasPermission("purge.emerald")){
			Bukkit.broadcastMessage(ChatColor.GREEN + e.getPlayer().getName() + ChatColor.GRAY + ": " + ChatColor.WHITE + e.getMessage());
			return;
		}else if(e.getPlayer().hasPermission("purge.diamond")){
			Bukkit.broadcastMessage(ChatColor.DARK_AQUA + e.getPlayer().getName() + ChatColor.GRAY + ": " + ChatColor.WHITE + e.getMessage());
			return;
		}else if(e.getPlayer().hasPermission("purge.gold")){
			Bukkit.broadcastMessage(ChatColor.GOLD + e.getPlayer().getName() + ChatColor.GRAY + ": " + ChatColor.WHITE + e.getMessage());
			return;
		}else{
			Bukkit.broadcastMessage(e.getPlayer().getName() + ChatColor.GRAY + ": " + ChatColor.WHITE + e.getMessage());
		}
	}
	@EventHandler
	public void onLogin(PlayerLoginEvent e){
		if(e.getResult().equals(Result.KICK_FULL)){
			if(e.getPlayer().hasPermission("purge.admin") || e.getPlayer().hasPermission("purge.mod")){
				e.allow();
				e.setResult(Result.ALLOWED);
			}
		}
	}
}
