package br.com.gmfane.playerdisguise;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import br.com.gmfane.playerdisguise.data.skin.SkinData;
import br.com.gmfane.playerdisguise.data.skin.SkinHandler;
import br.com.gmfane.playerdisguise.reflection.PlayerReflection;
import net.minecraft.server.v1_7_R4.DedicatedPlayerList;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class Utilitaries {

	private static final Pattern INVALID_NAME_PATTERN = Pattern.compile("[^a-zA-Z0-9_]");

	public static void updateName(Player player, String newName, String newDisplayName, String newPlayerListName) {
		CraftPlayer cPlayer = (CraftPlayer) player;

		PlayerReflection.setGameProfile(cPlayer.getProfile(), newName);

		player.setDisplayName(newDisplayName);
		player.setPlayerListName(newPlayerListName);
	}

	public static void updateEntityModel(SkinHandler skinHandler, Player player, boolean performSkinLookup) {
		CraftPlayer cPlayer = (CraftPlayer) player;
		EntityPlayer nmsPlayer = cPlayer.getHandle();

		PacketPlayOutPlayerInfo removeTabData = PacketPlayOutPlayerInfo.removePlayer(nmsPlayer);
		PacketPlayOutPlayerInfo addTabData = PacketPlayOutPlayerInfo.addPlayer(nmsPlayer);

		SkinData skinData = skinHandler.getCachedSkinData(player.getName());
		cPlayer.getProfile().getProperties().clear();
		if (skinData == null) {
			if (performSkinLookup) {
				skinHandler.retrieveSkinData(player, player.getName());
			}
		} else {
			if (skinData.getProperty() != null) {
				cPlayer.getProfile().getProperties().put("textures", skinData.getProperty());
			}
		}

		for (Player target : Bukkit.getOnlinePlayers()) {
			EntityPlayer nmsTarget = ((CraftPlayer) target).getHandle();
			nmsTarget.playerConnection.sendPacket(removeTabData);
			nmsTarget.playerConnection.sendPacket(addTabData);

			if (target != player) {
				target.hidePlayer(player);
				target.showPlayer(player);
			}
		}
	}

	public static boolean isNameValid(String name) {
		return name.length() <= 16 && !INVALID_NAME_PATTERN.matcher(name).find();
	}

	public static boolean isNameInUse(String name) {
		if (Bukkit.getPlayerExact(name) != null) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static boolean isPlayerListNameInUse(String playerListName) {
		DedicatedPlayerList playerList = ((CraftServer) Bukkit.getServer()).getHandle();
		for (EntityPlayer nmsPlayer : (List<EntityPlayer>) playerList.players) {
			if (nmsPlayer.listName.equals(playerListName)) {
				return true;
			}
		}
		return false;
	}

	public static Property getTextureProperty(Player player) {
		CraftPlayer cPlayer = (CraftPlayer) player;
		GameProfile profile = cPlayer.getProfile();
		Collection<Property> textures = profile.getProperties().get("textures");

		return textures.size() < 1 ? null : textures.iterator().next();
	}

	public static int unixTimestamp() {
		return (int) (System.currentTimeMillis() / 1000);
	}
}
