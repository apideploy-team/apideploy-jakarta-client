package com.kalman03.apideploy.core.builder;

import com.kalman03.apideploy.core.domain.ApibuilderParam;
import com.kalman03.apideploy.core.domain.ApideployData;
import com.kalman03.apideploy.core.utils.ApideployClientUtils;

/**
 * @author kalman03
 * @since 2023-08-20
 */
public interface ApiBuilderService<T extends ApideployData> {


	T getApiObjects(ApibuilderParam apibuilderParam);

	default String getApiData(ApibuilderParam apibuilderParam){
		T data = getApiObjects(apibuilderParam);
		return ApideployClientUtils.toJSONString(data);
	}
}
