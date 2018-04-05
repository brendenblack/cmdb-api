package ca.bc.gov.nrs.cmdb.api.infrastructure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration
{
    @Bean
    public Docket api()
    {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    @Value("${cmdb.api.title}")
    private String apiTitle;

    @Value("${cmdb.api.description}")
    private String apiDescription;

    @Value("${cmdb.api.version}")
    private String apiVersion;

    @Value("${cmdb.api.termsOfService.url}")
    private String apiTermsOfServiceUrl;

    @Value("${cmdb.api.contact.name}")
    private String apiContactName;

    @Value("${cmdb.api.contact.url}")
    private String apiContactUrl;

    @Value("${cmdb.api.contact.email}")
    private String apiContactEmail;

    @Value("${cmdb.api.license.name}")
    private String licenseName;

    @Value("${cmdb.api.license.url}")
    private String licenseUrl;

    private ApiInfo apiInfo()
    {
        Contact contact = new Contact(apiContactName, apiContactUrl, apiContactEmail);
        return new ApiInfo(
                apiTitle,
                apiDescription,
                apiVersion,
                apiTermsOfServiceUrl,
                contact,
                licenseName,
                licenseUrl,
                new ArrayList<>());
    }
}
