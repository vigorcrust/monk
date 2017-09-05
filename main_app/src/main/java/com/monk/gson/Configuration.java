package com.monk.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ahatzold on 11.07.2017.
 */
public class Configuration {

	@SerializedName("monitoringbackendprovider_default")
	private String defaultMonitoringBackendProvider;

	@SerializedName("monitoringbackendprovider_fallback")
	private String fallbackMonitoringBackendProvider;

	@SerializedName("dbbackendprovider_default")
	private String defaultDbBackendProvider;

	@SerializedName("monitoringbackendprovider")
	private List<Provider> monitoringBackendProvider;

	@SerializedName("dbbackendprovider")
	private List<Provider> dbBackendProvider;

	@SerializedName("queries")
	private List<Query> queries;

	public String getDefaultMonitoringBackendProvider() {
		return defaultMonitoringBackendProvider;
	}

	public String getFallbackMonitoringBackendProvider() {
		return fallbackMonitoringBackendProvider;
	}

	public String getDefaultDbBackendProvider() {
		return defaultDbBackendProvider;
	}

	public List<Provider> getMonitoringBackendProvider() {
		return monitoringBackendProvider;
	}

	public List<Provider> getDbBackendProvider() {
		return dbBackendProvider;
	}

	public List<Query> getQueries() {
		return queries;
	}
}
