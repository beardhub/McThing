package com.beardman.bukkit.exampleplugin;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

public class FilterUI implements Listener {
	private final Inventory inv;
	private ItemStack filterTypeNever, filterTypeAlways;
	private ArrayList<ItemStack> neverPickup, alwaysPickup;
	private boolean neverMode;

	public FilterUI() {
		inv = Bukkit.createInventory(null, 54, "Item Filter");
		initializeItems();
		neverMode = true;
		neverPickup = new ArrayList<ItemStack>();
		alwaysPickup = new ArrayList<ItemStack>();
	}

	private void initializeItems() {
		filterTypeNever = createGuiItem("Filter Type", new ArrayList<String>(Arrays.asList("Never pick up mode.","Click to switch modes.")), new Wool(DyeColor.RED).toItemStack(1));
		filterTypeAlways = createGuiItem("Filter Type", new ArrayList<String>(Arrays.asList("Always pick up mode.","Click to switch modes.")), new Wool(DyeColor.LIME).toItemStack(1));
		inv.setItem(49, filterTypeNever);
	}
	private ItemStack createGuiItem(String name, ArrayList<String> desc, ItemStack i){
		ItemMeta iMeta = i.getItemMeta();
		iMeta.setDisplayName(name);
		iMeta.setLore(desc);
		i.setItemMeta(iMeta);
		return i;
	}
	public void openInventory(Player p) {
		p.openInventory(inv);
		return;
	}
	private void switchFilterMode(Player p){
		ItemStack[] newItems;
		if (inv.getItem(49).equals(filterTypeNever)){
			inv.setItem(49, filterTypeAlways);
			neverMode = false;
			newItems = alwaysPickup.toArray(new ItemStack[0]);
		} else {
			inv.setItem(49, filterTypeNever);
			neverMode = true;
			newItems = neverPickup.toArray(new ItemStack[0]);
		}
		for (int i = 0; i < 45; i++){
			if (i < newItems.length){
				inv.setItem(i, newItems[i]);
			} else {
				inv.setItem(i, new ItemStack(Material.AIR));
			}
		}
	}
	public boolean shouldPickup(ItemStack iStack){
		if (neverMode){
			for (ItemStack i : neverPickup)
				if (i.isSimilar(iStack))
					return false;
			return true;
		} else {
			for (ItemStack i : alwaysPickup)
				if (i.isSimilar(iStack))
					return true;
			return false;
		}
	}
    @EventHandler
    public void OnPlayerPickup(EntityPickupItemEvent event){
    	if (!shouldPickup(event.getItem().getItemStack())){
    		event.setCancelled(true);
    		return;
    	}
    }
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		
		Player p = (Player) e.getWhoClicked();
		ItemStack clickedItem = e.getCurrentItem();
		if (clickedItem == null || !e.getInventory().getName().equals(inv.getName())){// || !clickedItem.hasItemMeta()) {
			return;
		}
		ItemMeta meta = clickedItem.getItemMeta();
		int rawSlot = e.getRawSlot(),
			slot = e.getSlot();
		
		e.setCancelled(true);
		
		if (rawSlot == slot){ // in gui
			if (slot < 45){ // in filter list
				if (neverMode){
					for (int i = 0; i < neverPickup.size(); i++)
						if (neverPickup.get(i).isSimilar(clickedItem))
							neverPickup.remove(i--);
				} else {
					for (int i = 0; i < alwaysPickup.size(); i++)
						if (alwaysPickup.get(i).isSimilar(clickedItem))
							alwaysPickup.remove(i--);
				}
				inv.setItem(slot, new ItemStack(Material.AIR));
			} else if (slot <= 53){ // in gui menu
				if (meta.getDisplayName().equals("Filter Type")) {
					switchFilterMode(p);
					return;
				}
			}
			
		} else if (rawSlot >= 54 && rawSlot <= 89){ // in player inventory
			ItemStack i = clickedItem.clone();
			i.setAmount(1);
			if (neverMode){
				for (ItemStack iStack : neverPickup)
					if (i.isSimilar(iStack))
						return;
				neverPickup.add(i);
			} else {
				for (ItemStack iStack : alwaysPickup)
					if (i.isSimilar(iStack))
						return;
				alwaysPickup.add(i);
			}
			inv.addItem(i);
		}
	}
}
