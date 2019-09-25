package es.elzoo.omega;

import java.util.Optional;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
}
