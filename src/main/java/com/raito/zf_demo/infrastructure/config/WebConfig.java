package com.raito.zf_demo.infrastructure.config;

import com.raito.zf_demo.infrastructure.jwt.JwtConfig;
import com.raito.zf_demo.infrastructure.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * @author raito
 * @since 2024/09/06
 */
@Configuration
@RequiredArgsConstructor
@DependsOn("jwtConfig")
public class WebConfig {

    private final JwtConfig config;

    @Bean
    public FilterRegistrationBean<JwtFilter> jwtFilter() {
        FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtFilter(config));
        registrationBean.addUrlPatterns("/*"); // 过滤所有 URL
        return registrationBean;
    }
}