package com.beardman.bukkit.exampleplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin{
	private FilterUI filterui;
	@Override
	public void onEnable(){
	    getServer().getPluginManager().registerEvents(new MyListener(), this);
	    getServer().getPluginManager().registerEvents(filterui = new FilterUI(), this);
		getLogger().info("enabled");
	}
	@Override
	public void onDisable(){
		getLogger().info("disabled");
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("itemfilter") && sender instanceof Player){
			filterui.openInventory((Player)sender);
		}
		if (cmd.getName().equalsIgnoreCase("ceiling")) {
			if (!(sender instanceof Player)){
				sender.sendMessage("You must be a player.");
				return false;
			}
			Player player = (Player) sender;
		    Location loc = player.getLocation();
		    if (player.getWorld().getHighestBlockAt(loc).getLocation().getY()==loc.getY()){
		    	sender.sendMessage("There is no ceiling above you.");
		    	return false;
		    }
		    loc.add(0,2,0);
		    Block b = loc.getBlock();
		    while(b.getType()==Material.AIR){
		    	b = loc.add(0, 1, 0).getBlock();
		    }
		    if (loc.add(0, -3, 0).getY()>player.getLocation().getY()){
		    	loc.getBlock().setType(Material.GLASS);		
		    	loc.add(0,1,0);
		    	player.teleport(loc);
		    }
		    else {
		    	sender.sendMessage("Ceiling is too low.");
		    	return false;
		    }
			return true;
		}
		return false; 
	}
}
