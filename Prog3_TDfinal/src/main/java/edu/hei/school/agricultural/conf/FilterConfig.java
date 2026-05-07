package edu.hei.school.agricultural.conf;

import edu.hei.school.agricultural.security.ApiKeyFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ApiKeyFilter> apiKeyFilterRegistration(ApiKeyFilter filter) {
        FilterRegistrationBean<ApiKeyFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(filter);

        registrationBean.addUrlPatterns("/*");

        registrationBean.setOrder(1);

        return registrationBean;
    }
}