package com.create.chacha.domains.shared.member.service;

import com.create.chacha.domains.shared.member.dto.response.TokenResponseDTO;

public interface MemberLoginService {
    public TokenResponseDTO login(String mid, String password);
    public void logout(String mid, String accessToken);
    public TokenResponseDTO refresh(String mid, String refreshToken);
}