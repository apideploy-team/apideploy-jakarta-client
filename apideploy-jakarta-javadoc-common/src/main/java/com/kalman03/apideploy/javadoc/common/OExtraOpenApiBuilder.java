package com.kalman03.apideploy.javadoc.common;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.kalman03.apideploy.core.domain.ApibuilderParam;
import com.ly.doc.builder.openapi.OpenApiBuilder;
import com.ly.doc.constants.ComponentTypeEnum;
import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiDoc;
import com.ly.doc.model.ApiMethodDoc;
import com.ly.doc.model.DocMapping;
import com.ly.doc.model.TagDoc;
import com.ly.doc.model.openapi.OpenApiTag;

/**
 * @author kalman03
 * @since 2024-05-09
 */
@Deprecated
public class OExtraOpenApiBuilder extends OpenApiBuilder {

	public Map<String, Object> getOpenAPIJson(final ApibuilderParam apibuilderParam, ApiConfig apiConfig,
			List<ApiDoc> apiDocList) {
		this.setComponentKey(DocGlobalConstants.OPENAPI_3_COMPONENT_KRY);
		Map<String, Object> json = new HashMap<>(8);
		json.put("openapi", "3.0.3");
		json.put("info", buildInfo(apiConfig));
		json.put("servers", buildServers(apiConfig));
		Set<OpenApiTag> tags = new HashSet<>();
		json.put("tags", tags);
		json.put("paths", buildPaths2(apiConfig, apiDocList, tags));
		json.put("components", buildComponentsSchema(apiDocList,
				ComponentTypeEnum.getComponentEnumByCode(apiConfig.getComponentType())));
		return json;
	}

	private Map<String, Map<String, Object>> buildPaths2(ApiConfig apiConfig, List<ApiDoc> apiDocList,
			Set<OpenApiTag> tags) {
		Map<String, Map<String, Object>> pathMap = new HashMap<>(500);
		Set<ApiMethodDoc> methodDocs = DocMapping.METHOD_DOCS;
		for (ApiMethodDoc methodDoc : methodDocs) {
			String[] paths = methodDoc.getPath().split(";");
			for (String path : paths) {
				if (isBlank(path)) {
					continue;
				}
				path = path.trim();
				Map<String, Object> request = buildPathUrls(apiConfig, methodDoc, methodDoc.getClazzDoc());
				if (!pathMap.containsKey(path)) {
					pathMap.put(path, request);
				} else {
					Map<String, Object> oldRequest = pathMap.get(path);
					oldRequest.putAll(request);
				}
			}
		}
		for (Map.Entry<String, TagDoc> docEntry : DocMapping.TAG_DOC.entrySet()) {
			String tag = docEntry.getKey();
			tags.add(OpenApiTag.of(tag, tag));
		}
		return pathMap;
	}

	@Override
	public Map<String, Object> buildPathUrls(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc) {
		Map<String, Object> request = new HashMap<>(4);
		request.put(apiMethodDoc.getType().toLowerCase(), buildPathUrlsRequest(apiConfig, apiMethodDoc, apiDoc));
		return request;
	}

	private static Map<String, Object> buildInfo(ApiConfig apiConfig) {
		Map<String, Object> infoMap = new HashMap<>(8);
		infoMap.put("title", apiConfig.getProjectName() == null ? "Project Name is Null." : apiConfig.getProjectName());
		infoMap.put("version", "1.0.0");
		return infoMap;
	}

	private static List<Map<String, Object>> buildServers(ApiConfig config) {
		List<Map<String, Object>> serverList = new ArrayList<>();
		Map<String, Object> serverMap = new HashMap<>(8);
		serverMap.put("url", config.getServerUrl() == null ? "" : config.getServerUrl());
		serverList.add(serverMap);
		return serverList;
	}
}