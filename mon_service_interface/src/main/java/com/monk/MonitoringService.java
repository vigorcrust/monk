package com.monk;

import com.monk.spi.MonitoringBackend;
import org.pmw.tinylog.Logger;

import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * A singleton interface to return an instance of this MonitoringService
 * <p>
 *
 *
 * @author ahatzold on 24.08.2017
 */
public class MonitoringService {

	private static MonitoringService service;
	private ServiceLoader<MonitoringBackend> loader;

	/**
	 * Creates a new MonitoringService
	 * @param classLoader The ClassLoader to use
	 */
	private MonitoringService(ClassLoader classLoader) {
		loader = ServiceLoader.load(MonitoringBackend.class, classLoader);
	}

	/**
	 * Gets the singelton instance of the MonitoringService
	 * @param classLoader The ClassLoader to use
	 * @return The MonitoringService to use
	 */
	public static synchronized MonitoringService getInstance(ClassLoader classLoader) {
		if (service == null) {
			service = new MonitoringService(classLoader);
		}
		return service;
	}

	/**
	 * Searches the MonitoringBackend with the given driverClass
	 * @param driverClass The driverClass to search for
	 * @return The MonitoringBackend
	 */
	public MonitoringBackend getBackend(String driverClass) {

		try {
			//search for the correct driverClass in all implementations
			//of MonitoringBackend in the classloader
			for (MonitoringBackend m : loader) {
				if (m.getClass().getCanonicalName().equals(driverClass)) {
					//and if you found it, return it
					Logger.info("Using implementation with driverClass '"
							+ driverClass + "'");
					return m;
				}
			}
		} catch (ServiceConfigurationError serviceError) {
			Logger.error(serviceError.getMessage());
			System.exit(1);
		}
		return null;

	}

}
