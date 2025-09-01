package com.create.chacha.domains.seller.areas.resumes.dto.response;

import com.create.chacha.domains.shared.constants.AcceptStatusEnum;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeResponseDTO {
    private Long resumeId;                     // 이력서 ID
    private AcceptStatusEnum status;                     // APPROVED (제출 직후 상태)
    private List<ResumeImageResponseDTO> images;  // 이미지 URL + 설명
}
