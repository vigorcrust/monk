package com.monk;

import com.monk.spi.MonitoringBackend;

import java.util.HashMap;

/**
 * Created by ahatzold on 31.08.2017 in project monk_project.
 */
public class CSV implements MonitoringBackend {
	@Override
	public void establishConnection(String connectionString, String username, String password) {

	}

	@Override
	public void pushSinglePoint(String measurement, HashMap<String, Double> fields, String timestamp, String extra) {

	}

	@Override
	public void closeConnection() {

	}
}
