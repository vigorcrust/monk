package com.monk.loader;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Simple wrapper for java.sql.Driver
 *
 * @author damarten on 13.07.2017
 * @see java.sql.Driver
 */
class DriverShim implements java.sql.Driver {

	private java.sql.Driver driver;

	DriverShim(java.sql.Driver d) {
		this.driver = d;
	}

	public boolean acceptsURL(String u) throws SQLException {
		return this.driver.acceptsURL(u);
	}

	public Connection connect(String u, Properties p) throws SQLException {
		return this.driver.connect(u, p);
	}

	public int getMajorVersion() {
		return this.driver.getMajorVersion();
	}

	public int getMinorVersion() {
		return this.driver.getMinorVersion();
	}

	public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException {
		return this.driver.getPropertyInfo(u, p);
	}

	public boolean jdbcCompliant() {
		return this.driver.jdbcCompliant();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		Logger logger = null;
		try {
			Method method = this.driver.getClass().getMethod("getParentLogger");
			logger = (Logger) method.invoke(this.driver);
		} catch (Exception e) {
			org.pmw.tinylog.Logger.error(e.getMessage());
			System.exit(1);
		}
		return logger;
	}
}
