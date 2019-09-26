package es.elzoo.omega;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Door;

import es.elzoo.omega.casa.Casa;
import es.elzoo.omega.casa.CasaAsistenteCrear;

public class EventosCasa implements Listener {
	@EventHandler
	public void onAsistenteClick(PlayerInteractEvent event) {
		if(!event.hasBlock()) {
			return;
		}
		
		Optional<CasaAsistenteCrear> asistente = CasaAsistenteCrear.get(event.getPlayer().getName());
		if(!asistente.isPresent()) {
			return;
		}
		
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			asistente.get().rightClick(event);
		} else if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			asistente.get().leftClick(event);
		}
	}
	
	@EventHandler
	public void onCartelBreak(BlockBreakEvent event) {
		Optional<Casa> casa = Casa.getCasaByCartel(event.getBlock().getLocation());
		if(casa.isPresent()) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(Mensajes.HOUSE_CANT_DESTROY_SIGN.toString());
		}
	}
	
	@EventHandler
	public void onCartelInteract(PlayerInteractEvent event) {
		if(!event.hasBlock()) {
			return;
		}
		
		if(!(event.getClickedBlock().getState() instanceof Sign)) {
			return;
		}
		
		Optional<Casa> casa = Casa.getCasaByCartel(event.getClickedBlock().getLocation());
		if(!casa.isPresent()) {
			return;
		}
		
		event.setCancelled(true);
		casa.get().onClickCartel(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.isCancelled()) {
			return;
		}
		if(!event.hasBlock()) {
			return;
		}
		
		if(!event.getClickedBlock().getType().equals(Material.CHEST) && !(event.getClickedBlock().getState() instanceof Door)) {
			return;
		}
		
		Optional<Casa> casa = Casa.getCasaByArea(event.getClickedBlock().getLocation());
		if(!casa.isPresent()) {
			return;
		}
		
		casa.get().onPlayerInteract(event);
	}
}
