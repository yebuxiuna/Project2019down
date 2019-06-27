package com.example.demo;

import com.example.demo.upload.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@Configuration
public class WebMvc implements WebMvcConfigurer {

    private Logger logger = LoggerFactory.getLogger(WebMvc.class);

    @Autowired
    private MessageProperties config;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
                .addResourceLocations("file:/D:///javaweb/projectwarehouse/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(intercepto())
//        .addPathPatterns("/user/**")
//        .addPathPatterns("/video/uploadVideo")
//        .addPathPatterns("/video/saveComments")
//        ;
    }

}
