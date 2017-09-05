package com.monk.main;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.monk.executor.QueryExecutor;
import com.monk.gson.Configuration;
import com.monk.gson.Query;
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
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {

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
			Logger.error("App terminated with errors.");
			System.exit(1);
		}
		ClassLoader loader =
				ClassLoaderHelper.buildClassLoader(Arrays.asList(new File(jdbcJarsPath.toString())),
						true);

		//Load all needed classes
		JarLoader jarLoader = new JarLoader(config, loader);
		jarLoader.loadAllJars();

		//Get the list of queries and execute them
		List<Query> queries = config.getQueries();
		QueryExecutor qe = new QueryExecutor(config, queries, loader);
		qe.executeQueries();

		Logger.info("App terminated.");

	}
}

