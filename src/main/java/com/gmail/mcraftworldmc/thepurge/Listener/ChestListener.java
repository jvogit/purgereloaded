package com.gmail.mcraftworldmc.thepurge.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.gmail.mcraftworldmc.thepurge.Main;

public class ChestListener implements Listener{
	private Main plugin;
	private List<Location> chestLocation = new ArrayList<>();
	public ChestListener(Main instance){
		this.plugin = instance;
	}
	private Random rGen = new Random();
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
	    /* if(!plugin.game.getGameState().equals(GameState.GAME)){
			return;
		} */
		if(plugin.game.getSpectatorList().contains(e.getPlayer().getName())){
			e.setCancelled(true);
			return;
		}
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			Block block = e.getClickedBlock();
			if(block.getType().equals(Material.CHEST) && !chestLocation.contains(block.getLocation())){
				if(plugin.game.getSpectatorList().contains(e.getPlayer().getName())){
					e.setCancelled(true);
					return;
				}
				Chest chest = (Chest) block.getState();
				chestLocation.add(block.getLocation());
				chest.getInventory().clear();
				for(int i = 0; i <= 6; i++){
					int slot = rGen.nextInt(chest.getInventory().getSize());
					if(i < 3){
						while(chest.getInventory().getItem(slot) != null){
							slot = rGen.nextInt(chest.getInventory().getSize());
						}
						chest.getInventory().setItem(slot, getRandomItem());
						continue;
					}
					if(rGen.nextInt(4) + 1 > 2){
						while(chest.getInventory().getItem(slot) != null){
							slot = rGen.nextInt(chest.getInventory().getSize());
						}
						chest.getInventory().setItem(slot, getRandomItem());
					}
				}
			}
		}
	}
	private ItemStack getRandomItem(){
		ItemStack stack = null;
		do{
			for(String s : plugin.itemConfig.getItemsConfig().getStringList("chest.items")){
				String[] split = s.split(",");
				if(rGen.nextInt(10) + 1 < 5){
					continue;
				}
				int i = rGen.nextInt(20) + 1;
				if(i == Integer.parseInt(split[2])){
					stack = new ItemStack(Material.getMaterial(split[0].toUpperCase()), rGen.nextInt(Integer.parseInt(split[1])) + 1);
					break;
				}
			}
		}while(stack == null);
		return stack;
	}
}
