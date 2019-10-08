package es.elzoo.omega;

import org.bukkit.ChatColor;

public enum Mensajes {
	PLAYER_ONLY("&cThis command can only be used by a player."),
	NO_PERMISOS("&cYou do not have permissions to do that."),
	
	HOUSE_CREATE_AREA("&7Select a region for the house"),
	HOUSE_CREATE_LEFT_POS("&7Left position set. Use &o/house create &r&7for the next step when you are ready."),
	HOUSE_CREATE_RIGHT_POS("&7Right position set. Use &o/house create &r&7for the next step when you are ready."),
	HOUSE_CREATE_SIGN("&7Click on a sign to set the main door. &oNote: It must be above or to the side of an iron door."),
	HOUSE_CREATE_NO_DOOR("&cCould not find a door. You must place the sign above or to the side of an iron door."),
	HOUSE_CREATE_CANCEL("&7Assistant canceled."),
	HOUSE_NO_ASSISTANT("&cYou do not have any assistant to cancel."),
	HOUSE_CANT_DESTROY_SIGN("&cYou can't destroy the sign of a house. First delete it with &7/house remove <class> <number>&c."),
	HOUSE_BUY_HAS_OWNER("&cThis house is already sold."),
	HOUSE_BUY_NO_MONEY("&cYou do not have enough money to buy this house. You need: &7$"),
	HOUSE_SELL_NO_OWNER("&cYou are not the owner of this house."),
	HOUSE_SELL_ERROR("&cAn error ocurred while selling the house. Try again or contact an admin."),
	HOUSE_CREATED("&7House created."),
	HOUSE_DELETED("&7House deleted."),
	HOUSE_GUEST_ADDED("&7Guest added to house."),
	HOUSE_GUEST_REMOVED("&7Guest removed from house."),
	HOUSE_TRUSTED_ADDED("&7Trusted added to house."),
	HOUSE_TRUSTED_REMOVED("&7Trusted removed from house."),
	HOUSE_BOUGHT("&7House bought."),
	HOUSE_SOLD("&7House sold.");
	
	private String msg;
	
	private Mensajes(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String toString() {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}
