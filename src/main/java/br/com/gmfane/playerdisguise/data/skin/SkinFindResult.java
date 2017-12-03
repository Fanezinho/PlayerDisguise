package br.com.gmfane.playerdisguise.data.skin;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public final class SkinFindResult {

	private final Result result;
	private final SkinData skinData;

	public SkinFindResult(Result result, SkinData skinData) {
		this.result = result;
		this.skinData = skinData;
	}

	public Result getResult() {
		return result;
	}

	public SkinData getSkinData() {
		return skinData;
	}

	public enum Result {
		FOUND,
		NOT_FOUND,
		ERROR;
	}
}
