package com.monk;

import com.monk.spi.MonitoringBackend;
import org.pmw.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

/**
 * Created by ahatzold on 31.08.2017 in project monk_project.
 */
public class CSV implements MonitoringBackend {
	@Override
	public void establishConnection(String connectionString, String username, String password) {
		//not necessary
	}

	@Override
	public void pushSinglePoint(String measurement, Map<String, Double> fields, String timestamp, String extra) {

		//If no timestamp is given, use the current time
		//and convert the string to long in order to use it
		long tmstp;
		if (timestamp.isEmpty()) {
			Logger.info("No timestamp given. Using the current time.");
			tmstp = System.currentTimeMillis();
		} else {
			tmstp = Long.parseLong(timestamp);
		}

		//Extract the information from extra
		String outputFilePath = getInfoFromExtra("location", extra);
		if (outputFilePath == null) {
			Logger.error("Output File location is not set correctly in config.json.");
			System.exit(1);
		}

		//Then create a new file, if it doesn't exist
		//and append the head (coloumn names)
		String headOfCsv = "timestamp, key, value\r\n";
		File outputFile = new File(outputFilePath);
		if (!outputFile.exists()) {
			try {
				boolean successfullyCreated = outputFile.createNewFile();
				if (!successfullyCreated) {
					Logger.error("Couldn't create output file. Exiting program.");
					System.exit(1);
				}
				Files.write(Paths.get(outputFilePath), headOfCsv.getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				Logger.error(e.getMessage());
			}
		}

		//Append every result as a single line
		StringBuilder fieldsForLog = new StringBuilder();
		for (Map.Entry<String, Double> entry : fields.entrySet()) {
			fieldsForLog.append(entry.getKey());
			fieldsForLog.append("=");
			fieldsForLog.append(entry.getValue());
			String line = tmstp + ", " + entry.getKey() + ", " + entry.getValue() + "\r\n";
			try {
				Files.write(Paths.get(outputFilePath), line.getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				Logger.error(e.getMessage());
			}
		}

		Logger.info("Pushing following point: " +
				"measurement: " + measurement + ", " +
				"fields: " + fieldsForLog + ", " +
				"timestamp: " + tmstp);
	}

	@Override
	public void closeConnection() {
		//not necessary
	}

	private String getInfoFromExtra(String tag, String extra) {

		//TODO Trennzeichen $ dokumentieren
		//split the extra string
		if (!extra.isEmpty()) {
			String[] allExtras = extra.split("\\$");
			for (String info : allExtras) {
				String[] singleExtra = info.split("=");
				//and if you see the key = db
				//use it as the current database
				String key = singleExtra[0];
				String value = singleExtra[1];
				//and if you see the key = db
				//use it as the current database
				if (key.equals(tag)) {
					Logger.info("Using file '" + value + "' as output file");
					return value;
				}
			}
		} else {
			Logger.info("Tag couldn't be found.");
		}
		return null;
	}
}
