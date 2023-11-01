package com.kalman03.apideploy.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kalman03
 * @since 2023-08-20
 */
@Slf4j
public class ApideployHttpUtils {
	
	private static HttpURLConnection openConnection(String url, String requestMethod, Map<String, String> headers)
			throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		connection.setRequestMethod(requestMethod);
		if (headers != null) {
			for (Map.Entry<String, String> entry : headers.entrySet()) {
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		return connection;
	}

	private static String readResponse(HttpURLConnection connection) throws IOException {
		int responseCode = connection.getResponseCode();
		log.info("Apideploy-->request to {} with response code:{}", connection.getURL(), responseCode);
		StringBuilder response = new StringBuilder();

		try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
		}
		return response.toString();
	}

	public static String get(String url, Map<String, String> headers) throws IOException {
		HttpURLConnection connection = null;
		try {
			connection = openConnection(url, "GET", headers);
			return readResponse(connection);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static String post(String url, Map<String, String> headers, String postData) throws IOException {
		HttpURLConnection connection = null;
		try {
			connection = openConnection(url, "POST", headers);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			try (OutputStream os = connection.getOutputStream()) {
				byte[] postDataBytes = postData.getBytes("UTF-8");
				os.write(postDataBytes);
			}
			return readResponse(connection);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

}
