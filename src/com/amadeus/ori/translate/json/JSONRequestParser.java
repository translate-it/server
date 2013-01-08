package com.amadeus.ori.translate.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

public class JSONRequestParser {

	
	public static JSONObject parseRequest(HttpServletRequest request) throws IOException {

		//we need to parse the post request
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));

		StringBuilder sb = new StringBuilder();
		String line;

		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		return JSONObject.fromObject( sb.toString() ); 

	}
}
