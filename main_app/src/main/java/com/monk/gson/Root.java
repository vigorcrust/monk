package com.monk.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the Root of GSON parsed config file
 *
 * @author ahatzold on 11.07.2017
 */
public class Root {

	@SerializedName("application")
	private String application;

	@SerializedName("version")
	private String version;

	@SerializedName("authors")
	private String authors;

	@SerializedName("libspath")
	private String libsPath;

	@SerializedName("configuration")
	private Configuration configuration;

	public String getApplication() {
		return application;
	}

	public String getVersion() {
		return version;
	}

	public String getAuthors() {
		return authors;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public String getLibsPath() {
		return libsPath;
	}
}
