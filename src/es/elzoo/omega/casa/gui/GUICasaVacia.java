package es.elzoo.omega.casa.gui;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import es.elzoo.omega.casa.Casa;
import es.elzoo.omega.gui.GUI;

public class GUICasaVacia extends GUI {
	public GUICasaVacia(Casa casa) {
		super(54, "");
		
		ponerMarcoGrande();
		
		ponerItem(GUI.getSlot(2, 2), GUI.crearItem(Material.NAME_TAG, ChatColor.BLUE+"Class: "+casa.getClase().getId()));
		ponerItem(GUI.getSlot(2, 4), GUI.crearItem(Material.REDSTONE_TORCH_ON, ChatColor.WHITE+"Number: "+casa.getNumero()));
		ponerItem(GUI.getSlot(2, 6), GUI.crearItem(Material.CHEST, ChatColor.GRAY+"Chests: "+casa.getClase().getCofres()));
		
		ItemStack itemBuy = GUI.crearItem(Material.DOUBLE_PLANT, ChatColor.GOLD + "$"+casa.getClase().getPrecio());
		ItemMeta meta = itemBuy.getItemMeta();
		meta.setLore(Arrays.asList(new String[] {ChatColor.BOLD + "- CLICK TO BUY -"}));
		itemBuy.setItemMeta(meta);
		
		ponerItem(GUI.getSlot(4, 4), itemBuy, p -> {
			p.closeInventory();
			casa.comprar(p);
		});
	}
}