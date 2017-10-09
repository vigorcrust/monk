# Guidelines for development

## Requirements (all done)

- Must be written in Java
- Must be a command line application
  - args parsing, validating and error handling
- Maven/Bazel/Gradle must be used for build and dependency management
- Must be packaged into an executable Jar file (with all imminent dependencies)
- Connection to database must be done with JDBC
- Suport for multiple databases must be possible (without recompiling)
- JDBC jars are stored in a subfolder "libs"
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

### Optional requirements

- Configuration of custom truststore location must be possible
- Possibility to bundle JRE with the application and to have an executable
- Must be able to export CSV (done)
- Location of the config file can be set by a flag (done)
- Executions can be scheduled

## Frameworks

- SLF4J + tinylog/Log4J
- config (https://github.com/typesafehub/config)
- packr (https://github.com/libgdx/packr/)
- TestNG
- Quartz (not used yet)
