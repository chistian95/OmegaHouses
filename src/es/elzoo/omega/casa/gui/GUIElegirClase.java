package es.elzoo.omega.casa.gui;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import es.elzoo.omega.casa.CasaAsistenteCrear;
import es.elzoo.omega.casa.Clase;
import es.elzoo.omega.gui.GUI;
import net.md_5.bungee.api.ChatColor;

public class GUIElegirClase extends GUI {
	public GUIElegirClase(CasaAsistenteCrear asistente) {
		super(54, "Choose a class");
		
		ponerMarcoGrande();
		
		List<Clase> clases = Clase.getClases();
		
		for(int i=0, len=clases.size(); i<24 && i<len; i++) {
			final Clase clase = clases.get(i);
			ItemStack itemClase = GUI.crearItem(Material.NAME_TAG, ChatColor.BLUE + "" + clase.getId());
			ItemMeta itemMeta = itemClase.getItemMeta();
			itemMeta.setLore(Arrays.asList(new String[] {
				ChatColor.GRAY + "Chests: " + clase.getCofres(),
				ChatColor.GOLD + "Price: $" + clase.getPrecio()
			}));
			itemClase.setItemMeta(itemMeta);
			
			int fila = i/7 + 1;
			int columna = i%7 + 1;
			
			ponerItem(getSlot(fila, columna), itemClase, p -> {
				p.closeInventory();
				asistente.setClase(clase);
			});
		}
	}
}
