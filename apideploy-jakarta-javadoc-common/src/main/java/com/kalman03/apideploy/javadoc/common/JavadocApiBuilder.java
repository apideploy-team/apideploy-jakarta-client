package com.kalman03.apideploy.javadoc.common;

import java.io.IOException;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.kalman03.apideploy.core.builder.ApiBuilderService;
import com.kalman03.apideploy.core.constants.ApiBuilderType;
import com.kalman03.apideploy.core.domain.ApibuilderParam;
import com.ly.doc.builder.ProjectDocConfigBuilder;
import com.ly.doc.constants.ComponentTypeEnum;
import com.ly.doc.constants.FrameworkEnum;
import com.ly.doc.factory.BuildTemplateFactory;
import com.ly.doc.helper.JavaProjectBuilderHelper;
import com.ly.doc.model.ApiConfig;
import com.ly.doc.model.ApiDoc;
import com.ly.doc.model.TagDoc;
import com.ly.doc.template.IDocBuildTemplate;
import com.thoughtworks.qdox.JavaProjectBuilder;

/**
 * @author kalman03
 * @since 2023-08-20
 */
public abstract class JavadocApiBuilder implements ApiBuilderService<JavadocSyncData> {

	private final static ObjectMapper objectMapper = JsonMapper.builder().build()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.registerModule(new SimpleModule().addSerializer(TagDoc.class, new TagDocJSONSerializer()))
			.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
			.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true)
			.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));

	@Override
	public String getApiData(ApibuilderParam apibuilderParam) {
		JavadocSyncData data = getApiObjects(apibuilderParam);
		return toJSONString(data);
	}

	protected abstract ApiBuilderType getApiBuilderType();

	@SuppressWarnings("unchecked")
	@Override
	public JavadocSyncData getApiObjects(ApibuilderParam apibuilderParam) {
		ApiConfig apiConfig = ApiConfig.getInstance();
		apiConfig.setServerUrl(apibuilderParam.getApideployConfig().getServerUrls().get(0));
		apiConfig.setServerEnv(apibuilderParam.getApideployConfig().getServerUrls().get(0));
		apiConfig.setParamsDataToTree(true);
		apiConfig.setComponentType(ComponentTypeEnum.NORMAL.getComponentType());
		ApiBuilderType apiBuilderType = getApiBuilderType();
		String framework = apiBuilderType.equals(ApiBuilderType.JAVADOC_SPRINGWEB) ? FrameworkEnum.SPRING.getFramework()
				: FrameworkEnum.DUBBO.getFramework();
		IDocBuildTemplate<ApiDoc> docBuildTemplate = BuildTemplateFactory.getDocBuildTemplate(framework);
		JavaProjectBuilder javaProjectBuilder = JavaProjectBuilderHelper.create();
		ProjectDocConfigBuilder configBuilder = new ProjectDocConfigBuilder(apiConfig, javaProjectBuilder);
		List<ApiDoc> apiDocList = docBuildTemplate.getApiData(configBuilder);

		JavadocSyncData apiSyncData = new JavadocSyncData();
		Map<String, Object> openApiMap = new ExtraOpenApiBuilder().getOpenAPIJson(apibuilderParam, apiConfig,
				apiDocList);
		apiSyncData.setOpenAPI(toJSONString(openApiMap));
		apiDocList.forEach(item -> {
			item.getList().forEach(child -> {
				child.setClazzDoc(null);
			});
		});
		apiSyncData.setApiDocList(apiDocList);
		apiSyncData.setApiBuilderType(apiBuilderType);
		return apiSyncData;
	}

	private String toJSONString(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static class TagDocJSONSerializer extends StdSerializer<TagDoc> {
		private static final long serialVersionUID = 1L;

		public TagDocJSONSerializer() {
			this(null, false);
		}

		protected TagDocJSONSerializer(Class<?> t, boolean dummy) {
			super(t, dummy);
		}

		@Override
		public void serialize(TagDoc value, JsonGenerator gen, SerializerProvider provider) throws IOException {
			gen.writeStartObject();
			gen.writeStringField("tag", value.getTag());
			gen.writeEndObject();
		}
	}
}
