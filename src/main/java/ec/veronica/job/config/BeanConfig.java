package ec.veronica.job.config;

import lombok.AllArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

}