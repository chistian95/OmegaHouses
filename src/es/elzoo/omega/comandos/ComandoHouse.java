package es.elzoo.omega.comandos;

import java.util.Optional;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import es.elzoo.omega.Mensajes;
import es.elzoo.omega.Permisos;
import es.elzoo.omega.casa.CasaAsistenteCrear;

public class ComandoHouse implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			sender.sendMessage(Mensajes.PLAYER_ONLY.toString());
			return true;
		}
		
		Player player = (Player) sender;
		
		if(args.length == 0) {
			mostrarAyuda(player);
		} else if(args[0].equalsIgnoreCase("create")) {
			crearCasa(player, args);
		} else if(args[0].equalsIgnoreCase("cancel")) {
			cancelarAsistente(player);
		}
		
		return true;
	}
	
	private static void crearCasa(Player player, String[] args) {
		if(!player.hasPermission(Permisos.CASA_CREAR.toString())) {
			player.sendMessage(Mensajes.NO_PERMISOS.toString());
			return;
		}
		
		CasaAsistenteCrear asistente = CasaAsistenteCrear.getOrCreate(player.getName());
		asistente.next();
	}
	
	private static void cancelarAsistente(Player player) {
		Optional<CasaAsistenteCrear> asis = CasaAsistenteCrear.get(player.getName());
		if(!asis.isPresent()) {
			player.sendMessage(Mensajes.HOUSE_NO_ASSISTANT.toString());
			return;
		}
		asis.get().cancel();
	}
	
	private static void mostrarAyuda(Player player) {
		player.sendMessage("&7/house create - Starts the assistant to add a new house.");
		player.sendMessage("&7/house cancel - Cancels the current assistant.");
	}
}
