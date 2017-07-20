package com.monk.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ahatzold on 11.07.2017 in project monk_project.
 */
public class Query {

	private String name;
	private String measurement;
	private String statement;
	private String timestamp;
	private String extra;

	@SerializedName("monitoring_backend")
	private String monitoringBackend;
	@SerializedName("database_backend")
	private String databaseBackend;

	public String getName() {
		return name;
	}

	public String getMeasurement() {
		return measurement;
	}

	public String getStatement() {
		return statement;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getExtra() {
		return extra;
	}

	public String getMonitoringBackend() {
		return monitoringBackend;
	}

	public String getDatabaseBackend() {
		return databaseBackend;
	}
}
