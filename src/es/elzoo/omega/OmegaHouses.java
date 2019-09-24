package es.elzoo.omega;

import java.sql.Connection;
import java.sql.DriverManager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class OmegaHouses extends JavaPlugin {
	static String url;
	static String user;
	static String pass;
	private static Connection conexion;
	
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
		
		//Conexión a MySQL
		try {
			Bukkit.getLogger().info("Connecting to MySQL...");
			
			url = "jdbc:mysql://"+getConfig().getString("url")+":"+getConfig().getString("port")+"/"+getConfig().getString("database")+"?autoReconnect=true";
			user = getConfig().getString("user");
			pass = getConfig().getString("pass");
			conexion = DriverManager.getConnection(url, user, pass);	
			crearTablas();
		} catch(Exception e) {
			e.printStackTrace();
			Bukkit.getServer().shutdown();
		}
		
		//Registrar eventos
		//getServer().getPluginManager().registerEvents(new EventosClanes(), this);
		
		//Registrar comandos
		//getCommand("clan").setExecutor(new ComandoClan());
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
		
	}
	
	public static Connection getConexion() {
		checkConexion();
		return conexion;
	}
}
