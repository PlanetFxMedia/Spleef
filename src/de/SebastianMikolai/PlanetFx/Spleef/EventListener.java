package de.SebastianMikolai.PlanetFx.Spleef;

import java.util.HashSet;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.SebastianMikolai.PlanetFx.Spleef.Game.Game;
import de.SebastianMikolai.PlanetFx.Spleef.Utils.ChatUtils;

public class EventListener implements Listener {
	
	public EventListener(Spleef main) {
		main.getServer().getPluginManager().registerEvents(this, main);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Spleef.getAPI().removePlayer(e.getPlayer());
	}
	
	public HashSet<Block> getSurroundingBlocks(Block block) {
		HashSet<Block> blocks = new HashSet<Block>();
		blocks.add(block.getRelative(BlockFace.UP));
		blocks.add(block.getRelative(BlockFace.EAST));
		blocks.add(block.getRelative(BlockFace.WEST));
		blocks.add(block.getRelative(BlockFace.NORTH));
		blocks.add(block.getRelative(BlockFace.SOUTH)); 
		return blocks;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Game game = Spleef.getAPI();
		if (game != null) {
			game.addPlayer(e.getPlayer());
		} else {
			ChatUtils.sendMessage(e.getPlayer(), "&cDieses Spiel funktioniert momentan nicht.");
			Spleef.sendToServer(e.getPlayer(), "lobby");
		}
	}
}