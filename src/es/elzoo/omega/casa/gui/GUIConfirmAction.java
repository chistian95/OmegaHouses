package es.elzoo.omega.casa.gui;

import org.bukkit.Material;

import es.elzoo.omega.gui.GUI;
import es.elzoo.omega.gui.GUIAccion;
import net.md_5.bungee.api.ChatColor;

public class GUIConfirmAction extends GUI {
	public GUIConfirmAction(GUI guiBack, GUIAccion accion) {
		super(27, "Confirm Action");
		
		ponerItem(11, GUI.crearItem(Material.WOOL, ChatColor.GREEN + "Confirm", (short) 5), accion);
		
		ponerItem(13, GUI.crearItem(Material.STAINED_GLASS_PANE, "", (short) 15));
		
		ponerItem(15, GUI.crearItem(Material.WOOL, ChatColor.RED + "Cancel", (short) 14), p -> {
			guiBack.abrir(p);
		});
	}
}
