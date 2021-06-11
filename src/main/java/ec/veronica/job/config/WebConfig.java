package ec.veronica.job.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //registry.addResourceHandler("/asset/**").addResourceLocations("classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/", "/");
        //registry.addResourceHandler("/asset/**").addResourceLocations("classpath:/resources/");
        //registry.addResourceHandler("/asset/**").addResourceLocations("classpath:/static/");
    }

}
