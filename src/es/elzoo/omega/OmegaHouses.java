package es.elzoo.omega;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import es.elzoo.omega.casa.Casa;
import es.elzoo.omega.casa.CasaAsistenteCrear;
import es.elzoo.omega.comandos.ComandoHouse;
import es.elzoo.omega.gui.GUIEventos;
import net.milkbowl.vault.economy.Economy;

public class OmegaHouses extends JavaPlugin {
	static String url;
	static String user;
	static String pass;
	private static Connection conexion;
	
	private static Economy economy;
	
	@Override
	public void onEnable() {
		//Crear configuración con valores por defecto
		getConfig().addDefault("url", "localhost");
		getConfig().addDefault("port", "3306");
		getConfig().addDefault("database", "database");
		getConfig().addDefault("user", "username");
		getConfig().addDefault("pass", "password");
		getConfig().options().copyDefaults(true);
		saveConfig();
		
		if(!setupEconomy()) {
			this.getLogger().severe("Vault plugin not found. Shutting down...");
			Bukkit.getServer().shutdown();
			return;
		}
		
		//Conexión a MySQL
		try {
			Bukkit.getLogger().info("Connecting to MySQL...");
			
			url = "jdbc:mysql://"+getConfig().getString("url")+":"+getConfig().getString("port")+"/"+getConfig().getString("database")+"?autoReconnect=true";
			user = getConfig().getString("user");
			pass = getConfig().getString("pass");			
			conexion = DriverManager.getConnection(url, user, pass);	
			
			crearTablas();
			
			Casa.cargarCasas();
		} catch(Exception e) {
			e.printStackTrace();
			Bukkit.getLogger().severe("Error connecting to MySQL. Shutting down...");
			Bukkit.getServer().shutdown();
		}
		
		//Registrar eventos
		getServer().getPluginManager().registerEvents(new GUIEventos(), this);
		getServer().getPluginManager().registerEvents(new EventosCasa(), this);
		
		//Registrar comandos
		getCommand("house").setExecutor(new ComandoHouse());
		getCommand("house").setTabCompleter(new ComandoHouse());
		
		//Tarea asistente
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
			CasaAsistenteCrear.tickAsistente();
		}, 20, 20); 
	}
	
	@Override
	public void onDisable() {	
		try {
			Bukkit.getLogger().info("Closing MySQL connection...");
			
			if(conexion != null && !conexion.isClosed()) {
				conexion.close();
			}
			
			Bukkit.getLogger().info("Connection closed.");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null) {
			return false;
		}
		
		economy = rsp.getProvider();
		return economy != null;
	}
	
	public static Economy getEconomy() {
		return economy;
	}
	
	public static void checkConexion() {
		try {
			if(conexion == null || conexion.isClosed() || !conexion.isValid(0)) {
				conexion = DriverManager.getConnection(url, user, pass);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void crearTablas() throws Exception {		
		PreparedStatement[] stmts = {
			conexion.prepareStatement("CREATE TABLE IF NOT EXISTS oh_house(clase_id int, numero int, owner text, pos1 text, pos2 text, cartel text, UNIQUE(clase_id, numero));"),
			conexion.prepareStatement("CREATE TABLE IF NOT EXISTS oh_guest(clase_id int, numero int, user text);"),
			conexion.prepareStatement("CREATE TABLE IF NOT EXISTS oh_trusted(clase_id int, numero int, user text);"),
			conexion.prepareStatement("CREATE TABLE IF NOT EXISTS oh_class(id int, precio double, cofres int);")
		};
		
		for(int i=0,len=stmts.length; i<len; i++) {
			stmts[i].execute();
			stmts[i].close();
		}
	}
	
	public static Connection getConexion() {
		checkConexion();
		return conexion;
	}
}
