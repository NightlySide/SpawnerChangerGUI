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
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block b = event.getClickedBlock();
            Player p = event.getPlayer();
            
            if(b != null && b.getType() == Material.MOB_SPAWNER && p.hasPermission("spawnerchangergui.open")) {
                event.setCancelled(true);
               
                if(plugin.getConfig().getBoolean("Settings.SneakToOpen") && p.isSneaking()) {
                	plugin.openGUI((CreatureSpawner)b.getState(), p);
                } else if(plugin.getConfig().getBoolean("Settings.SneakToOpen") == false && !p.isSneaking()) {
                	plugin.openGUI((CreatureSpawner)b.getState(), p);
                }
            }
        }
    }
    
    @EventHandler
    public void handleClick(SpawnerChangerClickEvent event) {
        Player p = event.getPlayer();
        CreatureSpawner spawner = event.getSpawner();
        
        if(spawner.getBlock().getType() != Material.MOB_SPAWNER) {
            p.sendMessage("§cThe spawner block is no longer valid! (§7" + spawner.getBlock().getType().name().toLowerCase() + "§c)");
            return;
        }
        String clicked = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName().toLowerCase());
        Spawnable current = Spawnable.from(spawner.getSpawnedType());

        if(clicked.equalsIgnoreCase("balance")) {
            event.setWillClose(false);
        } else {
            for(Spawnable e : Spawnable.values()) {
                if(clicked.equalsIgnoreCase(e.getName().toLowerCase())) {
                    p.playSound(p.getLocation(), Sound.NOTE_PLING, 1, 1);

                    if(!plugin.noAccess(p, e)) {
                        if(plugin.econ != null && !p.hasPermission("spawnerchangergui.eco.bypass.*")) {
                            double price = p.hasPermission("spawnerchangergui.eco.bypass." + clicked) ? 0.0 : plugin.getPrice(e);

                            if(price > 0.0) {
                                if(plugin.econ.has(p.getName(), price)) {
                                    p.sendMessage("§7Charged §f" + price + " §7of your balance.");
                                    plugin.econ.withdrawPlayer(p.getName(), price);
                                } else {
                                    p.sendMessage("§cYou need at least §7" + price + " §cin balance to do this!");
                                    return;
                                }
                            }
                        }
                        spawner.setSpawnedType(e.getType());
                        spawner.update(true);
                        p.sendMessage("§9Spawner type changed from §7" + current.getName().toLowerCase() + " §9to §7" + clicked + "§9!");
                        return;
                    }
                    p.sendMessage("§cYou are not allowed to change to that type!");
                    break;
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) 
	{
		if(event.getBlock().getType() == Material.MOB_SPAWNER)
		{
			if(event.getPlayer().getGameMode()!= GameMode.CREATIVE)
			{
				if(event.getPlayer().hasPermission("spawnerchangergui.recoveronbreak"))
				{
					Location blockLoc = event.getBlock().getLocation();
					event.getPlayer().getWorld().dropItem(blockLoc, new ItemStack(Material.MOB_SPAWNER, 1));
			
				}
			}
		}
	}
}