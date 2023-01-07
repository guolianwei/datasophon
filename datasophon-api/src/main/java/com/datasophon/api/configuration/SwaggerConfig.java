package com.datasophon.api.configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
@Configuration // 标明是配置类
@EnableOpenApi
public class SwaggerConfig {
    /**
     * 是否开启swagger，生产环境一般关闭，所以这里成变量
     */
    @Value("${swagger.enable}")
    private Boolean enable;
    /**
     * 项目应用名
     */
    @Value("${swagger.application-name}")
    private String applicationName;
    /**
     * 项目版本信息
     */
    @Value("${swagger.application-version}")
    private String applicationVersion;
    /**
     * 项目描述信息
     */
    @Value("${swagger.application-description}")
    private String applicationDescription;
    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .enable(true)
                .groupName("ZRJ")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.smredis.controller"))
                .paths(PathSelectors.ant("/controller/**"))
                .build();
    }


    @SuppressWarnings("all")
    public ApiInfo apiInfo(){
        return new ApiInfo(
                "zrj's api",
                "redis project",
                "v1.0",
                "2261839618@qq.com", //开发者团队的邮箱
                "ZRJ",
                "Apache 2.0",  //许可证
                "http://www.apache.org/licenses/LICENSE-2.0" //许可证链接
        );
    }
}
