package es.elzoo.omega.casa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;

public class Casa {
	private static List<Casa> casas;
	
	private Clase clase;
	private int numero;
	private UUID owner;
	private Location pos1, pos2;
	private Location cartel;
	private List<UUID> guests;
	private List<UUID> trusteds;	
	
	private Casa(Clase clase, int numero, UUID owner, Location pos1, Location pos2, Location cartel, List<UUID> guests, List<UUID> trusteds) {
		super();
		this.clase = clase;
		this.numero = numero;
		this.owner = owner;
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.cartel = cartel;
		this.guests = guests;
		this.trusteds = trusteds;
		
		casas.add(this);
	}	
	
	public Casa(Clase clase, UUID owner, Location pos1, Location pos2, Location cartel) {		
		this(clase, clase.getNextNumero(), owner, pos1, pos2, cartel, new ArrayList<UUID>(), new ArrayList<UUID>());
		
		actualizarCartel();
		
		//TODO Guardar en mysql
	}
	
	public void actualizarCartel() {
		if(getOwner().isPresent()) {
			Sign cartelState = (Sign) this.cartel.getBlock().getState();
			cartelState.setLine(0, "Class: "+clase.id);
			cartelState.setLine(1, "Number: "+this.numero);
			cartelState.setLine(2, "SOLD");
			cartelState.setLine(3, Bukkit.getPlayer(this.owner).getName());
		} else {
			Sign cartelState = (Sign) this.cartel.getBlock().getState();
			cartelState.setLine(0, "Class: "+clase.id);
			cartelState.setLine(1, "Number: "+this.numero);
			cartelState.setLine(2, "");
			cartelState.setLine(3, "$"+clase.precio);
		}
	}
	
	public static Optional<Casa> getCasaByCartel(Location loc) {
		return casas.parallelStream().filter(casa -> casa.cartel.equals(loc)).findFirst();
	}
	
	public static void cargarCasas() {
		//TODO Cargar casas
	}
	
	public static List<Casa> getCasas() {
		return casas;
	}
	
	public Clase getClase() {
		return clase;
	}
	public int getNumero() {
		return numero;
	}
	public Optional<UUID> getOwner() {
		return Optional.ofNullable(owner);
	}
	public Location getCartel() {
		return cartel;
	}
	public List<UUID> getGuests() {
		return guests;
	}
	public List<UUID> getTrusteds() {
		return trusteds;
	}
	public Location getPos1() {
		return pos1;
	}
	public Location getPos2() {
		return pos2;
	}	
}
