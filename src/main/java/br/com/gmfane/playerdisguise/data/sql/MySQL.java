package br.com.gmfane.playerdisguise.data.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import br.com.gmfane.playerdisguise.logger.FormattedLogger;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class MySQL {

	private static final Executor asyncExecutor = Executors.newSingleThreadExecutor((new ThreadFactoryBuilder()).setNameFormat("Async Thread").build());

	private final FormattedLogger logger;
	private final String user, pass, url, database;

	private Connection mainConnection, slaveConnection;

	public MySQL(FormattedLogger logger, String user, String pass, String url, String database) {
		this.logger = logger;
		this.user = user;
		this.pass = pass;
		this.url = url;
		this.database = database;
	}

	public boolean openConnections() {

		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			this.mainConnection = DriverManager.getConnection(url, user, pass);
			this.slaveConnection = DriverManager.getConnection(url, user, pass);

			String schemaQuery = String.format(MySQLQueries.SCHEMA.toString(), database);
			
			mainConnection.createStatement().executeUpdate(schemaQuery);

			mainConnection.createStatement().executeQuery("USE `" + database + "`;");
			slaveConnection.createStatement().executeQuery("USE `" + database + "`;");

			return insertDefaults();
		} catch (Exception exception) {
			logger.error("Impossible to create the connection of mysql with url: '%s/%s' and user '%s'.", exception, url, database, user);
		}

		return false;
	}

	public boolean insertDefaults() {
		try {

			for (MySQLQueries query : MySQLQueries.values()) {

				String[] name = query.toString().split("`");
				if (query.name().startsWith("TABLE")) {

					String tableName = name[1];

					logger.log(Level.WARNING, "Trying to create the table '%s' to the mysql database.", tableName);

					getConnection().prepareStatement(query.toString()).execute();
				}
			}

		} catch (Exception exception) {
			logger.error("Impossible to insert the default values on the schema.", exception);
			return false;
		}
		return true;
	}

	public Connection getConnection() {
		try {
			if (mainConnection == null || mainConnection.isClosed())
				openConnections();
		} catch (SQLException exception) {
			logger.error("Error to reconnect the mysql connections.", exception);
		}

		return mainConnection;
	}

	public Connection getSlaveConnection() {
		try {
			if (slaveConnection == null || slaveConnection.isClosed())
				openConnections();
		} catch (SQLException exception) {
			logger.error("Error to reconnect the mysql connections.", exception);
		}

		return slaveConnection;
	}

	public Executor getAsyncExecutor() {
		return asyncExecutor;
	}

	public static interface Callback<T> {
		public void finish(T t);
	}

}
