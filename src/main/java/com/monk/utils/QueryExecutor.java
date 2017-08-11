package com.monk.utils;

import com.monk.gson.Configuration;
import com.monk.gson.Provider;
import com.monk.gson.Query;
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
	private QEDelegate delegate;

	public QueryExecutor(Configuration config, ArrayList<Query> queries, QEDelegate delegate) {
		this.queries = queries;
		this.config = config;
		this.delegate = delegate;
	}

	private void executeQuery(Query query, Provider provider) {

		Connection conn = null;
		Statement stmt = null;


		try {
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

			ResultSet rs = null;
			ResultSetMetaData rsmd = null;
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(query.getStatement());
				if (rs != null) {
					rsmd = rs.getMetaData();
				}
			} catch (NullPointerException ex) {
				Logger.error("Something went wrong while executing query '" +
						query.getName() + "'. \r\n Please make sure the statement is correct.");
			}
			int count = 0;
			if (rsmd != null) {
				Logger.info("RESULT:");
				while (rs.next()) {
					count++;
				}
				Logger.info("Row Count - " + count);
			}

			String value = Integer.toString(count);
			HashMap<String, String> map = new HashMap<String, String>() {{
				put("rows", value);
			}};
			delegate.pushSinglePoint("rows", map, query.getTimestamp(), query.getExtra());

			/*MonitoringBackend prom = new PrometheusBackend();
			prom.establishConnection("127.0.0.1:9091", "", "");
			try {
				prom.pushSinglePoint("rowss", null, "", "");
			} catch (IOException e) {
				e.printStackTrace();
			}*/


			try {
				rs.close();
			} catch (NullPointerException e) {
				Logger.error("ResultSet couldn't be closed.");
			}
		} catch (SQLRecoverableException ex) {
			Logger.error(ex.getMessage());
			System.exit(1);
		} catch (SQLException sqlEx) {
			Logger.error(sqlEx.getMessage());
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
				Logger.info("No database backend given. " +
						"Using default database backend '" +
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
