package com.monk.main;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.monk.executor.QueryExecutor;
import com.monk.gson.Configuration;
import com.monk.gson.Root;
import com.monk.loader.ClassLoaderHelper;
import com.monk.loader.JarLoader;
import org.apache.commons.cli.*;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * <h1>Monk Tool</h1>
 * The monk tool executes given query against a database backend
 * and publishes the results to a monitoring backend.
 * <p>
 * The special thing about it is that both the database and the
 * monitoring backend can be changed without recompiling.
 * This means: If you want to use another database you can simply
 * put the jdbc driver side by side the program, tell the program,
 * where it is located and the libraries can be used dynamically.
 * <p>
 * Same applies to the monitoring backend: If you want to publish
 * your results to another monitoring backend, you can simply
 * write a new plugin (according to the defined interface) and
 * put it next to the other plugins. Then you are able to use
 * this monitoring backend as well.
 */
public class Main {

	/**
	 * The main method for the monk tool
	 *
	 * @param args Arguments from the command line
	 */
	public static void main(String[] args) {

		Logger.info("Welcome to MONK TOOL v0.1!");

		//First we look, if a logging prop is next to the jar
		//if so, we use it, otherwise we use the one inside the jar
		String pathToPropFile = System.getProperty("user.dir") + "\\tinylog.properties";
		File propFile = new File(pathToPropFile);
		try {
			Configurator.fromFile(propFile).activate();
			Logger.info("Using given tinylog.properties file: " + pathToPropFile);
		} catch (IOException e) {
			Logger.info("Couldn't find tinylog.properties file side by side with jar. " +
					"Using default properties.");
			Configurator.currentConfig().activate();
		}

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
			formatter.printHelp("java -jar monkt_tool-1.x.jar", options);
			System.exit(1);
			return;
		}

		String jsonPath = cmd.getOptionValue("json");
		File f = new File(jsonPath);
		if (!(f.isFile() && !f.isDirectory())) {
			Logger.error("Provided file '" + jsonPath + "' not found. Please make sure the path is correct.");
			Logger.error("App terminated with errors.");
			System.exit(1);
		}

		//GSON Parser
		JsonParser parser = new JsonParser();
		JsonObject json = null;
		try {
			json = parser.parse(new FileReader(jsonPath)).getAsJsonObject();
		} catch (FileNotFoundException e) {
			Logger.error(e.getMessage());
		}
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
			Logger.error("App terminated with errors.");
			System.exit(1);
		}
		ClassLoader loader =
				ClassLoaderHelper.buildClassLoader(Arrays.asList(new File(jdbcJarsPath.toString())),
						true);

		//Load all needed classes
		JarLoader jarLoader = new JarLoader(config, loader);
		jarLoader.loadAllJars();

		//Create a QueryExecutor and execute the queries
		QueryExecutor qe = new QueryExecutor(config, loader);
		qe.executeQueries();

		Logger.info("App terminated.");

	}
}

