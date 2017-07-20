package com.monk.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ahatzold on 11.07.2017 in project monk_project.
 */
public class Provider {

	@SerializedName("driver_class")
	private String driverClass;

	private String name;
	private String library;
	private Connection connection;

	public String getDriverClass() {
		return driverClass;
	}

	public String getName() {
		return name;
	}

	public String getLibrary() {
		return library;
	}

	public Connection getConnection() {
		return connection;
	}
}
