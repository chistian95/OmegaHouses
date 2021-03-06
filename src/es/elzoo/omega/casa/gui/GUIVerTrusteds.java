package es.elzoo.omega.casa.gui;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import es.elzoo.omega.casa.Casa;
import es.elzoo.omega.gui.GUI;
import net.md_5.bungee.api.ChatColor;

public class GUIVerTrusteds extends GUI {
	private static Plugin plugin = Bukkit.getPluginManager().getPlugin("OmegaHouses");
	
	private Casa casa;
	private boolean isOwner;
	
	public GUIVerTrusteds(Casa casa, boolean isOwner) {
		this(casa, isOwner, 1);
	}
	
	public GUIVerTrusteds(Casa casa, boolean isOwner, int pag) {
		super(54, "Trusted list");
		
		this.isOwner = isOwner;
		this.casa = casa;
		
		ponerMarcoGrande();
		
		List<UUID> guests = casa.getTrusteds();
		cargarCabezas(guests, pag);
		
		if(isOwner) {
			ponerItem(GUI.getSlot(0, 4), GUI.crearItem(Material.FISHING_ROD, ChatColor.GRAY + "Add trusted"), p -> {
				GUINuevoGuest gui = new GUINuevoGuest(casa, "trusted");
				gui.abrir(p);
			});
		}
		
		ponerItem(GUI.getSlot(5, 4), GUI.crearItem(Material.REDSTONE, ChatColor.RED + "Go Back"), p -> {
			if(isOwner) {
				GUICasaOwner gui = new GUICasaOwner(casa);
				gui.abrir(p);
			} else {
				GUICasaGuest gui = new GUICasaGuest(casa);
				gui.abrir(p);
			}
		});
		
		if(pag > 1) {
			ItemStack flecha = GUI.crearItem(Material.ARROW, ChatColor.BOLD+""+ChatColor.GRAY+"Previous Page");
			flecha.setAmount(pag-1);
			ponerItem(GUI.getSlot(5, 2), flecha, p -> {
				GUIVerTrusteds gui = new GUIVerTrusteds(casa, isOwner, pag-1);
				gui.abrir(p);
			});
		}
		
		if(guests.size() > 24*pag) {
			ItemStack flecha = GUI.crearItem(Material.ARROW, ChatColor.BOLD+""+ChatColor.GRAY+"Next Page");
			flecha.setAmount(pag+1);
			ponerItem(GUI.getSlot(5, 6), flecha, p -> {
				GUIVerTrusteds gui = new GUIVerTrusteds(casa, isOwner, pag+1);
				gui.abrir(p);
			});
		}
	}
	
	private void cargarCabezas(List<UUID> guests, int pagMala) {
		final int pag = pagMala-1;
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			for(int i=pag*24, len=guests.size(), index=0; i<len && i<pag*24+24; i++, index++) {
				final Player player = Bukkit.getPlayer(guests.get(i));
				
				ItemStack cabeza = GUI.crearItem(Material.SKULL_ITEM, (player.isOnline() ? ChatColor.GREEN : ChatColor.RED)+""+player.getName(), (short) 3);
				SkullMeta cabezaMeta = (SkullMeta) cabeza.getItemMeta();
				cabezaMeta.setOwner(player.getName());
				
				if(isOwner) {
					cabezaMeta.setLore(Arrays.asList(new String[] {ChatColor.GRAY + "- Click to delete -"}));
				}
				
				cabeza.setItemMeta(cabezaMeta);
				
				int fila = index/7 + 1;
				int columna = index%7 + 1;
				
				ponerItem(GUI.getSlot(fila, columna), cabeza, p -> {
					if(!isOwner) {
						return;
					}
					
					p.closeInventory();
					casa.borrarTrusted(player.getUniqueId(), p);
				});
			}
		});
	}
}
