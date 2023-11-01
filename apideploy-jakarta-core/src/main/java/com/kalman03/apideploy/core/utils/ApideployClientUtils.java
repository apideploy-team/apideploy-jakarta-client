package com.kalman03.apideploy.core.utils;

import java.time.ZoneId;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * @author kalman03
 * @since 2023-08-25
 */
public class ApideployClientUtils {

	private final static ObjectMapper OBJECT_MAPPER = JsonMapper.builder().build()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
			.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
			.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));

	public static String toJSONString(Object object) {
		if (object == null) {
			return null;
		}
		try {
			return OBJECT_MAPPER.writeValueAsString(object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static JsonNode parseJsonNode(String jsonString) {
		try {
			return OBJECT_MAPPER.readTree(jsonString);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
