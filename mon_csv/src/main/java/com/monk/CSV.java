package com.monk;

import com.monk.spi.MonitoringBackend;
import org.pmw.tinylog.Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
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
	public void pushSinglePoint(String measurement, HashMap<String, Double> fields, String timestamp, String extra) {

		//If no timestamp is given, use the current time
		//and convert the string to long in order to use it
		long tmstp;
		if (timestamp.isEmpty()) {
			Logger.info("No timestamp given. Using the current time.");
			tmstp = System.currentTimeMillis();
		} else {
			tmstp = Long.parseLong(timestamp);
		}

		String outputFilePath = getInfoFromExtra("location", extra);
		if (outputFilePath == null) {
			Logger.error("Output File couldn't be found.");
			System.exit(1);
		}

		PrintWriter writer = null;
		try {
			writer = new PrintWriter(outputFilePath, "UTF-8");
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			Logger.error(e.getMessage());
		}

		writer.println("timestamp, name, value");

		String fieldsForReport = "";
		for (Map.Entry<String, Double> entry : fields.entrySet()) {
			fieldsForReport += entry.getKey() + "=" + entry.getValue();
			writer.println(tmstp + ", " + entry.getKey() + ", " + entry.getValue());
		}

		Logger.info("Pushing following point: " +
				"measurement: " + measurement + ", " +
				"fields: " + fieldsForReport + ", " +
				"timestamp: " + tmstp);

		writer.close();



		/*MockResultSet rs = new MockResultSet("myMock");
		rs.addColumn("rows");
		//rs.addColumn("value");

		HashMap<String, Object> fields2 = new HashMap<String, Object>(fields);

		try {
			rs.addRow(fields2);
		} catch (Exception e) {
			e.printStackTrace();
		}


		try {
			//write the point to the database
			Logger.info("Pushing following point: " +
					"measurement: " + measurement + ", " +
					"fields: " + fieldsForReport + ", " +
					"timestamp: " + tmstp);

			File f = new File(location);
			f.getParentFile().mkdirs();
			f.createNewFile();

			PrintStream stream = new PrintStream(new FileOutputStream(location, true));
			CsvDriver.writeToCsv(rs, stream, true);*//*
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
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
