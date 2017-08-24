package com.monk.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ahatzold on 11.07.2017 in project monk_project.
 */
public class Provider {

	@SerializedName("driver_class")
	private String driverClass;

	private String name;
	private Connection connection;

	//constructor is only used for creation of both
	// - default backend provider
	// - default/fallback monitoring provider
	//needs to be done manually since defaultDatabaseBackend is
	//just a string and cannot be instantiated by GSON
	//for more information see class ProviderExtended
	public Provider(String driverClass, String name, Connection connection) {
		this.driverClass = driverClass;
		this.name = name;
		this.connection = connection;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public String getName() {
		return name;
	}

	public Connection getConnection() {
		return connection;
	}
}
