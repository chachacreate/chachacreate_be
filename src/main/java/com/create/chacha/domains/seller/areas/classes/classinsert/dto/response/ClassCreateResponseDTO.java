package com.create.chacha.domains.seller.areas.classes.classinsert.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClassCreateResponseDTO {
	 private List<Long> classIds;
	   private int createdCount;
}
