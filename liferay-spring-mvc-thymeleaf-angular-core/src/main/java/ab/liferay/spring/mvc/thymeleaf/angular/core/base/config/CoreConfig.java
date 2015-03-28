package ab.liferay.spring.mvc.thymeleaf.angular.core.base.config;

import ab.liferay.spring.mvc.thymeleaf.angular.core.base.adapter.PortletRequestBodyImpl;
import ab.liferay.spring.mvc.thymeleaf.angular.core.base.adapter.PortletResponseBodyImpl;
import ab.liferay.spring.mvc.thymeleaf.angular.core.base.service.PortletService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.web.portlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.portlet.mvc.annotation.AnnotationMethodHandlerAdapter;

import javax.portlet.PortletConfig;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Configuration
@Import(ThymeleafConfig.class)
@ComponentScan(basePackages = {"ab.liferay.spring.mvc.thymeleaf.angular.core.base.service", "ab.liferay.spring.mvc.thymeleaf.angular.core.controller"})
public class CoreConfig {

    public static final String MISSING_PROPERTY_INDICATOR = "??##**!!__";
    private static final String ERROR_VIEW = "error/error";

    @Bean
    public SimpleMappingExceptionResolver mappingExceptionResolver() {
        SimpleMappingExceptionResolver simpleMappingExceptionResolver = new SimpleMappingExceptionResolver();
        simpleMappingExceptionResolver.setDefaultErrorView(ERROR_VIEW);
        return simpleMappingExceptionResolver;
    }

    @Bean
    public AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter() {

        AnnotationMethodHandlerAdapter annotationMethodHandlerAdapter = new AnnotationMethodHandlerAdapter();
        annotationMethodHandlerAdapter.setCustomArgumentResolver(new PortletRequestBodyImpl());
        annotationMethodHandlerAdapter.setCustomModelAndViewResolver(new PortletResponseBodyImpl());
        annotationMethodHandlerAdapter.setOrder(0);

        return annotationMethodHandlerAdapter;
    }

    @Bean
    public MessageSource messageSource(final PortletService portletService) {

        MessageSource messageSource = new AbstractMessageSource() {
            @Override
            protected MessageFormat resolveCode(String code, Locale locale) {

                PortletConfig portletConfig = portletService.getPortletConfig();
                ResourceBundle resourceBundle = portletConfig.getResourceBundle(locale);

                String text;
                try {
                    text = resourceBundle.getString(code);
                } catch (MissingResourceException e) {
                    text = MISSING_PROPERTY_INDICATOR + code + "_" + locale;
                }

                return new MessageFormat(text);
            }
        };
        return messageSource;
    }
}