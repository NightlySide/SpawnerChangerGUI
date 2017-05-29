package io.github.nightlyside.spawnerchangergui;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUI implements Listener {
	
	private Main			context;
	
	public String 			name;
	public int 				size;
	public ItemStack[]		items;
	public CreatureSpawner 	spawner;
	
	public boolean			closeGUI = true;
	
	/*
	 * GUI creation : name, size, spawner(block)
	 */
	public GUI(String name, int size, CreatureSpawner spawner, Main context) {
		// Set GUI's variables
        this.name = name;
        this.size = size;
        this.items = new ItemStack[size];
        this.spawner = spawner;
        this.context = context;
        
        Bukkit.getPluginManager().registerEvents(this, context);
    }
	
	/*
	 *  Set the slot in the GUI (position, icon, title, lore (several lines) )
	 */
	public void setSlot(int pos, ItemStack item, String title, String... lore)
	{
		// Setting item meta for customization
		ItemMeta im = item.getItemMeta();
		// Setting name, lore
        im.setDisplayName(title);
        im.setLore(Arrays.asList(lore));
        // Putting back settings
        item.setItemMeta(im);
        // Updating the item at position
        items[pos] = item;
	}
	
	/*
	 *  Show the GUI to the player
	 */
	public void show(Player player)
	{
		// Creating a custom inventory for the GUI
		Inventory inv = Bukkit.createInventory(player, size, name);
		// Putting items in the GUI
		inv.setContents(items);
		// Open the GUI to the player
        player.openInventory(inv);
	}
	
	/*
	 *  Destroy the GUI
	 */
	public void destroy()
	{
		// Reset the GUI
		this.items = null;
        this.name = null;
        this.spawner = null;
        
        // Unregistering all listeners
        HandlerList.unregisterAll(this);
	}
	
	/*
	 *  Click item in GUI handler 
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(InventoryClickEvent event)
	{
		// Check if the GUI is the right inventory
		if (event.getInventory().getName().equals(name))
		{
			// Cancel the event
			event.setCancelled(true);
		
			Player player = (Player) event.getWhoClicked();
			
			// Prevent item stealing
			if (event.isShiftClick()) return;
			if (event.isRightClick()) return;
			
			// Get the clicked slot
			int slot = event.getRawSlot();
			
			// If the player clicks the economy icon
			if (slot == size-1)
			{
				return;
			}
			
			// Check if the slot exists and is not null
			if (slot >= 0 && slot < size-1 && items[slot] != null && event.getInventory().getItem(slot).getItemMeta().hasLore())
			{
				// if the player needs an item to change the type
				if (context.mainConfig.getConfig().getBoolean("Settings.ItemRequirement.enabled"))
				{
					// Get the item's material and amount
					Material reqItem = Material.getMaterial(context.mainConfig.getConfig().getInt("Settings.ItemRequirement.item_id"));
					int amount = context.mainConfig.getConfig().getInt("Settings.ItemRequirement.amount");
					
					// Check and remove the required item
					boolean hasItem = Utils.consumeItem(player, amount, reqItem);
					if (!hasItem)
					{
						// If the player doesn't have the item
						player.sendMessage(context.langConfig.getConfig().getString("noeggininv").replace("&", "§"));
						return;
					}
				}
				
				// Get the mobtype from the item
				int numberoflines = event.getInventory().getItem(slot).getItemMeta().getLore().size();
				String mobid = event.getInventory().getItem(slot).getItemMeta().getLore().get(numberoflines-1).split(" ")[1];
				SpawnTypes mobtype = SpawnTypes.fromID(Integer.valueOf(mobid));
				
				if (mobtype == null)
				{
					player.sendMessage(context.langConfig.getConfig().getString("mobnotfound").replace("&", "§"));
					return;
				}
				
				// Change item in spawner (to be done)
				player.sendMessage(context.langConfig.getConfig().getString("changeType")
						.replace("&", "§")
						.replace("%oldmob%", SpawnTypes.fromType(spawner.getSpawnedType()).getDisplayname())
						.replace("%newmob%", mobtype.getDisplayname()));
				spawner.setSpawnedType(mobtype.getType());
				spawner.update();
				spawner.setDelay(20);
				
				// Close the GUI
				if (closeGUI)
				{
					event.getWhoClicked().closeInventory();
				}
			}
		}
	}
	
	/*
	 *  Closing GUI handler
	 */
	@EventHandler
	public void onClose(InventoryCloseEvent event)
	{
		// Remove this GUI from the opened list
		Main.openGUIs.remove(event.getPlayer().getName());
		// Destroy the GUI
		destroy();
	}
	
	
}
