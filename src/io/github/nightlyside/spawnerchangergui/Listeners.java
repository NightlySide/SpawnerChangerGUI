package io.github.nightlyside.spawnerchangergui;

import java.util.Arrays;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Listeners implements Listener{

	private Main context;
	
	/*
	 *  Listeners class where all actions are handled
	 */
	public Listeners(Main context)
	{
		this.context = context;
	}
	
	@EventHandler
	@SuppressWarnings("deprecation")
    public void onPlaceSpawner(BlockPlaceEvent event)
    {
    	Block block = event.getBlockPlaced();
        Player player = event.getPlayer();
		ItemStack itemblock = player.getItemInHand();
        
        // Check if everything is fine
    	if (block == null || block.getType() != Material.MOB_SPAWNER)
    		return;
    	if (!context.canOpenAtLoc(player, block.getLocation()))
    		return;
    	
    	CreatureSpawner spawner = (CreatureSpawner) block.getState();
    	if (itemblock.getItemMeta().hasLore())
    	{
    		int numberoflines = itemblock.getItemMeta().getLore().size();
    		int id = Integer.parseInt(itemblock.getItemMeta().getLore().get(numberoflines-1).split(" ")[1]);
    		//player.sendMessage(String.valueOf(id)); // DEBUG
    		SpawnTypes mobtype = SpawnTypes.fromID(id);
    		spawner.setSpawnedType(mobtype.getType());
    	}
    }
	
	@EventHandler
	public void onBreakSpawner(BlockBreakEvent event) 
	{
    	Block block = event.getBlock();
    	Player player = event.getPlayer();
    	
    	// Check if everything is fine
    	if (block == null || block.getType() != Material.MOB_SPAWNER)
    		return;
    	if (player.getGameMode() == GameMode.CREATIVE)
    		return;
    	if (!player.hasPermission("spawnerchangergui.recoveronbreak"))
    		return;
    	
    	// Setting dropped bloc caracteristics
		ItemStack spawner = new ItemStack(Material.MOB_SPAWNER, 1);
		CreatureSpawner spawnerBlock = (CreatureSpawner) block.getState();
		SpawnTypes mobtype = SpawnTypes.fromType(spawnerBlock.getSpawnedType());
		ItemMeta meta = spawner.getItemMeta();
		
		// Setting name and lore
		meta.setDisplayName("§6Mob spawner : §e"+mobtype.getName());
		meta.setLore(Arrays.asList("§2Spawns : "+mobtype.getName(), "§2Right-click to modify", "§7ID: "+String.valueOf(mobtype.getId())));
		spawner.setItemMeta(meta);
		//spawner.setDurability((short) mobtype.getId());
		
		player.getWorld().dropItem(block.getLocation(), spawner);
				
		// If the GUI is still open destroy it
		if (Main.openGUIs.remove(player.getName()))
			player.closeInventory();
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		// Check if everything is fine
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.getPlayer().hasPermission("spawnerchangergui.openonclick"))
			return;
		
		Block block = event.getClickedBlock();
    	Player player = event.getPlayer();
    	
    	// Check if the block is legit
    	if (block == null || block.getType() != Material.MOB_SPAWNER)
    		return;
    	// Check if the GUI is not already open
    	if (Main.openGUIs.contains(player.getName()))
    		return;
    	
    	Main.openGUIs.add(player.getName());
    	context.createGUI((CreatureSpawner) block.getState(), player);
	}
}
