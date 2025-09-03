package com.create.chacha.common.util;

import com.create.chacha.domains.shared.constants.MemberRoleEnum;
import com.create.chacha.domains.shared.entity.member.MemberEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-validity}") long accessTokenValidity,
            @Value("${jwt.refresh-token-validity}") long refreshTokenValidity) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    /** AccessToken 생성 */
    public String createAccessToken(Long id, String email, String name, String phone, MemberRoleEnum role) {
        return buildToken(id, email, name, phone, role, accessTokenValidity);
    }

    /** RefreshToken 생성 */
    public String createRefreshToken(Long id, String email, String name, String phone, MemberRoleEnum role) {
        return buildToken(id, email, name, phone, role, refreshTokenValidity);
    }

    /** JWT 토큰 생성 */
    private String buildToken(Long id, String email, String name, String phone, MemberRoleEnum role, long validity) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setSubject(email)
                .claim("id", id)
                .claim("email", email)
                .claim("name", new String(name.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8))
                .claim("phone", phone)
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** JWT에서 ID 추출 */
    public Long getId(String token) {
        return parseClaims(token).get("id", Long.class);
    }

    /** JWT에서 subject(email) 추출 */
    public String getEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /** JWT에서 name 추출 */
    public String getName(String token) {
        return parseClaims(token).get("name", String.class);
    }

    /** JWT에서 phone 추출 */
    public String getPhone(String token) {
        return parseClaims(token).get("phone", String.class);
    }

    /** JWT에서 role 추출 */
    public MemberRoleEnum getRole(String token) {
        String roleStr = parseClaims(token).get("role", String.class);
        return MemberRoleEnum.valueOf(roleStr);
    }

    /** JWT에서 모든 Claims 추출 */
    public Claims getAllClaims(String token) {
        return parseClaims(token);
    }

    /** JWT 검증 */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT Token", e);
            return false;
        }
    }

    /** JWT 만료 시간 확인(ms) */
    public long getExpiration(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration.getTime() - System.currentTimeMillis();
    }

    /** JWT 만료 여부 확인 */
    public boolean isTokenExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    /** Claims 파싱, 만료된 토큰도 Claims 반환 */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}