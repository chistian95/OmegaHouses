package es.elzoo.omega.casa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Sign;

import es.elzoo.omega.Mensajes;
import es.elzoo.omega.casa.gui.GUIElegirClase;

public class CasaAsistenteCrear {
	private static List<CasaAsistenteCrear> usuarios = new ArrayList<CasaAsistenteCrear>();
	
	private String nick;
	private int paso;
	private Location locIzq;
	private Location locDrc;
	private Location cartel;
	private Clase clase;
	
	private CasaAsistenteCrear(String nick) {
		this.nick = nick;
		this.paso = 0;		
		
		usuarios.add(this);
	}
	
	public void next() {
		if(paso == 0) {
			if(!checkSiguientePaso()) {
				Bukkit.getPlayer(nick).sendMessage(Mensajes.HOUSE_CREATE_AREA.toString());
			} else {
				paso++;
				next();
			}
		} else if(paso == 1) {
			if(!checkSiguientePaso()) {
				Bukkit.getPlayer(nick).sendMessage(Mensajes.HOUSE_CREATE_SIGN.toString());
			} else {
				paso++;
				next();
			}
		}else if(paso == 2) {
			if(!checkSiguientePaso()) {
				GUIElegirClase gui = new GUIElegirClase(this);
				gui.abrir(Bukkit.getPlayer(getNick()));
			} else {
				paso++;
				crear();
			}
		}
	}
	
	private void crear() {
		if(paso != 3) {
			return;
		}
		
		usuarios.remove(this);
		
		int xMin = (locIzq.getBlockX() < locDrc.getBlockX() ? locIzq.getBlockX() : locDrc.getBlockX());
		int yMin = (locIzq.getBlockY() < locDrc.getBlockY() ? locIzq.getBlockY() : locDrc.getBlockY());
		int zMin = (locIzq.getBlockZ() < locDrc.getBlockZ() ? locIzq.getBlockZ() : locDrc.getBlockZ());
		
		int xMax = (locIzq.getBlockX() > locDrc.getBlockX() ? locIzq.getBlockX() : locDrc.getBlockX());
		int yMax = (locIzq.getBlockY() > locDrc.getBlockY() ? locIzq.getBlockY() : locDrc.getBlockY());
		int zMax = (locIzq.getBlockZ() > locDrc.getBlockZ() ? locIzq.getBlockZ() : locDrc.getBlockZ());
		
		Location pos1 = new Location(locIzq.getWorld(), xMin, yMin, zMin);
		Location pos2 = new Location(locIzq.getWorld(), xMax, yMax, zMax);
		
		new Casa(clase, null, pos1, pos2, cartel);
		
		Bukkit.getPlayer(nick).sendMessage(Mensajes.HOUSE_CREATED.toString());
	}
	
	public void cancel() {
		Bukkit.getPlayer(nick).sendMessage(Mensajes.HOUSE_CREATE_CANCEL.toString());
		usuarios.remove(this);
	}
	
	private boolean checkSiguientePaso() {
		if(paso == 0) {
			return locIzq != null && locDrc != null;
		}
		if(paso == 1) {
			return cartel != null; 
		}
		if(paso == 2) {
			return clase != null;
		}
		
		return false;
	}
	
	public void leftClick(PlayerInteractEvent event) {
		if(paso == 0) {
			event.setCancelled(true);			
			locIzq = event.getClickedBlock().getLocation();
			
			if(locDrc != null && !locIzq.getWorld().getName().equalsIgnoreCase(locDrc.getWorld().getName())) {
				locDrc = null;
			}
			
			Bukkit.getPlayer(nick).sendMessage(Mensajes.HOUSE_CREATE_LEFT_POS.toString());
		} else if(paso == 1) {
			event.setCancelled(true);
			setCartel(event.getClickedBlock());
		}
	}
	
	public void rightClick(PlayerInteractEvent event) {
		if(paso == 0) {
			event.setCancelled(true);
			locDrc = event.getClickedBlock().getLocation();
			
			if(locIzq != null && !locDrc.getWorld().getName().equalsIgnoreCase(locIzq.getWorld().getName())) {
				locIzq = null;
			}
			
			Bukkit.getPlayer(nick).sendMessage(Mensajes.HOUSE_CREATE_RIGHT_POS.toString());
		} else if(paso == 1) {
			event.setCancelled(true);
			setCartel(event.getClickedBlock());
		}
	}
	
	private void setCartel(Block block) {
		if(!(block.getState().getData() instanceof Sign)) {
			return;
		}		
		Sign cartel = (Sign) block.getState().getData();
		
		boolean hayPuerta = false;
		BlockFace[] faces = new BlockFace[] {BlockFace.DOWN, BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH};
		for(int i=0,len=faces.length; i<len; i++) {
			Block puerta = block.getRelative(cartel.getFacing().getOppositeFace()).getRelative(faces[i]);
			if(puerta.getType().equals(Material.IRON_DOOR) || puerta.getType().equals(Material.IRON_DOOR_BLOCK)) {
				hayPuerta = true;
				break;
			}
		}
		
		if(!hayPuerta) {
			Bukkit.getPlayer(getNick()).sendMessage(Mensajes.HOUSE_CREATE_NO_DOOR.toString());
		} else {
			this.cartel = block.getLocation();
			next();
		}
	}
	
	public void setClase(Clase clase) {
		this.clase = clase;
		next();
	}
	
	private void particulasAsistente() {
		World mundo = locIzq.getWorld();
		Location loc = locIzq.clone();
		int salto = 1;
		
		if(locIzq.distanceSquared(locDrc) > 2500) {
			salto = 5;
		} else if(locIzq.distanceSquared(locDrc) > 400) {
			salto = 2;
		}
		
		int xMin = (locIzq.getBlockX() < locDrc.getBlockX() ? locIzq.getBlockX() : locDrc.getBlockX());
		int yMin = (locIzq.getBlockY() < locDrc.getBlockY() ? locIzq.getBlockY() : locDrc.getBlockY());
		int zMin = (locIzq.getBlockZ() < locDrc.getBlockZ() ? locIzq.getBlockZ() : locDrc.getBlockZ());
		
		int xMax = (locIzq.getBlockX() > locDrc.getBlockX() ? locIzq.getBlockX() : locDrc.getBlockX());
		int yMax = (locIzq.getBlockY() > locDrc.getBlockY() ? locIzq.getBlockY() : locDrc.getBlockY());
		int zMax = (locIzq.getBlockZ() > locDrc.getBlockZ() ? locIzq.getBlockZ() : locDrc.getBlockZ());
		
		for(int x=xMin; x<=xMax; x+=salto) {
			if(x > xMax) {
				x = xMax;
			}
			loc.setX(x+0.5);
			
			for(int y=yMin; y<=yMax; y+=salto) {
				if(y > yMax) {
					y = yMax;
				}
				loc.setY(y+0.5);
				
				for(int z=zMin; z<=zMax; z+=salto) {
					if(z > zMax) {
						z = zMax;
					}
					loc.setZ(z+0.5);
					
					mundo.playEffect(loc, Effect.LAVADRIP, 0);
				}
			}
		}
	}
	
	public static void tickAsistente() {
		usuarios.stream().forEach(asis -> {
			if(asis.getPaso() == 0) {
				if(asis.locIzq == null || asis.locDrc == null) {
					return;
				}
				
				asis.particulasAsistente();
			}
		});
	}
	
	public static Optional<CasaAsistenteCrear> get(String nick) {
		return usuarios.parallelStream().filter(asis -> asis.getNick().equalsIgnoreCase(nick)).findFirst();
	}
	
	public static CasaAsistenteCrear getOrCreate(String nick) {
		Optional<CasaAsistenteCrear> asis = get(nick);
		if(asis.isPresent()) {
			return asis.get();
		}
		
		return new CasaAsistenteCrear(nick);
	}
	
	public String getNick() {
		return nick;
	}
	public int getPaso() {
		return paso;
	}
}
