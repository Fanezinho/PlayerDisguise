package br.com.gmfane.playerdisguise.data.skin;

import br.com.gmfane.playerdisguise.Utilitaries;
import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.mojang.authlib.properties.Property;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public final class SkinData {

	private static final String JSON_PROPERTY_FORMAT = "{\"name\":\"%s\",\"value\":\"%s\",\"signature\":\"%s\"}";

	private final int timestamp;
	private final Property property;

	public SkinData() {
		this.timestamp = Utilitaries.unixTimestamp();
		this.property = null;
	}

	public SkinData(int timestamp, Property property) {
		this.timestamp = timestamp;
		this.property = property;
	}

	public SkinData(int timestamp, String jsonData) {
		JsonObject properties = new Gson().fromJson(jsonData, JsonObject.class);

		String name = properties.get("name").getAsString();
		String base = properties.get("value").getAsString();
		String signature = properties.get("signature").getAsString();

		this.timestamp = timestamp;
		this.property = new Property(name, base, signature);
	}

	public int getTimestamp() {
		return timestamp;
	}

	public Property getProperty() {
		return property;
	}

	public String getCompressedData() {
		return String.format(JSON_PROPERTY_FORMAT, property.getName(), property.getValue(), property.getSignature());
	}
}
