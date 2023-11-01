package com.kalman03.apideploy.swagger3.webmvc;

import java.util.Map;

import org.springdoc.webmvc.ui.SwaggerWelcomeActuator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.mock.web.MockHttpServletRequest;
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
@Order(9)
@Component
public class Swagger3WebMVCApiBuilder implements ApiBuilderService<ApideployData> {

	@Autowired
	private SwaggerWelcomeActuator swaggerWelcomeActuator;

	@Override
	public ApideployData getApiObjects(ApibuilderParam apibuilderParam) {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setMethod("GET");
		request.setRequestURI("/");
		Map<String, Object> map = swaggerWelcomeActuator.openapiJson(request);
		if (map != null) {
			ApideployData apiSyncData = new ApideployData();
			apiSyncData.setOpenAPI(ApideployClientUtils.toJSONString(map));
			apiSyncData.setApiBuilderType(ApiBuilderType.SWAGGER3_WEBMVC);
			return apiSyncData;
		}
		throw new RuntimeException("Swagger API document build error");
	}

}
