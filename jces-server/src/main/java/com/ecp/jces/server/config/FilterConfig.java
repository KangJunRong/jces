package com.ecp.jces.server.config;


import com.ecp.jces.server.filter.AccessLogFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FilterConfig {
    @Autowired
    private AccessLogFilter accessLogFilter;
    @Bean
    public FilterRegistrationBean registFilter() {
        FilterRegistrationBean<AccessLogFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(accessLogFilter);
        // context-path 后面
        registration.addUrlPatterns("/*");
        registration.setName("accessLogFilter");
        registration.setOrder(1);
        return registration;
    }

}