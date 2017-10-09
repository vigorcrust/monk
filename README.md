# MONK-Project

## Idea

This tool allows you to query databases and publish the information to a given monitoring backend. Both the monitoring database and the database to query can be changed without recompiling the core of the program. The plugins are loaded at runtime and thus recompiling is not necessary. You simply have to specify connection parameters and the location of the plugins and it will work. If you want to use another database backend than the following, please feel free to implement a JDBC plugin and use it with this tool. The interface can be found in section 'Create your own Plugins'. 
Following backends are supported:
- all database backends with JDBC functionality, e.g. Oracle, mySQL, ...
- monitoring backends, such as influxDB, Prometheus (uses pushgateway), csv export (and the ones you can write)


## Configuration

The configuration defines which plugins and connection parameters should be used and which queries should be executed.

Example config:
```
{
  "root": {
    "application": "monk",
    "version": "v1",
    "authors": "vigorcrust, alha96",
    "libspath": "/home/monk/path/to/your/libs",
    "configuration": {
      "queryconstants": {
        "###mycustomdate###": "2017-10-09",
        "###yesterday###": "to_date(sysdate-2)",
        "###tablename": "AS MONK_VALUE"
      },
      "monitoringbackendprovider_default": "influx01",
      "monitoringbackendprovider_fallback": "csv01",
      "dbbackendprovider_default": "oracle01",
      "monitoringbackendprovider": [
        {
          "name": "influx01",
          "driver_class": "com.monk.Influx",
          "connection": {
            "connection_string": "http://127.0.0.1:8086/",
            "username": "root",
            "password": "root"
          }
        },
        {
          "name": "prometheus01",
          "driver_class": "com.monk.Prometheus",
          "connection": {
            "connection_string": "127.0.0.1:9091/",
            "username": "",
            "password": ""
          }
        },
        {
          "name": "csv01",
          "driver_class": "com.monk.CSV",
          "connection": {
            "connection_string": "/file/to/use.csv",
            "username": "",
            "password": ""
          }
        }
      ],
      "dbbackendprovider": [
        {
          "name": "oracle01",
          "driver_class": "oracle.jdbc.OracleDriver",
          "connection": {
            "connection_string": "jdbc:oracle:thin:@localhost:11521:XE",
            "username": "system",
            "password": "oracle"
          }
        },
        {
          "name": "csv01",
          "driver_class": "org.relique.jdbc.csv.CsvDriver",
          "connection": {
            "connection_string": "jdbc:relique:csv:/folder/to/export/files/to",
            "username": "",
            "password": ""
          }
        },
        {
          "name": "mysql01",
          "driver_class": "com.mysql.cj.jdbc.Driver",
          "connection": {
            "connection_string": "jdbc:mysql://127.0.0.1:3307/?verifyServerCertificate=false&useSSL=true",
            "username": "root",
            "password": "bla"
          }
        }
      ],
      "queries": [
        {
          "name": "FirstQuery",
          "measurement": "rows",
          "statement": "SELECT name FROM people",
          "timestamp": "",
          "extra": "db=monk$location=/folder/to/export/files/to/output1.csv",
          "database_backend": "csv01"
        },
        {
          "name": "SecondQuery",
          "measurement": "rows",
          "statement": "select count(*) as MONK_VALUE from system.mon_users",
          "timestamp": "",
          "extra": "db=monk",
          "database_backend": "oracle01"
        },
        {
          "name": "ThirdQuery",
          "measurement": "rows",
          "statement": "SELECT COUNT(*) ###tablename### FROM monk.Names",
          "timestamp": "",
          "extra": "db=monk",
          "database_backend": "mysql01"
        }
      ]
    }
  }
}
```

## Create your own Plugins

You can simply create your own plugins and use them with this tool. The interface looks like this:

```
public interface MonitoringBackend {

	/**
	 * Establishes a connection to the monitoring backend
	 * by generating an individual object depending on the backend.
	 * The connectionString can contain additional params (for further
	 * monitoring backends) called 'extra', which are separated by '$'.
	 *
	 * @param connectionString The connection string to use
	 * @param username The username to establish the connection
	 * @param password The password to establish the connection
	 */
	void establishConnection(String connectionString, String username, String password);

	/**
	 * Pushes a single point to the monitoring backend
	 * using the connection established before.
	 *
	 * @param measurement The measurement to use in the database
	 * @param fields The fields to save
	 * @param timestamp The timestamp of this point
	 * @param extra Additional information
	 */
	void pushSinglePoint(String measurement, Map<String, Double> fields, String timestamp, String extra);

	/**
	 * Closes the connection,
	 * if one has been established before.
	 */
	void closeConnection();

}
```
Don't forget to put the mon_service_interface in the libspath!

If there are any questions, don't hesitate to ask.