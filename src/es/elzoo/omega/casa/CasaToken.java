package es.elzoo.omega.casa;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import es.elzoo.omega.OmegaHouses;

public class CasaToken {
	private static Plugin plugin = Bukkit.getPluginManager().getPlugin("OmegaHouses");
	private static List<CasaToken> tokens = new ArrayList<CasaToken>();
	
	private UUID player;
	private Clase clase;
	
	public CasaToken(UUID player, Clase clase) {
		this(player, clase, true);
	}
	
	public CasaToken(UUID player, Clase clase, boolean mysql) {
		this.player = player;
		this.clase = clase;
		tokens.add(this);
		
		if(mysql) {
			Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
				PreparedStatement stmt = null;
				try {
					stmt = OmegaHouses.getConexion().prepareStatement("INSERT INTO oh_tokens (player, clase_id) VALUES (?,?);");
					stmt.setString(1, player.toString());
					stmt.setInt(2, clase.getId());
					stmt.execute();
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if(stmt != null) {
							stmt.close();
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	public void consume() {
		tokens.remove(this);
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			PreparedStatement stmt = null;
			try {
				stmt = OmegaHouses.getConexion().prepareStatement("DELETE FROM oh_tokens WHERE player=? AND clase_id=? LIMIT 1;");
				stmt.setString(1, player.toString());
				stmt.setInt(2, clase.getId());
				stmt.execute();
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(stmt != null) {
						stmt.close();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static Optional<CasaToken> getToken(UUID player, Clase clase) {
		return tokens.parallelStream().filter(tk -> tk.player.equals(player) && tk.clase.equals(clase)).findFirst();
	}
}
