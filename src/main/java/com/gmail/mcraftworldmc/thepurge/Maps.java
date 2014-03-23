package com.gmail.mcraftworldmc.thepurge;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
/**
 * Maps Generate and handle the maps configuration
 * 
 * @author MineOCraftMC
 * @version 1.1.0
 * @since 1.0.0
 *
 */
public class Maps {
	private final Main plugin;
	public Maps(Main instance){
		plugin = instance;
	}
	private FileConfiguration mapsConfig = null;
	private FileConfiguration mapsInfoConfig = null;
	private File mapsFile = null;
	private File mapsInfoFile = null;
	/*
	 * Saves the defaults maps.yml configuration
	 * 
	 */
	public void saveDefaultMapsConfig(){
		if(this.mapsFile == null){
			this.mapsFile = new File(plugin.getDataFolder(), "maps.yml");
		}
		if(!this.mapsFile.exists()){
			plugin.saveResource("maps.yml", false);
		}
	}
	/*
	 * Reloads the maps.yml configuration
	 * 
	 */
	public void reloadMapsConfig(){
		if(this.mapsFile == null){
			this.mapsFile = new File(plugin.getDataFolder(), "maps.yml");
		}
		this.mapsConfig = YamlConfiguration.loadConfiguration(mapsFile);
		InputStream mapsConfigStream = plugin.getResource("maps.yml");
		if(mapsConfigStream != null){
			YamlConfiguration defaultMapsConfig = YamlConfiguration.loadConfiguration(mapsConfigStream);
			mapsConfig.setDefaults(defaultMapsConfig);
		}
	}
	/*
	 * Saves the maps.yml configuration
	 * 
	 * @throws IOException if the config cannot be saved
	 * 
	 */
	public void saveMapsConfig(){
		if(mapsConfig == null || mapsFile == null){
			return;
		}
		try{
			this.mapsConfig.save(this.mapsFile);
		}catch(IOException e){
			plugin.getLogger().severe("Could not save config!");
			e.printStackTrace();
		}
	}
	/*
	 * Get the maps.yml configuration
	 * 
	 * @returns mapsConfig
	 * 
	 */
	public FileConfiguration getMapsConfig(){
		if(mapsConfig == null){
			reloadMapsConfig();
		}
		return this.mapsConfig;
	}
	/*
	 * Gets the mapsInfo.yml configuration
	 * 
	 * @returns mapsInfoConfig
	 * 
	 */
	public FileConfiguration getMapsInfoConfig(){
		if(mapsInfoConfig == null){
			reloadMapsInfoConfig();
		}
		return this.mapsInfoConfig;
	}
	/*
	 * Reloads the mapsInfo.yml configuration
	 * 
	 */
	public void reloadMapsInfoConfig(){
		if(this.mapsFile == null){
			this.mapsFile = new File(plugin.getDataFolder(), "mapsInfo.yml");
		}
		this.mapsInfoConfig = YamlConfiguration.loadConfiguration(mapsInfoFile);
		InputStream mapsConfigStream = plugin.getResource("mapsInfo.yml");
		if(mapsConfigStream != null){
			YamlConfiguration defaultMapsConfig = YamlConfiguration.loadConfiguration(mapsConfigStream);
			mapsInfoConfig.setDefaults(defaultMapsConfig);
		}
	}
	/*
	 * Saves the mapsInfo.yml configuration
	 * 
	 * @throws IOException if config could not be saved
	 */
	public void saveMapsInfoConfig(){
		if(mapsInfoConfig == null || mapsInfoFile == null){
			return;
		}
		try{
			this.mapsInfoConfig.save(this.mapsInfoFile);
		}catch(IOException e){
			plugin.getLogger().severe("Could not save config!");
			e.printStackTrace();
		}
	}
	/*
	 * Saves the default mapsInfo.yml config
	 * 
	 */
	public void saveDefaultMapsInfoConfig(){
		if(this.mapsInfoFile == null){
			this.mapsInfoFile = new File(plugin.getDataFolder(), "mapsInfo.yml");
		}
		if(!this.mapsInfoFile.exists()){
			plugin.saveResource("mapsInfo.yml", false);
		}
	}
	
}
