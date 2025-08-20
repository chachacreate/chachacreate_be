package com.create.chacha.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private List<String> whiteList;

    private RoleWhiteList role;

    // 중첩 클래스를 사용하는 이유 : application.properties의 설정과 맞춰주기 위해서
    @Getter
    @Setter
    public static class RoleWhiteList {
        private List<String> user;
        private List<String> seller;
        private List<String> personal_seller;
        private List<String> admin;
    }
}
