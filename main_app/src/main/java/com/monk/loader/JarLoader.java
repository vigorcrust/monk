package com.monk.loader;

import com.monk.gson.Configuration;
import com.monk.gson.Provider;
import com.monk.gson.Query;
import com.monk.utils.ProviderExtended;
import org.pmw.tinylog.Logger;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads all Jars specified in Configuration to the ClassLoader
 *
 * @author damarten on 13.07.2017
 * @see java.net.URLClassLoader
 */
public class JarLoader {

	private static List<String> alreadyLoadedDrivers = new ArrayList<>();
	private Configuration config;
	private ClassLoader loader;

	/**
	 * Creates a new JarLoader
	 *
	 * @param config The config to use
	 * @param loader The ClassLoader to add the jars to
	 */
	public JarLoader(Configuration config, ClassLoader loader) {
		this.config = config;
		this.loader = loader;
	}

	/**
	 * Loads all jars - entry point of this class
	 */
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

	/**
	 * Loads the default database backend
	 */
	private void loadDefaultDatabaseBackend() {

		Provider defaultBackendProvider = ProviderExtended.createDefaultDbBackend(this.config);
		String classnameDefault = defaultBackendProvider.getDriverClass();
		registerDriver(classnameDefault);

	}

	/**
	 * Adds the necessary drivers to the
	 * <p>
	 * This method searches in the list of queries
	 * for all necessary drivers and adds them to the list
	 * of drivers to load.
	 *
	 * @return The , which will be loaded
	 */
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

	/**
	 * Registers a driver to the classloader
	 * <p>
	 * Takes the classname, creates a new Driver and registers
	 * it by using the DriverManager.
	 *
	 * @param classname The classname of the driver to register
	 */
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
