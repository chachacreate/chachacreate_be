package com.create.chacha.domains.seller.areas.resumes.service;

import com.create.chacha.domains.seller.areas.resumes.dto.request.ResumeRequestDTO;
import com.create.chacha.domains.seller.areas.resumes.dto.response.ResumeResponseDTO;

public interface ResumeVerificationService {
    ResumeResponseDTO createResume(String storeUrl, ResumeRequestDTO dto);
}
