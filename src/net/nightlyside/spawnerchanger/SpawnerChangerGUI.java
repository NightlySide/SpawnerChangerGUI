package net.nightlyside.spawnerchanger;

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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnerChangerGUI extends JavaPlugin {
	protected Economy econ = null;
	protected static final Logger log = Logger.getLogger("Minecraft");
	public static final Set<String> openGUIs = new HashSet<>();
	protected boolean isEconEnabled = true;
	
	@Override
    public void onDisable() {
		log.log(Level.INFO, "[SpawnerChangerGUI] Version {0} disabled.", getDescription().getVersion());
    }
	
	@Override
	public void onEnable()
	{
		this.saveDefaultConfig();
	    if(!setupEconomy() /*|| !config.isEconActivated()*/)
	    {
	    	this.isEconEnabled = false;
	    }
	    getServer().getPluginManager().registerEvents(new SpawnerChangerGUIListeners(this), this);
	    log.info(String.format("[%s] - Enabled!", getDescription().getName()));
	}
	
	private boolean setupEconomy()
	{
		if (getServer().getPluginManager().getPlugin("Vault") == null)
		{
			log.severe(String.format("[%s] - Economy functions disabled due to no Vault dependency found!", getDescription().getName()));
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null)
		{
			log.severe(String.format("[%s] - Economy functions disabled due to no Economy plugin found!", getDescription().getName()));
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
	
	@Override
    public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(cmd.getName().equalsIgnoreCase("spawnerchangergui")||cmd.getName().equalsIgnoreCase("scgui")) {
            if(sender instanceof Player) {
                if(sender.hasPermission("spawnerchangergui.reload")) {
                    this.reloadConfig();
                    sender.sendMessage("§aSpawnerChangerGUI reloaded.");
                } else {
                    sender.sendMessage("§cYou do not have permission to do this!");
                }
            } else {
                reloadConfig();
                sender.sendMessage("SpawnerChangerGUI reloaded.");
            }
            return true;
        }
        return false;
    }
	
	public void openGUI(CreatureSpawner spawner, Player p) {
        Spawnable type = Spawnable.from(spawner.getSpawnedType());
        GUIHandler gui = new GUIHandler("Spawner Type: " + type.getName(), 45, spawner);
        int j = 0;
        
        for(Spawnable e : Spawnable.values()) {
            if(getConfig().getBoolean("Settings.RemoveNoAccessEggs") && noAccess(p, e)) continue;
            double price = getPrice(e);
            String editLine = "§7Set to: §a" + e.getName();
            String priceLine = price > 0.0 ? "§e" + price : "§aFree";
            String accessLine = noAccess(p, e) ? "§7Access: §cNo" : "§7Access: §aYes";
            
            priceLine += (p.hasPermission("spawnerchangergui.eco.bypass." + e.getName().toLowerCase()) || p.hasPermission("spawnerchangergui.eco.bypass.*")) && price > 0.0 ? " §a§o(Free for you)" : "";
            
            if(econ != null && getConfig().getBoolean("Settings.ShowCostInLore")) {
                if(getConfig().getBoolean("Settings.ShowAccessInLore")) {
                    gui.setItem(j, e.getSpawnEgg(), "§6" + e.getName(), editLine, "§7Price: " + priceLine, accessLine);
                } else {
                    gui.setItem(j, e.getSpawnEgg(), "§6" + e.getName(), editLine, "§7Price: " + priceLine);
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
            String s = econ != null ? "§aYour Balance: §e" + Math.round(econ.getBalance(p.getName()) * 100.0) / 100.0 : "§cEconomy is not enabled!";
            gui.setItem(44, new ItemStack(Material.SKULL_ITEM, 1, (byte)3), "§bBalance", s);
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
                Bukkit.getPlayerExact(s).sendMessage("§cThe GUI was forced to close.");
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
