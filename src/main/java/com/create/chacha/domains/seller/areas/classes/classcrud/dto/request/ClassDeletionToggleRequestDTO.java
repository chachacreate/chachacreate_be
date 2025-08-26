package com.create.chacha.domains.seller.areas.classes.classcrud.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class ClassDeletionToggleRequestDTO {
    private List<Long> classIds; // 필수
}