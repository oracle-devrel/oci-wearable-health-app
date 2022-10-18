package com.oracle.cloud.wearable.admin.fn;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
	
	public static boolean isEmptyString(String s) {
		return s==null || s.trim().equals("");
	}

	public static String getQueryParameter(String queryString, String userId) {
		if(isEmptyString(queryString))
			return null;
		return parseQueryString(queryString).get(userId);
	}
	
	public static String decode(String value) {
	    try {
			return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			throw new UtilsException("error decoding", e, new com.oracle.cloud.wearable.admin.response.Error(502, "Internal server error"));
		}
	}

	public static Map<String, String> parseQueryString(String input) {
		Map<String,String> queryParameters = new HashMap<String,String>();
		
		Arrays.stream(input.split("&")).forEach(param -> {
			String key = param.split("=")[0];
			
			String value = param.split("=")[1];
			
			if(queryParameters.containsKey(key)) {
				queryParameters.put(key, queryParameters.get(key)+","+decode(value));			
			}else {
				queryParameters.put(key, decode(value));
			}
			
		});
		return queryParameters;
	}
	
	public static <T> T parseJson(String data, ObjectMapper objectMapper, Class<T> objType) {
			try {
				return objectMapper.readValue(data, objType);
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new UtilsException("error parsing json", e1, new com.oracle.cloud.wearable.admin.response.Error(500, "Error parsing post data"));
			}
	}
	

}
