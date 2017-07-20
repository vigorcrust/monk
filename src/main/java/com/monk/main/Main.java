package com.monk.main;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.monk.gson.Configuration;
import com.monk.gson.Query;
import com.monk.gson.Root;
import com.monk.utils.ClassLoaderHelper;
import com.monk.utils.JarLoader;
import com.monk.utils.QueryExecutor;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		final Logger logger = LoggerFactory.getLogger(Main.class);
		logger.info("Welcome to MONK TOOL v0.1!");

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
			logger.info("Wrong usage of cmd line parameters. Exiting program.");
			logger.info(e.getMessage());
			formatter.printHelp("java -jar monk_project-1.x.jar", options);
			StringBuffer buff = new StringBuffer();
			System.exit(1);
			return;
		}

		String jsonPath = cmd.getOptionValue("json");
		File f = new File(jsonPath);
		if (!(f.isFile() && !f.isDirectory())) {
			logger.error("Provided file '" + jsonPath + "' not found. Please make sure the path is correct.");
			logger.error("App terminated with errors.");
			return;
		}

		//GSON Parser
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(new FileReader(jsonPath)).getAsJsonObject();
		Gson gson = new Gson();
		Root root = gson.fromJson(json.get("root"), Root.class);
		Configuration config = root.getConfiguration();
		logger.info("Configuration parsed");

		//Load the libfolder
		Path jdbcJarsPath = Paths.get(root.getLibsPath());
		ClassLoader loader = ClassLoaderHelper.buildClassLoader(Arrays.asList(new File(jdbcJarsPath.toString())), false);

		//Load all needed classes
		JarLoader.loadAllJars(config, loader);

		//Execute all queries
		ArrayList<Query> queries = config.getQueries();
		QueryExecutor qe = new QueryExecutor(config, queries);
		qe.executeQueries();

		logger.info("App terminated without errors.");
	}
}

