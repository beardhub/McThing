package com.beardman.bukkit.exampleplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class MyListener implements Listener {
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent ev) {
		Block block = ev.getClickedBlock();
		if(ev.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(block.getType() == Material.WALL_SIGN) {
				Sign sign = (Sign) block.getState();
				LiftSign lift = LiftSign.parseLiftSign(sign);
				if (lift == null){	// not a valid lift sign
					return;
				}
				Player player = ev.getPlayer();
				Location destination = lift.getDestination(player, 50);
				if (destination == null){	// no valid destination within 50 blocks
					player.sendMessage("No desintation found.");
					return;
				}
				if (destination.getBlock().getType() == Material.AIR 
						&& destination.clone().add(0, 1, 0).getBlock().getType() == Material.AIR){
					player.teleport(destination);
					player.sendMessage("You took the lift "+lift.getLiftDir().toLowerCase()+".");
				}
			}
		}
	}
}