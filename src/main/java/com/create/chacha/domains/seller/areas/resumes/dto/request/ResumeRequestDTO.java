package com.create.chacha.domains.seller.areas.resumes.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ResumeRequestDTO {
    private List<ResumeImageDTO> images;
}
