package es.elzoo.omega.casa.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import es.elzoo.omega.casa.Casa;
import es.elzoo.omega.gui.GUI;

public class GUICasaOwner extends GUI {
	public GUICasaOwner(Casa casa) {
		super(54, Bukkit.getPlayer(casa.getOwner().get()).getName()+"'s house");
		
		ponerMarcoGrande();
		
		Player owner = Bukkit.getPlayer(casa.getOwner().get());
		
		ItemStack cabeza = GUI.crearItem(Material.SKULL_ITEM, owner.getName(), (short) 3);
		SkullMeta cabezaMeta = (SkullMeta) cabeza.getItemMeta();
		cabezaMeta.setOwner(owner.getName());
		cabeza.setItemMeta(cabezaMeta);
		
		ponerItem(GUI.getSlot(0, 4), cabeza);
		
		ponerItem(GUI.getSlot(2, 2), GUI.crearItem(Material.NAME_TAG, ChatColor.BLUE+"Class: "+casa.getClase().getId()));
		ponerItem(GUI.getSlot(2, 4), GUI.crearItem(Material.REDSTONE_TORCH_ON, ChatColor.WHITE+"Number: "+casa.getNumero()));
		ponerItem(GUI.getSlot(2, 6), GUI.crearItem(Material.CHEST, ChatColor.GRAY+"Chests: "+casa.getClase().getCofres()));
		
		ponerItem(GUI.getSlot(4, 3), GUI.crearItem(Material.IRON_BLOCK, ChatColor.WHITE+"Click to see the Guests"), p -> {
			GUIVerGuests gui = new GUIVerGuests(casa, true);
			gui.abrir(p);
		});
		
		ponerItem(GUI.getSlot(4, 5), GUI.crearItem(Material.GOLD_BLOCK, ChatColor.GOLD+"Click to see the Trusteds"), p -> {
			GUIVerTrusteds gui = new GUIVerTrusteds(casa, true);
			gui.abrir(p);
		});
		
		ponerItem(GUI.getSlot(5, 4), GUI.crearItem(Material.TNT, ChatColor.RED+"Click to sell the house"), p -> {
			p.closeInventory();
			casa.vender(p);
		});
	}
}
