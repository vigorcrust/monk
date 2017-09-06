package com.monk.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a Provider of GSON parsed config file
 *
 * @author ahatzold on 11.07.2017
 */
public class Provider {

	@SerializedName("driver_class")
	private String driverClass;

	@SerializedName("name")
	private String name;

	@SerializedName("connection")
	private Connection connection;

	/**
	 * Creates a Provider
	 * <p>
	 * Constructor is only used for creation of
	 * <ul>
	 *     <li>
	 *         default backend provider
	 *     </li>
	 *     <li>
	 *         default/fallback monitoring provider
	 *     </li>
	 * </ul>
	 * <p>
	 * This needs to be done manually since these providers
	 * are just a string and cannot be instantiated by GSON.
	 *
	 * @param driverClass The driverClass to use
	 * @param name The name to use
	 * @param connection The Connection to use
	 * @see com.monk.utils.ProviderExtended
	 */
	public Provider(String driverClass, String name, Connection connection) {
		this.driverClass = driverClass;
		this.name = name;
		this.connection = connection;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public String getName() {
		return name;
	}

	public Connection getConnection() {
		return connection;
	}
}
