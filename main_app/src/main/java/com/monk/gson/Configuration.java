package com.monk.gson;

import java.util.ArrayList;

/**
 * Created by ahatzold on 11.07.2017.
 */
public class Configuration {

	private String monitoringbackendprovider_default;
	private String monitoringbackendprovider_fallback;
	private String dbbackendprovider_default;
	private ArrayList<Provider> monitoringbackendprovider;
	private ArrayList<Provider> dbbackendprovider;
	private ArrayList<Query> queries;

	public String getMonitoringBackendProvider_default() {
		return monitoringbackendprovider_default;
	}

	public String getMonitoringBackendProvider_fallback() {
		return monitoringbackendprovider_fallback;
	}

	public String getDbBackendProvider_default() {
		return dbbackendprovider_default;
	}

	public ArrayList<Provider> getMonitoringBackendProvider() {
		return monitoringbackendprovider;
	}

	public ArrayList<Provider> getDbBackendProvider() {
		return dbbackendprovider;
	}

	public ArrayList<Query> getQueries() {
		return queries;
	}
}
