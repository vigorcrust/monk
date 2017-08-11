package com.monk.utils;

import java.util.HashMap;

/**
 * Created by ahatzold on 10.08.2017 in project monk_project.
 */
public interface QEDelegate {

	void pushSinglePoint(String measurement, HashMap<String, String> fields, String timestamp, String extra);
}
