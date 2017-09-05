package com.monk.loader;

import com.monk.gson.Configuration;
import com.monk.gson.Provider;
import com.monk.gson.ProviderExtended;
import com.monk.gson.Query;
import org.pmw.tinylog.Logger;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahatzold on 17.07.2017 in project monk_project.
 */
public class JarLoader {

	private static List<String> alreadyLoadedDrivers = new ArrayList<>();
	private Configuration config;
	private ClassLoader loader;

	public JarLoader(Configuration config, ClassLoader loader) {
		this.config = config;
		this.loader = loader;
	}

	public void loadAllJars() {

		//first load the defaultDatabaseBackend
		loadDefaultDatabaseBackend();

		//then create a list with all relevant providers
		//and register them via DriverManager
		List<Provider> providersToRegister = addDriversToList();
		for (Provider provider : providersToRegister) {
			Logger.debug("Found depending dbbackend driver: " + provider.getName());
			String classname = provider.getDriverClass();
			registerDriver(classname);
		}

	}

	private void loadDefaultDatabaseBackend() {

		Provider defaultBackendProvider = ProviderExtended.createDefaultDbBackend(this.config);
		String classnameDefault = defaultBackendProvider.getDriverClass();
		registerDriver(classnameDefault);

	}

	private List<Provider> addDriversToList() {

		List<Provider> driversToLoad = new ArrayList<>();
		List<Query> queries = this.config.getQueries();
		for (Query query : queries) {
			if (query.getDatabaseBackend() != null) {
				Logger.debug("Found database backend: " + query.getDatabaseBackend());
				List<Provider> mbp = config.getDbBackendProvider();
				for (Provider provider : mbp) {
					if (provider.getName().equals(query.getDatabaseBackend())) {
						if (!alreadyLoadedDrivers.contains(provider.getDriverClass())) {
							driversToLoad.add(provider);
						} else {
							Logger.debug("Driver has already been registered. Skipping.");
						}
					}
				}
			} else {
				Logger.info("Query has no DatabaseBackend. Skipping.");
			}
		}
		return driversToLoad;
	}

	private void registerDriver(String classname) {

		try {
			Driver driver = (Driver) Class.forName(classname, true, loader).newInstance();
			DriverManager.registerDriver(new DriverShim(driver));
			alreadyLoadedDrivers.add(classname);
			Logger.info("Default database backend driver registered: " + classname);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			Logger.error(e.getMessage());
			System.exit(1);
		}

	}

}
