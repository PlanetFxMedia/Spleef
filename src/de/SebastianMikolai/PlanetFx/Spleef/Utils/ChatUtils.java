package de.SebastianMikolai.PlanetFx.Spleef.Utils;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class ChatUtils {

	public static void sendMessage(Player p, String msg) {
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&9Spleef&7]&r " + msg));
	}
}