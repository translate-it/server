package com.amadeus.ori.translate.json;

import net.sf.json.JsonConfig;

/**
 * Custom JSON Configuration allows to adapt result format.
 * 
 * @author tobias
 *
 */
public class JSONConfig extends JsonConfig {

	public JSONConfig() {
		super();
		registerJsonValueProcessor( java.util.Date.class, new ISODateJsonValueProcessor() );
	}
}
