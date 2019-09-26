package es.elzoo.omega.comandos;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import es.elzoo.omega.Mensajes;
import es.elzoo.omega.Permisos;
import es.elzoo.omega.casa.Casa;
import es.elzoo.omega.casa.CasaAsistenteCrear;
import es.elzoo.omega.casa.Clase;
import es.elzoo.omega.casa.gui.GUICasaGuest;
import es.elzoo.omega.casa.gui.GUICasaOwner;

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
		} else if(args[0].equalsIgnoreCase("delete")) {
			borrarCasa(player, args);
		} else if(args[0].equalsIgnoreCase("cancel")) {
			cancelarAsistente(player);
		} else if(args[0].equalsIgnoreCase("createClass")) {
			comandoClass(player, args);
		} else if(args[0].equalsIgnoreCase("info")) {
			info(player, args);
		} else if(args[0].equalsIgnoreCase("buy")) {
			comprar(player, args);
		} else if(args[0].equalsIgnoreCase("sell")) {
			vender(player, args);
		}
		
		return true;
	}
	
	private static void mostrarAyuda(Player player) {
		player.sendMessage(ChatColor.GRAY+"/house create - Starts the assistant to add a new house.");
		player.sendMessage(ChatColor.GRAY+"/house delete <class> <number> - Deletes the house.");
		player.sendMessage(ChatColor.GRAY+"/house cancel - Cancels the current assistant.");
		player.sendMessage(ChatColor.GRAY+"/house createClass <id> <price> <chests> - Cancels the current assistant.");
		player.sendMessage(ChatColor.GRAY+"/house info <class> <number> - Shows the info of the selected house.");
		player.sendMessage(ChatColor.GRAY+"/house buy <class> <number> - Buy a house.");
		player.sendMessage(ChatColor.GRAY+"/house sell <class> <number> - Sell a house.");
	}
	
	private static void crearCasa(Player player, String[] args) {
		if(!player.hasPermission(Permisos.CASA_CREAR.toString())) {
			player.sendMessage(Mensajes.NO_PERMISOS.toString());
			return;
		}
		
		CasaAsistenteCrear asistente = CasaAsistenteCrear.getOrCreate(player.getName());
		asistente.next();
	}
	
	private static void borrarCasa(Player player, String[] args) {
		if(!player.hasPermission(Permisos.CASA_CREAR.toString())) {
			player.sendMessage(Mensajes.NO_PERMISOS.toString());
			return;
		}
		
		if(args.length != 3) {
			player.sendMessage(ChatColor.GRAY+"/house delete <class> <number> - Deletes the house.");
			return;
		}
		
		int classId = 0;
		try {
			classId = Integer.parseInt(args[1]);
		} catch(Exception e) {
			classId = 0;			
		}
		if(classId <= 0) {
			player.sendMessage(ChatColor.RED + "Error parsing the class. It must be a number bigger than 0.");
			return;
		}
		
		int houseNumber = 0;
		try {
			houseNumber = Integer.parseInt(args[3]);
		} catch(Exception e) {
			houseNumber = 0;			
		}
		if(houseNumber <= 0) {
			player.sendMessage(ChatColor.RED + "Error parsing the house number. It must be a number bigger than 0.");
			return;
		}
		
		Optional<Clase> clase = Clase.getClaseById(classId);
		if(!clase.isPresent()) {
			player.sendMessage(ChatColor.RED + "Could not find a class with that number.");
			return;
		}
		
		Optional<Casa> casa = Casa.getCasaByClaseYNumero(clase.get(), houseNumber);
		if(!casa.isPresent()) {
			player.sendMessage(ChatColor.RED + "Could not find a house by that class and number.");
			return;
		}
		
		casa.get().borrar(player);
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
		
		//TODO Guardar clase mysql
	}	
	
	private static void info(Player player, String[] args) {
		if(args.length != 3) {
			player.sendMessage(ChatColor.GRAY+"/house info <class> <number> - Shows the info of the selected house.");
			return;
		}
		
		int classId = 0;
		try {
			classId = Integer.parseInt(args[1]);
		} catch(Exception e) {
			classId = 0;			
		}
		if(classId <= 0) {
			player.sendMessage(ChatColor.RED + "Error parsing the class. It must be a number bigger than 0.");
			return;
		}
		
		int houseNumber = 0;
		try {
			houseNumber = Integer.parseInt(args[3]);
		} catch(Exception e) {
			houseNumber = 0;			
		}
		if(houseNumber <= 0) {
			player.sendMessage(ChatColor.RED + "Error parsing the house number. It must be a number bigger than 0.");
			return;
		}
		
		Optional<Clase> clase = Clase.getClaseById(classId);
		if(!clase.isPresent()) {
			player.sendMessage(ChatColor.RED + "Could not find a class with that number.");
			return;
		}
		
		Optional<Casa> casa = Casa.getCasaByClaseYNumero(clase.get(), houseNumber);
		if(!casa.isPresent()) {
			player.sendMessage(ChatColor.RED + "Could not find a house by that class and number.");
			return;
		}
		
		if(casa.get().isOwner(player)) {
			GUICasaOwner gui = new GUICasaOwner(casa.get());
			gui.abrir(player);
		} else {
			GUICasaGuest gui = new GUICasaGuest(casa.get());
			gui.abrir(player);
		}
	}
	
	private static void comprar(Player player, String[] args) {
		if(args.length != 3) {
			player.sendMessage(ChatColor.GRAY+"/house buy <class> <number> - Buy a house.");
			return;
		}
		
		int classId = 0;
		try {
			classId = Integer.parseInt(args[1]);
		} catch(Exception e) {
			classId = 0;			
		}
		if(classId <= 0) {
			player.sendMessage(ChatColor.RED + "Error parsing the class. It must be a number bigger than 0.");
			return;
		}
		
		int houseNumber = 0;
		try {
			houseNumber = Integer.parseInt(args[3]);
		} catch(Exception e) {
			houseNumber = 0;			
		}
		if(houseNumber <= 0) {
			player.sendMessage(ChatColor.RED + "Error parsing the house number. It must be a number bigger than 0.");
			return;
		}
		
		Optional<Clase> clase = Clase.getClaseById(classId);
		if(!clase.isPresent()) {
			player.sendMessage(ChatColor.RED + "Could not find a class with that number.");
			return;
		}
		
		Optional<Casa> casa = Casa.getCasaByClaseYNumero(clase.get(), houseNumber);
		if(!casa.isPresent()) {
			player.sendMessage(ChatColor.RED + "Could not find a house by that class and number.");
			return;
		}
		
		casa.get().comprar(player);
	}
	
	private static void vender(Player player, String[] args) {
		if(args.length != 3) {
			player.sendMessage(ChatColor.GRAY+"/house sell <class> <number> - Sell a house.");
			return;
		}
		
		int classId = 0;
		try {
			classId = Integer.parseInt(args[1]);
		} catch(Exception e) {
			classId = 0;			
		}
		if(classId <= 0) {
			player.sendMessage(ChatColor.RED + "Error parsing the class. It must be a number bigger than 0.");
			return;
		}
		
		int houseNumber = 0;
		try {
			houseNumber = Integer.parseInt(args[3]);
		} catch(Exception e) {
			houseNumber = 0;			
		}
		if(houseNumber <= 0) {
			player.sendMessage(ChatColor.RED + "Error parsing the house number. It must be a number bigger than 0.");
			return;
		}
		
		Optional<Clase> clase = Clase.getClaseById(classId);
		if(!clase.isPresent()) {
			player.sendMessage(ChatColor.RED + "Could not find a class with that number.");
			return;
		}
		
		Optional<Casa> casa = Casa.getCasaByClaseYNumero(clase.get(), houseNumber);
		if(!casa.isPresent()) {
			player.sendMessage(ChatColor.RED + "Could not find a house by that class and number.");
			return;
		}
		
		casa.get().vender(player);
	}
}
