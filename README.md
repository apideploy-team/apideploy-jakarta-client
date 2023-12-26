English | [中文](README-zh_CN.md)

### Introduction

**apideploy-jakarta-client** is a java project **API document automatic generation toolkit and [apideploy.com](https://www.apideploy.com) website deployment SDK**. It supports [Swagger3 (OAS 3.0)](https://swagger.io/specification/v3/), [Javadoc specifications](https://en.wikipedia.org/wiki/Javadoc), and Java development frameworks such as [Spring WebMVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html), [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html) and [Apache Dubbo](https://dubbo.apache.org/en/index.html)'s documentation comments.

Note: This client is suitable for springboot 3.0+ projects using Java 17 or above. The swagger part is implemented based on [springdoc](https://github.com/springdoc/springdoc-openapi). For the implementation of java1.8 or below, please use [apideploy-java-client](https://github.com/apideploy-team/apideploy-java-client).

### Guides

1.Add maven dependency to pom.xml.

> You have to choose the appropriate dependencies according to the framework of your project. The following is the correspondence between maven dependencies and frameworks.
>

<table style="overflow-x: auto;width: 100%;border-collapse;">
    <tr>
        <th>/</th>
        <th>Swagger3(OAS 3.0)</th>
        <th>Javadoc</th>
    </tr>
    <tr>
        <td>Spring WebMVC</td>
        <td>&lt;dependency&gt; &lt;groupId&gt;com.kalman03&lt;/groupId&gt; &lt;artifactId&gt;apideploy-jakarta-swagger3-webmvc&lt;/artifactId&gt; &lt;/dependency&gt;</td>
        <td>&lt;dependency&gt; &lt;groupId&gt;com.kalman03&lt;/groupId&gt; &lt;artifactId&gt;apideploy-jakarta-javadoc-springweb&lt;/artifactId&gt; &lt;/dependency&gt;</td>
    </tr>
    <tr>
        <td>Spring WebFlux</td>
        <td>&lt;dependency&gt; &lt;groupId&gt;com.kalman03&lt;/groupId&gt; &lt;artifactId&gt;apideploy-jakarta-swagger3-webflux&lt;/artifactId&gt; &lt;/dependency&gt;</td>
        <td>&lt;dependency&gt; &lt;groupId&gt;com.kalman03&lt;/groupId&gt; &lt;artifactId&gt;apideploy-jakarta-javadoc-springweb&lt;/artifactId&gt; &lt;/dependency&gt;</td>
    </tr>
    <tr>
        <td>Apache Dubbo</td>
        <td></td>
        <td>&lt;dependency&gt; &lt;groupId&gt;com.kalman03&lt;/groupId&gt; &lt;artifactId&gt;apideploy-jakarta-javadoc-dubbo&lt;/artifactId&gt; &lt;/dependency&gt;</td>
    </tr>
</table>


2.Config the application.properties or application.yaml.

```properties
apideploy.config.enabled=true
apideploy.config.endpoint=https://www.apideploy.cn/openapi/sync
apideploy.config.appId=${appId}
apideploy.config.appSecret=${appSecret}
apideploy.config.autoPublish=true
```

The `${appId}` and `${appSecret}` in the configuration file can be applied for free through the apideploy.com website. 

For more parameter configuration, please refer to: ：[https://doc.apideploy.cn/](https://doc.apideploy.cn/)

3.Run it and see the final result.

The above three steps have completed all the configuration of apideploy. The next step is to write your Javadoc (strongly recommended) or complete the API definition based on Swagger. When everything is ready, run the system locally and the API will be automatically generated and synchronized to the [apideploy.com](https://www.apideploy.com) website. Then you can happily check, debug and do version management on [apideploy.com](https://www.apideploy.com) .

### Samples

For more samples, please refer to [apideploy-java-demos](https://github.com/apideploy-team/apideploy-java-demos).

### License

apideploy-java-client is released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).

