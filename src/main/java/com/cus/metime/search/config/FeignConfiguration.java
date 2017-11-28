package com.cus.metime.search.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.cus.metime.search")
public class FeignConfiguration {

}
