package com.monk.monitoring;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.pmw.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by ahatzold on 26.07.2017 in project monk_project.
 */
public class InfluxBackend implements MonitoringBackend {

	private InfluxDB influxDB;

	@Override
	public void establishConnection(String connectionString, String username, String password) {
		this.influxDB = InfluxDBFactory.connect(connectionString, username, password);
	}

	@Override
	public void pushSinglePoint(String measurement, HashMap<String, String> fields, String timestamp, String extra) {

		//If no timestamp is given, use the current time
		//and convert the string to long in order to use it
		long tmstp;
		if (timestamp.isEmpty()) {
			Logger.info("No timestamp given. Using the current time.");
			tmstp = System.currentTimeMillis();
		} else {
			tmstp = Long.parseLong(timestamp);
		}

		//create the pointBuilder
		Point.Builder pointBuilder = Point.measurement(measurement)
				.time(tmstp, TimeUnit.MILLISECONDS);

		//and add all necessary fields
		for (Map.Entry<String, String> entry : fields.entrySet()) {
			pointBuilder.addField(entry.getKey(), entry.getValue());
		}

		//last build the point
		Point point = pointBuilder.build();

		//TODO auslagern und vereinfachen
		//split the extra string
		if (!extra.isEmpty()) {
			String[] allExtras = extra.split("\\$");
			for (String info : allExtras) {
				String[] singleExtra = info.split("=");
				String key = singleExtra[0];
				String value = singleExtra[1];
				//and if you see the key = db
				//use it as the current database
				//other keys should be added
				if (key.equals("db")) {
					Logger.info("Using database " + value + " for this query");
					influxDB.setDatabase(value);
				} else {
					Logger.info("No database name given. Please specify one in 'extra'.");
				}
			}
		} else {
			Logger.info("'Extra' is empty. This can cause problems when using InfluxDB.");
		}

		//write the point to the database
		influxDB.write(point);

	}

	@Override
	public void closeConnection() {
		influxDB.close();
	}
}
