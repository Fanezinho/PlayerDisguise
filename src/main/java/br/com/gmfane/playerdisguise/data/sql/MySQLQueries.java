package br.com.gmfane.playerdisguise.data.sql;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public enum MySQLQueries {
	
	SCHEMA("CREATE SCHEMA IF NOT EXISTS `%s`;"),
	
	SKIN_CACHE_INSERT("INSERT INTO `skin_cache` VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `timestamp`=?, `skin_data`=?"),
	SKIN_CACHE_GET("SELECT * FROM `skin_cache` WHERE `name`=?;"),
	SKIN_CACHE_DELETE("DELETE FROM `skin_cache` WHERE `name`=?"),

	TABLE_SKIN_CACHE("CREATE TABLE IF NOT EXISTS `skin_cache` (`name` CHAR(16) NOT NULL,`timestamp` INT NOT NULL,`skin_data` TEXT NOT NULL,PRIMARY KEY (`name`)) ENGINE = InnoDB"),

	LAST_ID("SELECT `AUTO_INCREMENT` FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=? AND TABLE_NAME=?;");

	private final String query;

	private MySQLQueries(String query) {
		this.query = query;
	}

	public String toString() {
		return query;
	}

}
