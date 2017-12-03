package br.com.gmfane.playerdisguise.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import br.com.gmfane.playerdisguise.Utilitaries;
import br.com.gmfane.playerdisguise.event.PlayerDisguiseEvent;
import br.com.gmfane.playerdisguise.handler.DisguiseHandler;
import br.com.gmfane.playerdisguise.player.DisguisePlayer;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class DisguiseCommand implements CommandExecutor {

	private final DisguiseHandler disguiseHandler;

	public DisguiseCommand(DisguiseHandler disguiseHandler) {
		this.disguiseHandler = disguiseHandler;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Este comando é reservado para jogadores!");
			return true;
		}

		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "A sintaxe correta para o uso do comando é: /disguise <nome>");
			return true;
		}

		if (args[0].length() > 16) {
			sender.sendMessage(ChatColor.RED + "Seu não pode conter mais que 16 caractéres!");
			return true;
		}

		Player player = (Player) sender;
		DisguisePlayer fPlayer = disguiseHandler.getSkinHandler().getDisguisePlayer(player);
		String newName = args[0];

		if (!Utilitaries.isNameValid(newName)) {
			sender.sendMessage(ChatColor.RED + "O nome especificado não é valido!");
			return true;
		}

		PlayerDisguiseEvent event = new PlayerDisguiseEvent(player, newName, null, null);
		Bukkit.getPluginManager().callEvent(event);

		if (Utilitaries.isNameInUse(event.getNewName()) || Utilitaries.isPlayerListNameInUse(event.getNewPlayerListName())) {
			sender.sendMessage(ChatColor.RED + "Já existe um jogador conectado com esse nome!");
			return true;
		}

		Utilitaries.updateName(player, event.getNewName(), event.getNewDisplayName(), event.getNewPlayerListName());
		Utilitaries.updateEntityModel(disguiseHandler.getSkinHandler(), player, true);
		fPlayer.setCurrentDisguiseName(newName);

		player.sendMessage(ChatColor.GREEN + "Você agora está disfarçado como " + ChatColor.YELLOW + newName);
		return true;
	}
}
