package es.elzoo.omega.casa;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

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
		
		//TODO Guardar en mysql
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
	public UUID getOwner() {
		return owner;
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
