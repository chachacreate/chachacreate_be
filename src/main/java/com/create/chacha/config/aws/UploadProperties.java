package com.create.chacha.config.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.upload")
public class UploadProperties {
    private String maxFileSize;
    private String maxRequestSize;

    // Getter / Setter
    public String getMaxFileSize() { return maxFileSize; }
    public void setMaxFileSize(String maxFileSize) { this.maxFileSize = maxFileSize; }

    public String getMaxRequestSize() { return maxRequestSize; }
    public void setMaxRequestSize(String maxRequestSize) { this.maxRequestSize = maxRequestSize; }
}

