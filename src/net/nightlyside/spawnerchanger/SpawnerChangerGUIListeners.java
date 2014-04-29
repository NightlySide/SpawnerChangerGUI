package net.nightlyside.spawnerchanger;

import net.nightlyside.spawnerchanger.SpawnerChangerGUI.Spawnable;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnerChangerGUIListeners implements Listener {
	private SpawnerChangerGUI plugin = null;
	
	public SpawnerChangerGUIListeners(SpawnerChangerGUI plugin)
	{
		this.plugin = plugin;
	}
	
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void handleInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().hasPermission("spawnerchangergui.openonclick")) {
            Block b = event.getClickedBlock();
            Player p = event.getPlayer();
            
            if(b != null && b.getType() == Material.MOB_SPAWNER && p.hasPermission("spawnerchangergui.open")) {
                event.setCancelled(true);
                if(plugin.worldguard != null)
                {
	                if(!plugin.worldguardhook.canOpenAtLoc(p, b.getLocation())) {
	                    p.sendMessage(plugin.getLangConfig().getString("notEnoughPerm").replace("&","§"));
	                    return;
	                }
                }
                if(plugin.getConfig().getBoolean("Settings.SneakToOpen") && p.isSneaking()) {
                	plugin.openGUI((CreatureSpawner)b.getState(), p, false);
                } else if(plugin.getConfig().getBoolean("Settings.SneakToOpen") == false && !p.isSneaking()) {
                	plugin.openGUI((CreatureSpawner)b.getState(), p, false);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlaceSpawner(BlockPlaceEvent event)
    {
    	Block b = event.getBlockPlaced();
        Player p = event.getPlayer();
        if(b != null && b.getType() == Material.MOB_SPAWNER && p.hasPermission("spawnerchangergui.open"))
        {
        	plugin.openGUI((CreatureSpawner)b.getState(), p, true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
	public void onBreakSpawner(BlockBreakEvent event) 
	{
    	Block b = event.getBlock();
    	Player p = event.getPlayer();
		if(b != null && b.getType() == Material.MOB_SPAWNER && p.getGameMode()!= GameMode.CREATIVE)
		{
			if(p.hasPermission("spawnerchangergui.recoveronbreak"))
			{
				Location blockLoc = event.getBlock().getLocation();
				p.getWorld().dropItem(blockLoc, new ItemStack(Material.MOB_SPAWNER, 1));
			}
		}
	}
    
    @EventHandler
    public void handleClick(SpawnerChangerClickEvent event) {
        Player p = event.getPlayer();
        CreatureSpawner spawner = event.getSpawner();
        
        if(spawner.getBlock().getType() != Material.MOB_SPAWNER) {
            p.sendMessage(plugin.getLangConfig().getString("blockNotValidAnymore").replace("&","§") + " (§7" + spawner.getBlock().getType().name().toLowerCase() + "§c)");
            return;
        }
        String clicked = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName().toLowerCase());
        Spawnable current = Spawnable.from(spawner.getSpawnedType());

        if(clicked.equalsIgnoreCase("balance")) {
            event.setWillClose(false);
        } else {
            for(Spawnable e : Spawnable.values()) {
                if(clicked.equalsIgnoreCase(e.getName().toLowerCase())) {
                    p.playSound(p.getLocation(), Sound.CLICK, 1, 1);

                    if(!plugin.noAccess(p, e)) {
                        if(plugin.econ != null && !p.hasPermission("spawnerchangergui.eco.bypass.*")) {
                            double price = p.hasPermission("spawnerchangergui.eco.bypass." + clicked) ? 0.0 : plugin.getPrice(e);

                            if(price > 0.0) {
                                if(plugin.econ.has(p.getName(), price)) {
                                	p.sendMessage(plugin.getLangConfig().getString("takeMoney").replace("&","§").replace("%money%", String.valueOf(price)));
                                    plugin.econ.withdrawPlayer(p.getName(), price);
                                } else {
                                	p.sendMessage(plugin.getLangConfig().getString("notEnoughMoney").replace("&","§").replace("%money%", String.valueOf(price)));
                                    return;
                                }
                            }
                        }
                        spawner.setSpawnedType(e.getType());
                        spawner.update(true);
                        p.sendMessage(plugin.getLangConfig().getString("changeType").replace("&","§").replace("%oldmob%", current.getName().toLowerCase()).replace("%newmob%", clicked));
                        return;
                    }
                    p.sendMessage(plugin.getLangConfig().getString("notEnoguhPerm").replace("&","§"));
                    break;
                }
            }
        }
    }
}