package ec.veronica.job.config;

import lombok.AllArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.StandardCharsets;

@SuppressWarnings("rawtypes")
@Configuration
@AllArgsConstructor
public class BeanConfig {

    private final ApplicationContext context;

    @Bean
    public CamelContext camelContext() {
        SpringCamelContext springCamelContext = new SpringCamelContext(context);
        SimpleRegistry registry = new SimpleRegistry();
        springCamelContext.setRegistry(registry);
        return springCamelContext;
    }

    @Bean
    public ResourceOwnerPasswordAccessTokenProvider resourceOwnerPasswordAccessTokenProvider() {
        return new ResourceOwnerPasswordAccessTokenProvider();
    }

    @Bean
    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addDialect(new Java8TimeDialect());
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        return templateEngine;
    }

    @Bean
    public SpringResourceTemplateResolver htmlTemplateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return templateResolver;
    }

}
