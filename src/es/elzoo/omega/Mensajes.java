package es.elzoo.omega;

import org.bukkit.ChatColor;

public enum Mensajes {
	PLAYER_ONLY("&cThis command can only be used by a player."),
	NO_PERMISOS("&cYou do not have permissions to do that."),
	
	HOUSE_CREATE_AREA("&aSelect a region for the house"),
	HOUSE_CREATE_LEFT_POS("&aLeft position set. Use &r/house create &afor the next step when you are ready."),
	HOUSE_CREATE_RIGHT_POS("&aRight position set. Use &r/house create &afor the next step when you are ready."),
	HOUSE_CREATE_SIGN("&aClick on a sign to set the main door. &rNote: It must be above or to the side of an iron door."),
	HOUSE_CREATE_NO_DOOR("&cCould not find a door. You must place the sign above or to the side of an iron door."),
	HOUSE_CREATE_CANCEL("&7Assistant canceled."),
	HOUSE_NO_ASSISTANT("&cYou do not have any assistant to cancel."),
	HOUSE_CANT_DESTROY_SIGN("&cYou can't destroy the sign of a house. First delete it with &r/house remove <class> <number>&c."),
	HOUSE_BUY_HAS_OWNER("&cThis house is already sold."),
	HOUSE_BUY_NO_MONEY("&cYou do not have enough money to buy this house. You need: &r$"),
	HOUSE_SELL_NO_OWNER("&cYou are not the owner of this house."),
	HOUSE_SELL_ERROR("&cAn error ocurred while selling the house. Try again or contact an admin."),
	HOUSE_CREATED("&aHouse created."),
	HOUSE_DELETED("&aHouse deleted."),
	HOUSE_GUEST_ADDED("&aGuest added to house."),
	HOUSE_GUEST_REMOVED("&aGuest removed from house."),
	HOUSE_TRUSTED_ADDED("&aTrusted added to house."),
	HOUSE_TRUSTED_REMOVED("&aTrusted removed from house."),
	HOUSE_BOUGHT("&aHouse bought."),
	HOUSE_SOLD("&aHouse sold.");
	
	private String msg;
	
	private Mensajes(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String toString() {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}
