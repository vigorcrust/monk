package com.monk.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a Query of GSON parsed config file
 *
 * @author ahatzold on 11.07.2017
 */
public class Query {

	@SerializedName("name")
	private String name;

	@SerializedName("measurement")
	private String measurement;

	@SerializedName("statement")
	private String statement;

	@SerializedName("timestamp")
	private String timestamp;

	@SerializedName("extra")
	private String extra;

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

	public String getDatabaseBackend() {
		return databaseBackend;
	}

}
