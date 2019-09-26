package es.elzoo.omega.casa;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import es.elzoo.omega.OmegaHouses;

public class Clase {
	private static Plugin plugin = Bukkit.getPluginManager().getPlugin("OmegaHouses");
	private static List<Clase> clases = new ArrayList<Clase>();
	
	int id;
	double precio;
	int cofres;
	
	public Clase(int tipo, double precio, int cofre) {
		this(tipo,precio,cofre,false);
	}
	
	public Clase(int tipo, double precio, int cofres, boolean mysql) {
		super();
		this.id = tipo;
		this.precio = precio;
		this.cofres = cofres;
		
		clases.add(this);
		clases.sort((a,b) -> b.id-a.id);
		
		if(mysql) {
			Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
				PreparedStatement stmtClase = null;
				
				try {
					stmtClase = OmegaHouses.getConexion().prepareStatement("INSERT INTO oh_class (id,precio,cofres) VALUES (?,?,?);");
					stmtClase.setInt(1, this.id);
					stmtClase.setDouble(2, this.precio);
					stmtClase.setInt(3, this.cofres);
					stmtClase.execute();
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if(stmtClase != null) {
							stmtClase.close();
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	public int getNextNumero() {
		return Casa.getCasas().parallelStream()
			.filter(casa -> casa.getClase().equals(this))
			.max(Comparator.comparing(Casa::getNumero))
			.map(casa -> casa.getNumero() + 1)
			.orElse(1);
	}
	
	public static Optional<Clase> getClaseById(int id) {
		return clases.parallelStream().filter(cl -> cl.id == id).findFirst();
	}
	
	public static List<Clase> getClases() {
		return clases;
	}
	
	public int getId() {
		return id;
	}
	public double getPrecio() {
		return precio;
	}
	public int getCofres() {
		return cofres;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Clase other = (Clase) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
