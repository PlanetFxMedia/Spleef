package de.SebastianMikolai.PlanetFx.Spleef.Utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigFile {
	
	private FileConfiguration config = null;
	private File configFile = null;
	private JavaPlugin plugin;
	private String path;
	private String name;
	
	public ConfigFile(JavaPlugin _plugin, String _name, String _folder) {
		plugin = _plugin;
		if (!_name.substring(_name.length() - 4).equals(".yml")) {
			name = (_name + ".yml");
		} else {
			name = _name;
		}
		if (_folder != null) {
			if (_folder.startsWith("\\", 0)) {
				path = (plugin.getDataFolder().getPath() + _folder);
			} else {
				path = (plugin.getDataFolder().getPath() + "\\" + _folder);
			}
		} else {
			path = plugin.getDataFolder().getPath();
		}
	}
	
	public void reloadConfig() {
		if (configFile == null) {
			configFile = new File(path, name);
			if (!configFile.exists()) {
				try {
					configFile.getParentFile().mkdirs();
					configFile.createNewFile();
				} catch (IOException e) {
					plugin.getLogger().log(Level.SEVERE, "Problem creating the File " + configFile, e);
				}
			}
		}
		config = YamlConfiguration.loadConfiguration(configFile);
	}
	
	public FileConfiguration getConfig() {
		if (config == null) {
			reloadConfig();
		}
		return config;
	}
	
	public void saveConfig() {
		if (config == null || configFile == null) {
			return;
		}
		try {
			getConfig().save(configFile);
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Error while saving the config to " + configFile, e);
		}
	}
}