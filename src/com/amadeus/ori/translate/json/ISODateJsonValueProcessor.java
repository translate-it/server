package com.amadeus.ori.translate.json;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * Converts java.util.Date to ISO date String
 * 
 * @author tobias
 *
 */
public class ISODateJsonValueProcessor implements JsonValueProcessor {

	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	public Object processArrayValue(Object value, JsonConfig jsonConfig) {
		return process(value);
	}

	public Object processObjectValue(Object value, JsonConfig jsonConfig) {
		return process(value);
	}

	private Object process(Object value) {

		if (value == null) {
			return null;
		}

		return FORMATTER.format((Date) value);
	}

	public Object processObjectValue(String arg0, Object value, JsonConfig arg2) {

		return process(value);
	}

}
