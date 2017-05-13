package io.github.nightlyside.spawnerchangergui;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin{

	public ConfigUtils			langConfig;
	public ConfigUtils			mainConfig;
	public Economy				economy;
	public boolean				isEconomyEnabled;
	public boolean				isWorldGuardEnabled;
	public WorldGuardPlugin 	worldguard;
	public static final Logger log = Logger.getLogger("Minecraft");
	public static final Set<String> openGUIs = new HashSet<>();
	public Listeners listeners;
	
	/*
	 * On plugin's disabling
	 */
	@Override
    public void onDisable() {
		// Send a message to tell the plugin in disabled
		log.info(String.format("[%s] version %s disabled", getDescription().getName(), getDescription().getVersion()));
    }
	
	/*
	 * On plugin's enabling
	 */
	@Override
	public void onEnable()
	{
		// Setup economy to work with the plugin
		setupEconomy();
		// Setup worldguard
		setupWorldGuard();
		
		// Setup lang configuration
		langConfig = new ConfigUtils(this, "lang.yml");
		langConfig.reloadConfig();
		langConfig.saveDefauftConfig();
		
		// Setup main configuration
		mainConfig = new ConfigUtils(this, "config.yml");
		mainConfig.saveDefauftConfig();
		mainConfig.reloadConfig();
		
		// Setup listeners
		listeners = new Listeners(this);
		Bukkit.getPluginManager().registerEvents(listeners, this);
		
		// Send a message to tell the plugin in enabled
		log.info(String.format("[%s] version %s enabled", getDescription().getName(), getDescription().getVersion()));
	}
	
	public void setupEconomy()
	{
		// If Vault isn't detected
		if (getServer().getPluginManager().getPlugin("Vault") == null)
		{
			// Tell the user the plugin won't be using economy
			log.info(String.format("[%s] Economy functions disabled due to no Vault dependency found!", getDescription().getName()));
		}
		else
		{
			// Get the economy plugin hooked to vault
			RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
			// If there is no plugins hooked
			if(rsp == null)
			{
				// Tell the user the plugin won't be using economy
				log.info(String.format("[%s] Economy functions disabled due to no Economy plugin found!", getDescription().getName()));
			}
			// Setup the economy variable
			economy = rsp.getProvider();
			// Tell the user economic functions are available
			log.info(String.format("[%s] Vault hooked!", getDescription().getName()));
		}
		isEconomyEnabled = (economy != null);
	}
	
	public void setupWorldGuard()
	{
		// If WorldGuard isn't detected
		if (getServer().getPluginManager().getPlugin("WorldGuard") == null)
		{
			// Tell the user the plugin won't be using WorldGuard
			log.info(String.format("[%s] Region functions disabled due to no WorldGuard dependency found!", getDescription().getName()));
		}
		// Get WorldGuard Plugin
		worldguard = (WorldGuardPlugin)getServer().getPluginManager().getPlugin("WorldGuard");
		// Tell the user worldguard functions are available
		log.info(String.format("[%s] WorldGuard Hooked!", getDescription().getName()));
		isWorldGuardEnabled = (worldguard != null);
	}

	@SuppressWarnings("deprecation")
	public void destroyGUIs() {
		// For each opened GUI
        for(String s : openGUIs) {
        	// Get who opened it
            if(Bukkit.getOfflinePlayer(s).isOnline()) {
            	// Close it and send a message
                Bukkit.getPlayer(s).closeInventory();
                Bukkit.getPlayer(s).sendMessage(langConfig.getConfig().getString("forceCloseGUI").replace("&","§"));
            }
        }
        // Then reset the list
        openGUIs.clear();
    }
	
	/*
	 *  Command handler
	 */
	@Override
    public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args) {
        // If the reload command has been typed
		if(cmd.getName().equalsIgnoreCase("spawnerchangergui")||cmd.getName().equalsIgnoreCase("scgui")) {
            // If the typer is a player
			if(sender instanceof Player) {
				// Check if he has the right permission
                if(sender.hasPermission("spawnerchangergui.reload")) {
                	// Reloading of config files
                    mainConfig.reloadConfig();
                    langConfig.reloadConfig();
                    // Tell the user the reload is completed
                    sender.sendMessage(langConfig.getConfig().getString("reloadMessage").replace("&","§"));
                } else {
                	// Tell the user he hasn't the right permissions
                    sender.sendMessage(langConfig.getConfig().getString("notEnoughPerm").replace("&","§"));
                }
            } else {
            	// The sender is the console
                reloadConfig();
                // Tell the console the reload is complete
                sender.sendMessage(langConfig.getConfig().getString("reloadMessage").replace("&","§"));
            }
            return true;
        }
        return false;
    }
	
	/*
	 *  Creates the GUI
	 */
	public void createGUI(CreatureSpawner spawner, Player player) {
		// Stop the spawning process of the spawner
		spawner.setDelay(spawner.getDelay()+99999);
        // Getting the entity type of the spawner
		SpawnTypes type = SpawnTypes.fromType(spawner.getSpawnedType());
		// Debugging
		//player.sendMessage(type.toString());
		// Creating the GUI
        GUI gui = new GUI("Spawner Type: " + type.getName(), 45, spawner, this);
        
        // Filling the GUI
        int j = 0;
        for(SpawnTypes e : SpawnTypes.values()) {
        	// If the player don't have the permission and the config want to remove eggs
            if(mainConfig.getConfig().getBoolean("Settings.RemoveNoAccessEggs") && !hasAccess(player, e)) continue;
            
            // Getting the price of the mob
            double price = mainConfig.getConfig().getDouble("MobPrices." + e.getName());
            String editLine = langConfig.getConfig().getString("setTo").replace("&","§") + " §a" + e.getDisplayname();
            String priceLine = price > 0.0 ? "§e" + price : langConfig.getConfig().getString("priceFree").replace("&","§");
            String accessLine = hasAccess(player, e) ? langConfig.getConfig().getString("yesAccess").replace("&","§") : langConfig.getConfig().getString("noAccess").replace("&","§");
            String idLine = "§7ID: "+String.valueOf(e.getId());
            
            // Editing the price line
            priceLine += (player.hasPermission("spawnerchangergui.eco.bypass." + e.getName().toLowerCase()) || player.hasPermission("spawnerchangergui.eco.bypass.*")) && price > 0.0 ? " §a§o("+ langConfig.getConfig().getString("freeForYou").replace("&","§") +")" : "";
            
            // Putting the item in the GUI
            if(isEconomyEnabled && mainConfig.getConfig().getBoolean("Settings.ShowCostInLore")) {
                if(mainConfig.getConfig().getBoolean("Settings.ShowAccessInLore")) {
                    gui.setSlot(j, e.getItem(), "§6" + e.getDisplayname(), editLine, langConfig.getConfig().getString("price").replace("&","§") + " " + priceLine, accessLine, idLine);
                } else {
                    gui.setSlot(j, e.getItem(), "§6" + e.getDisplayname(), editLine, langConfig.getConfig().getString("price").replace("&","§") + " " + priceLine, idLine);
                }
            } else {
                if(mainConfig.getConfig().getBoolean("Settings.ShowAccessInLore")) {
                    gui.setSlot(j, e.getItem(), "§6" + e.getDisplayname(), editLine, accessLine, idLine);
                } else {
                    gui.setSlot(j, e.getItem(), "§6" + e.getDisplayname(), editLine, idLine);
                }
            }
            j++;
        }
        
        // If balance is shown in the GUI
        if(getConfig().getBoolean("Settings.ShowBalanceIcon")) {
            String s;
            // If there is no economy hook
            if (isEconomyEnabled) 
            {
            	s = langConfig.getConfig().getString("yourBalance").replace("&","§") + " §e" + Math.round(economy.getBalance(Bukkit.getPlayer((player.getName()))) * 100.0) / 100.0;
            }
            else
            	s = "§cEconomy is not enabled!";
            // Setting of the correct item
            gui.setSlot(44, new ItemStack(Material.SKULL_ITEM, 1, (byte)3), "§b"+langConfig.getConfig().getString("balance").replace("&","§"), s);
        }
        // Show the GUI to the player
        gui.show(player);
        // Add the GUI to opened GUIs' list
        openGUIs.add(player.getName());
    }
	
	/*
	 *  Check if the player has access at the location
	 */
	public boolean canOpenAtLoc(Player player, Location loc) {
		// If the player is an Admin or there is not Hook
		if (player.isOp() || !isWorldGuardEnabled)
			return true;
		return worldguard.canBuild(player, loc);
    }
    
    public boolean hasAccess(Player player, SpawnTypes type) {
    	// Check if the player has the right permission
        return player.hasPermission("spawnerchangergui.edit.*") || player.hasPermission("spawnerchangergui.edit." + type.getName().toLowerCase());
    }
}
