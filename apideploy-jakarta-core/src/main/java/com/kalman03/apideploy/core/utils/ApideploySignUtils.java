package com.kalman03.apideploy.core.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author kalman03
 * @since 2023-08-18
 */
public class ApideploySignUtils {

	public static String getSign(String appId, String appSecret, String requestBody, long timestamp)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		Map<String, String> treeMap = new TreeMap<>();
		treeMap.put("appId", appId);
		treeMap.put("timestamp", String.valueOf(timestamp));
		treeMap.put("requestBody", requestBody);
		StringBuilder buffer = new StringBuilder();
		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
			buffer.append(entry.getKey()).append(entry.getValue()).append("&");
		}
		buffer.append(appSecret);
		String sourceString = buffer.toString();
		return md5(sourceString).toUpperCase();
	}

	private static String md5(String plainText) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(plainText.getBytes(StandardCharsets.UTF_8));
		byte[] bytes = md.digest();
		StringBuilder sb = new StringBuilder();
		for (byte aByte : bytes) {
			String hex = Integer.toHexString(0xff & aByte);
			if (hex.length() == 1)
				sb.append('0');
			sb.append(hex);
		}
		return sb.toString();
	}
}
