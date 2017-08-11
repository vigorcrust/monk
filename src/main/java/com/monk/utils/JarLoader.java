package com.monk.utils;

import com.monk.gson.Configuration;
import com.monk.gson.Provider;
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

	/**
	 * This method loads the defaultBackendProvider first
	 * and afterwards all other needed drivers.
	 *
	 * @param config
	 * @param loader //TODO alle exceptions so lassen oder wie handeln?
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void loadAllJars(Configuration config, ClassLoader loader) throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
		List<String> alreadyLoadedDrivers = new ArrayList<>();

		//first load the defaultDatabaseBackend
		Provider defaultBackendProvider = JarLoader.createDefaultDbBackend(config);
		String classnameDefault = defaultBackendProvider.getDriverClass();
		Driver driver = (Driver) Class.forName(classnameDefault, true, loader).newInstance();
		DriverManager.registerDriver(new DriverShim(driver));
		alreadyLoadedDrivers.add(classnameDefault);
		Logger.info("Default database backend driver registered: " + classnameDefault);

		//afterwards load all other used databaseBackends
		ArrayList<Query> queries = config.getQueries();
		for (Query query : queries) {
			if (query.getDatabaseBackend() != null) {
				Logger.debug("Found database backend: " + query.getDatabaseBackend());
				ArrayList<Provider> mbp = config.getDbBackendProvider();
				for (Provider provider : mbp) {
					if (provider.getName().equals(query.getDatabaseBackend())) {
						if (!alreadyLoadedDrivers.contains(provider.getDriverClass())) {
							Logger.debug("Found depending dbbackend driver: " + provider.getName());
							String classname = provider.getDriverClass();
							Driver d = (Driver) Class.forName(classname, true, loader).newInstance();
							DriverManager.registerDriver(new DriverShim(d));
							alreadyLoadedDrivers.add(classname);
							Logger.info("Driver registered: " + classname);
						} else {
							Logger.debug("Driver has already been registered. Skipping.");
						}
					}
				}
			} else {
				Logger.info("Query has no DatabaseBackend. Skipping.");
			}
		}
	}

	/**
	 * The default backend is given in the form of the name (e.g. "oracle01").
	 * GSON only instatiates objects if you give all the information about this objects.
	 * Therefore the default backend must be created manually.
	 *
	 * @param config
	 * @return defaultBackend Provider
	 */
	private static Provider createDefaultDbBackend(Configuration config) {

		//this is the given name
		String name = config.getDbBackendProvider_default();
		Provider defaultProvider = null;
		ArrayList<Provider> mbp = config.getDbBackendProvider();
		//now we are searching in the list of all providers for this name
		//and if we find it, we create the object and return
		for (Provider provider : mbp) {
			if (provider.getName().equals(name)) {
				defaultProvider = new Provider(provider.getDriverClass(),
						name,
						provider.getLibrary(),
						provider.getConnection());
				return defaultProvider;
			}
		}

		Logger.error("Couldn't find the given default database backend.");
		System.exit(1);
		return null;
	}

}
