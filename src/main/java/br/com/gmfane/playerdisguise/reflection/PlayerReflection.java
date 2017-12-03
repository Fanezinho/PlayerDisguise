package br.com.gmfane.playerdisguise.reflection;

import java.lang.reflect.Field;

import net.minecraft.util.com.mojang.authlib.GameProfile;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public final class PlayerReflection {

	private static final Field GAMEPROFILE_FIELD_NAME = Reflection.getField(GameProfile.class, "name");

	public static void setGameProfile(GameProfile profile, String newName) {
		try {
			GAMEPROFILE_FIELD_NAME.set(profile, newName);
		} catch (Exception e) {
		}
	}
}
