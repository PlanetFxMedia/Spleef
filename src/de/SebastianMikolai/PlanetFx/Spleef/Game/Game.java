package de.SebastianMikolai.PlanetFx.Spleef.Game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import de.SebastianMikolai.PlanetFx.Spleef.Spleef;
import de.SebastianMikolai.PlanetFx.Spleef.Utils.ChatUtils;

public class Game {
	
	private Spleef main;
	private String name;
	private Vector l1;
	private Vector l2;
  	private Location spawn;
  	private String worldName;
  	private World world;
  	private int minPlayers;
  	private int maxPlayers;
  	private int deathHeight;
  	private int layerspace;
  	private static boolean running = false;
  	private static boolean preparing = false;
  	private GameEventListener listener;
  	private BukkitTask startTask;
  	
  	public Game(Spleef _main, String _name, int _minPlayers, int _maxPlayers, String _worldName, Vector _l1, Vector _l2, int _layerspace) {
  		main = _main;
  		l1 = _l1;
  		l2 = _l2;
    	minPlayers = _minPlayers;
    	maxPlayers = _maxPlayers;
    	name = _name;
    	deathHeight = ((int)((_l1.getY() <= _l2.getY() ? _l1.getY() : _l2.getY()) - 1.0D));
    	worldName = _worldName;
    	layerspace = _layerspace;
    	listener = new GameEventListener(this);
  	}
  	
  	public boolean start(final boolean forced) {
  		if ((!running) && (isWorldValid()) && (!preparing)) {
  			preparing = true;
  			for (Player p : Bukkit.getOnlinePlayers()) {
  				p.teleport(this.spawn);
  			}
  			startTask = Bukkit.getScheduler().runTaskTimer(main, new Runnable() {
  				int seconds = 30;
				public void run() {
  					if (this.seconds <= 0) {
  						if ((Bukkit.getOnlinePlayers().size() >= Game.this.minPlayers) || ((forced) && (Bukkit.getOnlinePlayers().size() >= 2))) {
  							for (Player p : Bukkit.getOnlinePlayers()) {
  								ChatUtils.sendMessage(p, "&eDas Spiel startet jetzt.");
  								p.getInventory().addItem(new ItemStack[] { new ItemStack(Material.DIAMOND_SPADE) });
  							}
  							Game.running = true;
  							Game.preparing = false;
  						} else {
  							for (Player p : Bukkit.getOnlinePlayers()) {
  								ChatUtils.sendMessage(p, "&eDas Spiel konnte aufgrund von zu wenigen Spielern nicht gestartet werden.");
  								Spleef.sendToServer(p, "lobby");
  							}
  							Game.preparing = false;
  						}
  						Game.this.startTask.cancel();
  						return;
  					}
  					if ((this.seconds == 30) || (this.seconds == 20) || (this.seconds == 15) || (this.seconds == 10) || ((this.seconds <= 5) && (this.seconds >= 1))) {
  						for (Player p : Bukkit.getOnlinePlayers()) {
  							ChatUtils.sendMessage(p, "&eDas Spiel startet in " + this.seconds + " Sekunden.");
  						}
  					}
  					this.seconds -= 1;
  				}
  			}, 0L, 20L);
  			return true;
  		}
  		return false;
  	}
  	
  	public void stop() {
  		if (running) {
  			running = false;
  		}
  		preparing = false;
  		for (Player p : Bukkit.getOnlinePlayers()) {
	    	ChatUtils.sendMessage(p, "&cDu hast das Spiel verloren.");
			Spleef.sendToServer(p, "lobby");
  		}
  		genArenaFloor();
  		if (this.startTask != null) {
  			try {
  				this.startTask.cancel();
  			} catch (Exception e) {}
  		}
  	}
  	
  	public Spleef getSpleef() {
  		return this.main;
  	}
  	
  	public List<Player> getPlayers() {
  		List<Player> players = new ArrayList<Player>();
  		for (Player p : Bukkit.getOnlinePlayers()) {
  			players.add(p);
  		}
  		return players;
  	}
  	
  	public int getPlayerCount() {
  		return Bukkit.getOnlinePlayers().size();
  	}
  	
  	public boolean isPlayerInGame(Player p) {
  		return Bukkit.getOnlinePlayers().contains(p);
  	}
  	
  	public boolean isRunning() {
  		return running;
  	}
  	
  	public boolean isPreparign() {
  		return preparing;
  	}
  	
  	public void addPlayer(Player p) {
  		if (isWorldValid()) {
  			if (!running) {
  				if (Bukkit.getOnlinePlayers().size() + 1 <= this.maxPlayers) {
  					p.getInventory().clear();
  					p.getInventory().setArmorContents(null);
  					p.getInventory().setItem(8, this.main.getLeaveItem());
  					p.setGameMode(GameMode.SURVIVAL);
  					p.teleport(this.spawn);
  					p.setFoodLevel(20);
  					p.setTotalExperience(0);
  					p.updateInventory();
  					if (Bukkit.getOnlinePlayers().size() >= this.minPlayers) {
  						start(false);
  					}
  				} else {
  					ChatUtils.sendMessage(p, "&cDas Spiel ist voll.");
  				}
  			} else {
  				ChatUtils.sendMessage(p, "&cDas Spiel läuft bereits.");
  			}
  		} else {
  			ChatUtils.sendMessage(p, "&cDie Game-Welt scheint nicht geladen zu sein. Bitte kontaktiere einen Admin/Moderator.");
  		}
  	}
  	
	public void removePlayer(Player p) {
  	    if (running) {
  	    	if (Bukkit.getOnlinePlayers().size() == 2 || Bukkit.getOnlinePlayers().size() == 1) {
  	    		ChatUtils.sendMessage(p, "&aDu hast das Spiel gewonnen.");
  	  			Spleef.sendToServer(p, "lobby");
  	  			stop();
  	    	} else {
  	    		ChatUtils.sendMessage(p, "&cDu hast das Spiel verloren.");
  				Spleef.sendToServer(p, "lobby");
  	    	}
  		} else {
  			ChatUtils.sendMessage(p, "&cDas Spiel wurde beendet.");
			Spleef.sendToServer(p, "lobby");
  		}
  	}
  	
  	public int getDeathHeight() {
  		return this.deathHeight;
  	}
  	
  	public void genArenaFloor() {
  		if (isWorldValid()) {
  			int coordX1 = getSmaller(this.l1.getBlockX(), this.l2.getBlockX());
  			int coordX2 = getBigger(this.l1.getBlockX(), this.l2.getBlockX());
  			int coordY1 = getSmaller(this.l1.getBlockY(), this.l2.getBlockY());
  			int coordY2 = getBigger(this.l1.getBlockY(), this.l2.getBlockY());
  			int coordZ1 = getSmaller(this.l1.getBlockZ(), this.l2.getBlockZ());
  			int coordZ2 = getBigger(this.l1.getBlockZ(), this.l2.getBlockZ());
  			int lastY = coordY1;
  			for (int y = coordY1; y <= coordY2; y++) {
  				if ((y == coordY2) || (y == coordY1) || (y - lastY > this.layerspace)) {
  					lastY = y;
  					for (int x = coordX1; x <= coordX2; x++) {
  						for (int z = coordZ1; z <= coordZ2; z++) {
  							new Location(this.world, x, y, z).getBlock().setType(Material.NETHERRACK);
  						}
  					}
  				}
  			}
  		}
  	}
  	
  	public int getSmaller(int x, int y) {
  		if (x < y) {
  			return x;
  		}
  		return y;
  	}
  	
  	public int getBigger(int x, int y) {
  		if (x > y) {
  			return x;
  		}
  		return y;
  	}
  	
  	public boolean isWorldValid() {
  		if (this.world == null) {
  			if (Bukkit.getWorld(this.worldName) == null) {
  				return false;
  			}
  			this.world = Bukkit.getWorld(this.worldName);
  			this.spawn = new Location(this.world, (this.l1.getX() + this.l2.getX()) / 2.0D, this.l1.getY() >= this.l2.getY() ? this.l1.getY() : this.l2.getY(), (this.l1.getZ() + this.l2.getZ()) / 2.0D);
  			this.spawn.add(0.0D, 2.0D, 0.0D);
  			genArenaFloor();
  			return true;
  		}
  		return true;
  	}
  	
  	public String getName() {
  		return this.name;
  	}
  	
  	public Vector getPos1() {
  		return this.l1;
  	}
  	
  	public Vector getPos2() {
  		return this.l2;
  	}
  	
  	public String getWorldName() {
  		return this.worldName;
  	}
  	
  	public int getMaxPlayers() {
  		return this.maxPlayers;
  	}
  	
  	public int getMinPlayers() {
  		return this.minPlayers;
  	}
  	
  	public int getLayerSpace() {
  		return this.layerspace;
  	}
  	
  	public void finalize() throws Throwable {
  		this.listener.finalize();
  		stop();
  	}
}