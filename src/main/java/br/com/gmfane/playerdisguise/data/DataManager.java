package br.com.gmfane.playerdisguise.data;

import org.bukkit.configuration.ConfigurationSection;

import br.com.gmfane.playerdisguise.data.sql.MySQL;
import br.com.gmfane.playerdisguise.handler.DisguiseHandler;
import br.com.gmfane.playerdisguise.handler.SimpleHandler;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class DataManager extends SimpleHandler {

	private MySQL mysql;

	private String addressSql, usernameSql, passwordSql, schemaSql;
	private int portSql;

	public DataManager(DisguiseHandler disguiseHandler) {
		super(disguiseHandler);

		this.addressSql = "localhost";
		this.portSql = 3306;
		this.usernameSql = "root";
		this.passwordSql = "";
		this.schemaSql = "disguise";
	}

	public boolean initialize() {
		try {

			ConfigurationSection connectionSection = getDisguiseHandler().getConfigurationManager().getConfig().getConfigurationSection("database-connection");
			if (connectionSection == null) {
				getLogger().log("Error to find the 'database-connection' section on the config. Using default values!");
			} else {
				if (connectionSection.contains("address")) {
					this.addressSql = connectionSection.getString("address");
				} else {
					getLogger().log("The plugin don't find the 'address' in the config file. Using the default '%s' value", addressSql);
				}
				if (connectionSection.contains("port")) {
					this.portSql = connectionSection.getInt("port");
				} else {
					getLogger().log("The plugin don't find the 'port' in the config file. Using the default '%s' value", portSql);
				}
				if (connectionSection.contains("username")) {
					this.usernameSql = connectionSection.getString("username");
				} else {
					getLogger().log("The plugin don't find the 'username' in the config file. Using the default '%s' value", usernameSql);
				}
				if (connectionSection.contains("password")) {
					this.passwordSql = connectionSection.getString("password");
				} else {
					getLogger().log("The plugin don't find the 'password' in the config file. Using the default '%s' value", passwordSql);
				}
				if (connectionSection.contains("schema")) {
					this.schemaSql = connectionSection.getString("schema");
				} else {
					getLogger().log("The plugin don't find the 'schema' in the config file. Using the default '%s' value", schemaSql);
				}
			}

			mysql = new MySQL(getLogger(), usernameSql, passwordSql, "jdbc:mysql://" + addressSql + ":" + portSql, schemaSql);

			if (!mysql.openConnections()) {
				return false;
			}

			getLogger().log("The mysql connections have been established.");
			return true;

		} catch (Exception exception) {
			getLogger().error("Error to load the config file, in the 'database-connection' section.", exception);
			return false;
		}
	}

	public MySQL getMySQL() {
		return mysql;
	}

}
