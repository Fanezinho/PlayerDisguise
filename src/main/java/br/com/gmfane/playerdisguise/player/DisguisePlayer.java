package br.com.gmfane.playerdisguise.player;

import org.bukkit.entity.Player;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public final class DisguisePlayer {

	private final Player player;

	private final String originalName;
	private String currentDisguiseName;

	public DisguisePlayer(Player player) {
		this.player = player;

		this.originalName = player.getName();
		this.currentDisguiseName = null;
	}

	public Player getPlayer() {
		return player;
	}

	public String getOriginalName() {
		return originalName;
	}

	public String getCurrentDisguiseName() {
		return currentDisguiseName;
	}

	public void setCurrentDisguiseName(String currentDisguiseName) {
		this.currentDisguiseName = currentDisguiseName;
	}
}
