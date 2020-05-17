package es.elzoo.omega.casa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Door;
import org.bukkit.plugin.Plugin;

import es.elzoo.omega.Mensajes;
import es.elzoo.omega.OmegaHouses;
import es.elzoo.omega.Permisos;
import es.elzoo.omega.Utils;
import es.elzoo.omega.casa.gui.GUICasaGuest;
import es.elzoo.omega.casa.gui.GUICasaOwner;
import es.elzoo.omega.casa.gui.GUICasaVacia;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Casa {
	private static Plugin plugin = Bukkit.getPluginManager().getPlugin("OmegaHouses");
	private static List<Casa> casas = new ArrayList<Casa>();
	
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
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			PreparedStatement stmtHouse = null;
			
			try {
				stmtHouse = OmegaHouses.getConexion().prepareStatement("INSERT INTO oh_house(clase_id, numero, pos1, pos2, cartel) VALUES (?,?,?,?,?);");
				stmtHouse.setInt(1, this.getClase().getId());
				stmtHouse.setInt(2, this.getNumero());
				stmtHouse.setString(3, Utils.locationToString(this.pos1));
				stmtHouse.setString(4, Utils.locationToString(this.pos2));
				stmtHouse.setString(5, Utils.locationToString(this.cartel));
				stmtHouse.execute();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(stmtHouse != null) {
						stmtHouse.close();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void borrar(Player player) {
		casas.remove(this);
		borrarCartel();
		
		player.sendMessage(Mensajes.HOUSE_DELETED.toString());
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			PreparedStatement stmtHouse = null;
			PreparedStatement stmtTrusted = null;
			PreparedStatement stmtGuest = null;
			
			try {
				stmtHouse = OmegaHouses.getConexion().prepareStatement("DELETE FROM oh_house WHERE clase_id=? AND numero=?;");
				stmtHouse.setInt(1, this.getClase().getId());
				stmtHouse.setInt(2, this.getNumero());
				stmtHouse.execute();
				
				stmtTrusted = OmegaHouses.getConexion().prepareStatement("DELETE FROM oh_trusted WHERE clase_id=? AND numero=?;");
				stmtTrusted.setInt(1, this.getClase().getId());
				stmtTrusted.setInt(2, this.getNumero());
				stmtTrusted.execute();
				
				stmtGuest = OmegaHouses.getConexion().prepareStatement("DELETE FROM oh_guest WHERE clase_id=? AND numero=?;");
				stmtGuest.setInt(1, this.getClase().getId());
				stmtGuest.setInt(2, this.getNumero());
				stmtGuest.execute();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(stmtHouse != null) {
						stmtHouse.close();
					}
					if(stmtTrusted != null) {
						stmtTrusted.close();
					}
					if(stmtGuest != null) {
						stmtGuest.close();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void borrarCartel() {
		Sign cartelState = (Sign) this.cartel.getBlock().getState();
		for(int i=0; i<4; i++) {
			cartelState.setLine(i, "");
		}
		cartelState.update();
	}
	
	public void actualizarCartel() {
		if(getOwner().isPresent()) {
			Sign cartelState = (Sign) this.cartel.getBlock().getState();
			cartelState.setLine(0, ChatColor.RED+"Class: "+ChatColor.GRAY+clase.id);
			cartelState.setLine(1, ChatColor.RED+"Number: "+ChatColor.GRAY+this.numero);
			cartelState.setLine(2, ChatColor.GRAY+""+ChatColor.STRIKETHROUGH+"SOLD");
			cartelState.setLine(3, Bukkit.getPlayer(this.owner).getName());
			cartelState.update();
		} else {
			Sign cartelState = (Sign) this.cartel.getBlock().getState();
			cartelState.setLine(0, ChatColor.RED+"Class: "+ChatColor.GRAY+clase.id);
			cartelState.setLine(1, ChatColor.RED+"Number: "+ChatColor.GRAY+this.numero);
			cartelState.setLine(2, "");
			if(this.getClase().isVip()) {
				cartelState.setLine(3, ChatColor.GRAY+"DONOR HOUSE");
			} else {
				cartelState.setLine(3, ChatColor.GRAY+"$"+clase.precio);
			}			
			cartelState.update();
		}
	}
	
	public void onClickCartel(Player player) {
		Optional<UUID> owner = getOwner();
		if(!owner.isPresent() || owner.get() == null) {
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
			if(!this.isOwner(event.getPlayer()) && !this.isTrusted(event.getPlayer()) && !event.getPlayer().hasPermission(Permisos.CASA_BYPASS.toString())) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(Mensajes.NO_PERMISOS.toString());
			}
		} else if(event.getClickedBlock().getType().equals(Material.IRON_DOOR) || event.getClickedBlock().getType().equals(Material.IRON_DOOR_BLOCK)) {
			if(!this.isOwner(event.getPlayer()) && !this.isTrusted(event.getPlayer()) && !this.isGuest(event.getPlayer()) && !event.getPlayer().hasPermission(Permisos.CASA_BYPASS.toString())) {				
				event.setCancelled(true);
				event.getPlayer().sendMessage(Mensajes.NO_PERMISOS.toString());
			} else {
				event.setCancelled(true);
				BlockState doorState = event.getClickedBlock().getState();					
				Door puerta = (Door) doorState.getData();
				
				if(puerta.isTopHalf()) {
					doorState = event.getClickedBlock().getRelative(BlockFace.DOWN).getState();
					puerta = (Door) doorState.getData();
				}
				
				puerta.setOpen(!puerta.isOpen());
				doorState.setData(puerta);
				doorState.update();
				
				if(OmegaHouses.close_doors) {
					Bukkit.getScheduler().runTaskLater(plugin, () -> {
						BlockState doorStateAfter = event.getClickedBlock().getState();					
						Door puertaAfter = (Door) doorStateAfter.getData();
						
						if(puertaAfter.isTopHalf()) {
							doorStateAfter = event.getClickedBlock().getRelative(BlockFace.DOWN).getState();
							puertaAfter = (Door) doorStateAfter.getData();
						}
						
						puertaAfter.setOpen(false);
						doorStateAfter.setData(puertaAfter);
						doorStateAfter.update();
					}, OmegaHouses.close_doors_delay*20);
				}
			}
		} else if(event.getClickedBlock().getState().getData() instanceof Door) {
			if(!this.isOwner(event.getPlayer()) && !this.isTrusted(event.getPlayer()) && !event.getPlayer().hasPermission(Permisos.CASA_BYPASS.toString())) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(Mensajes.NO_PERMISOS.toString());
			}
		}
	}
	
	public void comprarVip(Player player) {
		if(!this.getClase().isVip()) {
			return;
		}
		
		if(Casa.getCasaByUser(player.getUniqueId()).size() >= OmegaHouses.house_limit) {
			player.sendMessage(Mensajes.HOUSE_BUY_LIMIT.toString());
			return;
		}
		
		if(this.getOwner().isPresent()) {
			player.sendMessage(Mensajes.HOUSE_BUY_HAS_OWNER.toString());
			return;
		}
		
		Optional<CasaToken> token = CasaToken.getToken(player.getUniqueId(), this.getClase());
		if(!token.isPresent()) {
			player.sendMessage(Mensajes.HOUSE_NO_TOKEN.toString());
			return;
		}
		
		token.get().consume();
		
		this.owner = player.getUniqueId();
		actualizarCartel();
		
		player.sendMessage(Mensajes.HOUSE_BOUGHT.toString());
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			PreparedStatement stmtHouse = null;
			
			try {
				stmtHouse = OmegaHouses.getConexion().prepareStatement("UPDATE oh_house SET owner=? WHERE clase_id=? AND numero=?;");
				stmtHouse.setString(1, this.owner.toString());
				stmtHouse.setInt(2, this.getClase().getId());
				stmtHouse.setInt(3, this.getNumero());
				stmtHouse.execute();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(stmtHouse != null) {
						stmtHouse.close();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void comprar(Player player) {
		if(this.getClase().isVip()) {
			player.sendMessage(Mensajes.HOUSE_CANT_BUY_DONOR.toString());
			return;
		}
		
		if(Casa.getCasaByUser(player.getUniqueId()).size() >= OmegaHouses.house_limit) {
			player.sendMessage(Mensajes.HOUSE_BUY_LIMIT.toString());
			return;
		}
		
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
		actualizarCartel();
		
		player.sendMessage(Mensajes.HOUSE_BOUGHT.toString());
		
		String msgBroadcast = OmegaHouses.buy_broadcast;
		msgBroadcast.replaceAll("%player%", player.getName());
		msgBroadcast.replaceAll("%house%", (clase.id + ": " + numero));
		msgBroadcast.replaceAll("%amount%", ("$"+this.getClase().getPrecio()));
		Bukkit.getServer().broadcastMessage(msgBroadcast);
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			PreparedStatement stmtHouse = null;
			
			try {
				stmtHouse = OmegaHouses.getConexion().prepareStatement("UPDATE oh_house SET owner=? WHERE clase_id=? AND numero=?;");
				stmtHouse.setString(1, this.owner.toString());
				stmtHouse.setInt(2, this.getClase().getId());
				stmtHouse.setInt(3, this.getNumero());
				stmtHouse.execute();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(stmtHouse != null) {
						stmtHouse.close();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void vender(Player player) {
		vender(player, false);
	}
	
	public void vender(Player player, boolean force) {
		Economy economy = OmegaHouses.getEconomy();
		
		if(!force && (!this.getOwner().isPresent() || !this.isOwner(player))) {
			player.sendMessage(Mensajes.HOUSE_SELL_NO_OWNER.toString());
			return;
		}
		
		if(!force && !this.getClase().vip) {
			OfflinePlayer offPlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
			EconomyResponse res = economy.depositPlayer(offPlayer, this.getClase().getPrecio()*OmegaHouses.return_percentage);
			
			if(!res.transactionSuccess()) {			
				player.sendMessage(Mensajes.HOUSE_SELL_ERROR.toString());
				return;
			}
		}
		
		this.owner = null;
		this.guests.clear();
		this.trusteds.clear();
		actualizarCartel();
		
		player.sendMessage(Mensajes.HOUSE_SOLD.toString());
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			PreparedStatement stmtHouse = null;
			PreparedStatement stmtTrusted = null;
			PreparedStatement stmtGuest = null;
			
			try {
				stmtHouse = OmegaHouses.getConexion().prepareStatement("UPDATE oh_house SET owner=? WHERE clase_id=? AND numero=?;");
				stmtHouse.setString(1, null);
				stmtHouse.setInt(2, this.getClase().getId());
				stmtHouse.setInt(3, this.getNumero());
				stmtHouse.execute();
				
				stmtTrusted = OmegaHouses.getConexion().prepareStatement("DELETE FROM oh_trusted WHERE clase_id=? AND numero=?;");
				stmtTrusted.setInt(1, this.getClase().getId());
				stmtTrusted.setInt(2, this.getNumero());
				stmtTrusted.execute();
				
				stmtGuest = OmegaHouses.getConexion().prepareStatement("DELETE FROM oh_guest WHERE clase_id=? AND numero=?;");
				stmtGuest.setInt(1, this.getClase().getId());
				stmtGuest.setInt(2, this.getNumero());
				stmtGuest.execute();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(stmtHouse != null) {
						stmtHouse.close();
					}
					if(stmtTrusted != null) {
						stmtTrusted.close();
					}
					if(stmtGuest != null) {
						stmtGuest.close();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void addGuest(UUID guest, Player player) {
		guests.add(guest);
		player.sendMessage(Mensajes.HOUSE_GUEST_ADDED.toString());
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			PreparedStatement stmt = null;
			
			try {
				stmt = OmegaHouses.getConexion().prepareStatement("INSERT INTO oh_guest (clase_id,numero,user) VALUES (?,?,?);");
				stmt.setInt(1, this.getClase().getId());
				stmt.setInt(2, this.getNumero());
				stmt.setString(3, guest.toString());
				stmt.execute();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(stmt != null) {
						stmt.close();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void addTrusted(UUID trusted, Player player) {
		trusteds.add(trusted);
		player.sendMessage(Mensajes.HOUSE_TRUSTED_ADDED.toString());
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			PreparedStatement stmt = null;
			
			try {
				stmt = OmegaHouses.getConexion().prepareStatement("INSERT INTO oh_trusted (clase_id,numero,user) VALUES (?,?,?);");
				stmt.setInt(1, this.getClase().getId());
				stmt.setInt(2, this.getNumero());
				stmt.setString(3, trusted.toString());
				stmt.execute();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(stmt != null) {
						stmt.close();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void borrarGuest(UUID guest, Player player) {
		guests.remove(guest);
		player.sendMessage(Mensajes.HOUSE_GUEST_REMOVED.toString());
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			PreparedStatement stmt = null;
			
			try {
				stmt = OmegaHouses.getConexion().prepareStatement("DELETE FROM oh_guest WHERE clase_id=? AND numero=? AND user=?;");
				stmt.setInt(1, this.getClase().getId());
				stmt.setInt(2, this.getNumero());
				stmt.setString(3, guest.toString());
				stmt.execute();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(stmt != null) {
						stmt.close();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void borrarTrusted(UUID trusted, Player player) {
		trusteds.remove(trusted);
		player.sendMessage(Mensajes.HOUSE_TRUSTED_REMOVED.toString());
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			PreparedStatement stmt = null;
			
			try {
				stmt = OmegaHouses.getConexion().prepareStatement("DELETE FROM oh_trusted WHERE clase_id=? AND numero=? AND user=?;");
				stmt.setInt(1, this.getClase().getId());
				stmt.setInt(2, this.getNumero());
				stmt.setString(3, trusted.toString());
				stmt.execute();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(stmt != null) {
						stmt.close();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
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
	
	public static List<Casa> getCasaByUser(UUID uid) {
		return casas.parallelStream().filter(casa -> casa.owner != null && casa.owner.equals(uid)).collect(Collectors.toList());
	}
	
	public static Optional<Casa> getCasaByArea(Location loc) {
		return casas.parallelStream().filter(casa -> {
			if(!loc.getWorld().equals(casa.pos1.getWorld())) {
				return false;
			}
			
			if(loc.getBlockX() >= casa.pos1.getBlockX() && loc.getBlockY() >= casa.pos1.getBlockY() && loc.getBlockZ() >= casa.pos1.getBlockZ()) {
				if(loc.getBlockX() <= casa.pos2.getBlockX() && loc.getBlockY() <= casa.pos2.getBlockY() && loc.getBlockZ() <= casa.pos2.getBlockZ()) {
					return true;
				}
			}
			
			return false;
		}).findFirst();
	}
	
	public static void cargarCasas() throws Exception {		
		PreparedStatement stmtClass = OmegaHouses.getConexion().prepareStatement("SELECT id,precio,cofres,vip FROM oh_class ORDER BY id;");
		ResultSet resClass = stmtClass.executeQuery();
		while(resClass.next()) {
			int id = resClass.getInt(1);
			double precio = resClass.getDouble(2);
			int cofres = resClass.getInt(3);
			boolean vip = resClass.getBoolean(4);
			
			new Clase(id, precio, cofres, false, vip);
		}
		stmtClass.close();
		
		PreparedStatement stmtHouse = OmegaHouses.getConexion().prepareStatement("SELECT clase_id, numero, owner, pos1, pos2, cartel FROM oh_house;");
		ResultSet resHouse = stmtHouse.executeQuery();
		while(resHouse.next()) {
			Optional<Clase> clase = Clase.getClaseById(resHouse.getInt(1));
			if(!clase.isPresent()) {
				continue;
			}
			int numero = resHouse.getInt(2);
			String ownerRaw = resHouse.getString(3);
			UUID owner = null;
			if(ownerRaw != null) {
				owner = UUID.fromString(ownerRaw);
			}
			Location pos1 = Utils.parseLocation(resHouse.getString(4));
			Location pos2 = Utils.parseLocation(resHouse.getString(5));
			Location cartel = Utils.parseLocation(resHouse.getString(6));
			new Casa(clase.get(), numero, owner, pos1, pos2, cartel, new ArrayList<UUID>(), new ArrayList<UUID>());
		}
		stmtHouse.close();
		
		PreparedStatement stmtGuest = OmegaHouses.getConexion().prepareStatement("SELECT clase_id, numero, user FROM oh_guest;");
		ResultSet resGuest = stmtGuest.executeQuery();
		while(resGuest.next()) {
			Optional<Clase> clase = Clase.getClaseById(resGuest.getInt(1));
			if(!clase.isPresent()) {
				continue;
			}
			int numero = resGuest.getInt(2);
			Optional<Casa> casa = Casa.getCasaByClaseYNumero(clase.get(), numero);
			if(!casa.isPresent()) {
				continue;
			}
			casa.get().guests.add(UUID.fromString(resGuest.getString(3)));
		}
		stmtGuest.close();
		
		PreparedStatement stmtTrusted = OmegaHouses.getConexion().prepareStatement("SELECT clase_id, numero, user FROM oh_trusted;");
		ResultSet resTrusted = stmtTrusted.executeQuery();
		while(resTrusted.next()) {
			Optional<Clase> clase = Clase.getClaseById(resTrusted.getInt(1));
			if(!clase.isPresent()) {
				continue;
			}
			int numero = resTrusted.getInt(2);
			Optional<Casa> casa = Casa.getCasaByClaseYNumero(clase.get(), numero);
			if(!casa.isPresent()) {
				continue;
			}
			casa.get().trusteds.add(UUID.fromString(resTrusted.getString(3)));
		}
		stmtTrusted.close();
		
		PreparedStatement stmtTokens = OmegaHouses.getConexion().prepareStatement("SELECT player, clase_id FROM oh_tokens;");
		ResultSet resTokens = stmtTokens.executeQuery();
		while(resTokens.next()) {
			UUID player = UUID.fromString(resTokens.getString(1));
			Optional<Clase> clase = Clase.getClaseById(resTokens.getInt(2), true);
			if(!clase.isPresent()) {
				continue;
			}
			new CasaToken(player, clase.get(), false);
		}
		stmtTokens.close();
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
