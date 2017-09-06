package com.monk.gson;

import com.google.gson.annotations.SerializedName;
/**
 * Represents a Connection of a Provider of GSON parsed config file
 *
 * @author ahatzold on 11.07.2017
 */
public class Connection {

	@SerializedName("connection_string")
	private String connectionString;

	@SerializedName("username")
	private String username;

	@SerializedName("password")
	private String password;

	public String getConnectionString() {
		return connectionString;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
