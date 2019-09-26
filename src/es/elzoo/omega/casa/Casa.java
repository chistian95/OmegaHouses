package es.elzoo.omega.casa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Door;

import es.elzoo.omega.Mensajes;
import es.elzoo.omega.OmegaHouses;
import es.elzoo.omega.casa.gui.GUICasaGuest;
import es.elzoo.omega.casa.gui.GUICasaOwner;
import es.elzoo.omega.casa.gui.GUICasaVacia;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

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
	
	public void borrar(Player player) {
		casas.remove(this);
		borrarCartel();
		
		player.sendMessage(Mensajes.HOUSE_DELETED.toString());
		
		//TODO Borrar casa en mysql
	}
	
	public void borrarCartel() {
		Sign cartelState = (Sign) this.cartel.getBlock().getState();
		for(int i=0; i<4; i++) {
			cartelState.setLine(i, "");
		}
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
	
	public void onClickCartel(Player player) {
		Optional<UUID> owner = getOwner();
		if(!owner.isPresent()) {
			GUICasaVacia gui = new GUICasaVacia(this);
			gui.abrir(player);
			return;
		}
		
		if(isOwner(player)) {
			GUICasaOwner gui = new GUICasaOwner(this);
			gui.abrir(player);
		} else {
			GUICasaGuest gui = new GUICasaGuest(this);
			gui.abrir(player);
		}
	}
	
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(event.getClickedBlock().getType().equals(Material.CHEST)) {
			if(!this.isOwner(event.getPlayer()) && !this.isTrusted(event.getPlayer())) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(Mensajes.NO_PERMISOS.toString());
			}
		} else if(event.getClickedBlock().getType().equals(Material.IRON_DOOR) || event.getClickedBlock().getType().equals(Material.IRON_DOOR_BLOCK)) {
			if(!this.isOwner(event.getPlayer()) && !this.isTrusted(event.getPlayer()) && !this.isGuest(event.getPlayer())) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(Mensajes.NO_PERMISOS.toString());
			} else {
				Door puerta = (Door) event.getClickedBlock().getState();
				puerta.setOpen(!puerta.isOpen());
			}
		} else if(event.getClickedBlock().getState() instanceof Door) {
			if(!this.isOwner(event.getPlayer()) && !this.isTrusted(event.getPlayer())) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(Mensajes.NO_PERMISOS.toString());
			}
		}
	}
	
	public void comprar(Player player) {
		Economy economy = OmegaHouses.getEconomy();
		
		if(this.getOwner().isPresent()) {
			player.sendMessage(Mensajes.HOUSE_BUY_HAS_OWNER.toString());
			return;
		}
		
		OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(player.getUniqueId());		
		EconomyResponse res = economy.withdrawPlayer(offPlayer, this.getClase().getPrecio());
		
		if(!res.transactionSuccess()) {
			player.sendMessage(Mensajes.HOUSE_BUY_NO_MONEY.toString()+this.getClase().getPrecio());
			return;
		}
		
		this.owner = player.getUniqueId();
		this.guests.clear();
		this.trusteds.clear();
		actualizarCartel();
		
		//TODO Guardar compra mysql
	}
	
	public void vender(Player player) {
		Economy economy = OmegaHouses.getEconomy();
		
		if(!this.getOwner().isPresent() || !this.isOwner(player)) {
			player.sendMessage(Mensajes.HOUSE_SELL_NO_OWNER.toString());
			return;
		}
		
		OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
		EconomyResponse res = economy.depositPlayer(offPlayer, this.getClase().getPrecio());
		
		if(!res.transactionSuccess()) {			
			player.sendMessage(Mensajes.HOUSE_SELL_ERROR.toString());
			return;
		}
		
		this.owner = null;
		this.guests.clear();
		this.trusteds.clear();
		actualizarCartel();
		
		//TODO Guardar venta mysql
	}
	
	public void addGuest(UUID guest, Player player) {
		guests.add(guest);
		player.sendMessage(Mensajes.HOUSE_GUEST_ADDED.toString());
		
		//TODO A�adir guest mysql
	}
	
	public void addTrusted(UUID trusted, Player player) {
		trusteds.add(trusted);
		player.sendMessage(Mensajes.HOUSE_TRUSTED_ADDED.toString());
		
		//TODO A�adir trusted mysql
	}
	
	public void borrarGuest(UUID guest, Player player) {
		guests.remove(guest);
		player.sendMessage(Mensajes.HOUSE_GUEST_REMOVED.toString());
		
		//TODO Borrar guest mysql
	}
	
	public void borrarTrusted(UUID trusted, Player player) {
		trusteds.remove(trusted);
		player.sendMessage(Mensajes.HOUSE_TRUSTED_REMOVED.toString());
		
		//TODO Borrar trusted mysql
	}
	
	public boolean isOwner(Player player) {
		return owner != null && owner.equals(player.getUniqueId());
	}
	
	public boolean isTrusted(Player player) {
		return trusteds.parallelStream().anyMatch(tr -> tr.equals(player.getUniqueId()));
	}
	
	public boolean isGuest(Player player) {
		return guests.parallelStream().anyMatch(guest -> guest.equals(player.getUniqueId()));
	}
	
	public static Optional<Casa> getCasaByCartel(Location loc) {
		return casas.parallelStream().filter(casa -> casa.cartel.equals(loc)).findFirst();
	}
	
	public static Optional<Casa> getCasaByClaseYNumero(Clase clase, int numero) {
		return casas.parallelStream().filter(casa -> casa.clase.equals(clase) && casa.numero == numero).findFirst();
	}
	
	public static Optional<Casa> getCasaByArea(Location loc) {
		return casas.parallelStream().filter(casa -> {
			if(loc.getBlockX() >= casa.pos1.getBlockX() && loc.getBlockY() >= casa.pos1.getBlockY() && loc.getBlockZ() >= casa.pos1.getBlockZ()) {
				if(loc.getBlockX() <= casa.pos2.getBlockX() && loc.getBlockY() <= casa.pos2.getBlockY() && loc.getBlockZ() <= casa.pos2.getBlockZ()) {
					return true;
				}
			}
			return false;
		}).findFirst();
	}
	
	public static void cargarCasas() {
		//TODO Cargar casas mysql
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
