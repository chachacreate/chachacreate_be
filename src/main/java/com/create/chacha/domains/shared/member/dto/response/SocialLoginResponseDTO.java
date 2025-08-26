package com.create.chacha.domains.shared.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialLoginResponseDTO {
    private boolean isNewUser;           // 신규 회원 여부
    private String email;
    private String name;
    private String phone;
    private String gender;
    private String birthday;
    private String birthyear;
    private String accessToken;          // 기존 회원인 경우 토큰
    private String provider;             // "kakao" 또는 "naver"

    // 기존 회원용 생성자
    public static SocialLoginResponseDTO existingUser(String email, String accessToken, String provider) {
        return SocialLoginResponseDTO.builder()
                .isNewUser(false)
                .email(email)
                .accessToken(accessToken)
                .provider(provider)
                .build();
    }

    // 신규 회원용 생성자
    public static SocialLoginResponseDTO newUser(String email, String name, String phone,
                                                 String gender, String birthday, String birthyear, String provider) {
        return SocialLoginResponseDTO.builder()
                .isNewUser(true)
                .email(email)
                .name(name)
                .phone(phone)
                .gender(gender)
                .birthday(birthday)
                .birthyear(birthyear)
                .provider(provider)
                .build();
    }
}