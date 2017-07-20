package com.monk.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ahatzold on 11.07.2017 in project monk_project.
 */
public class Connection {

	@SerializedName("connection_string")
	private String connectionString;

	private String username;
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
