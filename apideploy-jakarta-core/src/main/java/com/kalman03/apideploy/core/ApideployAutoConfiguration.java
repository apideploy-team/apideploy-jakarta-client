package com.kalman03.apideploy.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import lombok.extern.slf4j.Slf4j;

/**
 * @author kalman03
 * @since 2023-08-18
 */
@Slf4j
@Configuration
@Import(ApideployService.class)
public class ApideployAutoConfiguration implements ApplicationListener<ApplicationReadyEvent> {

	private ApideployService apideployService;

	@Autowired(required = false)
	public ApideployAutoConfiguration(ApideployService apideployService) {
		this.apideployService = apideployService;
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		if (apideployService != null) {
			apideployService.onApplicationReady();
		}else {
			log.warn("Apideploy service is not enabled.Consider configuring apideploy.config.enabled=true first");
		}
	}
}
