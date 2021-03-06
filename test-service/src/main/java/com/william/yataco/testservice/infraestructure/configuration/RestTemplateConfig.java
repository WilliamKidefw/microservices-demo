package com.william.yataco.testservice.infraestructure.configuration;

import com.william.yataco.testservice.infraestructure.provider.restclient.handler.DemoErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        restTemplate.setErrorHandler(new DemoErrorHandler());
        restTemplate.setInterceptors(Collections.singletonList(new CustomRestTemplateInterceptor()));
        return restTemplate;
    }
}
