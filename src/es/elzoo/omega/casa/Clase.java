package es.elzoo.omega.casa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Clase {
	private static List<Clase> clases = new ArrayList<Clase>();
	
	int id;
	double precio;
	int cofres;
	
	public Clase(int tipo, double precio, int cofres) {
		super();
		this.id = tipo;
		this.precio = precio;
		this.cofres = cofres;
		
		clases.add(this);
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
	
	public static void cargarClases() {
		//TODO Cargar clases mysql
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
