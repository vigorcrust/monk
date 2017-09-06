package com.monk;

import com.monk.spi.MonitoringBackend;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * Plugin to use Prometheus as MonitoringBackend
 * <p>
 * Uses Pushgateway as dependency
 *
 * @author ahatzold on 24.08.2017
 */
public class Prometheus implements MonitoringBackend {

	private PushGateway pg;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void establishConnection(String connectionString, String username, String password) {
		this.pg = new PushGateway(connectionString);
	}

	/**
	 * {@inheritDoc}
	 */
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

		//create a registry
		CollectorRegistry registry = new CollectorRegistry();

		//create for every entry a new Gauge and register it
		StringBuilder fieldsForLog = new StringBuilder();
		for (Map.Entry<String, Double> entry : fields.entrySet()) {
			Gauge g = Gauge.build()
					.name(measurement)
					.help("description")
					.register(registry);
			g.set(entry.getValue());
			fieldsForLog.append(entry.getKey());
			fieldsForLog.append("=");
			fieldsForLog.append(entry.getValue());
		}

		//last push the point
		try {
			Logger.info("Pushing following point: " +
					"measurement: " + measurement + ", " +
					"fields: " + fieldsForLog + ", " +
					"timestamp: " + tmstp);
			pg.pushAdd(registry, "rowsJob");
		} catch (IOException e) {
			Logger.error(e.getMessage());
			System.exit(1);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void closeConnection() {
		//doesn't need to be closed
	}
}
