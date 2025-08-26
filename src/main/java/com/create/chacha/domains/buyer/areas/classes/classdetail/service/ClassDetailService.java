package com.create.chacha.domains.buyer.areas.classes.classdetail.service;

import java.util.List;

import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassImagesResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassScheduleResponseDTO;
import com.create.chacha.domains.buyer.areas.classes.classdetail.dto.response.ClassSummaryResponseDTO;

public interface ClassDetailService {
    ClassSummaryResponseDTO getSummary(Long classId);
    ClassImagesResponseDTO getImages(Long classId);
    List<ClassScheduleResponseDTO> getSchedule(Long classId);

}
