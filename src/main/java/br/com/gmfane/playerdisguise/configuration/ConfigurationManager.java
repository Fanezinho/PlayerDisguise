package br.com.gmfane.playerdisguise.configuration;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import br.com.gmfane.playerdisguise.handler.DisguiseHandler;
import br.com.gmfane.playerdisguise.handler.SimpleHandler;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class ConfigurationManager extends SimpleHandler {

	private static final String CONFIG_FILE_NAME = "config.yml";

	private File configFile;
	private FileConfiguration config;

	public ConfigurationManager(DisguiseHandler disguiseHandler) {
		super(disguiseHandler);
	}

	public boolean initialize() {
		try {
			this.configFile = new File(getDisguiseHandler().getPlugin().getDataFolder(), CONFIG_FILE_NAME);
			if (!configFile.exists()) {
				getLogger().log(Level.WARNING, "The configuration file '%s' doesn't exist. Creating...", CONFIG_FILE_NAME);
				getDisguiseHandler().getPlugin().saveResource(CONFIG_FILE_NAME, true);
			}
			this.config = YamlConfiguration.loadConfiguration(configFile);

			return true;
		} catch (Exception exception) {
			getLogger().error("Error to link the configuration files with the plugin.", exception);
			return false;
		}
	}

	public File getConfigFile() {
		return configFile;
	}

	public FileConfiguration getConfig() {
		return config;
	}
}
