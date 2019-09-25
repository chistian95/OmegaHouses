package es.elzoo.omega;

import org.bukkit.ChatColor;

public enum Mensajes {
	PLAYER_ONLY("&cThis command can only be used by a player."),
	NO_PERMISOS("&cYou do not have permission to use this command."),
	
	HOUSE_CREATE_AREA("&aSelect a region for the house"),
	HOUSE_CREATE_LEFT_POS("&aLeft position set. Use &r/house create &afor the next step when you are ready."),
	HOUSE_CREATE_RIGHT_POS("&aRight position set. Use &r/house create &afor the next step when you are ready."),
	HOUSE_CREATE_SIGN("&aClick on a sign to set the main door. &rNote: It must be above or to the side of an iron door."),
	HOUSE_CREATE_NO_DOOR("&cCould not find a door. You must place the sign above or to the side of an iron door."),
	HOUSE_CREATE_CANCEL("&7Assistant canceled."),
	HOUSE_NO_ASSISTANT("&cYou do not have any assistant to cancel.");
	
	private String msg;
	
	private Mensajes(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String toString() {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}
