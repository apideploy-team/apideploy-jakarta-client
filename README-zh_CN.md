[English](README.md) | 中文

### 介绍

**apideploy-jakarta-client** 是一个Java系统的API 文档自动生成工具包与[apideploy.com](https://www.apideploy.com)网站的部署SDK，它兼容[Swagger3 (OAS 3.0)](https://swagger.io/specification/v3/), [Javadoc](https://zh.wikipedia.org/wiki/Javadoc)规范，支持Java开发框架如 [Spring WebMVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html), [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html) 以及[Apache Dubbo](https://cn.dubbo.apache.org/zh-cn/)的文档注释。

**注意：**该客户端适用于Java 17或以上版本的springboot3.0以上项目，swagger的部分基于springdoc实现。java1.8或者以下版本的实现请使用[apideploy-java-client](https://github.com/apideploy-team/apideploy-java-client)。

### 使用指南

1.添加Maven依赖到pom.xml：

> 你得根据你系统的框架选择合适的依赖，以下是maven依赖与框架的对应关系

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



2.配置application.properties or application.yaml（以下基于springboot的项目application.properties配置示例）：

```properties
apideploy.config.enabled=true
apideploy.config.endpoint=https://localhost/openapi/sync
apideploy.config.appId=${appId}
apideploy.config.appSecret=${appSecret}
apideploy.config.autoPublish=true
```

配置文件中的`${appId}`和`${appSecret}`可以通过apideploy.com网站免费申请。

关于更多参数配置请参考：[https://doc.apideploy.cn/](https://doc.apideploy.cn/)

3.运行并查看结果.

以上步骤已经完成所有关于apideploy的配置，接下来就是写你的Javadoc（推荐）或者基于Swagger的方式完成API的定义。当一切就绪，在本地运行该系统，API会自动生成并同步到apideploy网站，接下来就可以在apideploy上愉快的查阅、调试并做版本管理了。

### Demo

更多使用案例，请参考[apideploy-java-demos](https://github.com/apideploy-team/apideploy-java-demos).

### License

apideploy-java8-client is released under the [Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0).

