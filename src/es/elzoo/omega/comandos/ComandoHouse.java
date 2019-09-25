package es.elzoo.omega.comandos;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import es.elzoo.omega.Mensajes;
import es.elzoo.omega.Permisos;
import es.elzoo.omega.casa.CasaAsistenteCrear;
import es.elzoo.omega.casa.Clase;

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
		} else if(args[0].equalsIgnoreCase("class")) {
			comandoClass(player, args);
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
	
	private static void comandoClass(Player player, String[] args) {
		if(!player.hasPermission(Permisos.CASA_CREAR_CLASE.toString())) {
			player.sendMessage(Mensajes.NO_PERMISOS.toString());
			return;
		}
		
		if(args.length != 4) {
			player.sendMessage(ChatColor.GRAY+"/house createClass <id> <price> <chests> - Cancels the current assistant.");
			return;
		}
		
		int id = 0;
		try {
			id = Integer.parseInt(args[1]);
		} catch(Exception e) {
			id = 0;			
		}
		if(id <= 0) {
			player.sendMessage(ChatColor.RED + "Error parsing the id. It must be a number bigger than 0.");
			return;
		}
		
		double price = 0.0;
		try {
			price = Double.parseDouble(args[2]);
		} catch(Exception e) {
			price = 0.0;			
		}
		if(price <= 0.0) {
			player.sendMessage(ChatColor.RED + "Error parsing the price. It must be a number bigger than 0.");
			return;
		}
		
		int chests = 0;
		try {
			chests = Integer.parseInt(args[3]);
		} catch(Exception e) {
			chests = 0;			
		}
		if(chests <= 0) {
			player.sendMessage(ChatColor.RED + "Error parsing the chests. It must be a number bigger than 0.");
			return;
		}
		
		Optional<Clase> clase = Clase.getClaseById(id);
		if(clase.isPresent()) {
			player.sendMessage(ChatColor.RED + "There is already a class with that ID.");
			return;
		}
		
		new Clase(id, price, chests);
		player.sendMessage(ChatColor.GREEN + "Class created.");
	}
	
	private static void mostrarAyuda(Player player) {
		player.sendMessage(ChatColor.GRAY+"/house create - Starts the assistant to add a new house.");
		player.sendMessage(ChatColor.GRAY+"/house cancel - Cancels the current assistant.");
		player.sendMessage(ChatColor.GRAY+"/house createClass <id> <price> <chests> - Cancels the current assistant.");
	}
}
