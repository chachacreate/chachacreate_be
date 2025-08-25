package com.create.chacha.domains.buyer.areas.classes.classdetail.service;

import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassSummaryResponseDTO;

public interface ClassDetailQueryService {
    ClassSummaryResponseDTO getSummary(Long classId);
    
}
