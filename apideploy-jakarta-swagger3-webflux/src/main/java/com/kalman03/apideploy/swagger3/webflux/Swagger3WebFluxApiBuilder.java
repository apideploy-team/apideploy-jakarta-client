package com.kalman03.apideploy.swagger3.webflux;

import java.util.Locale;

import org.springdoc.webflux.api.OpenApiWebfluxResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.stereotype.Component;

import com.kalman03.apideploy.core.builder.ApiBuilderService;
import com.kalman03.apideploy.core.constants.ApiBuilderType;
import com.kalman03.apideploy.core.domain.ApibuilderParam;
import com.kalman03.apideploy.core.domain.ApideployData;

/**
 * @author kalman03
 * @since 2023-08-20
 */
@Order(7)
@Component
public class Swagger3WebFluxApiBuilder implements ApiBuilderService<ApideployData> {

	@Autowired
	private OpenApiWebfluxResource openApiResource;

	@Override
	public ApideployData getApiObjects(ApibuilderParam apibuilderParam) {
		MockServerHttpRequest request = MockServerHttpRequest.get("/", new Object[] {}).build();
		byte[] jsonByte = null;
		try {
			jsonByte = openApiResource.openapiJson(request, "", Locale.CHINA).block();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (jsonByte != null) {
			ApideployData apiSyncData = new ApideployData();
			apiSyncData.setOpenAPI(new String(jsonByte));
			apiSyncData.setApiBuilderType(ApiBuilderType.SWAGGER3_WEBMVC);
			return apiSyncData;
		}
		throw new RuntimeException("Swagger API document build error");
	}

}
