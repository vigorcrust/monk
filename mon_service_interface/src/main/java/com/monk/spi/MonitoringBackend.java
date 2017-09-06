package com.monk.spi;

import java.util.Map;

/**
 * Created by ahatzold on 26.07.2017 in project monk_project.
 */
public interface MonitoringBackend {

	/**
	 * Establishes a connection to the monitoring backend
	 * by generating an individual object depending on the backend.
	 * The connectionString can contain additional params (for further
	 * monitoring backends) called 'extra', which are separated by '$'.
	 *
	 * @param connectionString The connection string to use
	 * @param username The username to establish the connection
	 * @param password The password to establish the connection
	 */
	void establishConnection(String connectionString, String username, String password);

	/**
	 * Pushes a single point to the monitoring backend
	 * using the connection established before.
	 *
	 * @param measurement The measurement to use in the database
	 * @param fields The fields to save
	 * @param timestamp The timestamp of this point
	 * @param extra Additional information
	 */
	void pushSinglePoint(String measurement, Map<String, Double> fields, String timestamp, String extra);

	/**
	 * Closes the connection,
	 * if one has been established before.
	 */
	void closeConnection();

}
