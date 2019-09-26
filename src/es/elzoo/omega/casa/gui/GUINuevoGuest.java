package es.elzoo.omega.casa.gui;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import es.elzoo.omega.casa.Casa;
import es.elzoo.omega.gui.GUI;
import net.md_5.bungee.api.ChatColor;

public class GUINuevoGuest extends GUI {
	private static Plugin plugin = Bukkit.getPluginManager().getPlugin("OmegaHouses");
	
	private Casa casa;
	private String accion;
	
	public GUINuevoGuest(Casa casa, String accion) {
		this(casa, accion, 1);
	}
	public GUINuevoGuest(Casa casa, String accion, int pag) {
		super(54, "Add a new "+accion);
		
		this.casa = casa;
		this.accion = accion;
		
		ponerMarcoGrande();
		
		List<UUID> guests = Bukkit.getOnlinePlayers()
			.parallelStream()
			.sorted((a,b) -> b.getName().compareTo(a.getName()))
			.map(p -> p.getUniqueId())
			.collect(Collectors.toList());
		cargarCabezas(guests, pag);
		
		ponerItem(GUI.getSlot(5, 4), GUI.crearItem(Material.REDSTONE, ChatColor.GRAY + "Go Back"), p -> {
			if(accion.equalsIgnoreCase("guest")) {
				GUIVerGuests gui = new GUIVerGuests(casa, true);
				gui.abrir(p);
			} else if(accion.equalsIgnoreCase("trusted")) {
				GUIVerTrusteds gui = new GUIVerTrusteds(casa, true);
				gui.abrir(p);
			}
		});
		
		if(pag > 1) {
			ItemStack flecha = GUI.crearItem(Material.ARROW, ChatColor.BOLD+"Previous Page");
			flecha.setAmount(pag-1);
			ponerItem(GUI.getSlot(5, 2), flecha, p -> {
				GUINuevoGuest gui = new GUINuevoGuest(casa, accion, pag-1);
				gui.abrir(p);
			});
		}
		
		if(guests.size() > 24*pag) {
			ItemStack flecha = GUI.crearItem(Material.ARROW, ChatColor.BOLD+"Next Page");
			flecha.setAmount(pag+1);
			ponerItem(GUI.getSlot(5, 6), flecha, p -> {
				GUINuevoGuest gui = new GUINuevoGuest(casa, accion, pag+1);
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
				cabeza.setItemMeta(cabezaMeta);
				
				int fila = index/7 + 1;
				int columna = index%7 + 1;
				
				ponerItem(GUI.getSlot(fila, columna), cabeza, p -> {
					p.closeInventory();
					if(accion.equalsIgnoreCase("guest")) {
						casa.addGuest(player.getUniqueId(), p);
					} else if(accion.equalsIgnoreCase("trusted")) {
						casa.addTrusted(player.getUniqueId(), p);
					}
				});
			}
		});
	}
}
