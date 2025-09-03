package com.create.chacha.config.security;

import com.create.chacha.common.util.JwtTokenProvider;
import com.create.chacha.domains.shared.constants.MemberRoleEnum;
import com.create.chacha.domains.shared.member.serviceimpl.MemberSecurityServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final MemberSecurityServiceImpl memberSecurityService;
    private final SecurityProperties securityProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                	
                	authorize.requestMatchers("/api/files/upload").permitAll();

                    // 공통 화이트리스트
                    if (securityProperties.getWhiteList() != null) {
                        authorize.requestMatchers(securityProperties.getWhiteList().toArray(String[]::new))
                                .permitAll();
                    }

                    // Role별 화이트리스트
                    if (securityProperties.getRole() != null) {
                        if (securityProperties.getRole().getUser() != null) {
                            authorize.requestMatchers(securityProperties.getRole().getUser().toArray(String[]::new))
                                    .hasRole(MemberRoleEnum.USER.name());
                        }
                        if (securityProperties.getRole().getSeller() != null) {
                            authorize.requestMatchers(securityProperties.getRole().getSeller().toArray(String[]::new))
                                    .hasRole(MemberRoleEnum.SELLER.name());
                        }
                        if (securityProperties.getRole().getPersonal_seller() != null) {
                            authorize.requestMatchers(securityProperties.getRole().getPersonal_seller().toArray(String[]::new))
                                    .hasRole(MemberRoleEnum.PERSONAL_SELLER.name());
                        }
                        if (securityProperties.getRole().getAdmin() != null) {
                            authorize.requestMatchers(securityProperties.getRole().getAdmin().toArray(String[]::new))
                                    .hasRole(MemberRoleEnum.ADMIN.name());
                        }
                    }

                    // 나머지 요청은 인증 필요
                    authorize.anyRequest().authenticated();
                });

        // JWT 필터 추가
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate, memberSecurityService),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }
}
