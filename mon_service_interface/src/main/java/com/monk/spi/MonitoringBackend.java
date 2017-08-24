package com.monk.spi;

import java.util.HashMap;

/**
 * Created by ahatzold on 26.07.2017 in project monk_project.
 */
public interface MonitoringBackend {

	/**
	 * This method establishes a connection to the monitoring backend by
	 * generating an individual object depending on the backend.
	 * The connectionString can contain additional params (for further monitoring backends),
	 * which are separated by '$'.
	 *
	 * @param connectionString
	 * @param username
	 * @param password
	 */
	void establishConnection(String connectionString, String username, String password);

	/**
	 * This method sends a single point to the monitoring backend
	 * using the connection established before.
	 *
	 * @param fields
	 * @param timestamp
	 * @param measurement
	 * @param extra
	 */
	void pushSinglePoint(String measurement, HashMap<String, String> fields, String timestamp, String extra);

	/**
	 * This method closes the connection,
	 * if one has been established before.
	 */
	void closeConnection();


}
