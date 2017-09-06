package com.monk.executor;

import com.monk.MonitoringService;
import com.monk.gson.Configuration;
import com.monk.gson.Provider;
import com.monk.gson.Query;
import com.monk.spi.MonitoringBackend;
import com.monk.utils.ProviderExtended;
import com.monk.utils.Utils;
import org.pmw.tinylog.Logger;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Executes the queries and sends them to the monitoring backend
 *
 * @author ahatzold on 17.07.2017
 */
public class QueryExecutor {

	private Configuration config;
	private ClassLoader loader;

	/**
	 * Creates a new QueryExecutor
	 *
	 * @param config The configuration parsed by GSON
	 * @param loader The ClassLoader to which the MonitoringBackend is added
	 */
	public QueryExecutor(Configuration config, ClassLoader loader) {
		this.config = config;
		this.loader = loader;
	}

	/**
	 * Checks, if a query contains a prohibited word
	 * such as INSERT, UPDATE, DELETE
	 *
	 * @param query The query to check
	 * @return true, if the query contains a prohibited word, otherwise false
	 */
	public static boolean containsProhibited(String query) {

		String[] prohibitedWords = {"INSERT", "UPDATE", "DELETE"};
		String firstWord = Utils.getFirstWord(query).toLowerCase();
		for (String prohibitedWord : prohibitedWords) {
			if (prohibitedWord.equalsIgnoreCase(firstWord)) {
				return true;
			}
		}
		return false;

	}

	/**
	 * Contains the logic to select which provider is used and starts execution
	 */
	public void executeQueries() {

		List<Provider> mbp = config.getDbBackendProvider();
		List<Query> queries = config.getQueries();

		for (Query query : queries) {
			//check if query contains a prohibited word
			if (containsProhibited(query.getStatement())) {
				Logger.error("Query '" + query.getName() + "' contains one of the prohibited operators: " +
						"INSERT, UPDATE, DELETE. " +
						"Skipping this query.");
				continue;
			}

			Logger.info("Executing query '" + query.getName() + "'");
			Logger.info("|-> " + query.getStatement());

			//if you find a database backend, use it
			if (!Utils.isEmpty(query.getDatabaseBackend())) {
				for (Provider provider : mbp) {
					if (provider.getName().equals(query.getDatabaseBackend())) {
						executeSingleQuery(query, provider);
					}
				}
			} else {
				//otherwise use the default one
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

	/**
	 * Executes a single query with the given Provider and sends response to monitoring backend
	 *
	 * @param query    The given query to execute
	 * @param provider The provider to use to execute this query
	 */
	private void executeSingleQuery(Query query, Provider provider) {

		//first we get a connection to this Provider
		Connection conn = getConnection(provider);

		//then we get the result by executing the query
		Double count = getResult(query, conn);

		//afterwards we put the results in a map to use it later
		Map<String, Double> map = new HashMap<>();
		map.put("rows", count);

		//we create a MonitoringBackend and get the correct implementation
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

	/**
	 * Gets the required implementation of MonitoringBackend
	 *
	 * @param monProv The Provider which represents the MonitoringBackend
	 * @return The implementation of the MonitoringBackend
	 */
	private MonitoringBackend getMonitoringImpl(Provider monProv) {

		MonitoringService service;
		MonitoringBackend mb = null;

		try {
			//create an singleton instance of this service
			service = MonitoringService.getInstance(loader);
			//in order to get the MonitoringBackend
			mb = service.getBackend(monProv.getDriverClass());
		} catch (NullPointerException e) {
			Logger.error("Implementation of MonitoringBackend could not be found. Check if config.json is set correctly.");
			System.exit(1);
		}

		return mb;
	}

	/**
	 * Sends the query to the database backend and receives the result
	 *
	 * @param query The query to execute
	 * @param conn  The Connection to use
	 * @return Double, which is the result of the query
	 */
	private Double getResult(Query query, Connection conn) {

		ResultSet rs;
		ResultSetMetaData rsmd = null;

		//we need a double here, because some monitoring backends
		//only accept doubles instead of ints
		double count = 0;

		try (Statement stmt = conn.createStatement()) {

			rs = stmt.executeQuery(query.getStatement());
			if (rs != null) {
				rsmd = rs.getMetaData();
			}

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

		} catch (NullPointerException ex) {
			Logger.error("Something went wrong while executing query '" +
					query.getName() + "'. \r\n Please make sure the statement is correct.");
			Logger.error(ex.getMessage());
			System.exit(1);
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
		return count;
	}

	/**
	 * Gets a connection to the given provider
	 *
	 * @param provider The Provider to connect to
	 * @return Connection to the Provider
	 */
	private Connection getConnection(Provider provider) {

		Connection conn = null;

		String databaseURL = provider.getConnection().getConnectionString();
		String dbUsername = provider.getConnection().getUsername();
		String dbPassword = provider.getConnection().getPassword();

		try {

			Logger.info("Connecting to '" + databaseURL + "'");
			if (dbUsername.isEmpty() || dbPassword.isEmpty()) {
				conn = DriverManager.getConnection(databaseURL);
			} else {
				conn = DriverManager.getConnection(databaseURL,
						dbUsername,
						dbPassword);
			}
			conn.setReadOnly(true);

		} catch (SQLException ex) {
			Logger.error(ex.getMessage());
			System.exit(1);
		}
		return conn;
	}

}
