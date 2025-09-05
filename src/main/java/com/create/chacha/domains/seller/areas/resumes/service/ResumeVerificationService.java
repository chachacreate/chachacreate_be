package com.create.chacha.domains.seller.areas.resumes.service;

import com.create.chacha.domains.seller.areas.resumes.dto.request.ResumeRequestDTO;
import com.create.chacha.domains.seller.areas.resumes.dto.response.ResumeResponseDTO;
import com.create.chacha.domains.seller.areas.settlement.dto.response.ClassOptionResponseDTO;

public interface ResumeVerificationService {
    ResumeResponseDTO createResume(String storeUrl, ResumeRequestDTO dto);
    ClassOptionResponseDTO getPrefill(String storeUrl);
}
