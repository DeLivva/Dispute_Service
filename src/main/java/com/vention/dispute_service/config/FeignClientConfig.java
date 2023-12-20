package com.vention.dispute_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor feignClient1Interceptor() {
        return new CoreFeignClient();
    }

    public static class CoreFeignClient implements RequestInterceptor {
        @Override
        public void apply(RequestTemplate requestTemplate) {
            requestTemplate.header("host", "delivva-dispute-env.eba-chhhwrqq.eu-north-1.elasticbeanstalk.com");
        }
    }
}
