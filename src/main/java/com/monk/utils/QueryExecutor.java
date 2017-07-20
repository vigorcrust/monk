package com.monk.utils;

import com.monk.gson.Configuration;
import com.monk.gson.Provider;
import com.monk.gson.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by ahatzold on 17.07.2017 in project monk_project.
 */
public class QueryExecutor {

	private static Logger logger = LoggerFactory.getLogger(QueryExecutor.class);

	private ArrayList<Query> queries;
	private Configuration config;

	public QueryExecutor(Configuration config, ArrayList<Query> queries) {
		this.queries = queries;
		this.config = config;
	}

	private void executeQuery(Query query, Provider provider) throws SQLException {

		Connection conn = null;
		Statement stmt = null;

		try {
			String databaseURL = provider.getConnection().getConnectionString();
			String dbUsername = provider.getConnection().getUsername();
			String dbPassword = provider.getConnection().getPassword();

			logger.debug("Connecting to '" + databaseURL + "'");
			conn = DriverManager.getConnection(databaseURL,
					dbUsername,
					dbPassword);
			conn.setReadOnly(true);
			/*if (conn != null) {
				logger.debug("Successfully connected!");
			}*/

			ResultSet rs = null;
			ResultSetMetaData rsmd = null;
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query.getStatement());
				if (rs != null) {
					rsmd = rs.getMetaData();
				}
			} catch (NullPointerException ex) {
				logger.warn("Something went wrong while executing the query '" + query.getName() + "'. \r\n Please make sure the statement is correct.", ex);
			}
			if (rsmd != null)
				logger.info("RESULT: Column Count " + Integer.toString(rsmd.getColumnCount()));

			//TODO Handle result individually

			try {
				rs.close();
			} catch (NullPointerException e) {
				logger.warn("ResultSet couldn't be closed.", e);
			}
		} finally {
			try {
				if (stmt != null) {
					conn.close();
				}
			} catch (SQLException se) {
				logger.error("An error occured while closing the connection: ", se.getMessage());
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
				logger.error("An error occured while closing the connection: ", ex.getMessage());
			}
		}
	}

	public void executeQueries() throws SQLException {
		ArrayList<Provider> mbp = config.getDbBackendProvider();
		for (Query thisQuery : queries) {
			logger.info("Executing query '" + thisQuery.getName() + "'");
			logger.info("|-> " + thisQuery.getStatement());
			if (!Utils.isEmpty(thisQuery.getDatabaseBackend())) {
				for (Provider provider : mbp) {
					if (provider.getName().equals(thisQuery.getDatabaseBackend())) {
						executeQuery(thisQuery, provider);
					}
				}
			} else {
				logger.info("No DatabaseBackend given. " +
						"Using default DatabaseBackend '" +
						config.getDbBackendProvider_default() + "'");
				for (Provider provider : mbp) {
					if (provider.getName().equals(config.getDbBackendProvider_default())) {
						executeQuery(thisQuery, provider);
					}
				}
			}
		}
	}

}
