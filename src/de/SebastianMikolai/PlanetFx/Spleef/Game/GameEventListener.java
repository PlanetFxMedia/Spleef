package de.SebastianMikolai.PlanetFx.Spleef.Game;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import de.SebastianMikolai.PlanetFx.Spleef.Spleef;
import de.SebastianMikolai.PlanetFx.Spleef.Utils.ChatUtils;

public class GameEventListener implements Listener {
	
	private Game game;
	private Random rnd;
	
	public GameEventListener(Game game) {
		this.game = game;
		game.getSpleef().getServer().getPluginManager().registerEvents(this, game.getSpleef());
		this.rnd = new Random();
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (this.game.isPlayerInGame(e.getPlayer())) {
			if ((e.getAction() == Action.LEFT_CLICK_BLOCK) && (e.getPlayer().getInventory().getItemInMainHand() != null) && (this.game.isRunning()) && (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.DIAMOND_SPADE) &&  (e.getClickedBlock().getType() == Material.SNOW_BLOCK || e.getClickedBlock().getType() == Material.NETHERRACK)) {
				e.getClickedBlock().setType(Material.AIR);
				if (this.rnd.nextBoolean()) {
					e.getPlayer().getInventory().addItem(new ItemStack[] {new ItemStack(Material.SNOW_BALL) });
				}
			}
			if (((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)) && (e.getItem() != null)) {
				if (Spleef.compareItems(e.getItem(), this.game.getSpleef().getLeaveItem())) {
					this.game.removePlayer(e.getPlayer());
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e.getEntityType() == EntityType.PLAYER) {
			Player p = (Player)e.getEntity();
			if (this.game.isPlayerInGame(p)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerItemDrop(PlayerDropItemEvent e) {
		if (this.game.isPlayerInGame(e.getPlayer())) {
			e.setCancelled(false);
			ChatUtils.sendMessage(e.getPlayer(), "&4Du kannst hier hier keine Items droppen.");
			e.getPlayer().getInventory().getItemInMainHand().isSimilar(e.getItemDrop().getItemStack());
			e.getItemDrop().remove();
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent e) {
		if (this.game.isPlayerInGame(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if (this.game.isPlayerInGame(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onCommandInput(PlayerCommandPreprocessEvent e) {
		if ((this.game.isPlayerInGame(e.getPlayer())) && (!e.getPlayer().hasPermission("spleef.cmdbypass"))) {
			e.setCancelled(true);
			ChatUtils.sendMessage(e.getPlayer(), "&4Du kannst hier keine Commands eingeben.");
		}
	}
	
	@EventHandler
	public void onHungerUpdate(FoodLevelChangeEvent e) {
		if (this.game.isPlayerInGame((Player)e.getEntity())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (this.game.isPlayerInGame((Player)e.getWhoClicked())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent e) {
		if ((this.game.isRunning()) && (e.getEntity().getType() == EntityType.SNOWBALL)) {
			Snowball snowball = (Snowball)e.getEntity();
			if ((snowball.getShooter() instanceof Player)) {
				Player p = (Player)snowball.getShooter();
				if (this.game.isPlayerInGame(p)) {
					BlockIterator blockIterator = new BlockIterator(e.getEntity().getLocation().getWorld(), e.getEntity().getLocation().toVector(), e.getEntity().getVelocity().normalize(), 0.0D, 4);
					while (blockIterator.hasNext()) {
						Block block = blockIterator.next();
						if (block.getType() != Material.AIR) {
							if (block.getType() == Material.SNOW_BLOCK || block.getType() == Material.NETHERRACK ) {
								block.setType(Material.AIR);
								break;
							}
							break;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if ((this.game.isPlayerInGame(e.getPlayer())) && (e.getTo().getY() <= this.game.getDeathHeight())) {
			this.game.removePlayer(e.getPlayer());
		}
	}
	
	public void finalize() throws Throwable {
		HandlerList.unregisterAll(this);
	}
}