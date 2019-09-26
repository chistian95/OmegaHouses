package es.elzoo.omega;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Utils {
	public static Location parseLocation(String locationRaw) {
		String[] componentes = locationRaw.split(",");
		World mundo = Bukkit.getWorld(componentes[0]);
		int x = 0;
		int y = 0;
		int z = 0;
		
		try {
			x = Integer.parseInt(componentes[1]);
			y = Integer.parseInt(componentes[2]);
			z = Integer.parseInt(componentes[3]);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return new Location(mundo, x, y, z);
	}
	
	public static String locationToString(Location loc) {
		return loc.getWorld().getName()+","+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ();
	}
}
