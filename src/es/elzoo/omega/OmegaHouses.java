package es.elzoo.omega;

import java.sql.Connection;
import java.sql.DriverManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

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
			//TODO Descomentar esto
			//conexion = DriverManager.getConnection(url, user, pass);	
			crearTablas();
		} catch(Exception e) {
			e.printStackTrace();
			//TODO Descomentar esto
			//Bukkit.getServer().shutdown();
		}
		
		//Registrar eventos
		getServer().getPluginManager().registerEvents(new GUIEventos(), this);
		getServer().getPluginManager().registerEvents(new EventosCasa(), this);
		
		//Registrar comandos
		getCommand("house").setExecutor(new ComandoHouse());
		
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
		//TODO Crear tablas mysql
	}
	
	public static Connection getConexion() {
		checkConexion();
		return conexion;
	}
}
