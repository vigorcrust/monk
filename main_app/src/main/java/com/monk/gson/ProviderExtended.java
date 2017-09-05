package com.monk.gson;

import org.pmw.tinylog.Logger;

import java.util.List;

/**
 * Created by ahatzold on 24.08.2017 in project monk_project.
 */
public class ProviderExtended {

	private ProviderExtended() {
		throw new IllegalStateException("ProviderExtended is a utility class!");
	}

	/**
	 * The default backend is given in the form of the name (e.g. "oracle01").
	 * GSON only instantiates objects if you give all the information about this objects.
	 * Therefore the default backend must be created manually.
	 *
	 * @param config
	 * @return defaultBackend Provider
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
	 * The default monitoring backend is given in the form of the name (e.g. "influx01").
	 * GSON only instantiates objects if you give all the information about this objects.
	 * Therefore the default monitoring backend must be created manually.
	 *
	 * @param config
	 * @return defaultBackend Provider
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
		Logger.error("Couldn't find both the given default monitoring backend and fallback monitoring backend.");
		System.exit(1);
		return null;

	}

	/**
	 * This helper method finds a provider by its name (String)
	 * in an ArrayList<Provider> and returns the appropriate object
	 *
	 * @param mbp
	 * @param nameToFind
	 * @return
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

		//if we couldn't find it, it must be null or misspelled
		return null;
	}
}
