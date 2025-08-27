package com.create.chacha.config.app.toss;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter@Setter
@ConfigurationProperties(prefix = "toss")
public class TossProperties {

    private String clientKey;
    private String clientSecret;
    private String clientSecurity;

}