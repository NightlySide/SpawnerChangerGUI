package io.github.nightlyside.spawnerchangergui;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigUtils {
	
	private JavaPlugin context;
	
	public String				filename; 
	public FileConfiguration 	config;
	public File 				configFile;
	
	public ConfigUtils(JavaPlugin context, String filename)
	{
		this.context = context;
		this.filename = filename;
	}
	
	public void reloadConfig() {
	    if (configFile == null) {
	    	configFile = new File(context.getDataFolder(), filename);
	    }
	 
		config = YamlConfiguration.loadConfiguration(configFile);
		
	    // Look for defaults in the jar
	    InputStreamReader defConfigStream = new InputStreamReader(context.getResource(filename));
	    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	    config.setDefaults(defConfig);
	}
	
	public FileConfiguration getConfig() {
	    if (config == null) {
	        reloadConfig();
	    }
	    return config;
	}
	
	public void saveConfig() {
	    if (config == null || configFile == null) {
	        return;
	    }
	    try {
	        getConfig().save(configFile);
	    } catch (IOException ex) {
	        Main.log.severe("Could not save config to " + configFile +", " + ex.toString());
	    }
	}
	
	public void saveDefauftConfig() {
	    if (configFile == null) {
	    	configFile = new File(context.getDataFolder(), filename);
	    }
	    if (!configFile.exists()) {            
	         context.saveResource(filename, false);
	     }
	}
}
