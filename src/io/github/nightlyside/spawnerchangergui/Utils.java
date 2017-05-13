package io.github.nightlyside.spawnerchangergui;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Utils {

	/*
	 *  Thanks to superpeanut911 for this function
	 *  link to post : https://www.spigotmc.org/threads/remove-1-item-from-the-inventory.15034/
	 */
	public static boolean consumeItem(Player player, int count, Material mat) {
	    Map<Integer, ? extends ItemStack> ammo = player.getInventory().all(mat);

	    int found = 0;
	    for (ItemStack stack : ammo.values())
	        found += stack.getAmount();
	    if (count > found)
	        return false;

	    for (Integer index : ammo.keySet()) {
	        ItemStack stack = ammo.get(index);

	        int removed = Math.min(count, stack.getAmount());
	        count -= removed;

	        if (stack.getAmount() == removed)
	            player.getInventory().setItem(index, null);
	        else
	            stack.setAmount(stack.getAmount() - removed);

	        if (count <= 0)
	            break;
	    }

	    player.updateInventory();
	    return true;
	}
	
}
