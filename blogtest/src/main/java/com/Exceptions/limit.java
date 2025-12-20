package com.Exceptions;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

public class limit {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(4)); // 4MB per file
        factory.setMaxRequestSize(DataSize.ofMegabytes(20)); // total request size
        return factory.createMultipartConfig();
    }

}
