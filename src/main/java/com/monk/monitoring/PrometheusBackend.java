package com.monk.monitoring;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ahatzold on 08.08.2017 in project monk_project.
 */
public class PrometheusBackend implements MonitoringBackend {

	private PushGateway pg;

	@Override
	public void establishConnection(String connectionString, String username, String password) {
		this.pg = new PushGateway(connectionString);
	}

	@Override
	public void pushSinglePoint(String measurement, HashMap<String, String> fields, String timestamp, String extra) {

		CollectorRegistry registry = new CollectorRegistry();
		Gauge g = Gauge.build()
				.name(measurement)
				.help("description")
				.register(registry);

		double count = Math.random() * 10;
		g.set(count);

		try {
			pg.pushAdd(registry, "rows");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void closeConnection() {
		//doesn't need to be closed
	}
}
