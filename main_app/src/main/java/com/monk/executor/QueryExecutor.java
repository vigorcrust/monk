package com.monk.executor;

import com.monk.MonitoringService;
import com.monk.gson.Configuration;
import com.monk.gson.Provider;
import com.monk.gson.ProviderExtended;
import com.monk.gson.Query;
import com.monk.spi.MonitoringBackend;
import com.monk.utils.Utils;
import org.pmw.tinylog.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ahatzold on 17.07.2017 in project monk_project.
 */
public class QueryExecutor {

	private List<Query> queries;
	private Configuration config;
	private ClassLoader loader;

	public QueryExecutor(Configuration config, List<Query> queries, ClassLoader loader) {
		this.queries = queries;
		this.config = config;
		this.loader = loader;
	}

	public void executeQueries() {
		List<Provider> mbp = config.getDbBackendProvider();
		for (Query query : queries) {
			if (Utils.containsProhibited(query.getStatement())) {
				Logger.error("Query '" + query.getName() + "' contains one of the prohibited operators: " +
						"INSERT, UPDATE, DELETE. " +
						"Skipping this query.");
				continue;
			}

			Logger.info("Executing query '" + query.getName() + "'");
			Logger.info("|-> " + query.getStatement());

			if (!Utils.isEmpty(query.getDatabaseBackend())) {
				for (Provider provider : mbp) {
					if (provider.getName().equals(query.getDatabaseBackend())) {
						executeSingleQuery(query, provider);
					}
				}
			} else {
				Logger.info("No database backend given. " +
						"Using default database backend '" +
						config.getDefaultDbBackendProvider() + "'");
				for (Provider provider : mbp) {
					if (provider.getName().equals(config.getDefaultDbBackendProvider())) {
						executeSingleQuery(query, provider);
					}
				}
			}
		}
	}

	private void executeSingleQuery(Query query, Provider provider) {

		Connection conn = getConnection(query, provider);

		Double count = getResult(query, conn);

		//afterwards we put the results in a map to use it later
		Map<String, Double> map = new HashMap<>();
		map.put("rows", count);

		Provider monProv = ProviderExtended.createDefaultOrFallbackMonitoringBackend(config);
		com.monk.gson.Connection connection = monProv.getConnection();
		MonitoringBackend mb = getMonitoringImpl(monProv);

		//last we establish the connection, push the point and close the connection
		mb.establishConnection(connection.getConnectionString(),
				connection.getUsername(),
				connection.getPassword());
		mb.pushSinglePoint(query.getMeasurement(), map, query.getTimestamp(), query.getExtra());
		mb.closeConnection();

	}

	private MonitoringBackend getMonitoringImpl(Provider monProv) {

		MonitoringService service;
		MonitoringBackend mb = null;
		try {
			//and create an singleton instance of this service
			service = MonitoringService.getInstance(loader);
			//in order to get the MonitoringBackend
			mb = service.getBackend(monProv.getDriverClass());
		} catch (NullPointerException e) {
			Logger.error("Implementation of MonitoringBackend could not be found. Check if config.json is set correctly.");
			System.exit(1);
		}

		return mb;
	}

	private Double getResult(Query query, Connection conn) {

		//then we create the statement
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;

		try (Statement stmt = conn.createStatement()) {

			//and execute it
			rs = stmt.executeQuery(query.getStatement());
			if (rs != null) {
				rsmd = rs.getMetaData();
			}

			double count = 0;
			if (rsmd != null) {
				Logger.info("RESULT:");
				while (rs.next()) {
					count++;
				}
				Logger.info("Row Count - " + count);
			}

			if (rs != null) {
				rs.close();
			} else {
				Logger.error("ResultSet couldn't be closed.");
			}

			return count;

		} catch (NullPointerException ex) {
			Logger.error("Something went wrong while executing query '" +
					query.getName() + "'. \r\n Please make sure the statement is correct.");
		} catch (SQLException e) {
			Logger.error(e.getMessage());
			System.exit(1);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				Logger.error("An error occured while closing the connection.");
			}
		}
		return null;
	}

	private Connection getConnection(Query query, Provider provider) {

		Connection conn = null;

		try {
			//first we have to connect via DriverManager to db backend
			String databaseURL = provider.getConnection().getConnectionString();
			String dbUsername = provider.getConnection().getUsername();
			String dbPassword = provider.getConnection().getPassword();

			Logger.info("Connecting to '" + databaseURL + "'");
			if (dbUsername.isEmpty() || dbPassword.isEmpty()) {
				conn = DriverManager.getConnection(databaseURL);
			} else {
				conn = DriverManager.getConnection(databaseURL,
						dbUsername,
						dbPassword);
			}
			conn.setReadOnly(true);

			return conn;

		} catch (SQLException ex) {
			Logger.error(ex.getMessage());
			System.exit(1);
		}

		return null;
	}

}
