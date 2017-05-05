package io.github.nightlyside.spawnerchangergui;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class WorldGuardHook {
	private SpawnerChangerGUI plugin;
	
	public WorldGuardHook(SpawnerChangerGUI plugin)
	{
		this.plugin = plugin;
	}
	
	public boolean canOpenAtLoc(Player p, Location loc) {
        if(!p.isOp()) {
            RegionManager r = plugin.worldguard.getRegionManager(loc.getWorld());
            
            if(r != null) {
                ApplicableRegionSet regions = r.getApplicableRegions(loc);
                BukkitPlayer lp = new BukkitPlayer(plugin.worldguard, p);
                
                if(!regions.isOwnerOfAll(lp) && !regions.isMemberOfAll(lp)) {
                    return false;
                }
            }
        }
        return true;
    }
}
