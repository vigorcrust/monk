package com.monk.utils;

import com.monk.gson.Configuration;
import com.monk.gson.Provider;
import com.monk.gson.Query;
import org.pmw.tinylog.Logger;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahatzold on 17.07.2017 in project monk_project.
 */
public class JarLoader {

	public static void loadAllJars(Configuration config, ClassLoader loader) throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
		List<String> alreadyLoadedDrivers = new ArrayList<>();
		ArrayList<Query> queries = config.getQueries();
		for (Query query : queries) {
			if (query.getDatabaseBackend() != null) {
				Logger.debug("Found monitoring backend: " + query.getDatabaseBackend());
				ArrayList<Provider> mbp = config.getDbBackendProvider();
				for (Provider provider : mbp) {
					if (provider.getName().equals(query.getDatabaseBackend())) {
						if (!alreadyLoadedDrivers.contains(provider.getDriverClass())) {
							Logger.debug("Found depending dbbackend driver: " + provider.getName());
							String classname = provider.getDriverClass();
							java.sql.Driver d = (java.sql.Driver) Class.forName(classname, true, loader).newInstance();
							DriverManager.registerDriver(new DriverShim(d));
							alreadyLoadedDrivers.add(classname);
							Logger.info("Driver registered: " + classname);
						} else {
							Logger.debug("Driver has already been registered. Skipping.");
						}
					} else {
						Logger.debug("This DatabaseBackend is not interesting. Skipping.");
					}
				}
			} else {
				Logger.info("Query has no DatabaseBackend. Skipping.");
			}
		}
	}

}
