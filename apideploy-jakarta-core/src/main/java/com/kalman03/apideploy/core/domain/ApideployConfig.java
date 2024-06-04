package com.kalman03.apideploy.core.domain;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * @author kalman03
 * @since 2023-08-18
 */
@Data
@ConfigurationProperties(prefix = "apideploy.config")
public class ApideployConfig {

	/**
	 * Enable API documentation synchronization
	 */
	private boolean enabled = true;
	/**
	 * The apideploy synchronize endpoint
	 */
	private String endpoint = "https://open.apideploy.com/apisync";
	/**
	 * appId
	 */
	private String appId;
	/**
	 * appSecret
	 */
	@JsonIgnore
	private String appSecret;
	/**
	 * Gateway address, can be configured for different environments (e.g., local,
	 * test, pre-production, production) with distinct addresses
	 */
	private List<String> serverUrls = Arrays.asList("https://${gatewayUrl}");
	/**
	 * Global request headers
	 */
	private List<String> globalHeaders;
	/**
	 * Global request URL parameters
	 */
	private List<String> globalUrlParams;
	/**
	 * Set the publish strategy when API sync success. The default is true and will be published automatically.
	 */
	private boolean autoPublish = true;
	/**
	 * Add patterns for packages the API should be included in.
	 */
	private List<String> includePackagePatterns;
	/**
	 * Add patterns for packages the API should be excluded from.
	 */
	private List<String> excludePackagePatterns;
}