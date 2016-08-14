package de.SebastianMikolai.PlanetFx.Spleef;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import de.SebastianMikolai.PlanetFx.Spleef.Game.Game;
import de.SebastianMikolai.PlanetFx.Spleef.Utils.ConfigFile;

public class Spleef extends JavaPlugin {
	
	private static Spleef instance;
	private ConfigurationSection leaveItem;
	private ConfigurationSection infoItem;
	private ConfigurationSection markItem;
	public static Game game;
	private ConfigFile arenas;
	public EventListener listener;
	public Location spawn;
	
	public void onLoad() {
		instance = this;
	}
	

	public static Game getAPI() {
		return game;
	}
	
	public void onEnable() {
		saveDefaultConfig();
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		spawn = Bukkit.getWorld(getConfig().getString("lobbyworldname")).getSpawnLocation();
		arenas = new ConfigFile(this, "arenas", null);
		leaveItem = getConfig().getConfigurationSection("items.leaveItem");
		infoItem = getConfig().getConfigurationSection("items.infoItem");
		markItem = getConfig().getConfigurationSection("items.markItem");
		ConfigurationSection conf = arenas.getConfig().getConfigurationSection("arenas.game");
		int layerspace = 0;
		if ((conf.contains("layerspace")) && (conf.isInt("layerspace"))) {
			layerspace = conf.getInt("layerspace");
		} else {
			conf.set("layerspace", Integer.valueOf(layerspace));
		}
		game = new Game(this, "game", conf.getInt("minPlayers"), conf.getInt("maxPlayers"), conf.getString("world"), new Vector(conf.getInt("x1"), conf.getInt("y1"), conf.getInt("z1")), new Vector(conf.getInt("x2"), conf.getInt("y2"), conf.getInt("z2")), layerspace);
		listener = new EventListener(this);
	}
	
	public void onDisable() {
		game.stop();
		Bukkit.getScheduler().cancelTasks(this);
		saveConfig();
		arenas.saveConfig();
	}
	
	public ItemStack getLeaveItem() {
		ItemStack is = (ItemStack)getItemFromConfig(this.leaveItem);
		return is;
	}
	
	public ItemStack getInfoItem() {
		ItemStack is = (ItemStack)getItemFromConfig(this.infoItem);
		return is;
	}
	
	public ItemStack getMarkItem() {
		ItemStack is = (ItemStack)getItemFromConfig(this.markItem);
	    return is;
	}
	
	public static ItemStack getItemFromConfig(ConfigurationSection cs) {
		Material item = Material.getMaterial(cs.getString("material"));
	    if (item == null) {
	      item = Material.FIRE;
	    }
	    String name = ChatColor.translateAlternateColorCodes('$', cs.getString("name"));
	    String loreRaw = ChatColor.translateAlternateColorCodes('$', cs.getString("lore"));
	    String[] lore = loreRaw.split("/:");
	    short damage = (short)cs.getInt("damage");
	    return renameItem(new ItemStack(item, 1, damage), name, Arrays.asList(lore));
	}
	
	public static ItemStack renameItem(ItemStack item, String name, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
	    if (name != null) {
	      meta.setDisplayName(name);
	    }
	    if (lore != null) {
	      meta.setLore(lore);
	    }
	    item.setItemMeta(meta); 
	    return item;
	}
	
	public static boolean compareItems(ItemStack a, ItemStack b) {
		if ((a == null) || (b == null)) {
			return false;
		}
		if ((a.hasItemMeta()) && (b.hasItemMeta())) {
			if (a.getType() == b.getType()) {
				if ((a.getItemMeta().getDisplayName().equals(b.getItemMeta().getDisplayName())) && (a.getDurability() == b.getDurability())) {
					return true;
		        }
		    }
		    return false;
		}
		if ((a.hasItemMeta() ^ b.hasItemMeta())) {
		    return false;
		}
		return (a.getType() == b.getType()) && (a.getDurability() == b.getDurability());
	}
	
	public boolean isInAnGame(Player p) {
		if (game.isPlayerInGame(p)) {
			return true;
		}
		return false;
	}
	
	public static Spleef getInstance() {
		return instance;
	}
	
	public static void sendToServer(Player p, String servername) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("Connect");
			out.writeUTF(servername);
		} catch (IOException e) {}
		p.sendPluginMessage(Spleef.getInstance(), "BungeeCord", b.toByteArray());
	}
}