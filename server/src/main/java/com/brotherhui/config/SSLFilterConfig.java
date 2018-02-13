package com.brotherhui.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

public class SSLFilterConfig {

	
    @Bean
    public FilterRegistrationBean filterRegistrationBean(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        List<String> urlPatterns = new ArrayList<String>();

        SSLFilter testFilter = new SSLFilter();   //new过滤器
        urlPatterns.add("/");      //指定需要过滤的url
        filterRegistrationBean.setFilter(testFilter);       //set
        filterRegistrationBean.setUrlPatterns(urlPatterns);     //set

        return filterRegistrationBean;
    }
}
