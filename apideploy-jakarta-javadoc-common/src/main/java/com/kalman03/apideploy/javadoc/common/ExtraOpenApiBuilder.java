package com.kalman03.apideploy.javadoc.common;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.kalman03.apideploy.core.domain.ApibuilderParam;
import com.ly.doc.builder.openapi.OpenApiBuilder;
import com.ly.doc.constants.ComponentTypeEnum;
import com.ly.doc.constants.DocGlobalConstants;
import com.ly.doc.constants.Methods;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiDoc;
import com.ly.doc.model.ApiMethodDoc;
import com.ly.doc.model.ApiParam;
import com.ly.doc.model.ApiReqParam;
import com.ly.doc.model.DocMapping;
import com.ly.doc.model.TagDoc;
import com.ly.doc.model.openapi.OpenApiTag;
import com.ly.doc.utils.OpenApiSchemaUtil;
import com.power.common.util.CollectionUtil;

/**
 * @author kalman03
 * @since 2024-05-09
 */
public class ExtraOpenApiBuilder extends OpenApiBuilder {

	public Map<String, Object> getOpenAPIJson(final ApibuilderParam apibuilderParam, ApiConfig apiConfig,
			List<ApiDoc> apiDocList){
		this.setComponentKey(DocGlobalConstants.OPENAPI_3_COMPONENT_KRY);
		Map<String, Object> json = new HashMap<>(8);
		json.put("openapi", "3.0.3");
		json.put("info", buildInfo(apiConfig));
		json.put("servers", buildServers(apibuilderParam));
		Set<OpenApiTag> tags = new HashSet<>();
		json.put("tags", tags);
		json.put("paths", buildPaths2(apiConfig, apiDocList, tags));
		json.put("components", buildComponentsSchema(apiDocList,
				ComponentTypeEnum.getComponentEnumByCode(apiConfig.getComponentType())));
		return json;
	}
	
	private List<Map<String, Object>> buildServers(final ApibuilderParam apibuilderParam) {
		List<Map<String, Object>> serverList = new ArrayList<>();
		apibuilderParam.getApideployConfig().getServerUrls().forEach(url -> {
			Map<String, Object> serverMap = new HashMap<>(8);
			serverMap.put("url", url);
			serverList.add(serverMap);
		});
		return serverList;
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
				Map<String, Object> request = buildPathUrls2(apiConfig, methodDoc, methodDoc.getClazzDoc(), path);
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

	private Map<String, Object> buildPathUrlsRequest2(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc,
			String path) {
		Map<String, Object> request = new HashMap<>(20);
		request.put("summary", apiMethodDoc.getDesc());
		if (!Objects.equals(apiMethodDoc.getDesc(), apiMethodDoc.getDetail())) {
			request.put("description", apiMethodDoc.getDetail());
		}
		request.put("tags", apiMethodDoc.getTagRefs().stream().map(TagDoc::getTag).toArray());
		request.put("requestBody", buildRequestBody(apiConfig, apiMethodDoc));
		request.put("parameters", buildParameters2(apiMethodDoc));
		request.put("responses", buildResponses(apiConfig, apiMethodDoc));
		request.put("deprecated", apiMethodDoc.isDeprecated());
		List<String> paths = OpenApiSchemaUtil.getPatternResult("[A-Za-z0-9_{}]*", path);
		paths.add(apiMethodDoc.getType());

		String operationId = paths.stream().filter(StringUtils::isNotEmpty).collect(Collectors.joining("-"));
		request.put("operationId", operationId);

		return request;
	}

	private List<Map<String, Object>> buildParameters2(ApiMethodDoc apiMethodDoc) {
		Map<String, Object> parameters;
		List<Map<String, Object>> parametersList = new ArrayList<>();
		// Handling path parameters
		for (ApiParam apiParam : apiMethodDoc.getPathParams()) {
			parameters = getStringParams2(apiParam, apiParam.isHasItems());
			parameters.put("in", "path");
			List<ApiParam> children = apiParam.getChildren();
			if (CollectionUtil.isEmpty(children)) {
				parametersList.add(parameters);
			}
		}
		for (ApiParam apiParam : apiMethodDoc.getQueryParams()) {
			if (apiParam.isHasItems()) {
				parameters = getStringParams2(apiParam, false);
				Map<String, Object> arrayMap = new HashMap<>();
				arrayMap.put("type", DocGlobalConstants.ARRAY);
				arrayMap.put("items", getStringParams2(apiParam, apiParam.isHasItems()));
				parameters.put("schema", arrayMap);
				parametersList.add(parameters);
			} else {
				parameters = getStringParams2(apiParam, false);
				List<ApiParam> children = apiParam.getChildren();
				if (CollectionUtil.isEmpty(children)) {
					parametersList.add(parameters);
				}
			}
		}
		// with headers
		if (!CollectionUtil.isEmpty(apiMethodDoc.getRequestHeaders())) {
			for (ApiReqParam header : apiMethodDoc.getRequestHeaders()) {
				parameters = new HashMap<>(20);
				parameters.put("name", header.getName());
				parameters.put("description", header.getDesc());
				parameters.put("required", header.isRequired());
				parameters.put("example", header.getValue());
				parameters.put("schema", buildParametersSchema(header));
				parameters.put("in", "header");
				parametersList.add(parameters);
			}
		}
		return parametersList;
	}

	private Map<String, Object> getStringParams2(ApiParam apiParam, boolean hasItems) {
		Map<String, Object> parameters;
		parameters = new HashMap<>(20);
		if (!hasItems) {
			parameters.put("name", apiParam.getField());
			parameters.put("description", apiParam.getDesc());
			parameters.put("required", apiParam.isRequired());
			parameters.put("in", "query");
			parameters.put("schema", buildParametersSchema(apiParam));
		} else {
			if (DocGlobalConstants.OBJECT.equals(apiParam.getType())
					|| (DocGlobalConstants.ARRAY.equals(apiParam.getType()) && apiParam.isHasItems())) {
				parameters.put("type", "object");
				parameters.put("description", "(complex POJO please use @RequestBody)");
			} else {
				String desc = apiParam.getDesc();
				if (desc.contains(DocGlobalConstants.PARAM_TYPE_FILE)) {
					parameters.put("type", DocGlobalConstants.PARAM_TYPE_FILE);
				} else if (desc.contains("string")) {
					parameters.put("type", "string");
				} else {
					parameters.put("type", "integer");
				}
			}
			parameters.putAll(buildParametersSchema(apiParam));
		}

		return parameters;
	}

	/**
	 * Build request body
	 *
	 * @param apiMethodDoc ApiMethodDoc
	 */
	private Map<String, Object> buildRequestBody(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc) {
		Map<String, Object> requestBody = new HashMap<>(8);
		boolean isPost = (apiMethodDoc.getType().equals(Methods.POST.getValue())
				|| apiMethodDoc.getType().equals(Methods.PUT.getValue())
				|| apiMethodDoc.getType().equals(Methods.PATCH.getValue()));
		// add content of post method
		if (isPost) {
			requestBody.put("content", buildContent(apiConfig, apiMethodDoc, false));
			return requestBody;
		}
		return null;
	}

	private Map<String, Object> buildPathUrls2(ApiConfig apiConfig, ApiMethodDoc apiMethodDoc, ApiDoc apiDoc,
			String path) {
		Map<String, Object> request = new HashMap<>(4);
		request.put(apiMethodDoc.getType().toLowerCase(), buildPathUrlsRequest2(apiConfig, apiMethodDoc, apiDoc, path));
		return request;
	}

	private static Map<String, Object> buildInfo(ApiConfig apiConfig) {
		Map<String, Object> infoMap = new HashMap<>(8);
		infoMap.put("title", apiConfig.getProjectName() == null ? "Project Name is Null." : apiConfig.getProjectName());
		infoMap.put("version", "1.0.0");
		return infoMap;
	}

}