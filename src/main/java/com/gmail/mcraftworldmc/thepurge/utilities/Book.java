package com.gmail.mcraftworldmc.thepurge.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.gmail.mcraftworldmc.thepurge.Main;

public class Book {
	private Main plugin;
	public Book(Main instance){
		this.plugin = instance;
	}
	private FileConfiguration bookConfig = null;
	private File bookFile = null;
	public void saveDefaultBookConfig(){
		if(this.bookConfig == null){
			this.bookFile = new File(plugin.getDataFolder(), "book.yml");
		}
		if(!this.bookFile.exists()){
			plugin.saveResource("book.yml", false);
		}
	}
	public void saveBookConfig(){
		if(bookConfig == null || bookFile == null){
			return;
		}
		try{
			this.bookConfig.save(this.bookFile);
		}catch(IOException e){
			plugin.getLogger().severe("Could not save config!");
			e.printStackTrace();
		}
	}
	public FileConfiguration getBookConfig(){
		if(this.bookConfig == null){
			reloadBookConfig();
		}
		return this.bookConfig;
	}
	public void reloadBookConfig(){
		if(this.bookFile == null){
			this.bookFile = new File(plugin.getDataFolder(), "book.yml");
		}
		this.bookConfig = YamlConfiguration.loadConfiguration(bookFile);
		InputStream itemsConfigStream = plugin.getResource("book.yml");
		if(itemsConfigStream != null){
			YamlConfiguration defaultItemsConfig = YamlConfiguration.loadConfiguration(itemsConfigStream);
			bookConfig.setDefaults(defaultItemsConfig);
		}
	}
	public void giveSyndicateBook(Player p){
		p.getInventory().addItem(giveSelectedBook((ChatColor.RED + "Syndicate Guide"), (ChatColor.AQUA + "PurgeMC"), (ChatColor.RED + "Syndicate Guide"), this.getBookConfig().getStringList("book.syndicateBook")));
	}
	public void giveCivilianBook(Player p){
		p.getInventory().addItem(giveSelectedBook((ChatColor.RED + "Civilian Guide"), (ChatColor.AQUA + "PurgeMC"), (ChatColor.RED + "Civilian Guide"), this.getBookConfig().getStringList("book.civilianBook")));
	}
	public void giveTargetBook(Player p){
		p.getInventory().addItem(giveSelectedBook((ChatColor.RED + "Target Guide"), (ChatColor.AQUA + "PurgeMC"), (ChatColor.RED + "Target Guide"), this.getBookConfig().getStringList("book.targetBook")));
	}
	private ItemStack giveSelectedBook(String title, String author, String displayName, List<String> pages){
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta bm = (BookMeta) book.getItemMeta();
		bm.setTitle(title);
		bm.setAuthor(author);
		bm.setDisplayName(displayName);
		bm.setPages(this.getPages(pages));
		book.setItemMeta(bm);
		return book;
	}
	private List<String> getPages(List<String> l){
		List<String> b = new ArrayList<>();
		for(int i = 0; i < l.size(); i++){
			b.add(ChatColor.translateAlternateColorCodes('&', l.get(i)));
		}
		return b;
	}
}
