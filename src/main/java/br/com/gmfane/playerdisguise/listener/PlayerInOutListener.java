package br.com.gmfane.playerdisguise.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import br.com.gmfane.playerdisguise.Utilitaries;
import br.com.gmfane.playerdisguise.event.PlayerDisguiseEvent;
import br.com.gmfane.playerdisguise.event.PlayerDisguiseEvent.DisguiseReason;
import br.com.gmfane.playerdisguise.handler.DisguiseHandler;
import br.com.gmfane.playerdisguise.player.DisguisePlayer;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public final class PlayerInOutListener implements Listener {

	private final DisguiseHandler disguiseHandler;

	public PlayerInOutListener(DisguiseHandler plugin) {
		this.disguiseHandler = plugin;
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		Player online = Bukkit.getPlayerExact(player.getName());

		if (online == null || player.getUniqueId().equals(online.getUniqueId())) {
			return;
		}

		DisguisePlayer fOnline = disguiseHandler.getSkinHandler().getDisguisePlayer(online);

		if (fOnline.getCurrentDisguiseName() != null) {

			PlayerDisguiseEvent disguiseEvent = new PlayerDisguiseEvent(online, DisguiseReason.LOGIN_OVERRIDE, fOnline.getOriginalName(), null, null);
			Bukkit.getPluginManager().callEvent(disguiseEvent);

			if (Utilitaries.isNameInUse(disguiseEvent.getNewName()) || disguiseEvent.getNewName().equals(player.getName()) || Utilitaries.isPlayerListNameInUse(disguiseEvent.getNewPlayerListName())) {
				online.kickPlayer(ChatColor.RED + "Um jogador com o mesmo nome de seu disfarce se conectou. Você foi removido do servidor!");
				return;
			}

			Utilitaries.updateName(online, disguiseEvent.getNewName(), disguiseEvent.getNewDisplayName(), disguiseEvent.getNewPlayerListName());
			Utilitaries.updateEntityModel(disguiseHandler.getSkinHandler(), online, true);

			online.sendMessage(ChatColor.RED + "Um jogador com o mesmo nome de seu disfarce se conectou. Seu disfarce foi removido!");
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		disguiseHandler.getSkinHandler().registerDisguisePlayer(new DisguisePlayer(player));
		disguiseHandler.getSkinHandler().cacheSkinData(player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		DisguisePlayer fPlayer = disguiseHandler.getSkinHandler().getDisguisePlayer(player);
		disguiseHandler.getSkinHandler().unregisterDisguisePlayer(fPlayer);
	}
}
