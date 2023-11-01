package com.kalman03.apideploy.javadoc.common;

import java.util.List;

import com.kalman03.apideploy.core.domain.ApideployData;
import com.ly.doc.model.ApiDoc;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author kalman03
 * @since 2023-08-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JavadocSyncData extends ApideployData {

	private List<ApiDoc> apiDocList;
}
