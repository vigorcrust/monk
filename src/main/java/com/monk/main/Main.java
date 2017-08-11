package com.monk.main;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.monk.gson.Configuration;
import com.monk.gson.Query;
import com.monk.gson.Root;
import com.monk.monitoring.InfluxBackend;
import com.monk.monitoring.MonitoringBackend;
import com.monk.monitoring.PrometheusBackend;
import com.monk.utils.ClassLoaderHelper;
import com.monk.utils.JarLoader;
import com.monk.utils.QueryExecutor;
import org.apache.commons.cli.*;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.labelers.TimestampLabeler;
import org.pmw.tinylog.policies.SizePolicy;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.RollingFileWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

		//Configuration of the logger
		Configurator.defaultConfig()
				.writer(new RollingFileWriter("log.txt", 1, new TimestampLabeler(), new SizePolicy(10 * 1024)))
				.addWriter(new ConsoleWriter())
				.formatPattern("{date:yyyy-MM-dd HH:mm:ss} {level}: {{class}.{method}()|min-size=50}\t{message}")
				.activate();

		Logger.info("Welcome to MONK TOOL v0.1!");

		//Parse the cmd line arguments
		Options options = new Options();

		Option jsonFile = new Option("j", "json", true, "Path to config.json to configure tool");
		jsonFile.setRequired(true);
		options.addOption(jsonFile);

		CommandLineParser cmdLineParser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = cmdLineParser.parse(options, args);
		} catch (ParseException e) {
			Logger.info("Wrong usage of cmd line parameters. Exiting program.");
			Logger.info(e.getMessage());
			formatter.printHelp("java -jar monk_project-1.x.jar", options);
			System.exit(1);
			return;
		}

		String jsonPath = cmd.getOptionValue("json");
		File f = new File(jsonPath);
		if (!(f.isFile() && !f.isDirectory())) {
			Logger.error("Provided file '" + jsonPath + "' not found. Please make sure the path is correct.");
			Logger.error("App terminated with errors.");
			return;
		}


		//GSON Parser
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(new FileReader(jsonPath)).getAsJsonObject();
		Gson gson = new Gson();
		Root root = gson.fromJson(json.get("root"), Root.class);
		Configuration config = root.getConfiguration();
		Logger.info("Configuration parsed: " + jsonPath);

		//Load the libfolder
		Path jdbcJarsPath = null;
		try {
			jdbcJarsPath = Paths.get(root.getLibsPath());
		} catch (NullPointerException e) {
			Logger.error("Couldn't find path to libraries. Please specify it in config.json");
			System.exit(1);
		}
		ClassLoader loader = ClassLoaderHelper.buildClassLoader(Arrays.asList(new File(jdbcJarsPath.toString())), false);

		//Load all needed classes
		JarLoader.loadAllJars(config, loader);

		//This will be replaced by Guice so that it is changeable
		MonitoringBackend influxDB = new InfluxBackend();
		influxDB.establishConnection("http://127.0.0.1:8086/", "root", "root");
		//Execute all queries
		ArrayList<Query> queries = config.getQueries();
		QueryExecutor qe = new QueryExecutor(config, queries, influxDB::pushSinglePoint);
		qe.executeQueries();
		influxDB.closeConnection();

		MonitoringBackend prometheus = new PrometheusBackend();
		prometheus.establishConnection("127.0.0.1:9091", "", "");
		ArrayList<Query> queries2 = config.getQueries();
		QueryExecutor qe2 = new QueryExecutor(config, queries2, prometheus::pushSinglePoint);
		qe2.executeQueries();



		Logger.info("App terminated.");
	}
}

