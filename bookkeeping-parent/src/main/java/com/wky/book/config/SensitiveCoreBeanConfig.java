package com.wky.book.config;

import com.wky.sensitive.aspect.SensitiveCoreBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SensitiveCoreBeanConfig {
    @Bean
    public SensitiveCoreBean sensitiveCoreBean() {
        return new SensitiveCoreBean();
    }

}