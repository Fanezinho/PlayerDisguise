package br.com.gmfane.playerdisguise.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.google.common.base.Preconditions;

import br.com.gmfane.playerdisguise.Utilitaries;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public final class PlayerDisguiseEvent extends PlayerEvent {

	private static final HandlerList handlers = new HandlerList();

	private final DisguiseReason reason;
	private String newName, newDisplayName, newPlayerListName;

	public PlayerDisguiseEvent(Player disguise, String newName, String newDisplayName, String newPlayerListName) {
		this(disguise, DisguiseReason.COMMAND, newName, newDisplayName, newPlayerListName);
	}

	public PlayerDisguiseEvent(Player disguise, DisguiseReason reason, String newName, String newDisplayName, String newPlayerListName) {
		super(disguise);
		this.reason = reason;
		this.newName = newName;
		this.newDisplayName = newDisplayName;
		this.newPlayerListName = newPlayerListName;
	}

	public DisguiseReason getReason() {
		return reason;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		Preconditions.checkNotNull(newName);
		Preconditions.checkArgument(Utilitaries.isNameValid(newName));
		this.newName = newName;
	}

	public String getNewDisplayName() {
		return newDisplayName;
	}

	public void setNewDisplayName(String newDisplayName) {
		this.newDisplayName = newDisplayName;
	}

	public String getNewPlayerListName() {
		return newPlayerListName;
	}

	public void setNewPlayerListName(String newPlayerListName) {
		this.newPlayerListName = newPlayerListName;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public static enum DisguiseReason {
		COMMAND,
		LOGIN_OVERRIDE;
	}
}
