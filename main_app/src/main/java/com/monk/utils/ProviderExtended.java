package com.monk.utils;

import com.monk.gson.Configuration;
import com.monk.gson.Provider;
import org.pmw.tinylog.Logger;

import java.util.List;

/**
 * Utility class used for creation of Providers
 * <p>
 * The default database backend / monitoring backend is only given in the form
 * of the name (e.g. "oracle01"). GSON only instantiates objects if you give all
 * the information about this object. Therefore the default backend must be
 * created manually.
 *
 * @author ahatzold on 11.07.2017
 */
public class ProviderExtended {

	/**
	 * Constructor should not be used
	 */
	private ProviderExtended() {
		throw new IllegalStateException("ProviderExtended is a utility class!");
	}

	/**
	 * Creates the default database backend
	 *
	 * @param config The Configuration to use
	 * @return The default database backend Provider
	 */
	public static Provider createDefaultDbBackend(Configuration config) {

		//this is the given name we will be searching for
		String name = config.getDefaultDbBackendProvider();

		//this is the list of all available backend providers
		List<Provider> mbp = config.getDbBackendProvider();

		//now we search for the provider
		Provider p = ProviderExtended.findAndCreateProvider(mbp, name);

		//if we didn't find it, we have to exit the program
		if (p == null) {
			Logger.error("Couldn't find the given default database backend.");
			System.exit(1);
		}

		return p;
	}


	/**
	 * Creates the default or monitoring backend
	 * <p>
	 * If the default monitoring backend could not be found,
	 * use the fallback monitoring backend.
	 *
	 * @param config The configuration to use
	 * @return The default monitoring backend Provider and if not found
	 * 			the fallback monitoring backend Provider , otherwise null
	 */
	public static Provider createDefaultOrFallbackMonitoringBackend(Configuration config) {

		//this is the given name we will be searching for
		String nameDefault = config.getDefaultMonitoringBackendProvider();

		//this is the list of all available monitoring providers
		List<Provider> mbp = config.getMonitoringBackendProvider();

		//now we search for the provider and return it if found
		Provider p = ProviderExtended.findAndCreateProvider(mbp, nameDefault);
		if (p != null) {
			return p;
		}

		//if the provider couldn't be found,
		//the fallback monitoring backend will be used
		String nameFallback = config.getFallbackMonitoringBackendProvider();
		Provider pf = ProviderExtended.findAndCreateProvider(mbp, nameFallback);
		if (pf != null) {
			return pf;
		}

		//if none of the above could be found
		//we print an error and exit the program
		Logger.error("Couldn't find both the given default monitoring backend and fallback monitoring backend. " +
				"Exiting program.");
		System.exit(1);
		return null;

	}

	/**
	 * Finds a provider by its name (String) in an List
	 * and returns the appropriate object
	 *
	 * @param mbp The List to search in
	 * @param nameToFind The name of the Provider to find
	 * @return The Provider is found, otherwise null
	 */
	private static Provider findAndCreateProvider(List<Provider> mbp, String nameToFind) {

		Provider p = null;

		//now we are searching in the list of all providers for this name
		//and if we find it, we create the object and return
		for (Provider provider : mbp) {
			if (provider.getName().equals(nameToFind)) {
				if (provider.getDriverClass().isEmpty()
						|| provider.getConnection() == null) {
					Logger.error("Either driverClass or connection is null. Exiting program.");
					System.exit(1);
				}

				p = new Provider(provider.getDriverClass(),
						nameToFind,
						provider.getConnection());
				return p;
			}
		}

		//if we couldn't find it, it must be not there or misspelled
		return null;
	}
}
