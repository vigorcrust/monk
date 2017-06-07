# Guidelines for development

## Requirements

- Must be written in Java
- Must be a command line application
  - args parsing, validating and error handling
- Maven/Bazel/Gradle must be used for build and dependency management
- Must be packaged into an executable Jar file (with all imminent dependencies)
- Connection to database must be done with JDBC
- Suport for multiple databases must be possible (without recompiling)
- JDBC jars are stored in a subfolder "lib"
- Configuration is stored as config.json side-by-side to the jar file
- Insert/Update/Delete statements must be prohibited inside the code
- Logging must be implemented, with RollingFileAppender as default.
  - No exceptions are allowed in logging output
  - Logging should be kept to a minimal and only three levels are allowed (Debug, Info & Fatal)
  - Default LogLevel is Info
- Must be able to import CSV (ex. csvjdbc)
- Must be able to publish results to different backends
  - default backend is Influxdb
- IOC resp. DI must be used
- support for SSL & TLS

### Optional requirements

- Configuration of custom truststore location must be possible
- Possibility to bundle JRE with the application and to have an executable
- Must be able to export CSV
- Location of the config file can be set by a flag
- Executions can be scheduled

## Frameworks (recommendations)

- SLF4J + tinylog/Log4J
- config (https://github.com/typesafehub/config)
- Guice (https://github.com/google/guice)
- packr (https://github.com/libgdx/packr/)
- TestNG
- Quartz

## Resulting folder structure

```
.
+-- jre
|   +-- ...
+-- lib
|   +-- ojdbc8.jar
|   +-- mon_interface.jar
|   +-- mon_influx.jar
+-- log
|   +-- monk.log
+-- import
|   +-- example
|       +-- products.csv
+-- export
|   +-- example
|       +-- mon_products.csv
+-- config.json
+-- log4net.properties
+-- monk.jar
+-- monk-standalone.cmd
+-- monk-standalone.sh
+-- README.md
```

## Configuration

Example config:
```
{
    "application": "monk",
    "version": "v1",
    "author": "Daniel Martens <daniel.martens@t-systems.com",
    "configuration": {
        "monitoringbackendprovider": [{
                "name": "influx01",
                "library": "jar:file:./lib/mon_influx.jar!/",
                "driver_class": "monk.monitoring.provider.influx",
                "connection": {
                    "connection_string": "http://influxdb:8086/write?db=mydb",
                    "username": "influxdbuser",
                    "password": "influxdbpass"
                }
            },
            {
                "name": "prometheus01",
                "library": "jar:file:./lib/mon_prometheus.jar!/",
                "driver_class": "monk.monitoring.provider.prometheus",
                "connection": {
                    "connection_string": "http://pushgateway.example.org:9091/metrics/job/some_job",
                    "username": "prometheususer",
                    "password": "prometheuspass"
                }
            },
            {
                "name": "localfile",
                "library": "jar:file:./lib/mon_csv.jar!/",
                "driver_class": "monk.monitoring.provider.csv",
                "connection": {
                    "connection_string": "./transaction.log",
                    "username": "",
                    "password": ""
                }
            }
        ],
        "monitoringbackendprovider_default": "influx01",
        "monitoringbackendprovider_fallback": "localfile",
        "dbbackendprovider": [{
                "name": "oracle01",
                "library": "jar:file:./lib/ojdbc8.jar!/",
                "driver_class": "oracle.jdbc.OracleDriver",
                "connection": {
                    "connection_string": "dbc:oracle:thin:@host:1521:ORCL",
                    "username": "oracleuser",
                    "password": "oracleuser"
                }
            },
            {
                "name": "csv01",
                "library": "jar:file:./lib/csvjdbc.jar!/",
                "driver_class": "org.relique.jdbc.csv.CsvDriver",
                "connection": {
                    "connection_string": "jdbc:relique:csv:./importfolder/",
                    "username": "",
                    "password": ""
                }
            }
        ],
        "dbbackendprovider_default": "oracle01",
        "queries": [{
            "name": "FirstQuery",
            "measurement": "category_list",
            "statement": "SELECT COUNT(*) AS DBMON_VALUE, CURRENT_TIMESTAMP AS DBMON_TIMESTAMP, LOCATION AS DBMON_TAG_REGION FROM table WHERE category LIKE '%abc%'",
            "timestamp": "query|empty|now|19850412T232050",
            "extra": "host=server01"
        },
        {
            "name": "SecondQuery",
            "measurement": "user_list",
            "monitoring_backend": "prometheus01",
            "database_backend": "csv01",
            "statement": "SELECT COUNT(*) AS DBMON_VALUE FROM table WHERE user_name LIKE '%abc%'",
            "timestamp": "",
            "extra": ""
        }]
    }
}
```
