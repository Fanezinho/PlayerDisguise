package br.com.gmfane.playerdisguise;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import br.com.gmfane.playerdisguise.command.DisguiseCommand;
import br.com.gmfane.playerdisguise.handler.DisguiseHandler;
import br.com.gmfane.playerdisguise.listener.PlayerInOutListener;
import br.com.gmfane.playerdisguise.logger.FormattedLogger;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class PlayerDisguise extends JavaPlugin {

	private static final String REQUIRED_VERSION = "v1_7_R4";

	private final DisguiseHandler disguiseHandler;
	private final FormattedLogger formattedLogger;

	public PlayerDisguise() {
		this.formattedLogger = new FormattedLogger(getLogger(), null);
		this.disguiseHandler = new DisguiseHandler(this);
	}

	@Override
	public void onEnable() {
		getFormattedLogger().log("Starting the plugin " + getName() + " version " + getDescription().getVersion() + "...");

		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		if (!version.equals(REQUIRED_VERSION)) {
			getFormattedLogger().log("Incompatible server implementation! Please, use %s", REQUIRED_VERSION);
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		if (!disguiseHandler.initialize()) {
			getFormattedLogger().log("Failed to start the plugin. The plugin will be disable to avoid further damage!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		Bukkit.getPluginManager().registerEvents(new PlayerInOutListener(disguiseHandler), this);
		getCommand("disguise").setExecutor(new DisguiseCommand(disguiseHandler));

		getFormattedLogger().log("The plugin " + getName() + " version " + getDescription().getVersion() + " was started correcly.");
	}

	@Override
	public void onDisable() {
		if (disguiseHandler != null)
			disguiseHandler.getSkinHandler().onDisable();
	}

	public FormattedLogger getFormattedLogger() {
		return formattedLogger;
	}
}
