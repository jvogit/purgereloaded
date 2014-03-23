package com.gmail.mcraftworldmc.thepurge;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ItemsConfig {
	private Main plugin;
	public ItemsConfig(Main instance){
		this.plugin = instance;
	}
	private FileConfiguration itemsConfig = null;
	private File itemsFile = null;
	public void saveDefaultItemsConfig(){
		if(this.itemsConfig == null){
			this.itemsFile = new File(plugin.getDataFolder(), "items.yml");
		}
		if(!this.itemsFile.exists()){
			plugin.saveResource("items.yml", false);
		}
	}
	public void saveItemsConfig(){
		if(itemsConfig == null || itemsFile == null){
			return;
		}
		try{
			this.itemsConfig.save(this.itemsFile);
		}catch(IOException e){
			plugin.getLogger().severe("Could not save config!");
			e.printStackTrace();
		}
	}
	public FileConfiguration getItemsConfig(){
		if(this.itemsConfig == null){
			reloadItemsConfig();
		}
		return this.itemsConfig;
	}
	public void reloadItemsConfig(){
		if(this.itemsFile == null){
			this.itemsFile = new File(plugin.getDataFolder(), "items.yml");
		}
		this.itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
		InputStream itemsConfigStream = plugin.getResource("items.yml");
		if(itemsConfigStream != null){
			YamlConfiguration defaultItemsConfig = YamlConfiguration.loadConfiguration(itemsConfigStream);
			itemsConfig.setDefaults(defaultItemsConfig);
		}
	}
}
