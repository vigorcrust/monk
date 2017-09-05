package com.monk.gson;

/**
 * Created by ahatzold on 11.07.2017 in project monk_project.
 */
public class Root {

	private String application;
	private String version;
	private String authors;
	private String libsPath;
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
