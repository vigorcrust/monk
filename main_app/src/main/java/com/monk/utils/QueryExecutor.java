package com.monk.utils;

import com.monk.MonitoringService;
import com.monk.gson.Configuration;
import com.monk.gson.Provider;
import com.monk.gson.ProviderExtended;
import com.monk.gson.Query;
import com.monk.spi.MonitoringBackend;
import org.pmw.tinylog.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ahatzold on 17.07.2017 in project monk_project.
 */
public class QueryExecutor {

	private ArrayList<Query> queries;
	private Configuration config;
	private ClassLoader loader;

	public QueryExecutor(Configuration config, ArrayList<Query> queries, ClassLoader loader) {
		this.queries = queries;
		this.config = config;
		this.loader = loader;
	}

	/**
	 * This method contains the logic to decide,
	 * which query is executed with which db backend.
	 * It hands the queries over to the executeSingeQuery() method.
	 *
	 * @throws SQLException
	 */
	public void executeQueries() throws SQLException {
		ArrayList<Provider> mbp = config.getDbBackendProvider();
		for (Query thisQuery : queries) {
			Logger.info("Executing query '" + thisQuery.getName() + "'");
			Logger.info("|-> " + thisQuery.getStatement());
			if (Utils.containsProhibited(thisQuery.getStatement())) {
				Logger.error("Query '" + thisQuery.getName() + "' contains one of the prohibited operators: INSERT, UPDATE, DELETE. " +
						"Skipping this query.");
				continue;
			}
			if (!Utils.isEmpty(thisQuery.getDatabaseBackend())) {
				for (Provider provider : mbp) {
					if (provider.getName().equals(thisQuery.getDatabaseBackend())) {
						executeSingleQuery(thisQuery, provider);
					}
				}
			} else {
				Logger.info("No database backend given. " +
						"Using default database backend '" +
						config.getDbBackendProvider_default() + "'");
				for (Provider provider : mbp) {
					if (provider.getName().equals(config.getDbBackendProvider_default())) {
						executeSingleQuery(thisQuery, provider);
					}
				}
			}
		}
	}

	/**
	 * This method executes a single query by
	 * - establishing the db backend connection
	 * - executing the specified query and receiving the response
	 * - establishing the monitoring backend connection
	 * - sending the received response to the montitoring backend
	 * - and closing all connections
	 *
	 * @param query
	 * @param provider
	 */
	private void executeSingleQuery(Query query, Provider provider) {

		Connection conn = null;
		Statement stmt = null;

		try {
			//first we have to connect via DriverManager to db backend
			String databaseURL = provider.getConnection().getConnectionString();
			String dbUsername = provider.getConnection().getUsername();
			String dbPassword = provider.getConnection().getPassword();

			Logger.debug("Connecting to '" + databaseURL + "'");
			if (dbUsername.isEmpty() || dbPassword.isEmpty()) {
				conn = DriverManager.getConnection(databaseURL);
			} else {
				conn = DriverManager.getConnection(databaseURL,
						dbUsername,
						dbPassword);
			}
			conn.setReadOnly(true);

			//then we create the statement
			ResultSet rs = null;
			ResultSetMetaData rsmd = null;
			try {
				stmt = conn.createStatement();
				//and execute it
				rs = stmt.executeQuery(query.getStatement());
				if (rs != null) {
					rsmd = rs.getMetaData();
				}
			} catch (NullPointerException ex) {
				Logger.error("Something went wrong while executing query '" +
						query.getName() + "'. \r\n Please make sure the statement is correct.");
			}

			double count = 0;
			if (rsmd != null) {
				Logger.info("RESULT:");
				while (rs.next()) {
					count++;
				}
				Logger.info("Row Count - " + count);
			}

			//afterwards we put the results in a map to use it later
			HashMap<String, Double> map = new HashMap<>();
			map.put("rows", count);


			//Then we create a provider
			Provider monProv = null;
			com.monk.gson.Connection connection;
			MonitoringService service;
			MonitoringBackend mb;
			try {
				monProv = ProviderExtended.createDefaultOrFallbackMonitoringBackend(config);
				connection = monProv.getConnection();
				//and create an singleton instance of this service
				service = MonitoringService.getInstance(loader);
				//in order to get the MonitoringBackend
				mb = service.getBackend(monProv.getDriverClass());

				//last we establish the connection, push the point and close the connection
				mb.establishConnection(connection.getConnectionString(),
						connection.getUsername(),
						connection.getPassword());
				mb.pushSinglePoint(query.getMeasurement(), map, query.getTimestamp(), query.getExtra());
				mb.closeConnection();
			} catch (NullPointerException e) {
				Logger.error("Implementation of MonitoringBackend could not be found. Check if config.json is set correctly.");
				System.exit(1);
			}

			try {
				rs.close();
			} catch (NullPointerException e) {
				Logger.error("ResultSet couldn't be closed.");
			}
		} catch (SQLException ex) {
			Logger.error(ex.getMessage());
			System.exit(1);
		} finally {
			try {
				if (stmt != null) {
					conn.close();
				}
			} catch (SQLException se) {
				Logger.error("An error occured while closing the connection.");
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
				Logger.error("An error occured while closing the connection.");
			}
		}
	}


}
