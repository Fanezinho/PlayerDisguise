package br.com.gmfane.playerdisguise.handler;

import br.com.gmfane.playerdisguise.PlayerDisguise;
import br.com.gmfane.playerdisguise.configuration.ConfigurationManager;
import br.com.gmfane.playerdisguise.data.DataManager;
import br.com.gmfane.playerdisguise.data.skin.SkinHandler;
import br.com.gmfane.playerdisguise.logger.FormattedLogger;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class DisguiseHandler {

	private final PlayerDisguise playerDisguise;

	private ConfigurationManager configurationManager;
	private DataManager dataManager;
	private SkinHandler skinHandler;

	public DisguiseHandler(PlayerDisguise playerDisguise) {
		this.playerDisguise = playerDisguise;
	}

	public boolean initialize() {
		this.configurationManager = new ConfigurationManager(this);
		if (!configurationManager.correctlyStart())
			return false;

		this.dataManager = new DataManager(this);
		if (!dataManager.correctlyStart())
			return false;

		this.skinHandler = new SkinHandler(this);
		if (!skinHandler.correctlyStart())
			return false;

		return true;
	}

	public ConfigurationManager getConfigurationManager() {
		return configurationManager;
	}

	public DataManager getDataManager() {
		return dataManager;
	}

	public PlayerDisguise getPlugin() {
		return playerDisguise;
	}

	public SkinHandler getSkinHandler() {
		return skinHandler;
	}

	public FormattedLogger getLogger() {
		return getPlugin().getFormattedLogger();
	}

}
