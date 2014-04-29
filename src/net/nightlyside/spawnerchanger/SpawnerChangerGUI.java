package net.nightlyside.spawnerchanger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class SpawnerChangerGUI extends JavaPlugin {
	protected Economy econ = null;
	protected static final Logger log = Logger.getLogger("Minecraft");
	public static final Set<String> openGUIs = new HashSet<>();
	protected WorldGuardPlugin worldguard = null;
	protected WorldGuardHook worldguardhook;
	protected boolean isEconEnabled = true;
	protected FileConfiguration langConfig;
	protected File langConfigFile;
	
	@Override
    public void onDisable() {
		this.saveLangConfig();
		log.log(Level.INFO, "[SpawnerChangerGUI] Version {0} disabled.", getDescription().getVersion());
    }
	
	@Override
	public void onEnable()
	{
		this.saveDefaultConfig();
		this.saveDefaultLangConfig();
		
	    if(!setupEconomy() /*|| !config.isEconActivated()*/)
	    	this.isEconEnabled = false;
	    setupWorldGuard();
	    getServer().getPluginManager().registerEvents(new SpawnerChangerGUIListeners(this), this);
	    log.info(String.format("[%s] - Enabled!", getDescription().getName()));
	}
	
	private boolean setupEconomy()
	{
		if (getServer().getPluginManager().getPlugin("Vault") == null)
		{
			log.info(String.format("[%s] - Economy functions disabled due to no Vault dependency found!", getDescription().getName()));
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null)
		{
			log.info(String.format("[%s] - Economy functions disabled due to no Economy plugin found!", getDescription().getName()));
			return false;
		}
		econ = rsp.getProvider();
		Logger.getLogger("Minecraft").log(Level.INFO, "[SpawnerGUI] Vault hooked.");
		return econ != null;
	}
	
	private boolean setupWorldGuard()
	{
		if (getServer().getPluginManager().getPlugin("WorldGuard") == null)
		{
			log.info(String.format("[%s] - Region functions disabled due to no WorldGuard dependency found!", getDescription().getName()));
			return false;
		}
		worldguardhook = new WorldGuardHook(this);
		worldguard = (WorldGuardPlugin)getServer().getPluginManager().getPlugin("WorldGuard");
        Logger.getLogger("Minecraft").log(Level.INFO, "[SpawnerChangerGUI] WorldGuard hooked.");
		return worldguard != null;
	}
	
	public void reloadLangConfig() {
	    if (langConfigFile == null) {
	    	langConfigFile = new File(getDataFolder(), "lang.yml");
	    }
	    langConfig = YamlConfiguration.loadConfiguration(langConfigFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = this.getResource("lang.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        langConfig.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getLangConfig() {
	    if (langConfig == null) {
	        reloadLangConfig();
	    }
	    return langConfig;
	}
	
	public void saveLangConfig() {
	    if (langConfig == null || langConfigFile == null) {
	        return;
	    }
	    try {
	        getLangConfig().save(langConfigFile);
	    } catch (IOException ex) {
	        getLogger().log(Level.SEVERE, "Could not save config to " + langConfigFile, ex);
	    }
	}
	
	public void saveDefaultLangConfig() {
	    if (langConfigFile == null) {
	    	langConfigFile = new File(getDataFolder(), "lang.yml");
	    }
	    if (!langConfigFile.exists()) {            
	         this.saveResource("lang.yml", false);
	     }
	}
	
	@Override
    public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(cmd.getName().equalsIgnoreCase("spawnerchangergui")||cmd.getName().equalsIgnoreCase("scgui")) {
            if(sender instanceof Player) {
                if(sender.hasPermission("spawnerchangergui.reload")) {
                    this.reloadConfig();
                    this.reloadLangConfig();
                    sender.sendMessage(this.getLangConfig().getString("reloadMessage"));
                } else {
                    sender.sendMessage(this.getLangConfig().getString("notEnoughPerm"));
                }
            } else {
                reloadConfig();
                sender.sendMessage(this.getLangConfig().getString("reloadMessage"));
            }
            return true;
        }
        return false;
    }
	
	public void openGUI(CreatureSpawner spawner, Player p, boolean isBlockPlaced) {
        Spawnable type = Spawnable.from(spawner.getSpawnedType());
        GUIHandler gui = new GUIHandler("Spawner Type: " + type.getName(), 45, spawner, isBlockPlaced);
        int j = 0;
        
        for(Spawnable e : Spawnable.values()) {
            if(getConfig().getBoolean("Settings.RemoveNoAccessEggs") && noAccess(p, e)) continue;
            double price = getPrice(e);
            String editLine = this.getLangConfig().getString("setTo") + " §a" + e.getName();
            String priceLine = price > 0.0 ? "§e" + price : this.getLangConfig().getString("priceFree");
            String accessLine = noAccess(p, e) ? this.getLangConfig().getString("noAccess") : this.getLangConfig().getString("yesAccess");
            
            priceLine += (p.hasPermission("spawnerchangergui.eco.bypass." + e.getName().toLowerCase()) || p.hasPermission("spawnerchangergui.eco.bypass.*")) && price > 0.0 ? " §a§o("+ this.getLangConfig().getString("freeForYou") +")" : "";
            
            if(econ != null && getConfig().getBoolean("Settings.ShowCostInLore")) {
                if(getConfig().getBoolean("Settings.ShowAccessInLore")) {
                    gui.setItem(j, e.getSpawnEgg(), "§6" + e.getName(), editLine, this.getLangConfig().getString("price") + " " + priceLine, accessLine);
                } else {
                    gui.setItem(j, e.getSpawnEgg(), "§6" + e.getName(), editLine, this.getLangConfig().getString("price") + " " + priceLine);
                }
            } else {
                if(getConfig().getBoolean("Settings.ShowAccessInLore")) {
                    gui.setItem(j, e.getSpawnEgg(), "§6" + e.getName(), editLine, accessLine);
                } else {
                    gui.setItem(j, e.getSpawnEgg(), "§6" + e.getName(), editLine);
                }
            }
            j++;
        }
        
        if(getConfig().getBoolean("Settings.ShowBalanceIcon")) {
            String s = econ != null ? this.getLangConfig().getString("yourBalance") + " §e" + Math.round(econ.getBalance(p.getName()) * 100.0) / 100.0 : "§cEconomy is not enabled!";
            gui.setItem(44, new ItemStack(Material.SKULL_ITEM, 1, (byte)3), "§b"+this.getLangConfig().getString("balance"), s);
        }
        gui.open(p);
        openGUIs.add(p.getName());
    }
	
	public double getPrice(Spawnable type) {
        return getConfig().getDouble("MobPrices." + type.getName());
    }
    
    public boolean noAccess(Player p, Spawnable type) {
        return !p.hasPermission("spawnerchangergui.edit.*") && !p.hasPermission("spawnerchangergui.edit." + type.getName().toLowerCase());
    }
    
    @SuppressWarnings("deprecation")
	public static void eatGUIs() {
        for(String s : openGUIs) {
            if(Bukkit.getOfflinePlayer(s).isOnline()) {
                Bukkit.getPlayerExact(s).getOpenInventory().close();
                Bukkit.getPlayerExact(s).sendMessage(((SpawnerChangerGUI) Bukkit.getPluginManager().getPlugin("SpawnerChangerGUI")).getLangConfig().getString("forceCloseGUI"));
            }
        }
    }
    
    public enum Spawnable {
        CREEPER(EntityType.CREEPER, "Creeper", 383, (byte)50),
        SKELETON(EntityType.SKELETON, "Skeleton", 383, (byte)51),
        SPIDER(EntityType.SPIDER, "Spider", 383, (byte)52),
        GIANT(EntityType.GIANT, "Giant", 383, (byte)54),
        ZOMBIE(EntityType.ZOMBIE, "Zombie", 383, (byte)54),
        SLIME(EntityType.SLIME, "Slime", 383, (byte)55),
        GHAST(EntityType.GHAST, "Ghast", 385, (byte)56),
        PIG_ZOMBIE(EntityType.PIG_ZOMBIE, "PigZombie", 383, (byte)57),
        ENDERMAN(EntityType.ENDERMAN, "Enderman", 383, (byte)58),
        CAVE_SPIDER(EntityType.CAVE_SPIDER, "CaveSpider", 383, (byte)59),
        SILVERFISH(EntityType.SILVERFISH, "Silverfish", 383, (byte)60),
        BLAZE(EntityType.BLAZE, "Blaze", 383, (byte)61),
        MAGMA_CUBE(EntityType.MAGMA_CUBE, "MagmaCube", 383, (byte)62),
        ENDER_DRAGON(EntityType.ENDER_DRAGON, "EnderDragon", 122, (byte)0),
        WITHER(EntityType.WITHER, "Wither", 397, (byte)1),
        BAT(EntityType.BAT, "Bat", 383, (byte)65),
        WITCH(EntityType.WITCH, "Witch", 383, (byte)66),
        PIG(EntityType.PIG, "Pig", 383, (byte)90),
        SHEEP(EntityType.SHEEP, "Sheep", 383, (byte)91),
        COW(EntityType.COW, "Cow", 383, (byte)92),
        CHICKEN(EntityType.CHICKEN, "Chicken", 383, (byte)93),
        SQUID(EntityType.SQUID, "Squid", 383, (byte)94),
        WOLF(EntityType.WOLF, "Wolf", 383, (byte)95),
        MUSHROOM_COW(EntityType.MUSHROOM_COW, "Mooshroom", 383, (byte)96),
        SNOWMAN(EntityType.SNOWMAN, "SnowGolem", 332, (byte)0),
        OCELOT(EntityType.OCELOT, "Ocelot", 383, (byte)98),
        IRON_GOLEM(EntityType.IRON_GOLEM, "IronGolem", 265, (byte)0),
        HORSE(EntityType.HORSE, "Horse", 383, (byte)100),
        VILLAGER(EntityType.VILLAGER, "Villager", 383, (byte)120),
        BOAT(EntityType.BOAT, "Boat", 333, (byte)0),
        MINECART(EntityType.MINECART, "Minecart", 328, (byte)0),
        MINECART_CHEST(EntityType.MINECART_CHEST, "ChestMinecart", 342, (byte) 0),
        MINECART_FURNACE(EntityType.MINECART_FURNACE, "FurnaceMinecart", 343, (byte) 0),
        MINECART_TNT(EntityType.MINECART_TNT, "TntMinecart", 407, (byte) 0),
        MINECART_HOPPER(EntityType.MINECART_HOPPER, "HopperMinecart", 408, (byte) 0),
        MINECART_MOB_SPAWNER(EntityType.MINECART_MOB_SPAWNER, "MobSpawnerMinecart", 328, (byte) 0),
        ENDERCRYSTAL(EntityType.ENDER_CRYSTAL, "EnderCrystal", 368, (byte) 0),
        EXPERIENCEORB(EntityType.EXPERIENCE_ORB, "ExperienceOrb", 384, (byte) 0);
        
        private final EntityType type;
        private final String name;
        private final int item;
        private final byte data;
        
        private Spawnable(EntityType type, String name, int itemId, byte data) {
            this.type = type;
            this.name = name;
            this.item = itemId;
            this.data = data;
        }
        
        public String getName() {
            return name;
        }
        
        public byte getData() {
            return data;
        }
        
        public int getItemId() {
            return item;
        }
        
        public EntityType getType() {
            return type;
        }
        
        @SuppressWarnings("deprecation")
		public ItemStack getSpawnEgg() {
            return new ItemStack(item, 1, data);
        }
        
        public static Spawnable from(EntityType type) {
            for(Spawnable e : values()) {
                if(e.getType() == type) {
                    return e;
                }
            }
            return null;
        }
    }
}
