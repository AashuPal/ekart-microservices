package com.infy.ekart.apigateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.keep-alive")
public class KeepAliveProperties {
    private boolean enabled = false;
    private long intervalMs = 600000;
    private List<String> services = new ArrayList<>();
}