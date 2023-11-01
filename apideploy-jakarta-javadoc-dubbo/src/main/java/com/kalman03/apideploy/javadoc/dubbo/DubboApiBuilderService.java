package com.kalman03.apideploy.javadoc.dubbo;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.kalman03.apideploy.core.constants.ApiBuilderType;
import com.kalman03.apideploy.javadoc.common.JavadocApiBuilder;

/**
 * @author kalman03
 * @since 2023-08-25
 */
@Order(6)
@Component
public class DubboApiBuilderService extends JavadocApiBuilder {

	@Override
	protected ApiBuilderType getApiBuilderType() {
		return ApiBuilderType.JAVADOC_DUBBO;
	}

}
