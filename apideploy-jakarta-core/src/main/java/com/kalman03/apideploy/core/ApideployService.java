package com.kalman03.apideploy.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.kalman03.apideploy.core.builder.ApiBuilderService;
import com.kalman03.apideploy.core.domain.ApibuilderParam;
import com.kalman03.apideploy.core.domain.ApideployConfig;
import com.kalman03.apideploy.core.utils.ApideployClientUtils;
import com.kalman03.apideploy.core.utils.ApideployHttpUtils;
import com.kalman03.apideploy.core.utils.ApideploySignUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kalman03
 * @since 2023-08-18
 */
@Slf4j
@Service
@EnableConfigurationProperties(ApideployConfig.class)
@ConditionalOnProperty(prefix = "apideploy.config", name = "enabled", havingValue = "true", matchIfMissing = false)
@ComponentScan(basePackages = { "com.kalman03.apideploy.javadoc.dubbo", "com.kalman03.apideploy.javadoc.spring",
		"com.kalman03.apideploy.swagger3.webflux", "com.kalman03.apideploy.swagger3.webmvc" })
public class ApideployService implements InitializingBean, ApplicationContextAware {

	private ApideployConfig apideployConfig;
	private ApplicationContext applicationContext;
	private ApiBuilderService<?> apiBuilderService;

	@Autowired(required = false)
	public ApideployService(ApideployConfig apideployConfig) {
		this.apideployConfig = apideployConfig;
	}

	private void checkApideployConfig() {
		if (!StringUtils.hasText(apideployConfig.getAppId())) {
			throw new IllegalArgumentException("Apideploy--> apideploy.config.appId is required.");
		}
		if (!StringUtils.hasText(apideployConfig.getAppSecret())) {
			throw new IllegalArgumentException("Apideploy--> apideploy.config.appSecret is required.");
		}
	}
	@SuppressWarnings("rawtypes")
	@Override
	public void afterPropertiesSet() throws Exception {
		Map<String, ApiBuilderService> matchingBeans = BeanFactoryUtils
				.beansOfTypeIncludingAncestors(applicationContext, ApiBuilderService.class, true, false);
		if (matchingBeans.isEmpty()) {
			throw new RuntimeException(
					"Apideploy-->The implementation of ApiBuilderService was not found, please make sure to depend on at least one implementation");

		}
		List<ApiBuilderService> apiBuilderServices = new ArrayList<>(matchingBeans.values());
		AnnotationAwareOrderComparator.sort(apiBuilderServices);
		if (apiBuilderServices.size() > 1) {
			log.info(
					"Apideploy-->Total of {} ApiBuilderService implementations are found, and the one with the highest priority is taken:{}",
					apiBuilderServices.size(), apiBuilderServices.get(0).getClass().getName());
		}
		this.apiBuilderService = apiBuilderServices.get(0);
	}

	private String getApiData() throws UnsupportedEncodingException {
		ApibuilderParam apibuilderParam = new ApibuilderParam();
		apibuilderParam.setApideployConfig(apideployConfig);
		String apiData = apiBuilderService.getApiData(apibuilderParam);
		Map<String, Object> dataMap = new HashMap<>(8);
		dataMap.put("apiData", apiData);
		dataMap.put("apiConfig", apideployConfig);
		String data = ApideployClientUtils.toJSONString(dataMap);
		if (data.getBytes("utf-8").length / (1024 * 1024) > 10) {
			throw new UnsupportedOperationException(
					"Apideploy-->The maximum data synchronized by the api requested cannot exceed 10M.");
		}
		return data;
	}

	public void onApplicationReady() {
		this.checkApideployConfig();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					long startTime = System.nanoTime();
					log.info("Apideploy-->Start collecting API documentation information.....");
					String requestBody = getApiData();
					if (requestBody == null) {
						log.error(
								"Apideploy-->Completed collecting API documentation information,but got null,ignore it.");
						return;
					}
					log.info("Apideploy-->Completed collecting API documentation information,cost time:{}ms",
							System.nanoTime() - startTime);
					log.info("Apideploy-->Begin API documentation synchronization......");
					boolean success = sendRequest(requestBody, apideployConfig);
					if (success) {
						log.info("Apideploy-->Finished API documentation synchronization,cost time:{}ms",
								System.nanoTime() - startTime);
						log.info(
								"Apideploy-->Now,you can visit apideploy.com to access synchronized API documentation.");
					}
				} catch (Exception e) {
					log.info(
							"Apideploy-->It looks like something was wrong,please troubleshoot based on the exception:\n",
							e);
				}
			}
		});
		thread.setName("Thread-Apideploy-Sync");
		thread.start();
	}

	private boolean sendRequest(String requestBody, ApideployConfig apideployConfig)
			throws IOException, NoSuchAlgorithmException {
		String uri = buildRequestUrl(requestBody, apideployConfig);
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json;charset=UTF-8");
		headers.put("User-Agent", "Apideploy-Java-Client");
		String data = ApideployHttpUtils.post(uri, headers, requestBody);
		JsonNode jsonNode = ApideployClientUtils.parseJsonNode(data);
		if (jsonNode.get("code").asInt() != 0) {
			log.error("Apideploy-->Synchronize Apideploy umentation to {} with response code : {},message : {}",
					apideployConfig.getEndpoint(), jsonNode.get("code"), jsonNode.get("message"));
			return false;
		} else {
			log.info("Apideploy-->Synchronize Apideploy umentation to {} success.", apideployConfig.getEndpoint());
			return true;
		}
	}

	private String buildRequestUrl(String requestBody, ApideployConfig apideployConfig)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		long timestamp = System.nanoTime();
		String sign = ApideploySignUtils.getSign(apideployConfig.getAppId(), apideployConfig.getAppSecret(),
				requestBody, timestamp);
		String url = apideployConfig.getEndpoint() + "?appId=%s&timestamp=%s&sign=%s";
		return String.format(url, apideployConfig.getAppId(), timestamp, sign);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
