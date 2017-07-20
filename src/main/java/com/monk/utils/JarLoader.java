package com.monk.utils;

import com.monk.gson.Configuration;
import com.monk.gson.Provider;
import com.monk.gson.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ahatzold on 17.07.2017 in project monk_project.
 */
public class JarLoader {

	static Logger logger = LoggerFactory.getLogger(JarLoader.class);

	public static void loadAllJars(Configuration config, ClassLoader loader) throws ClassNotFoundException, SQLException, IllegalAccessException, InstantiationException {
		List<String> alreadyLoadedDrivers = new ArrayList<>();
		ArrayList<Query> queries = config.getQueries();
		for (Query query : queries) {
			if (query.getDatabaseBackend() != null) {
				logger.trace("Found monitoring backend: " + query.getDatabaseBackend());
				ArrayList<Provider> mbp = config.getDbBackendProvider();
				for (Provider provider : mbp) {
					if (provider.getName().equals(query.getDatabaseBackend())) {
						if (!alreadyLoadedDrivers.contains(provider.getDriverClass())) {
							logger.trace("Found depending dbbackend driver: " + provider.getName());
							String classname = provider.getDriverClass();
							java.sql.Driver d = (java.sql.Driver) Class.forName(classname, true, loader).newInstance();
							DriverManager.registerDriver(new DriverShim(d));
							alreadyLoadedDrivers.add(classname);
							logger.info("Driver registered: " + classname);
						} else {
							logger.trace("Driver has already been registered. Skipping.");
						}
					} else {
						logger.trace("This DatabaseBackend is not interesting. Skipping.");
					}
				}
			} else {
				logger.info("Query has no DatabaseBackend. Skipping.");
			}
		}
	}

}
