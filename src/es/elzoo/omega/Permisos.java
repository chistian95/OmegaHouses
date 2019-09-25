package es.elzoo.omega;

public enum Permisos {
	CASA_CREAR("omegahouses.create");
	
	private String permiso;
	
	private Permisos(String permiso) {
		this.permiso = permiso;
	}
	
	@Override
	public String toString() {
		return permiso;
	}
}
