package com.kalman03.apideploy.swagger3.webflux;

import java.util.Map;

import org.springdoc.webflux.ui.SwaggerWelcomeActuator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.stereotype.Component;

import com.kalman03.apideploy.core.builder.ApiBuilderService;
import com.kalman03.apideploy.core.constants.ApiBuilderType;
import com.kalman03.apideploy.core.domain.ApibuilderParam;
import com.kalman03.apideploy.core.domain.ApideployData;
import com.kalman03.apideploy.core.utils.ApideployClientUtils;

/**
 * @author kalman03
 * @since 2023-08-20
 */
@Order(7)
@Component
public class Swagger3WebFluxApiBuilder implements ApiBuilderService<ApideployData> {

	@Autowired
	private SwaggerWelcomeActuator swaggerWelcomeActuator;

	@Override
	public ApideployData getApiObjects(ApibuilderParam apibuilderParam) {
		MockServerHttpRequest request = MockServerHttpRequest.get("/", new Object[] {}).build();
		Map<String, Object> map = swaggerWelcomeActuator.getSwaggerUiConfig(request);
		if (map != null) {
			ApideployData apiSyncData = new ApideployData();
			apiSyncData.setOpenAPI(ApideployClientUtils.toJSONString(map));
			apiSyncData.setApiBuilderType(ApiBuilderType.SWAGGER3_WEBFLUX);
			return apiSyncData;
		}
		throw new RuntimeException("Swagger API document build error");
	}

}
