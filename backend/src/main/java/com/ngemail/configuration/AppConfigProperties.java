package com.ngemail.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "app")
@Validated
public class AppConfigProperties {
    public static class API {
        public static final int VERSION = 1;
    }
}
