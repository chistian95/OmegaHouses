package es.elzoo.omega.casa;

import java.util.Comparator;

public class Clase {
	int tipo;
	double precio;
	int cofres;
	
	public Clase(int tipo, double precio, int cofres) {
		super();
		this.tipo = tipo;
		this.precio = precio;
		this.cofres = cofres;
	}
	
	public int getNextNumero() {
		return Casa.getCasas().parallelStream()
			.filter(casa -> casa.getClase().equals(this))
			.max(Comparator.comparing(Casa::getNumero))
			.map(casa -> casa.getNumero() + 1)
			.orElse(1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + tipo;
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
		if (tipo != other.tipo)
			return false;
		return true;
	}
}
