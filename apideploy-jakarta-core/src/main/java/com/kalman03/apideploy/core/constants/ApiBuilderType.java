package com.kalman03.apideploy.core.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author kalman03
 * @since 2023-08-29
 */
@Getter
@AllArgsConstructor
public enum ApiBuilderType {

	JAVADOC_SPRINGWEB(1), JAVADOC_DUBBO(2), SWAGGER3_WEBMVC(3), SWAGGER2_WEBMVC(4), SWAGGER3_WEBFLUX(5);

	private int type;
}
