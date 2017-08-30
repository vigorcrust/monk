package com.monk;

import com.monk.spi.MonitoringBackend;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ahatzold on 24.08.2017 in project monk_project.
 */
public class Prometheus implements MonitoringBackend {

	private PushGateway pg;

	@Override
	public void establishConnection(String connectionString, String username, String password) {
		this.pg = new PushGateway(connectionString);
	}

	@Override
	public void pushSinglePoint(String measurement, HashMap<String, Double> fields, String timestamp, String extra) {

		CollectorRegistry registry = new CollectorRegistry();
		Gauge g = Gauge.build()
				.name(measurement)
				.help("description")
				.register(registry);

		double count = Math.random() * 10;
		g.set(count);

		try {
			Logger.info("Pushing point: ");
			pg.pushAdd(registry, "rows");
		} catch (IOException e) {
			Logger.error(e.getMessage());
			System.exit(1);
		}

	}

	@Override
	public void closeConnection() {
		//doesn't need to be closed
	}
}
