package br.com.gmfane.playerdisguise.handler;

import java.util.logging.Level;

import org.bukkit.Bukkit;

import br.com.gmfane.playerdisguise.logger.FormattedLogger;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public abstract class SimpleHandler {

	private final String name;
	private final FormattedLogger formattedLogger;
	private final DisguiseHandler disguiseHandler;

	private boolean correctlyStart = true;

	public SimpleHandler(DisguiseHandler disguiseHandler) {
		this.name = getClass().getSimpleName().replace("Handler", "").replace("Manager", "");
		this.disguiseHandler = disguiseHandler;
		this.formattedLogger = new FormattedLogger(disguiseHandler.getLogger(), name);

		formattedLogger.log("Trying to start the '" + name + "' handler.");

		checkStart(initialize());
	}

	public SimpleHandler(DisguiseHandler disguiseHandler, String name) {
		this.name = name;
		this.disguiseHandler = disguiseHandler;
		this.formattedLogger = new FormattedLogger(disguiseHandler.getLogger(), name);

		formattedLogger.log("Trying to start the '" + name + "' manager.");

		checkStart(initialize());
	}

	public abstract boolean initialize();

	protected boolean checkStart(boolean bool) {
		if (bool) {
			getLogger().log("The '%s' handler has been started correctly.", name);
		} else {
			getLogger().log(Level.SEVERE, "The '%s' handler has been not started correctly, stopping the load of the plugin.", name);
			getLogger().log(Level.SEVERE, "The plugin hasn't initialized correctly because the handler '%s' it was not started correctly.", name);
			Bukkit.getPluginManager().disablePlugin(getDisguiseHandler().getPlugin());
			correctlyStart = false;
		}
		return bool;
	}

	public boolean correctlyStart() {
		return correctlyStart;
	}

	public DisguiseHandler getDisguiseHandler() {
		return disguiseHandler;
	}

	protected String getName() {
		return name;
	}

	public FormattedLogger getLogger() {
		return formattedLogger;
	}

}
