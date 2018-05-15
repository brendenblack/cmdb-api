package ca.bc.gov.nrs.cmdb.api.infrastructure;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * Configures all controllers annotated with {@link RestController} to have a base path of /api/.
 */
@Configuration
public class RouteConfiguration
{
    @Bean
    public WebMvcRegistrations webMvcRegistrationsHandlerMapping()
    {
        return new WebMvcRegistrations()
        {
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping()
            {
                return new RequestMappingHandlerMapping()
                {
                    private final static String API_BASE_PATH = "api";

                    @Override
                    protected void registerHandlerMethod(Object handler, Method method, RequestMappingInfo mapping)
                    {
                        Class<?> beanType = method.getDeclaringClass();
                        if (AnnotationUtils.findAnnotation(beanType, RestController.class) != null)
                        {
                            PatternsRequestCondition apiPattern = new PatternsRequestCondition(API_BASE_PATH)
                                    .combine(mapping.getPatternsCondition());

                            mapping = new RequestMappingInfo(mapping.getName(), apiPattern,
                                                             mapping.getMethodsCondition(), mapping.getParamsCondition(),
                                                             mapping.getHeadersCondition(), mapping.getConsumesCondition(),
                                                             mapping.getProducesCondition(), mapping.getCustomCondition());
                        }

                        super.registerHandlerMethod(handler, method, mapping);
                    }
                };
            }
        };
    }

    @Bean
    public WebMvcConfigurer forwardToIndex()
    {
        return new WebMvcConfigurer()
        {
            @Override
            public void addViewControllers(ViewControllerRegistry registry)
            {
                // forward requests to /admin and /user to their index.html
                registry.addViewController("/admin").setViewName(
                        "forward:/admin/index.html");
                registry.addViewController("/").setViewName(
                        "forward:/home/index.html");
            }
        };
    }
}
