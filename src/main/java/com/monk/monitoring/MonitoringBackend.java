package com.monk.monitoring;

/**
 * Created by ahatzold on 26.07.2017 in project monk_project.
 */
public interface MonitoringBackend {


	/**
	 * This method establishes a connection to the monitoring backend by
	 * generating an individual object depending on the backend.
	 * It takes the connection string, username and password
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
	 * @param tags
	 * @param timestamp
	 * @param measurement
	 * @param extra
	 * @return
	 */
	boolean pushPoint(String[] tags, String measurement, String extra, String timestamp);


}
