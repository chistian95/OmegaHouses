package es.elzoo.omega.casa;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

public class Casa {
	private Clase clase;
	private int numero;
	private UUID owner;
	private Location cartel;
	private List<UUID> guests;
	private List<UUID> trusteds;	
	
	public Casa(Clase clase, int numero, UUID owner, Location cartel, List<UUID> guests, List<UUID> trusteds) {
		super();
		this.clase = clase;
		this.numero = numero;
		this.owner = owner;
		this.cartel = cartel;
		this.guests = guests;
		this.trusteds = trusteds;
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
	
	
}
