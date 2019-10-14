package es.elzoo.omega;

public enum Permisos {
	CASA_CREAR("omegahouses.create"),
	CASA_CREAR_CLASE("omegahouses.create.class"),
	CASA_FORCE_SELL("omegahouses.forcesell"),
	CASA_BYPASS("omegahouses.bypass"),
	CASA_GRANT_TOKEN("omegahouses.grantToken");
	
	private String permiso;
	
	private Permisos(String permiso) {
		this.permiso = permiso;
	}
	
	@Override
	public String toString() {
		return permiso;
	}
}
