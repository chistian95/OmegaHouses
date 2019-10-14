package es.elzoo.omega.casa.gui;

import org.bukkit.Material;

import es.elzoo.omega.gui.GUI;
import es.elzoo.omega.gui.GUIAccion;
import net.md_5.bungee.api.ChatColor;

public class GUIConfirmAction extends GUI {
	public GUIConfirmAction(GUI guiBack, GUIAccion accion) {
		super(27, "Confirm Action");
		
		for(int i=0; i<9; i++) {
			ponerItem(i, GUI.crearItem(Material.STAINED_GLASS_PANE, "", (short) 15));
		}
		for(int i=18; i<27; i++) {
			ponerItem(i, GUI.crearItem(Material.STAINED_GLASS_PANE, "", (short) 15));
		}
		
		ponerItem(9, GUI.crearItem(Material.STAINED_GLASS_PANE, "", (short) 15));
		
		ponerItem(11, GUI.crearItem(Material.WOOL, ChatColor.GREEN + "Confirm", (short) 5), accion);
		
		ponerItem(13, GUI.crearItem(Material.STAINED_GLASS_PANE, "", (short) 15));
		
		ponerItem(15, GUI.crearItem(Material.WOOL, ChatColor.RED + "Cancel", (short) 14), p -> {
			guiBack.abrir(p);
		});
		
		ponerItem(17, GUI.crearItem(Material.STAINED_GLASS_PANE, "", (short) 15));
	}
}
