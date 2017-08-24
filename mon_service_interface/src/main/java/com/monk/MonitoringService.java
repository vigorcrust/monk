package com.monk;

import com.monk.spi.MonitoringBackend;
import org.pmw.tinylog.Logger;

import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * Created by ahatzold on 22.08.2017 in project serviceProviderInterface.
 */
public class MonitoringService {

	private static MonitoringService service;
	private ServiceLoader<MonitoringBackend> loader;

	private MonitoringService(ClassLoader classLoader) {
		loader = ServiceLoader.load(MonitoringBackend.class, classLoader);
	}

	public static synchronized MonitoringService getInstance(ClassLoader classLoader) {
		if (service == null) {
			service = new MonitoringService(classLoader);
		}
		return service;
	}

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
