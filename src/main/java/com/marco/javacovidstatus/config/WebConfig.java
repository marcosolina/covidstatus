package com.marco.javacovidstatus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * This configure springboot in order to expose the files provided with my
     * custom webjar. My webjar inclused some utils Javascript functions, and css,
     * for example:
     * <ul>
     *  <li>Bootstrap 4</li>
     *  <li>Pnotify</li>
     *  <li>Font awesom</li>
     *  <li>My custom Javascript (MarcoUtils)</li>
     * </ul>
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry aRegistry) {
        aRegistry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
