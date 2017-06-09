package com.monk.database;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class JdbcDriverShim implements Driver{
    private Driver driver;

    public JdbcDriverShim(Driver driver) {
        this.driver = driver;
    }

    public Connection connect(String s, Properties properties) throws SQLException {
        return this.driver.connect(s, properties);
    }

    public boolean acceptsURL(String s) throws SQLException {
        return this.driver.acceptsURL(s);
    }

    public DriverPropertyInfo[] getPropertyInfo(String s, Properties properties) throws SQLException {
        return this.driver.getPropertyInfo(s, properties);
    }

    public int getMajorVersion() {
        return this.driver.getMajorVersion();
    }

    public int getMinorVersion() {
        return this.driver.getMinorVersion();
    }

    public boolean jdbcCompliant() {
        return this.driver.jdbcCompliant();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.driver.getParentLogger();
    }
}
