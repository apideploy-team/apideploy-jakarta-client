package com.kalman03.apideploy.core.domain;

import com.kalman03.apideploy.core.constants.ApiBuilderType;

import lombok.Data;

/**
 * @author kalman03
 * @since 2023-05-12
 */
@Data
public class ApideployData {

	private String openAPI;

	private ApiBuilderType apiBuilderType;
}
