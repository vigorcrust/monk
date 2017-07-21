package com.monk.utils;

import com.monk.gson.Configuration;
import com.monk.gson.Provider;
import com.monk.gson.Query;
import org.pmw.tinylog.Logger;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by ahatzold on 17.07.2017 in project monk_project.
 */
public class QueryExecutor {

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

			Logger.debug("Connecting to '" + databaseURL + "'");
			conn = DriverManager.getConnection(databaseURL,
					dbUsername,
					dbPassword);
			conn.setReadOnly(true);

			ResultSet rs = null;
			ResultSetMetaData rsmd = null;
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query.getStatement());
				if (rs != null) {
					rsmd = rs.getMetaData();
				}
			} catch (NullPointerException ex) {
				Logger.error("Something went wrong while executing the query '" +
						query.getName() + "'. \r\n Please make sure the statement is correct.");
			}
			if (rsmd != null)
				Logger.info("RESULT: Column Count " + Integer.toString(rsmd.getColumnCount()));

			//TODO Handle result individually

			try {
				rs.close();
			} catch (NullPointerException e) {
				Logger.error("ResultSet couldn't be closed.");
			}
		} catch (SQLRecoverableException ex) {
			Logger.error("Connection could not be established.");
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
						executeQuery(thisQuery, provider);
					}
				}
			} else {
				Logger.info("No DatabaseBackend given. " +
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
